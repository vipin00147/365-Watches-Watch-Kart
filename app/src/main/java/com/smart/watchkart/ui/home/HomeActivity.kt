package com.smart.watchkart.ui.home

import com.smart.watchkart.base.BaseActivity
import android.text.TextWatcher
import com.yarolegovich.slidingrootnav.SlidingRootNav
import android.os.Bundle
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.smart.watchkart.R
import com.smart.watchkart.ui.products.ProductsFragment
import com.smart.watchkart.ui.cart.CartFragment
import com.smart.watchkart.ui.auth.AuthenticationActivity
import com.smart.watchkart.ui.orderSuccess.OrderSuccessFragment
import android.text.Editable
import android.view.View
import com.smart.watchkart.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity<ActivityHomeBinding?>(), View.OnClickListener, TextWatcher {
    var navRoot: SlidingRootNav? = null
    override fun createBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navRoot = SlidingRootNavBuilder(this)
            .withMenuLayout(R.layout.menu_left_drawer)
            .withContentClickableWhenMenuOpened(false)
            .inject()
        homeActivity = this
        setDialogView()
        setViewListeners()
        changeTopBarColor(resources.getColor(R.color.app_bg_color))
        changeStatusBarIconColorToWhite(binding!!.root)
        changeFragment(false, ProductsFragment(), binding!!.homeContainer.id)
    }

    private fun setViewListeners() {
        binding!!.cartIcon.setOnClickListener(this)
        binding!!.menuIcon.setOnClickListener(this)
        binding!!.clearTextv.setOnClickListener(this)
        navRoot!!.layout.findViewById<View>(R.id.closeDrawerIv).setOnClickListener(this)
        navRoot!!.layout.findViewById<View>(R.id.homeTv).setOnClickListener(this)
        navRoot!!.layout.findViewById<View>(R.id.moreWatchesTv).setOnClickListener(this)
        navRoot!!.layout.findViewById<View>(R.id.CartTv).setOnClickListener(this)
        navRoot!!.layout.findViewById<View>(R.id.signOutBtn).setOnClickListener(this)
        binding!!.searchView.addTextChangedListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.cartIcon.id) {
            changeFragment(true, CartFragment(), R.id.homeContainer)
        } else if (view.id == binding!!.menuIcon.id) {
            navRoot!!.openMenu(true)
        } else if (view.id == binding!!.clearTextv.id) {
            binding!!.clearTextv.visibility = View.GONE
            binding!!.searchView.setText("")
            searchText = ""
            searchCallBack!!.searchProduct("")
        } else if (view.id == navRoot!!.layout.findViewById<View>(R.id.closeDrawerIv).id) {
            navRoot!!.closeMenu(true)
        } else if (view.id == navRoot!!.layout.findViewById<View>(R.id.homeTv).id) {
            if (currentDrawerPosition != 0) {
                changeFragment(false, ProductsFragment(), binding!!.homeContainer.id)
            }
            navRoot!!.closeMenu(true)
        } else if (view.id == navRoot!!.layout.findViewById<View>(R.id.CartTv).id) {
            if (currentDrawerPosition != 2) {
                changeFragment(true, CartFragment(), binding!!.homeContainer.id)
            }
            navRoot!!.closeMenu(true)
        } else if (view.id == navRoot!!.layout.findViewById<View>(R.id.moreWatchesTv).id) {
            navRoot!!.closeMenu(true)
        } else if (view.id == navRoot!!.layout.findViewById<View>(R.id.signOutBtn).id) {
            pref.setLoginStatus(this, false)
            changeActivity(true, AuthenticationActivity::class.java)
        }
    }

    override fun onBackPressed() {
        if (fragment is OrderSuccessFragment) {
            clearBackStack()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        searchText = editable.toString()
        searchCallBack!!.searchProduct(editable.toString())
        if (editable.toString().isEmpty()) {
            binding!!.clearTextv.visibility = View.GONE
        } else {
            binding!!.clearTextv.visibility = View.VISIBLE
        }
    }
}