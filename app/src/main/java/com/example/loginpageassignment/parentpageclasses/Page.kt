package com.example.loginpageassignment.parentpageclasses

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

abstract class Page : AppCompatActivity() {

    protected lateinit var buttonBack: Button

    public fun getButtonBackFun(): Button{
        return this.buttonBack
    }

    public fun setButtonBackFun(buttonBack: Button){
        this.buttonBack = buttonBack
    }

    protected fun backApp(){
        finish()
        exitProcess(0)
    }
}