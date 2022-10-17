package com.smart.watchkart.ui.orderSuccess

import com.smart.watchkart.base.BaseFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.smart.watchkart.databinding.FragmentOrderSuccessBinding

class OrderSuccessFragment : BaseFragment<FragmentOrderSuccessBinding?>() {
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentOrderSuccessBinding {
        return FragmentOrderSuccessBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment = this
        binding!!.orderId.text = (Math.random() * (99999 - 10000 + 1) + 10000).toInt().toString()
        binding!!.estimateDeliveryTv.text =
            "Your estimate delivery will be in " + (Math.random() * (2 - 7 + 1) + 7).toInt() + " working days."
        binding!!.continueShoppingBtn.setOnClickListener { baseActivity!!.clearBackStack() }
    }
}