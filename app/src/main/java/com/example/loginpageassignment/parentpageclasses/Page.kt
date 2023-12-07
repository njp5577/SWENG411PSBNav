package com.example.loginpageassignment.parentpageclasses

import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.loginpageassignment.R
import kotlin.system.exitProcess

abstract class Page : AppCompatActivity()
{

    private lateinit var buttonBack: Button

    protected abstract fun refresh()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        //set default app bar
        menuInflater.inflate(R.menu.menu, menu)


//        //refresh button listener
//        findViewById<Button>(R.id.refresh).setOnClickListener{
//
//        }


        return true
    }
}