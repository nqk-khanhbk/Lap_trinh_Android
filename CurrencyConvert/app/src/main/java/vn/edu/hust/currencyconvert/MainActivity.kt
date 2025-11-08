package vn.edu.hust.currencyconvert

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var editTextAmount1: EditText
    private lateinit var editTextAmount2: EditText
    private lateinit var spinnerCurrency1: Spinner
    private lateinit var spinnerCurrency2: Spinner

    // Cờ để ngăn việc cập nhật đệ quy vô hạn
    private var isUpdating = false

    // Sử dụng Map<String, Double> để lưu trữ tỷ giá
    // Lấy VND làm đơn vị cơ sở (VND = 1.0)
    private val exchangeRates = mapOf(
        "VND" to 1.0,
        "USD" to 25450.0,    // Đô la Mỹ
        "EUR" to 27200.0,    // Euro
        "JPY" to 162.5,      // Yên Nhật
        "GBP" to 32250.0,    // Bảng Anh
        "AUD" to 16800.0,    // Đô la Úc
        "SGD" to 18700.0,    // Đô la Singapore
        "CAD" to 18600.0,    // Đô la Canada
        "CHF" to 28100.0,    // Franc Thụy Sĩ
        "CNY" to 3500.0,     // Nhân dân tệ Trung Quốc
        "KRW" to 18.5        // Won Hàn Quốc
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ánh xạ các view
        editTextAmount1 = findViewById(R.id.editTextAmount1)
        editTextAmount2 = findViewById(R.id.editTextAmount2)
        spinnerCurrency1 = findViewById(R.id.spinnerCurrency1)
        spinnerCurrency2 = findViewById(R.id.spinnerCurrency2)

        // Lấy danh sách các loại tiền tệ từ `exchangeRates`
        val currencies = exchangeRates.keys.toTypedArray()

        // Tạo Adapter cho Spinner
        // Tạo Adapter cho Spinner
        // 1. Vẫn dùng layout có sẵn của Android cho mục đang được chọn
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)

        // 2. Dùng file XML tùy chỉnh của BẠN cho danh sách thả xuống
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item) // Thay đổi ở đây


        spinnerCurrency1.adapter = adapter
        spinnerCurrency2.adapter = adapter

        // Thiết lập giá trị mặc định cho Spinner (ví dụ: VND -> USD)
        spinnerCurrency1.setSelection(currencies.indexOf("VND"))
        spinnerCurrency2.setSelection(currencies.indexOf("USD"))

        // Thêm listener cho EditText
        editTextAmount1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isUpdating) { // Chỉ thực hiện khi không có tiến trình cập nhật nào khác
                    updateConversion(editTextAmount1, editTextAmount2, spinnerCurrency1, spinnerCurrency2)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        editTextAmount2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isUpdating) {
                    updateConversion(editTextAmount2, editTextAmount1, spinnerCurrency2, spinnerCurrency1)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Thêm listener cho Spinner
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Khi thay đổi lựa chọn trên spinner, cũng cập nhật lại kết quả từ ô 1
                updateConversion(editTextAmount1, editTextAmount2, spinnerCurrency1, spinnerCurrency2)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCurrency1.onItemSelectedListener = spinnerListener
        spinnerCurrency2.onItemSelectedListener = spinnerListener
    }

    private fun updateConversion(sourceEditText: EditText, targetEditText: EditText, sourceSpinner: Spinner, targetSpinner: Spinner) {
        // Đặt cờ để tránh vòng lặp vô hạn
        isUpdating = true

        val sourceAmountStr = sourceEditText.text.toString()
        if (sourceAmountStr.isEmpty()) {
            targetEditText.setText("")
            isUpdating = false
            return
        }

        val sourceAmount = sourceAmountStr.toDoubleOrNull()
        if (sourceAmount == null) {
            isUpdating = false
            return // Nếu người dùng nhập không phải là số thì bỏ qua
        }

        val sourceCurrency = sourceSpinner.selectedItem.toString()
        val targetCurrency = targetSpinner.selectedItem.toString()

        // Lấy tỷ giá, nếu không tìm thấy thì mặc định là 1.0
        val sourceRateInVND = exchangeRates[sourceCurrency] ?: 1.0
        val targetRateInVND = exchangeRates[targetCurrency] ?: 1.0

        // Công thức chuyển đổi chéo: (Số tiền nguồn * Tỷ giá nguồn ra VND) / Tỷ giá đích ra VND
        val amountInVND = sourceAmount * sourceRateInVND
        val targetAmount = amountInVND / targetRateInVND

        // Định dạng số để hiển thị đẹp hơn (có dấu phẩy ngăn cách hàng nghìn)
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        numberFormat.maximumFractionDigits = 4 // Hiển thị tối đa 4 chữ số sau dấu phẩy

        targetEditText.setText(numberFormat.format(targetAmount).replace(",", ""))

        // Bỏ cờ sau khi cập nhật xong
        isUpdating = false
    }
}
