package com.smart.watchkart.ui.splash

import com.smart.watchkart.base.BaseActivity
import android.os.Bundle
import android.os.Handler
import com.smart.watchkart.R
import com.smart.watchkart.databinding.ActivitySplashBinding
import com.smart.watchkart.ui.home.HomeActivity
import com.smart.watchkart.ui.auth.AuthenticationActivity

class SplashActivity : BaseActivity<ActivitySplashBinding?>() {
    override fun createBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeTopBarColor(resources.getColor(R.color.app_bg_color))
        changeStatusBarIconColorToWhite(binding!!.root)
        showCutoutsOnNotch()
        Handler().postDelayed({
            if (pref.getLoginStatus(applicationContext)) {
                changeActivity(true, HomeActivity::class.java)
            } else {
                changeActivity(true, AuthenticationActivity::class.java)
            }
        }, 2000)
    }
}