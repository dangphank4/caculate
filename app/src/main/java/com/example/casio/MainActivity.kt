package com.example.casio

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var op: TextView
    private lateinit var ans: TextView
    private var currentInput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        op = findViewById(R.id.op)
        ans = findViewById(R.id.ans)

        val buttons = listOf(
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six,
            R.id.sevent, R.id.eight, R.id.nine, R.id.plus, R.id.minius, R.id.multiply,
            R.id.div, R.id.dot
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                appendToInput((it as Button).text.toString())
            }
        }
        findViewById<Button>(R.id.equal).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.c).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.ce).setOnClickListener { clearEntry() }
        findViewById<Button>(R.id.bs).setOnClickListener { backspace() }
    }

    private fun appendToInput(value: String) {
        currentInput += value
        op.text = currentInput
    }

    private fun clearAll() {
        currentInput = ""
        op.text = ""
        ans.text = ""
    }

    private fun clearEntry() {
        if (currentInput.isNotEmpty()) {
            var i = currentInput.length - 1

            // Nếu ký tự cuối là toán tử, chỉ xóa toán tử đó
            if (!currentInput[i].isDigit()) {
                currentInput = currentInput.dropLast(1)
            } else {
                // Nếu ký tự cuối là số, xóa toàn bộ số cuối cùng
                while (i >= 0 && currentInput[i].isDigit()) {
                    i--
                }
                currentInput = currentInput.substring(0, i + 1)
            }

            op.text = currentInput
        }
    }



    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            op.text = currentInput
        }
    }

    private fun calculateResult() {
        val expression = op.text.toString().replace(" ", "")
        if (expression.isEmpty()) return

        try {
            val result = evaluate(expression)

            // Nếu kết quả là số nguyên, hiển thị không có phần thập phân
            ans.text = if (result % 1 == 0.0) {
                result.toInt().toString()  // Chuyển thành số nguyên
            } else {
                String.format("%.6f", result).trimEnd('0').trimEnd('.')  // Làm tròn số thực
            }
        } catch (e: Exception) {
            ans.text = "ERROR"
        }
    }


    private fun evaluate(expression: String): Double {
        val values = Stack<Double>()  // Stack số
        val ops = Stack<Char>()       // Stack toán tử
        var i = 0

        while (i < expression.length) {
            when {
                expression[i].isDigit() -> {
                    var num = 0.0
                    while (i < expression.length && expression[i].isDigit()) {
                        num = num * 10 + (expression[i] - '0')
                        i++
                    }
                    if (i < expression.length && expression[i] == '.') { // Xử lý số thực
                        i++
                        var decimalPlace = 0.1
                        while (i < expression.length && expression[i].isDigit()) {
                            num += (expression[i] - '0') * decimalPlace
                            decimalPlace /= 10
                            i++
                        }
                    }
                    values.push(num)
                    i--
                }

                expression[i] in "+-x/" -> {
                    while (ops.isNotEmpty() && precedence(ops.peek()) >= precedence(expression[i])) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    ops.push(expression[i])
                }
            }
            i++
        }

        while (ops.isNotEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun precedence(op: Char): Int {
        return when (op) {
            '+', '-' -> 1
            'x', '/' -> 2
            else -> 0
        }
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            'x' -> a * b
            '/' -> if (b == 0.0) throw ArithmeticException("Division by zero") else a / b
            else -> throw IllegalArgumentException("Invalid operator")
        }
    }
}
