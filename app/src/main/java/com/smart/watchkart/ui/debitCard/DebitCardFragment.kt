package com.smart.watchkart.ui.debitCard

import com.smart.watchkart.base.BaseFragment
import com.braintreepayments.cardform.view.CardEditText.OnCardTypeChangedListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import com.braintreepayments.cardform.view.CardForm
import com.smart.watchkart.R
import android.view.WindowManager
import android.text.InputType
import android.view.View
import com.smart.watchkart.ui.orderSuccess.OrderSuccessFragment
import com.braintreepayments.cardform.utils.CardType
import com.smart.watchkart.databinding.FragmentDebitCardBinding

class DebitCardFragment() : BaseFragment<FragmentDebitCardBinding?>(), OnCardTypeChangedListener {
    override fun onCreateBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentDebitCardBinding {
        return FragmentDebitCardBinding.inflate(inflater!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.supportedCardTypes.setSupportedCardTypes(*SUPPORTED_CARD_TYPES)
        binding!!.cardForm.cardRequired(true)
            .maskCardNumber(true)
            .maskCvv(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .postalCodeRequired(true)
            .mobileNumberRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
            .actionLabel(getString(R.string.place_order))
            .setup(baseActivity)
        binding!!.cardForm.setOnCardTypeChangedListener(this)
        baseActivity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding!!.cardForm.cvvEditText.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        binding!!.placeOrderBtn.setOnClickListener {
            binding!!.cardForm.validate()
            if (binding!!.cardForm.isValid) {
                baseActivity!!.showLoader()
                Handler().postDelayed({
                    baseActivity!!.hideLoader()
                    baseActivity!!.orderPlacedCallBack!!.orderSuccess()
                    changeFragment(true, OrderSuccessFragment(), R.id.homeContainer)
                }, 1500)
            }
        }


        binding?.backButton?.setOnClickListener {
            baseActivity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onCardTypeChanged(cardType: CardType) {
        if (cardType == CardType.EMPTY) {
            binding!!.supportedCardTypes.setSupportedCardTypes(*SUPPORTED_CARD_TYPES)
        } else {
            binding!!.supportedCardTypes.setSelected(cardType)
        }
    }

    companion object {
        private val SUPPORTED_CARD_TYPES = arrayOf(
            CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY,
            CardType.HIPER, CardType.HIPERCARD
        )
    }
}