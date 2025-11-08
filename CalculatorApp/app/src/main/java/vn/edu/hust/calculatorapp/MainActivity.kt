package vn.edu.hust.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.floor

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var textResult: TextView

    // Sử dụng Double để xử lý số thập phân và phép chia
    private var operand1: Double = 0.0
    private var pendingOperation: String? = null

    // Cờ để xác định xem có nên ghi đè số trên màn hình hay không
    private var isNewNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đảm bảo bạn sử dụng đúng file layout
        setContentView(R.layout.activity_main_linear_layout)

        // Trong layout bạn gửi, ID là tvDisplay
        textResult = findViewById(R.id.textView)

        // Gán OnClickListener cho tất cả các nút để code gọn hơn
        val buttonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv,
            R.id.btnBS, R.id.btnC, R.id.btnCE, R.id.btnEqual
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id)?.setOnClickListener(this)
        }
    }

    override fun onClick(view: View?) {
        // Sử dụng 'when' để code dễ đọc hơn
        when (view?.id) {
            // Các nút số
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9 -> {
                val button = view as Button
                appendNumber(button.text.toString())
            }

            // Các nút phép toán
            R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv -> {
                val button = view as Button
                setOperation(button.text.toString())
            }

            // Các nút chức năng
            R.id.btnEqual -> calculateResult()
            R.id.btnC -> clearAll()
            R.id.btnCE -> clearEntry()
            R.id.btnBS -> backspace()
        }
    }

    private fun appendNumber(number: String) {
        // Nếu đang chờ nhập số mới, hãy ghi đè lên màn hình
        if (isNewNumber) {
            textResult.text = number
            isNewNumber = false
        } else {
            // Nếu không, nối vào chuỗi hiện tại, nhưng không cho phép nhiều số 0 ở đầu
            if (textResult.text.toString() == "0") {
                textResult.text = number
            } else {
                textResult.append(number)
            }
        }
    }

    private fun setOperation(operation: String) {
        // Trước khi đặt phép toán mới, hãy tính kết quả của phép toán trước đó (nếu có)
        // Ví dụ: khi nhấn '+' trong "5 * 2 +", phép "5 * 2" sẽ được tính trước
        if (!isNewNumber) {
            calculateResult()
        }
        operand1 = textResult.text.toString().toDoubleOrNull() ?: 0.0
        pendingOperation = operation
        isNewNumber = true // Đánh dấu sẵn sàng để nhập toán hạng tiếp theo
    }

    private fun calculateResult() {
        // Chỉ tính toán khi có phép toán đang chờ và số thứ hai đã được nhập
        if (pendingOperation == null || isNewNumber) return

        val operand2 = textResult.text.toString().toDoubleOrNull() ?: 0.0
        var result = 0.0

        when (pendingOperation) {
            "+" -> result = operand1 + operand2
            "-" -> result = operand1 - operand2
            "x" -> result = operand1 * operand2
            "/" -> {
                if (operand2 == 0.0) {
                    textResult.text = "Error" // Xử lý lỗi chia cho 0
                    pendingOperation = null
                    isNewNumber = true
                    return
                }
                result = operand1 / operand2
            }
        }
        displayResult(result)
        operand1 = result // Lưu kết quả cho phép tính tiếp theo
        pendingOperation = null
        isNewNumber = true
    }

    /**
     * Nút C: Xóa toàn bộ phép toán, reset mọi thứ về ban đầu.
     */
    private fun clearAll() {
        operand1 = 0.0
        pendingOperation = null
        textResult.text = "0"
        isNewNumber = true
    }

    /**
     * Nút CE: Xóa giá trị toán hạng hiện tại về 0, không ảnh hưởng đến phép tính đang chờ.
     */
    private fun clearEntry() {
        textResult.text = "0"
        isNewNumber = true
    }

    /**
     * Nút BS: Xóa chữ số cuối cùng của toán hạng hiện tại.
     */
    private fun backspace() {
        if (isNewNumber) return // Không làm gì nếu đang ở trạng thái chờ nhập số mới

        val currentText = textResult.text.toString()
        if (currentText.length > 1) {
            textResult.text = currentText.dropLast(1)
        } else {
            textResult.text = "0"
        }
    }

    /**
     * Hiển thị kết quả một cách thông minh (số nguyên nếu có thể)
     */
    private fun displayResult(value: Double) {
        // Hiển thị số nguyên nếu không có phần thập phân
        if (value == floor(value)) {
            textResult.text = value.toLong().toString()
        } else {
            textResult.text = value.toString()
        }
    }
}
