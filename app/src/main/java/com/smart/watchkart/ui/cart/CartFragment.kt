package com.smart.watchkart.ui.cart

import com.smart.watchkart.base.BaseFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.smart.watchkart.ui.shippingAddress.ShippingAddressFragment
import com.smart.watchkart.R
import com.smart.watchkart.utils.CONSTANTS
import android.os.Build
import android.os.Handler
import android.view.View
import com.smart.watchkart.model.ProductModel
import androidx.annotation.RequiresApi
import com.smart.watchkart.databinding.FragmentCartBinding
import java.util.ArrayList
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

class CartFragment : BaseFragment<FragmentCartBinding?>() {
    private var adapter: CartAdapter? = null
    var totalSaving = 0
    var discountPercentage = 0.0
    var subTotal = 0
    var totalTax = 0
    var amountAfterDiscount = 0
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentCartBinding {
        return FragmentCartBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity!!.currentDrawerPosition = 2
        checkIfListIsEmptyOrNot()
        updateTotal()
        binding!!.buyNowBtn.setOnClickListener {
            changeFragment(
                true,
                ShippingAddressFragment(),
                R.id.homeContainer
            )
        }
        binding!!.applyCouponBtn.setOnClickListener {
            baseActivity!!.showLoader()
            Handler().postDelayed({
                baseActivity!!.hideLoader()
                if (binding!!.couponCodeEt.text.toString()
                        .trim { it <= ' ' } == CONSTANTS.COUPON_10_OFF
                ) {
                    discountPercentage = 0.10
                    amountAfterDiscount = (subTotal - subTotal * discountPercentage).toInt()
                    showSuccessDialog(
                        """Yay!!
You saved ${"$"}${subTotal * discountPercentage} on your order"""
                    )
                    binding!!.couponCodeEt.setText("")
                    binding!!.couponRemoveLayout.visibility = View.VISIBLE
                    updateTotal()
                } else if (binding!!.couponCodeEt.text.toString()
                        .trim { it <= ' ' } == CONSTANTS.COUPON_20_OFF
                ) {
                    discountPercentage = 0.20
                    amountAfterDiscount = (subTotal - subTotal * discountPercentage).toInt()
                    showSuccessDialog(
                        """Yay!!
You saved ${"$"}${subTotal * discountPercentage} on your order"""
                    )
                    binding!!.couponCodeEt.setText("")
                    binding!!.couponRemoveLayout.visibility = View.VISIBLE
                    updateTotal()
                } else {
                    showAlertDialog(getString(R.string.invalid_coupon_code))
                }
            }, 500)
        }
        binding!!.removeCouponBtn.setOnClickListener {
            discountPercentage = 0.0
            binding!!.couponRemoveLayout.visibility = View.GONE
            updateTotal()
        }
    }

    private fun checkIfListIsEmptyOrNot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (getArrayListFromStream(
                    baseActivity!!.productList.stream()
                        .filter(Predicate { e: ProductModel -> e.get_is_AddedTo_Cart()!! })
                ).isEmpty()
            ) {
                binding!!.cartRecyclerView.visibility = View.GONE
                binding!!.notFound.visibility = View.VISIBLE
                binding!!.linearLayoutCompat.visibility = View.GONE
                binding!!.buyNowBtn.visibility = View.GONE
            } else {
                binding!!.linearLayoutCompat.visibility = View.VISIBLE
                binding!!.buyNowBtn.visibility = View.VISIBLE
                adapter = CartAdapter(
                    getArrayListFromStream(
                        baseActivity!!.productList.stream().filter(
                            Predicate { e: ProductModel -> e.get_is_AddedTo_Cart()!! })
                    ), this
                )
                binding!!.cartRecyclerView.adapter = adapter
            }
        }
    }

    fun decreaseQuantity(id: Int, adapterPosition: Int) {
        var listItemPosition = 0
        for (i in baseActivity!!.productList.indices) {
            if (id == baseActivity!!.productList[i].id) {
                listItemPosition = i
            }
        }
        val qty = baseActivity!!.productList[listItemPosition].get_quantity() - 1
        if (qty == 0) {
            baseActivity!!.productList[listItemPosition].set_is_AddedTo_Cart(false)
            adapter!!.removeItem(adapterPosition)
            checkIfListIsEmptyOrNot()
        } else {
            baseActivity!!.productList[listItemPosition].set_quantity(qty)
        }
        adapter!!.notifyDataSetChanged()
        updateTotal()
    }

    fun increaseQuantity(id: Int) {
        var listItemPosition = 0
        for (i in baseActivity!!.productList.indices) {
            if (id == baseActivity!!.productList[i].id) {
                listItemPosition = i
            }
        }
        val qty = baseActivity!!.productList[listItemPosition].get_quantity() + 1
        baseActivity!!.productList[listItemPosition].set_quantity(qty)
        adapter!!.notifyDataSetChanged()
        updateTotal()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getArrayListFromStream(stream: Stream<ProductModel?>): ArrayList<ProductModel> {
        // Convert the Stream to List
        val list =
            stream.collect(Collectors.toList())

        // Create an ArrayList of the List

        // Return the ArrayList
        return ArrayList<ProductModel>(list)
    }

    private fun updateTotal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val size = getArrayListFromStream(
                baseActivity!!.productList.stream().filter(
                    Predicate { e: ProductModel -> e.get_is_AddedTo_Cart() })
            ).size
            subTotal = 0
            totalTax = 0
            amountAfterDiscount = 0


            //set total tax amount
            for (i in 0 until size) {
                val quantity = getArrayListFromStream(
                    baseActivity!!.productList.stream().filter(
                        Predicate { e: ProductModel -> e.get_is_AddedTo_Cart() })
                )[i]!!
                    .get_quantity()
                totalTax += quantity * 900
            }


            //set sub total..
            for (i in 0 until size) {
                val quantity = getArrayListFromStream(
                    baseActivity!!.productList.stream().filter(
                        Predicate { e: ProductModel -> e.get_is_AddedTo_Cart()!! })
                )[i]!!
                    .get_quantity()
                val price = getArrayListFromStream(
                    baseActivity!!.productList.stream().filter(
                        Predicate { e: ProductModel -> e.get_is_AddedTo_Cart()!! })
                )[i]!!
                    .get_price()
                subTotal += quantity * price
            }
            subTotal += totalTax
            //calculate total saving...
            totalSaving = (subTotal * discountPercentage).toInt()
            binding!!.subTotalTv.text = "$ " + intSeparatorBy3("" + (subTotal - totalSaving))
            binding!!.totalSaving.text = "$ " + intSeparatorBy3("" + totalSaving)
            binding!!.totalTaxAmount.text = "$ " + intSeparatorBy3("" + totalTax)
        }
    }
}