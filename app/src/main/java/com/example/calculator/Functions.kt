package com.example.calculator

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import org.mariuszgromada.math.mxparser.Expression
import java.lang.Exception

// подсчет результата введенного выражения
public fun calculateResult(activity: Activity){
    val inputField : TextView = activity!!.findViewById(R.id.inputField)
    val resultField : TextView = activity!!.findViewById(R.id.resultField)
    val applicationContext : Context = activity!!.applicationContext

    try{
        val numbers = "0123456789"
        val numbersEPiFact = "${numbers}eπ!"
        var str = inputField.text.toString()
        if (str.isEmpty())// || !numbersEPiFact.contains(str[str.length-1]))
            return

        var balance = 0
        for (i in str.indices){
            if (str[i] == '(')
                balance++
            else if (str[i] == ')')
                balance--
        }

        if (balance != 0){
            for (i in 1 .. balance){
                str += ")"
            }
        }
        str = str.replace(activity.resources.getString(R.string.btn_multiply), "*")
        str = str.replace(activity.resources.getString(R.string.btn_divide), "/")
        str = str.replace(activity.resources.getString(R.string.btn_square_root),
            "sqrt", true)
        str = str.replace(activity.resources.getString(R.string.btn_log),
            "log10", true)
        str = str.replace(activity.resources.getString(R.string.btn_pi),
            "pi", true)

        //resultField.text=str


//            // библиотека похуже
//            val expression = ExpressionBuilder(str).build()
//            val result = expression.evaluate()

        val expression = Expression(str)
        val result = expression.calculate()

        if (result.toString() == "Infinity"){
            if (resultField.text.isNotEmpty()) {
                Toast.makeText(
                    applicationContext, "E (Переполнение)", Toast.LENGTH_LONG
                ).show()
                resultField.text = ""
            }
        }
        else if (result.toString() != "NaN") {
            val longResult = result.toLong()

            if (result == longResult.toDouble()) {
                var res = longResult.toString()
                if (res.contains('E')){
                    if (res.length > 15 && res[15] > '4'){
                        res = res.substring(0, 15)
                    }

                }
                resultField.text = longResult.toString()
            }
            else
                resultField.text = result.toString()
        }
        else{
//            Toast.makeText(
//                applicationContext, "Ошибка. Проверьте ввод", Toast.LENGTH_LONG
//            ).show()
        }
    }
    catch (e: Exception){
        Log.d("Ошибка", "Сообщение: ${e.message}")
        resultField.text = "error ${e.message}"
    }
}
