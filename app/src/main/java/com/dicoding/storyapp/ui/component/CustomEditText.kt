package com.dicoding.storyapp.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import com.dicoding.storyapp.R
import com.google.android.material.textfield.TextInputEditText


class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var showPasswordIcon: Drawable? = null
    private var hidePasswordIcon: Drawable? = null
    private var passwordIcon: Drawable? = null
    private var isPasswordVisible = false

    init {
        // Initialize icons
        showPasswordIcon = AppCompatResources.getDrawable(context, R.drawable.ic_show_password)
        hidePasswordIcon = AppCompatResources.getDrawable(context, R.drawable.ic_hide_password)
        passwordIcon = AppCompatResources.getDrawable(context, R.drawable.baseline_key_24)

        // Setup view
        setupInputType()
        setupIcon()
//        setupTextWatcher()
    }

    private fun setupInputType() {
        when (hint?.toString()?.lowercase()) {
            "password" -> {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                transformationMethod = PasswordTransformationMethod.getInstance()
            }

            else -> inputType = InputType.TYPE_CLASS_TEXT
        }
    }

    private fun setupIcon() {
        when (hint?.toString()?.lowercase()) {
            "email" -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, R.drawable.baseline_email_24),
                    null, null, null
                )
                compoundDrawablePadding = 16
            }
            "name" -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, R.drawable.baseline_person_24),
                    null, null, null
                )
                compoundDrawablePadding = 16
            }

            "password" -> {

                // Tambahkan ikon hide password di sebelah kanan
                setCompoundDrawablesWithIntrinsicBounds(
                    passwordIcon,
                    null,
                    hidePasswordIcon,
                    null
                )
                compoundDrawablePadding = 16
            }
        }
//
//        if (hint?.toString()?.lowercase() == "password") {
//            setCompoundDrawablesWithIntrinsicBounds(null, null, hidePasswordIcon, null)
//        }
    }

//    private fun setupTextWatcher() {
//        addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (!isValidInput(s)) {
//                    val errorMessage = when (hint?.toString()?.lowercase()) {
//                        "email" -> "Username harus minimal 6 karakter."
//                        "password" -> "Password tidak boleh kurang dari 8 karakter"
//                        else -> "Input tidak valid."
//                    }
//                    setError(errorMessage, null)
//                }
//            }
//
//            override fun afterTextChanged(s: android.text.Editable?) {}
//        })
//    }

//    private fun isValidInput(input: CharSequence?): Boolean {
//        return when (hint?.toString()?.lowercase()) {
//            "email" -> !input.isNullOrEmpty() && input.length >= 6
//            "password" -> !input.isNullOrEmpty() && input.length >= 8
//            else -> true
//        }
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawables[2]
            if (drawableEnd != null) {
                val hitBoxWidth = drawableEnd.bounds.width() + 50
                val drawableEndX = right - drawableEnd.bounds.width() - 50

                if (event.rawX >= (drawableEndX) && event.rawX <= (drawableEndX + hitBoxWidth)) {
                    togglePasswordVisibility()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        transformationMethod =
            if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
        setCompoundDrawablesWithIntrinsicBounds(
            passwordIcon, null,
            if (isPasswordVisible) showPasswordIcon else hidePasswordIcon,
            null
        )
        compoundDrawablePadding = 16
        setSelection(text?.length ?: 0) // Keep cursor at the end
    }
}
