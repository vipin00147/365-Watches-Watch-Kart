package com.smart.watchkart.ui.shippingAddress

import com.smart.watchkart.base.BaseFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.smart.watchkart.ui.debitCard.DebitCardFragment
import com.smart.watchkart.R
import com.smart.watchkart.databinding.FragmentShippingAddressBinding
import com.smart.watchkart.ui.orderSuccess.OrderSuccessFragment
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class ShippingAddressFragment : BaseFragment<FragmentShippingAddressBinding?>(),
    DatePickerDialog.OnDateSetListener {
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentShippingAddressBinding {
        return FragmentShippingAddressBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.codBtn.setOnClickListener { callCheckout() }

        /*binding.expiryDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
*/binding!!.debitCartBtn.setOnClickListener {
            if (isValidData) {
                changeFragment(true, DebitCardFragment(), R.id.homeContainer)
            }
        }
        binding!!.creditCardBtn.setOnClickListener {
            if (isValidData) {
                changeFragment(true, DebitCardFragment(), R.id.homeContainer)
            }
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )
        dpd.minDate = now
        dpd.show(baseActivity?.supportFragmentManager!!, "Datepickerdialog")
    }

    private fun callCheckout() {
        if (isValidData) {
            baseActivity!!.showLoader()
            Handler().postDelayed({
                baseActivity!!.hideLoader()
                baseActivity!!.orderPlacedCallBack!!.orderSuccess()
                changeFragment(true, OrderSuccessFragment(), R.id.homeContainer)
            }, 1500)
        }
    }

    private val isValidData: Boolean
        private get() = if (binding!!.countryRegionEt.text.toString().trim { it <= ' ' }
                .isEmpty()) {
            showAlertDialog(getString(R.string.region_field_cant_be_empty))
            false
        } else if (binding!!.fullNameEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.name_field_cant_be_empty))
            false
        } else if (binding!!.phoneNumberEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.phone_field_cant_empty))
            false
        } else if (binding!!.phoneNumberEt.text.toString().trim { it <= ' ' }.length < 10) {
            showAlertDialog(getString(R.string.phone_number_length))
            false
        } else if (binding!!.addressEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.add_cant_empty))
            false
        } else if (binding!!.cityEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.city_cant_empty))
            false
        } else if (binding!!.postalCodeEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.postal_cant_empty))
            false
        } else {
            true
        }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        //binding.expiryDateEt.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
    }
}