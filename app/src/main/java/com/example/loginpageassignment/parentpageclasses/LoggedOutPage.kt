package com.example.loginpageassignment.parentpageclasses

import android.widget.Button
import android.widget.EditText

abstract class LoggedOutPage : Page() {

    protected lateinit var buttonLogin: Button
    protected lateinit var editTextUsername: EditText
    protected lateinit var editTextPassword: EditText

    protected fun getButtonLoginFun(): Button{
        return this.buttonLogin
    }

    protected fun setButtonLoginFun(buttonLogin: Button){
        this.buttonLogin = buttonLogin
    }

    protected fun getEditTextUsernameFun() : EditText{
        return this.editTextUsername
    }

    protected fun setEditTextUsernameFun(editTextUsername: EditText){
        this.editTextUsername = editTextUsername
    }

    protected fun getEditTextPasswordFun() : EditText{
        return this.editTextPassword
    }

    protected fun setEditTextPasswordFun(editTextPassword: EditText){
        this.editTextPassword = editTextPassword
    }
}