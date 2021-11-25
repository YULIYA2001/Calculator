package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.app.Activity
import android.content.Context


class BaseFragment : Fragment(){

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

        val view = inflater.inflate(R.layout.fragment_base, container, false)

        // вывод вводимых чисел на экран
        view.findViewById<TextView>(R.id.btn0).setOnClickListener { inputNumber("0") }
        view.findViewById<TextView>(R.id.btn1).setOnClickListener { inputNumber("1") }
        view.findViewById<TextView>(R.id.btn2).setOnClickListener { inputNumber("2") }
        view.findViewById<TextView>(R.id.btn3).setOnClickListener { inputNumber("3") }
        view.findViewById<TextView>(R.id.btn4).setOnClickListener { inputNumber("4") }
        view.findViewById<TextView>(R.id.btn5).setOnClickListener { inputNumber("5") }
        view.findViewById<TextView>(R.id.btn6).setOnClickListener { inputNumber("6") }
        view.findViewById<TextView>(R.id.btn7).setOnClickListener { inputNumber("7") }
        view.findViewById<TextView>(R.id.btn8).setOnClickListener { inputNumber("8") }
        view.findViewById<TextView>(R.id.btn9).setOnClickListener { inputNumber("9") }

        // вывод вводимых операций на экран
        view.findViewById<TextView>(R.id.btnPlus).setOnClickListener{
            inputOperation(resources.getString(R.string.btn_plus))
        }
        view.findViewById<TextView>(R.id.btnMinus).setOnClickListener{
            inputOperation(resources.getString(R.string.btn_minus))
        }
        view.findViewById<TextView>(R.id.btnDivide).setOnClickListener{
            inputOperation(resources.getString(R.string.btn_divide))
        }
        view.findViewById<TextView>(R.id.btnMultiply).setOnClickListener{
            inputOperation(resources.getString(R.string.btn_multiply))
        }

        // вывод скобок () и .
        view.findViewById<TextView>(R.id.btnOpenBr).setOnClickListener{ inputOpenBracket() }
        view.findViewById<TextView>(R.id.btnCloseBr).setOnClickListener{ inputCloseBracket() }
        view.findViewById<TextView>(R.id.btnDot).setOnClickListener{ inputDot() }

        // другие кнопки: C = +/-
        view.findViewById<TextView>(R.id.btnC).setOnClickListener{ inputClear() }
        view.findViewById<TextView>(R.id.btnResult).setOnClickListener { inputEqualBtn() }
        view.findViewById<TextView>(R.id.btnSign).setOnClickListener { inputChangeSign() }
        return view
    }

    // (
    private fun inputOpenBracket(){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        val numbers = "0123456789"
        val numbersPiEFact = "${numbers}eπ!"

        val operations = "×÷+-"

        if (inputField.text.isEmpty())
            inputField.text = "("
        else {
            val str = inputField.text.toString()
            val last = str[str.length - 1]

            if (operations.contains(last) || last == '(')
                inputField.append("(")
            else if (numbersPiEFact.contains(last) || last == ')')
                inputField.append("×(")
        }
    }

    // )
    private fun inputCloseBracket(){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        val numbers = "0123456789"
        val numbersPiEFact = "${numbers}eπ!"

        if (inputField.text.isNotEmpty()){
            val str = inputField.text.toString()
            var balance = 0

            // поиск лишней открытой (которую можно закрыть)
            for (i in str.indices){
                if (str[i] == '(')
                    balance++
                else if (str[i] == ')')
                    balance--
            }
            if (balance > 0 && (numbersPiEFact.contains(str[str.length - 1]) || str[str.length - 1] == ')'))
                inputField.append(")")

            calculateResult(actty!!)
        }
    }

    // .
    private fun inputDot(){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        val numbers = "0123456789"
        val operations = "×÷+-"
        val operationsBr = "${operations}("

        if (countDigits() == 15 || countDigits() == 14){
            showToastForDigitsCount()
        }
        else {
            if (inputField.text.isEmpty()) {
                inputField.append("0.")
            } else {
                val str = inputField.text.toString()
                val last = str[str.length - 1]

                if (operationsBr.contains(last))
                    inputField.append("0.")
                else if (numbers.contains(last)) {
                    if (str.length == 1)
                        inputField.append(".")
                    else {
                        for (i in str.length - 2 downTo 0) {
                            if (str[i] == '.')
                                break
                            if (operations.contains(str[i]) || i == 0) {
                                inputField.append(".")
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    // C
    private fun inputClear(){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)
        val resultField : TextView = actty!!.findViewById(R.id.resultField)

        inputField.text = ""
        resultField.text = ""
    }

    // =
    private fun inputEqualBtn(){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)
        val resultField : TextView = actty!!.findViewById(R.id.resultField)

        if (resultField.text.isNotEmpty()) {
            val str = resultField.text.toString()

            if (str[0] == '-')
                inputField.text = "($str"
            else
                inputField.text = resultField.text
            resultField.text = ""
        }
    }

    // +/- смена знака    " (- "
    private fun inputChangeSign(){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        val operations = "×÷+-"
        val operationsBr = "${operations}("

        if (inputField.text.isEmpty()) {
            inputField.append("(-")
        }
        else {
            val str = inputField.text.toString()
            val last = str[str.length - 1]

            if (last == '!'){
                inputField.append("×(-")
            }
            else if (operations.contains(last) || last == '(' || last == ')') {
                // если в конце (-, то просто удалить их
                if(str.length >= 2 && last == '-' && str[str.length - 2] == '('){
                    if (str.length == 2)
                        inputField.text = ""
                    else
                        inputField.text = str.substring(0, str.length -2)
                }
                else {
                    if (last == ')')
                        inputField.append("×")
                    inputField.append("(-")
                }
            }
            else {      // если число
                // найти начало числа и поставить (- перед ним или убрать, если (- уже есть
                for (i in str.length - 1 downTo 0) {
                    if (operationsBr.contains(str[i])) {
                        if (i != 0 && str[i] == '-' && str[i - 1] == '(') {
                            inputField.text = str.replaceRange(i - 1, i + 1, "")
                            break
                        }
                        else {
                            inputField.text = StringBuilder(str).insert(i + 1, "(-").toString()
                            break
                        }
                    }
                    else if (i == 0)
                        inputField.text = StringBuilder(str).insert(0, "(-").toString()
                }
            }
            calculateResult(actty!!)
        }
    }

    // ×÷+-
    private fun inputOperation(char : String){
        // check limited demo version
        if (BuildConfig.FLAVOR.contains("demo") && (char == "+" || char == "×")){
            Toast.makeText(
                actty!!.applicationContext, "Недоступно в демо-версии", Toast.LENGTH_SHORT
            ).show()
            return
        }
        val inputField : TextView = actty!!.findViewById(R.id.inputField)
        val resultField : TextView = actty!!.findViewById(R.id.resultField)

        if (inputField.text.isNotEmpty()) {
            val str = inputField.text.toString()
            val numbers = "0123456789"
            val numbersPiEFact = "${numbers}πe!"
            val operations = "×÷+-"
            val last = str[str.length-1]

            if (numbersPiEFact.contains(last) || last == ')' ||
                (last == '(' && (char == "+" || char == "-"))) {
                inputField.append(char)
            }
            else if (operations.contains(last)) {
                if (str[str.length-2] != '(' || char == "+" || char == "-") {
                    str.subSequence(0, str.length - 1)
                    inputField.text = str.subSequence(0, str.length - 1)
                    inputField.append(char)
                }
            }
        }
        resultField.text = ""
    }

    // 0123456789
    private fun inputNumber(num: String){
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        if (countDigits() == 15){
            showToastForDigitsCount()
            return
        }
        if (inputField.text.isEmpty())
            inputField.append(num)
        else{
            val str = inputField.text.toString()
            val operations = "×÷+-"
            val operationsBr = "$operations)("
            val last = str[str.length-1]

            if (last == '0') {
                if (str.length > 1) {
                    if (!operationsBr.contains(str[str.length-2]))
                        inputField.append(num)
                    else {
                        inputField.text = str.subSequence(0, str.length - 1)
                        inputField.append(num)
                    }
                }
                else
                    inputField.text = num
            }
            else{
                if (last == ')' || last == '!' || last == 'π' || last == 'e')
                    inputField.append("×$num")
                else
                    inputField.append(num)
            }
        }
        calculateResult(actty!!)
    }

    // подсчет цифр в последнем введенном числе
    private fun countDigits():Int{
        val inputField : TextView = actty!!.findViewById(R.id.inputField)

        val str = inputField.text.toString()
        val operations = "×÷+-"
        val operationsOpBr = "$operations("
        if (str.isEmpty())
            return 0

        var i = str.length-1
        var count = 0

        while (!operationsOpBr.contains(str[i])){
            count++
            i--
            if (i == -1)
                break
        }
        return count
    }

    // всплывающее сообщение о превышении длины числа
    private fun showToastForDigitsCount() {
        Toast.makeText(
            actty!!.applicationContext, "Невозможно ввести более 15 цифр", Toast.LENGTH_SHORT
        ).show()
    }
}