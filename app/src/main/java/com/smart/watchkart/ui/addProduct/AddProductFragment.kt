package com.smart.watchkart.ui.addProduct

import android.Manifest

import com.smart.watchkart.base.BaseFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.smart.watchkart.R
import com.smart.watchkart.utils.ConnectivityReceiver
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.smart.watchkart.model.ProductModel
import com.google.android.gms.tasks.OnCompleteListener
import android.os.Build
import android.content.Intent
import com.github.dhaval2404.imagepicker.ImagePicker
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.app.Activity
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.smart.watchkart.databinding.FragmentAddProductBinding
import java.io.File
import java.util.*

class AddProductFragment() : BaseFragment<FragmentAddProductBinding?>(), View.OnClickListener {
    private var imageUri: Uri? = null
    private var firebaseImageUri: Uri? = null
    private val REQUEST_CODE = 123
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAddProductBinding {
        return FragmentAddProductBinding.inflate((inflater)!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding!!.addProductBtn.setOnClickListener(this)
        binding!!.productImage.setOnClickListener(this)
        binding!!.addImageBtn.setOnClickListener(this)
    }

    private fun callAddProduct() {
        if (isValidData) {
            rootNode = FirebaseDatabase.getInstance()
            reference = rootNode!!.getReference(getString(R.string.products_root_name))
            if (ConnectivityReceiver().isConnectedOrConnecting(baseActivity!!)) {
                saveCourse()
            } else {
                showAlertDialog(resources.getString(R.string.no_internet_available))
            }
        }
    }

    private val isValidData: Boolean
        private get() {
            if (imageUri == null) {
                showAlertDialog(getString(R.string.upload_product_image))
                return false
            } else if (binding!!.productNameEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.product_name_empty))
                return false
            } else if (binding!!.brandNameEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.brand_name_empty))
                return false
            } else if (binding!!.serialNumberEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.serial_number_empty))
                return false
            } else if (binding!!.priceEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.price_empty))
                return false
            } else if (binding!!.detailEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.product_detail_empty))
                return false
            } else {
                return true
            }
        }

    private fun saveCourse() {
        baseActivity!!.showLoader()
        val productName = binding!!.productNameEt.text.toString().trim { it <= ' ' }.uppercase(
            Locale.getDefault()
        )
        val brandName = binding!!.brandNameEt.text.toString().trim { it <= ' ' }
        val serialNumber = binding!!.serialNumberEt.text.toString().trim { it <= ' ' }
        val detail = binding!!.detailEt.text.toString().trim { it <= ' ' }
        val price = Integer.valueOf(binding!!.priceEt.text.toString().trim { it <= ' ' })
        val file = Uri.fromFile(File(imageUri!!.path))
        val riversRef = storageRef.child("images/" + file.lastPathSegment)
        val uploadTask = riversRef.putFile((imageUri)!!)
        uploadTask.addOnFailureListener(OnFailureListener { exception ->
            baseActivity!!.hideLoader()
            showAlertDialog(exception.message)
        }).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                storageRef.child(taskSnapshot.metadata!!.path).downloadUrl.addOnSuccessListener(
                    object : OnSuccessListener<Uri?> {
                        override fun onSuccess(uri: Uri?) {
                            firebaseImageUri = uri

                            // add product in database....
                            val model = ProductModel(
                                baseActivity!!.productList.size + 1,
                                productName,
                                brandName,
                                serialNumber,
                                detail,
                                price,
                                firebaseImageUri.toString(),
                                1,
                                false
                            )
                            baseActivity!!.productList.add(model)
                            baseActivity!!.searchCallBack!!.onAddProduct()
                            reference!!.child((baseActivity!!.productList.size + 1).toString())
                                .setValue(model)
                                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                    override fun onComplete(task: Task<Void?>) {
                                        baseActivity!!.hideLoader()
                                        Handler().postDelayed(object : Runnable {
                                            override fun run() {
                                                baseActivity!!.supportFragmentManager.popBackStack()
                                            }
                                        }, 200)
                                        if (task.isSuccessful) {
                                            showSuccessDialog(getString(R.string.product_added_successfully))
                                        } else {
                                            showSuccessDialog(getString(R.string.unable_to_add_product))
                                        }
                                    }
                                })
                        }
                    })
            }
        })
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.productImage.id || view.id == binding!!.addImageBtn.id) {
            checkSelfPermissions()
        } else if (view.id == binding!!.addProductBtn.id) {
            callAddProduct()
        }
    }

    private fun openImagePicker() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getPermission = Intent()
                getPermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getPermission)
            } else {
                ImagePicker.with(this)
                    .galleryMimeTypes(
                        arrayOf(
                            "image/png",
                            "image/jpg",
                            "image/jpeg", "image/webp"
                        )
                    )
                    .saveDir(
                        File(
                            baseActivity!!.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                            "Watch_Kart"
                        )
                    ).start()
            }
        } else {
            ImagePicker.with(this)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg", "image/webp"
                    )
                )
                .saveDir(
                    File(
                        baseActivity!!.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                        "Watch_Kart"
                    )
                ).start()
        }
    }

    private fun checkSelfPermissions() {
        if ((ContextCompat.checkSelfPermission(
                baseActivity!!.applicationContext,
                Manifest.permission.CAMERA
            ) +
                    ContextCompat.checkSelfPermission(
                        baseActivity!!.applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) +
                    ContextCompat.checkSelfPermission(
                        baseActivity!!.applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE
            )
        } else {
            openImagePicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            Log.e("ifsfdf", imageUri.toString())
            Glide.with(this@AddProductFragment)
                .load(imageUri)
                .into(binding!!.productImage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            }
        }
    }
}