package com.smart.watchkart.ui.signin

import com.smart.watchkart.base.BaseFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.smart.watchkart.utils.ConnectivityReceiver
import com.smart.watchkart.R
import com.smart.watchkart.ui.signup.SignUpFragment
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.smart.watchkart.ui.home.HomeActivity
import com.google.firebase.database.DatabaseError
import com.smart.watchkart.databinding.FragmentSignInBinding

class SignInFragment : BaseFragment<FragmentSignInBinding?>(), View.OnClickListener {
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSignInBinding {
        return FragmentSignInBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewListeners()
    }

    private fun setViewListeners() {
        binding!!.signUpText.setOnClickListener(this)
        binding!!.loginBtn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.loginBtn.id) {
            if (ConnectivityReceiver().isConnectedOrConnecting(baseActivity!!)) {
                callLoginApi()
            } else {
                showAlertDialog(resources.getString(R.string.no_internet_available))
            }
        } else if (view.id == binding!!.signUpText.id) {
            changeFragment(true, SignUpFragment(), R.id.authContainer)
        }
    }

    private fun callLoginApi() {
        if (isValidateData) {
            baseActivity!!.showLoader()
            val userName = binding!!.userNameEt.text.toString().trim { it <= ' ' }
            val password = binding!!.passwordEt.text.toString().trim { it <= ' ' }
            rootNode = FirebaseDatabase.getInstance()
            reference = rootNode!!.getReference(getString(R.string.root_name))
            val checkUser = reference!!.orderByChild("userName").equalTo(userName)
            checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val db_password = snapshot.child(userName).child("password").getValue(
                            String::class.java
                        )
                        val isAdmin = snapshot.child(userName).child("admin").getValue(Boolean::class.java)
                        if (db_password == password) {
                            baseActivity!!.hideLoader()
                            pref.setLoginStatus(context!!, true)
                            pref.setUserName(context!!, userName)
                            pref.setIsAdmin(context!!, isAdmin)
                            baseActivity!!.changeActivity(true, HomeActivity::class.java)
                        } else {
                            baseActivity!!.hideLoader()
                            showAlertDialog(getString(R.string.invalid_password))
                        }
                    } else {
                        baseActivity!!.hideLoader()
                        showAlertDialog(getString(R.string.username_does_not_exist))
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private val isValidateData: Boolean
        private get() = if (binding!!.userNameEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.username_cant_empty))
            false
        } else if (binding!!.passwordEt.text.toString().trim { it <= ' ' }.isEmpty()) {
            showAlertDialog(getString(R.string.password_cant_empty))
            false
        } else {
            true
        }

    override fun onResume() {
        super.onResume()
        binding!!.userNameEt.setText("")
        binding!!.passwordEt.setText("")
    }
}