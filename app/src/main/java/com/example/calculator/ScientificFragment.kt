package com.example.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.app.Activity
import android.content.Context
import org.mariuszgromada.math.mxparser.Expression


class ScientificFragment : Fragment() {

    private var actty: Activity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            actty = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_scientific, container, false)

        // функции
        view.findViewById<TextView>(R.id.btnSin).setOnClickListener { inputFunc("sin(") }
        view.findViewById<TextView>(R.id.btnCos).setOnClickListener { inputFunc("cos(") }
        view.findViewById<TextView>(R.id.btnTan).setOnClickListener { inputFunc("tan(") }
        view.findViewById<TextView>(R.id.btnArcSin).setOnClickListener { inputFunc("arcsin(") }
        view.findViewById<TextView>(R.id.btnArcCos).setOnClickListener { inputFunc("arccos(") }
        view.findViewById<TextView>(R.id.btnArcTan).setOnClickListener { inputFunc("arctan(") }
        view.findViewById<TextView>(R.id.btnLn).setOnClickListener { inputFunc("ln(") }
        view.findViewById<TextView>(R.id.btnLog).setOnClickListener { inputFunc("log(") }
        view.findViewById<TextView>(R.id.btnFactorial).setOnClickListener { inputFactorial() }
        view.findViewById<TextView>(R.id.btnE).setOnClickListener { inputFunc("e") }
        view.findViewById<TextView>(R.id.btnPi)
            .setOnClickListener { inputFunc(actty!!.resources.getString(R.string.btn_pi)) }
        view.findViewById<TextView>(R.id.btnSqRoot)
            .setOnClickListener { inputFunc("${actty!!.resources.getString(R.string.btn_square_root)}(") }
        view.findViewById<TextView>(R.id.btnABS).setOnClickListener { inputFunc("abs(") }
        view.findViewById<TextView>(R.id.btnX2).setOnClickListener { inputXFunc("^(2)") }
        view.findViewById<TextView>(R.id.btnXpowerY).setOnClickListener { inputXFunc("^(") }

        return view
    }

    // которые могут в начале строки: sin, asin, cos, acos, tan, atan, ln, log, e, π, √, |x|
    private fun inputFunc(func: String) {
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        if (inputField.text.isEmpty())
            inputField.append(func)
        else{
            val str = inputField.text.toString()
            val last = str[str.length - 1]
            val numbers = "0123456789"
            val numbersPiEBrFact = "${numbers}πe)!"
            val operation = "×÷+-"
            val operationBr = "${operation}("

            if (numbersPiEBrFact.contains(last))
                inputField.append("×$func")
            else if (operationBr.contains(last))
                inputField.append(func)
        }
        if (func == "e" || func == "π")
            calculateResult(actty!!)
    }

    // x!
    private fun inputFactorial() {
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        if (inputField.text.isEmpty()) {
            showToastWrongFormat()
        }
        else {
            val str = inputField.text.toString()
            val numbers = "0123456789"
            val operations = "×÷+-"
            val operationsBrDot = "$operations."
            var i = str.length - 1

            if (numbers.contains(str[i])) {
                // проверка на целое число перед факториалом
                while (i != 0) {
                    if (operationsBrDot.contains(str[i]))
                        break
                    i--
                }
                if (str[i] != '.') {
                    inputField.append("!")
                    calculateResult(actty!!)
                }
                else
                    showToastNotInt()
            }
            else if (str[i] == ')'){
                // проверка на целое число перед факториалом в скобках (...)!
                for (j in i downTo 0) {
                    if (str[j] == '('){
                        var substr : String = str.substring(j+1, i)
                        // посчитать будет ли в скобках целое, если да - поставить знак !
                        substr = substr.replace(resources.getString(R.string.btn_multiply), "*")
                        substr = substr.replace(resources.getString(R.string.btn_divide), "/")
                        val expression = Expression(substr)
                        val result = expression.calculate()
                        if (result.toString() != "NaN") {
                            val longResult = result.toLong()
                            if (result == longResult.toDouble()) {
                                inputField.append("!")
                                calculateResult(actty!!)
                            }
                            else
                                showToastNotInt()
                        }
                        break
                    }
                }
            }
            else
                showToastWrongFormat()
        }
    }

    // которые не могут стоять в начале строки: x^y, x^2
    private fun inputXFunc(func: String) {
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        if (inputField.text.isEmpty()) {
            showToastWrongFormat()
        } else {
            val str = inputField.text.toString()
            val numbers = "0123456789"
            val numbersEPiBr = "${numbers}eπ)"

            if (numbersEPiBr.contains(str[str.length - 1])) {
                inputField.append(func)
                if (func == "^(2)")
                    calculateResult(actty!!)
            }
            else
                showToastWrongFormat()
        }
    }

    // всплывающее сообщение о недопустимом формате
    private fun showToastWrongFormat(){
        Toast.makeText(
            actty!!.applicationContext, "Недопустимый формат", Toast.LENGTH_SHORT
        ).show()
    }

    // всплывающее сообщение о нецелом числе для факториала
    private fun showToastNotInt(){
        Toast.makeText(
            actty!!.applicationContext, "Нецелое число", Toast.LENGTH_SHORT
        ).show()
    }

}