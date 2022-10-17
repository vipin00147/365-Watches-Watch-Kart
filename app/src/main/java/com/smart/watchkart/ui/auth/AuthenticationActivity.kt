package com.smart.watchkart.ui.auth

import com.smart.watchkart.base.BaseActivity
import android.os.Bundle
import com.smart.watchkart.R
import com.smart.watchkart.databinding.ActivityAuthenticationBinding
import com.smart.watchkart.ui.introduction.IntroductionFragment

class AuthenticationActivity<T> : BaseActivity<ActivityAuthenticationBinding?>() {
    override fun createBinding(): ActivityAuthenticationBinding {
        return ActivityAuthenticationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDialogView()
        changeTopBarColor(resources.getColor(R.color.app_bg_color))
        changeStatusBarIconColorToWhite(binding!!.root)
        changeFragment(false, IntroductionFragment(), binding!!.authContainer.id)
    }
}