package com.smart.watchkart.ui.productDetail

import com.smart.watchkart.base.BaseFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.smart.watchkart.R
import com.smart.watchkart.databinding.FragmentProductDetailBinding
import com.smart.watchkart.ui.shippingAddress.ShippingAddressFragment

class ProductDetailFragment(var adapterPosition: Int) :
    BaseFragment<FragmentProductDetailBinding?>(), View.OnClickListener {
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentProductDetailBinding {
        return FragmentProductDetailBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewListeners()
        baseActivity!!.setImageWithGlide(
            binding!!.productImage, baseActivity!!.productList[adapterPosition].product_Image
        )
        binding!!.productNameTv.text = baseActivity!!.productList[adapterPosition].product_Name
        binding!!.serialNumberTv.text = baseActivity!!.productList[adapterPosition].serial_Number
        binding!!.productPriceTv.text = "$ " + intSeparatorBy3(
            "" + baseActivity!!.productList[adapterPosition].get_price()
        )
        binding!!.productDetail.text = baseActivity!!.productList[adapterPosition].get_detail()
    }

    private fun setViewListeners() {
        binding!!.addToCartBtn.setOnClickListener(this)
        binding!!.buyNowBtn.setOnClickListener(this)
        if (baseActivity!!.productList[adapterPosition].get_is_AddedTo_Cart()) {
            binding!!.addToCartBtn.setText(R.string.remove_from_cart)
        } else {
            binding!!.addToCartBtn.setText(R.string.add_to_cart)
        }
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.addToCartBtn.id) {
            baseActivity!!.showLoader()
            if (!baseActivity!!.productList[adapterPosition].get_is_AddedTo_Cart()) {
                baseActivity!!.productList[adapterPosition].set_is_AddedTo_Cart(true)
            } else {
                baseActivity!!.productList[adapterPosition].set_is_AddedTo_Cart(false)
            }
            Handler().postDelayed({
                baseActivity!!.hideLoader()
                if (baseActivity!!.productList[adapterPosition].get_is_AddedTo_Cart()) {
                    binding!!.addToCartBtn.setText(R.string.remove_from_cart)
                    showSuccessDialog(getString(R.string.item_added_to_cart))
                } else {
                    binding!!.addToCartBtn.setText(R.string.add_to_cart)
                    showSuccessDialog(getString(R.string.item_removed_to_cart))
                }
            }, 600)
        } else if (view.id == binding!!.buyNowBtn.id) {
            changeFragment(true, ShippingAddressFragment(), R.id.homeContainer)
        }
    }
}