package com.example.loginpageassignment.parentpageclasses

import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.example.loginpageassignment.R

abstract class LoggedOutPage : Page()
{
    private lateinit var buttonLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText

    abstract override fun refresh()

    protected fun getButtonLoginFun(): Button { return this.buttonLogin }

    protected fun setButtonLoginFun(buttonLogin: Button) { this.buttonLogin = buttonLogin }

    protected fun getEditTextUsernameFun() : EditText { return this.editTextUsername }

    protected fun setEditTextUsernameFun(editTextUsername: EditText) { this.editTextUsername = editTextUsername }

    protected fun getEditTextPasswordFun() : EditText{ return this.editTextPassword }

    protected fun setEditTextPasswordFun(editTextPassword: EditText){ this.editTextPassword = editTextPassword }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.refresh -> {
                refresh()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}