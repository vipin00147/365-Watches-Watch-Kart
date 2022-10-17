package com.smart.watchkart.base

import android.content.Context
import androidx.viewbinding.ViewBinding
import cn.pedant.SweetAlert.SweetAlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.smart.watchkart.ui.signup.SignUpFragment
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.smart.watchkart.R
import android.widget.TextView
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.Fragment
import com.smart.watchkart.utils.MyPref

abstract class BaseFragment<V : ViewBinding?> : Fragment() {
    @JvmField
    var binding: V? = null
    var baseActivity: BaseActivity<ViewBinding>? = null
    var sweetAlertDialog: SweetAlertDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = onCreateBinding(inflater, container, savedInstanceState)
        }
        return binding!!.root
    }

    abstract fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): V

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity<ViewBinding>
    }

    protected fun changeFragment(addToBackStack: Boolean?, fragment: Fragment?, container: Int) {
        baseActivity!!.changeFragment(addToBackStack!!, fragment!!, container)
    }

    protected fun showAlertDialog(message: String?) {
        sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        sweetAlertDialog!!.titleText = message
        sweetAlertDialog!!.confirmText = "Ok"
        sweetAlertDialog!!.showCancelButton(false)
        sweetAlertDialog!!.setConfirmClickListener { sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation() }
        sweetAlertDialog!!.show()
        setSweetAlertDialogStyle()
    }

    protected fun showSuccessDialog(message: String?) {
        sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog!!.titleText = message
        sweetAlertDialog!!.confirmText = "Ok"
        sweetAlertDialog!!.showCancelButton(false)
        sweetAlertDialog!!.setConfirmClickListener { sweetAlertDialog ->
            sweetAlertDialog.dismissWithAnimation()
            if (fragment is SignUpFragment) {
                baseActivity!!.supportFragmentManager.popBackStack()
            }
        }
        sweetAlertDialog!!.show()
        setSweetAlertDialogStyle()
    }

    private fun setSweetAlertDialogStyle() {
        var font = ResourcesCompat.getFont(baseActivity!!, R.font.nunito_sans_regular)
        sweetAlertDialog!!.getButton(SweetAlertDialog.BUTTON_CONFIRM).background =
            resources.getDrawable(R.drawable.sweet_alert_cancel_button_style)
        sweetAlertDialog!!.getButton(SweetAlertDialog.BUTTON_CONFIRM).backgroundTintList =
            resources.getColorStateList(R.color.app_bg_color)
        sweetAlertDialog!!.getButton(SweetAlertDialog.BUTTON_CONFIRM).typeface = font
        //sweetAlertDialog?.window?.setDimAmount(0f)
        val text = sweetAlertDialog!!.findViewById<TextView>(cn.pedant.SweetAlert.R.id.title_text)
        text.setTextColor(resources.getColor(R.color.black))
        filterConfig(text)

        //Setting font
        font = ResourcesCompat.getFont(baseActivity!!, R.font.nunito_sans_regular)
        text.typeface = font
        text.textSize = 15f
    }

    private fun filterConfig(text: TextView) {
        val params = text.layoutParams as MarginLayoutParams
        params.setMargins(0, 30, 0, 0)
        text.layoutParams = params
    }

    protected val pref: MyPref
        protected get() = MyPref()

    fun intSeparatorBy3(number: String?): String {
        return baseActivity!!.intSeparatorBy3(number!!)
    }

    var fragment: Fragment?
        get() = baseActivity!!.fragment
        set(fragment) {
            baseActivity!!.fragment = fragment
        }
}