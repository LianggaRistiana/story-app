package com.dicoding.storyapp.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
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
        showPasswordIcon = AppCompatResources.getDrawable(context, R.drawable.ic_show_password)
        hidePasswordIcon = AppCompatResources.getDrawable(context, R.drawable.ic_hide_password)
        passwordIcon = AppCompatResources.getDrawable(context, R.drawable.baseline_key_24)

        setupInputType()
        setupIcon()
        applyInitialPasswordState()
        setupTextValidation()
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

            "name", "nama" -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, R.drawable.baseline_person_24),
                    null, null, null
                )
                compoundDrawablePadding = 16
            }

            "password", "kata sandi" -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    passwordIcon,
                    null,
                    hidePasswordIcon,
                    null
                )
                compoundDrawablePadding = 16
            }
        }
    }

    private fun setupTextValidation() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (hint?.toString()?.lowercase()) {
                    "email" -> {
                        if (!p0.toString().isEmailValid()) {
                            setError(context.getString(R.string.email_invalid), null)
                        }
                    }

                    "name", "nama" -> {
                        if (!p0.toString().isNameValid()) {
                            setError(context.getString(R.string.name_invalid), null)
                        }
                    }

                    "password", "kata sandi" -> {
                        if (!p0.toString().isPasswordValid()) {
                            setError(context.getString(R.string.password_invalid), null)
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

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
        setSelection(text?.length ?: 0)
    }

    private fun applyInitialPasswordState() {
        if (hint?.toString()?.lowercase() in listOf("password", "kata sandi")) {
            isPasswordVisible = false
            transformationMethod = PasswordTransformationMethod.getInstance()
            setCompoundDrawablesWithIntrinsicBounds(
                passwordIcon, null, hidePasswordIcon, null
            )
            compoundDrawablePadding = 16
        }
    }


    private fun String.isNameValid(): Boolean {
        return !this.isNullOrEmpty()
    }

    private fun String.isEmailValid(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return this.matches(Regex(emailRegex))
    }

    private fun String.isPasswordValid(): Boolean {
        return !this.isNullOrEmpty() && this.length >= 8
    }
}
