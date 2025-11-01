package vn.edu.hust.personal_information

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

class MainActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etBirthday: EditText
    private lateinit var etAddress: EditText
    private lateinit var etEmail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var cbAgree: CheckBox
    private lateinit var btnSelectDate: Button
    private lateinit var btnRegister: Button
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_personal_information)

        // Ánh xạ view
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etBirthday = findViewById(R.id.etBirthday)
        etAddress = findViewById(R.id.etAddress)
        etEmail = findViewById(R.id.etEmail)
        rgGender = findViewById(R.id.rgGender)
        cbAgree = findViewById(R.id.cbAgree)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnRegister = findViewById(R.id.btnRegister)
        calendarView = findViewById(R.id.calendarView)

        // Ẩn/hiện CalendarView khi nhấn nút Select
        btnSelectDate.setOnClickListener {
            toggleCalendarVisibility()
        }


        // THÊM VÀO: Ẩn/hiện CalendarView khi nhấn vào EditText ngày sinh
        etBirthday.setOnClickListener {
            toggleCalendarVisibility()
        }

        // Khi người dùng chọn ngày → cập nhật vào EditText và ẩn lịch
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            etBirthday.setText(date)
            calendarView.visibility = View.GONE
        }

        // Khi nhấn nút Register → kiểm tra dữ liệu
        btnRegister.setOnClickListener {
            validateForm()
        }
    }

    // Hàm ẩn/hiện và tự động cuộn
    private fun toggleCalendarVisibility() {
        if (calendarView.visibility == View.GONE) {
            calendarView.visibility = View.VISIBLE
            // Cuộn đến vị trí của calendarView
            calendarView.post {
                val scrollView = findViewById<ScrollView>(R.id.scrollView)
                scrollView.smoothScrollTo(0, calendarView.top)
            }
        } else {
            calendarView.visibility = View.GONE
        }
    }

    private fun validateForm() {
        var allFilled = true

        // Reset màu nền
        resetBackground(etFirstName)
        resetBackground(etLastName)
        resetBackground(etBirthday)
        resetBackground(etAddress)
        resetBackground(etEmail)

        // Kiểm tra từng ô
        if (isEmpty(etFirstName)) {
            etFirstName.setBackgroundColor(Color.parseColor("#FFCDD2"))
            allFilled = false
        }
        if (isEmpty(etLastName)) {
            etLastName.setBackgroundColor(Color.parseColor("#FFCDD2"))
            allFilled = false
        }
        if (isEmpty(etBirthday)) {
            etBirthday.setBackgroundColor(Color.parseColor("#FFCDD2"))
            allFilled = false
        }
        if (isEmpty(etAddress)) {
            etAddress.setBackgroundColor(Color.parseColor("#FFCDD2"))
            allFilled = false
        }
        if (isEmpty(etEmail)) {
            etEmail.setBackgroundColor(Color.parseColor("#FFCDD2"))
            allFilled = false
        }

        // Giới tính
        if (rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            allFilled = false
        }

        // Checkbox
        if (!cbAgree.isChecked) {
            Toast.makeText(this, "You must agree to Terms of Use", Toast.LENGTH_SHORT).show()
            allFilled = false
        }

        if (allFilled) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

    private fun resetBackground(editText: EditText) {
        editText.setBackgroundColor(Color.parseColor("#DDDDDD"))
    }
}