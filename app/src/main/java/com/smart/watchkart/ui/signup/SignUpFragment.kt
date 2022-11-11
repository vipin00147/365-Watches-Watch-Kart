package com.smart.watchkart.ui.signup

import com.smart.watchkart.base.BaseFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.smart.watchkart.utils.ConnectivityReceiver
import com.smart.watchkart.R
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.smart.watchkart.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseError
import com.smart.watchkart.databinding.FragmentSignUpBinding

class SignUpFragment() : BaseFragment<FragmentSignUpBinding?>(), View.OnClickListener {
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate((inflater)!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewListeners()
        fragment = this
    }

    private fun setViewListeners() {
        binding!!.signInText.setOnClickListener(this)
        binding!!.signUpBtn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.signInText.id) {
            baseActivity!!.supportFragmentManager.popBackStack()
        } else if (view.id == binding!!.signUpBtn.id) {
            if (ConnectivityReceiver().isConnectedOrConnecting((context)!!)) {
                callSignUpApi()
            } else {
                showAlertDialog(resources.getString(R.string.no_internet_available))
            }
        }
    }

    private fun callSignUpApi() {
        if (isValidData) {
            baseActivity!!.showLoader()
            val name = binding!!.nameEt.text.toString().trim { it <= ' ' }
            val userName = binding!!.userNameEt.text.toString().trim { it <= ' ' }
            val password = binding!!.passwordEt.text.toString().trim { it <= ' ' }
            rootNode = FirebaseDatabase.getInstance()
            reference = rootNode!!.getReference(getString(R.string.root_name))
            val checkUser = reference!!.orderByChild("userName").equalTo(userName)
            checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        baseActivity!!.hideLoader()
                        showAlertDialog(getString(R.string.user_already_registered))
                    } else {
                        // set user in database....
                        val model = UserModel(name, userName, password, false)
                        reference!!.child(userName).setValue(model).addOnCompleteListener(
                            OnCompleteListener { task ->
                                baseActivity!!.hideLoader()
                                if (task.isSuccessful) {
                                    showSuccessDialog(getString(R.string.user_registered_successfully))
                                } else {
                                    showSuccessDialog(getString(R.string.unable_to_register_user))
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    baseActivity!!.hideLoader()
                    showAlertDialog(error.message)
                }
            })
        }
    }

    private val isValidData: Boolean
        private get() {
            if (binding!!.nameEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.name_field_cant_be_empty))
                return false
            }
            if (binding!!.userNameEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.username_cant_empty))
                return false
            }
            if (binding!!.passwordEt.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlertDialog(getString(R.string.password_cant_empty))
                return false
            } else {
                return true
            }
        }
}