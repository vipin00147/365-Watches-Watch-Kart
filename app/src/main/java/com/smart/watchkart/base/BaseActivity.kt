package com.smart.watchkart.base

import androidx.viewbinding.ViewBinding
import androidx.appcompat.app.AppCompatActivity
import com.smart.watchkart.model.ProductModel
import com.smart.watchkart.utils.OrderPlacedCallBack
import com.smart.watchkart.utils.SearchCallBack
import com.smart.watchkart.ui.home.HomeActivity
import android.os.Bundle
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.smart.watchkart.R
import com.smart.watchkart.utils.MyPref
import com.smart.watchkart.ui.products.ProductsFragment
import com.google.android.material.imageview.ShapeableImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import java.lang.StringBuilder
import java.util.ArrayList

/**
 * created by Vipin on 11-Aug-2022
 */
abstract class BaseActivity<V : ViewBinding?> : AppCompatActivity() {
    @JvmField
    var binding: V? = null
    private var loaderDialogView: View? = null
    private var loaderDialog: Dialog? = null
    val productList: ArrayList<ProductModel> = ArrayList<ProductModel>()
    var currentDrawerPosition = 0
    var orderPlacedCallBack: OrderPlacedCallBack? = null
        private set
    var searchCallBack: SearchCallBack? = null
    var fragment: Fragment? = null
    var homeActivity: HomeActivity? = null
    var searchText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = createBinding()
        setContentView(binding!!.root)
        super.onCreate(savedInstanceState)
    }

    abstract fun createBinding(): V
    fun changeActivity(setFinish: Boolean, T: Class<out Activity?>?) {
        startActivity(Intent(this, T))
        if (setFinish) {
            finish()
        }
    }

    fun changeFragment(addToBackStack: Boolean, fragment: Fragment, container: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.canonicalName)
        }
        transaction.commit()
    }

    protected fun showCutoutsOnNotch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    protected fun changeStatusBarIconColorToWhite(root: View?) {
        WindowInsetsControllerCompat(window, root!!).isAppearanceLightStatusBars = false
    }

    protected fun changeTopBarColor(color: Int) {
        window.statusBarColor = color
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is AppCompatEditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    protected fun setDialogView() {
        loaderDialog = Dialog(this)
        loaderDialogView = View.inflate(this, R.layout.layout_loader, null)
        loaderDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loaderDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        loaderDialog!!.setCancelable(false)
        loaderDialog!!.setContentView(loaderDialogView!!)
    }

    fun showLoader() {
        loaderDialog!!.show()
    }

    fun hideLoader() {
        loaderDialog!!.dismiss()
    }

    protected val pref: MyPref
        protected get() = MyPref()

    fun intSeparatorBy3(number: String): String {
        return if (number.length > 3) {
            val reversed = reverseString(number)
            var separated = StringBuilder()
            if (number.length <= 3) {
                separated = StringBuilder(number)
            } else {
                for (i in 0 until reversed.length) {
                    // when i is the third part of number
                    if (i != 0 && i % 3 == 0) separated.append(",")
                    separated.append(reversed[i])
                }
            }
            reverseString(separated.toString())
        } else {
            number
        }
    }

    fun reverseString(input: String): String {
        val strAsByteArray = input.toByteArray()
        val result = ByteArray(strAsByteArray.size)
        for (i in strAsByteArray.indices) result[i] = strAsByteArray[strAsByteArray.size - i - 1]
        return String(result)
    }

    fun setOrderPlacedCallBack(orderPlacedCallBack: ProductsFragment?) {
        this.orderPlacedCallBack = orderPlacedCallBack
    }

    fun clearBackStack() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

    fun setImageWithGlide(imageView: ShapeableImageView?, imageUri: String?) {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide.with(this)
            .load(imageUri)
            .placeholder(circularProgressDrawable)
            .into(imageView!!)
    }
}