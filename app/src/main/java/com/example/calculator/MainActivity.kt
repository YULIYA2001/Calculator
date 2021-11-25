package com.example.calculator

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // определяем изменение ориентации экрана
    private var modeScience = false
    private val input = "INPUT"
    private val result = "RESULT"
    private val mScience = "MODE"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // пролистывание экрана ввода, если не помещается выражение
        inputField.movementMethod = ScrollingMovementMethod()

        // кнопка: <-
        btnBack.setOnClickListener { inputBack() }

        // кнопка: change mode
        btnMode.setOnClickListener { clickChangeMode() }
    }

    // сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(input, inputField.text.toString())
        outState.putString(result, resultField.text.toString())
        outState.putString(mScience, modeScience.toString())
        super.onSaveInstanceState(outState)
    }

    // получение ранее сохраненного состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputField.text = savedInstanceState.getString(input)
        resultField.text = savedInstanceState.getString(result)

        inputField.addTextChangedListener(){
            if (modeScience && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            else if (!modeScience && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            //resultField.text = modeScience.toString()
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            modeScience = true
        else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            modeScience = false
        //resultField.text = modeScience.toString()
    }


    private fun clickChangeMode(){
        if (!modeScience) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            modeScience = true
        }
        else if (modeScience){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            modeScience = false
        }
        //resultField.text = modeScience.toString()
    }

    // <-
    private fun inputBack(){
        val numbers = "0123456789"
        val numbersPiEFactBr = "${numbers}eπ!)"
        var str = inputField.text.toString()
        val len = str.length

        if (str.isNotEmpty()){
            if (len == 1)
                str = ""
            else {
                if (str[len-1] == '(') {
                    if (str[len-2] in "√^(nsg") {
                        if (str[len - 2] == '(')
                            str = str.substring(0, len - 1)
                        // удаление ^( и √(
                        else if (str[len - 2] in "√^")
                            str = str.substring(0, len - 2)
                        else {
                            // удаление ln(
                            if (len >= 3 && str[len - 3] == 'l')
                                str = str.substring(0, len - 3)
                            else {
                                // удаление arcsin(, arccos(, arctan(
                                if (len > 4 && str[len - 5] == 'c')
                                    str = str.substring(0, len - 7)
                                else {
                                    // удаление sin(, cos(, tan(, log(, abs(
                                    if (len == 4 || len > 4 && str[len - 5] != 'c')
                                        str = str.substring(0, len - 4)
                                }
                            }
                        }
                    }
                    else {
                        // удаление одного последнего символа
                        str = str.substring(0, len - 1)
                    }
                }
                else { // не ...(
                    // удаление одного последнего символа
                    str = str.substring(0, len - 1)
                }
            }

            if (str == ""){
                inputField.text = ""
                resultField.text = ""
            }
            else {
                inputField.text = str
                if (numbersPiEFactBr.contains(str[str.length-1]))
                    calculateResult(this)
            }
        }
    }

}

