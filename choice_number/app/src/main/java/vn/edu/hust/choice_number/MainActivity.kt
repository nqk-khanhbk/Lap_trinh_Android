package vn.edu.hust.choice_number

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var edtNumber: EditText
    private lateinit var listView: ListView
    private lateinit var tvMessage: TextView
    private lateinit var adapter: ArrayAdapter<Long>

    // RadioButtons
    private lateinit var rbLe: RadioButton
    private lateinit var rbChan: RadioButton
    private lateinit var rbNguyenTo: RadioButton
    private lateinit var rbChinhPhuong: RadioButton
    private lateinit var rbHoanHao: RadioButton
    private lateinit var rbFibo: RadioButton
    private lateinit var allRadios: List<RadioButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtNumber = findViewById(R.id.edtNumber)
        listView = findViewById(R.id.listView)
        tvMessage = findViewById(R.id.tvMessage)

        rbLe = findViewById(R.id.rbLe)
        rbChan = findViewById(R.id.rbChan)
        rbNguyenTo = findViewById(R.id.rbNguyenTo)
        rbChinhPhuong = findViewById(R.id.rbChinhPhuong)
        rbHoanHao = findViewById(R.id.rbHoanHao)
        rbFibo = findViewById(R.id.rbFibo)

        allRadios = listOf(rbLe, rbChan, rbNguyenTo, rbChinhPhuong, rbHoanHao, rbFibo)

        // Khởi tạo adapter rỗng (kiểu Long)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<Long>())
        listView.adapter = adapter

        // TextWatcher - cập nhật ngay khi nhập thay đổi
        edtNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateList() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Bởi vì RadioButton không nằm trực tiếp trong RadioGroup, ta quản lý chọn thủ công:
        allRadios.forEach { radio ->
            radio.setOnClickListener {
                // bỏ chọn tất cả trước
                allRadios.forEach { it.isChecked = false }
                // chọn radio được click
                radio.isChecked = true
                // cập nhật list
                updateList()
            }
        }

        // Thông báo ban đầu
        tvMessage.text = "Nhập số nguyên để xem kết quả"
        tvMessage.visibility = TextView.VISIBLE
    }

    private fun updateList() {
        val input = edtNumber.text.toString().trim()
        if (input.isEmpty()) {
            adapter.clear()
            adapter.notifyDataSetChanged()
            tvMessage.text = "Nhập số nguyên để xem kết quả"
            tvMessage.visibility = TextView.VISIBLE
            return
        }

        val n = input.toLongOrNull()
        if (n == null || n <= 0) {
            adapter.clear()
            adapter.notifyDataSetChanged()
            tvMessage.text = "Vui lòng nhập số nguyên dương"
            tvMessage.visibility = TextView.VISIBLE
            return
        }

        val result = mutableListOf<Long>()

        when {
            rbLe.isChecked -> {
                var i = 1L
                while (i < n) {
                    if (i % 2L != 0L) result.add(i)
                    i++
                }
            }
            rbChan.isChecked -> {
                var i = 1L
                while (i < n) {
                    if (i % 2L == 0L) result.add(i)
                    i++
                }
            }
            rbNguyenTo.isChecked -> {
                // chú ý: nguyên tố kiểm tra n nhỏ; nếu quá lớn sẽ chậm
                var i = 2L
                while (i < n) {
                    if (isPrime(i)) result.add(i)
                    i++
                }
            }
            rbHoanHao.isChecked -> {
                // tìm số hoàn hảo giới hạn (tránh lặp quá lâu)
                val limit = if (n > 100000L) 100000L else n
                if (n > 100000L) {
                    Toast.makeText(this, "Tìm số hoàn hảo giới hạn đến 100,000", Toast.LENGTH_SHORT).show()
                }
                var i = 2L
                while (i < limit) {
                    if (isPerfect(i)) result.add(i)
                    i++
                }
            }
            rbChinhPhuong.isChecked -> {
                var i = 1L
                while (i * i < n) {
                    result.add(i * i)
                    i++
                }
            }
            rbFibo.isChecked -> {
                result.addAll(fibonacciLessThan(n))
            }
            else -> { /* không có chọn */ }
        }

        if (result.isEmpty()) {
            adapter.clear()
            adapter.notifyDataSetChanged()
            tvMessage.text = "Không có số nào thỏa mãn"
            tvMessage.visibility = TextView.VISIBLE
        } else {
            tvMessage.visibility = TextView.GONE
            adapter.clear()
            adapter.addAll(result)
            adapter.notifyDataSetChanged()
        }
    }

    // --- hàm kiểm tra ---
    private fun isPrime(num: Long): Boolean {
        if (num < 2) return false
        val r = sqrt(num.toDouble()).toLong()
        var i = 2L
        while (i <= r) {
            if (num % i == 0L) return false
            i++
        }
        return true
    }

    private fun isPerfect(num: Long): Boolean {
        if (num <= 1) return false
        var sum = 1L
        val r = sqrt(num.toDouble()).toLong()
        var i = 2L
        while (i <= r) {
            if (num % i == 0L) {
                sum += i
                val other = num / i
                if (other != i) sum += other
            }
            i++
        }
        return sum == num
    }

    private fun fibonacciLessThan(limit: Long): List<Long> {
        if (limit <= 1) return emptyList()
        val fibos = mutableListOf<Long>()
        var a = 0L
        var b = 1L
        while (b < limit) {
            fibos.add(b)
            val next = a + b
            a = b
            b = next
        }
        return fibos
    }
}
