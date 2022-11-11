package com.smart.watchkart.ui.introduction

import com.smart.watchkart.base.BaseFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.smart.watchkart.ui.signin.SignInFragment
import com.smart.watchkart.R
import com.smart.watchkart.databinding.FragmentIntroductionkBinding

class IntroductionFragment() : BaseFragment<FragmentIntroductionkBinding?>(), View.OnClickListener {
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentIntroductionkBinding {
        return FragmentIntroductionkBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewListeners()
    }

    private fun setViewListeners() {
        binding!!.signInText.setOnClickListener(this)
        binding!!.letsGoTV.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.signInText.id || view.id == binding!!.letsGoTV.id) {
            changeFragment(false, SignInFragment(), R.id.authContainer)
        }
    }
}