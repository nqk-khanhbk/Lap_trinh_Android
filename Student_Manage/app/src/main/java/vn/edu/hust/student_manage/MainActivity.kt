package vn.edu.hust.student_manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Model dữ liệu sinh viên
data class StudentModel(var name: String, var mssv: String)

class MainActivity : AppCompatActivity() {

    private lateinit var edtMSSV: EditText
    private lateinit var edtName: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnUpdate: Button
    private lateinit var listView: ListView

    // Danh sách dữ liệu
    private val studentList = mutableListOf<StudentModel>()
    private lateinit var adapter: StudentAdapter

    // Biến lưu vị trí item đang được chọn để sửa (-1 là chưa chọn gì)
    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ View
        edtMSSV = findViewById(R.id.editTextMSSV)
        edtName = findViewById(R.id.editTextName)
        btnAdd = findViewById(R.id.buttonAdd)
        btnUpdate = findViewById(R.id.buttonUpdate)
        listView = findViewById(R.id.listViewStudents)

        // Khởi tạo dữ liệu mẫu
        studentList.add(StudentModel("Nguyễn Văn A", "20200001"))
        studentList.add(StudentModel("Trần Thị B", "20200002"))
        studentList.add(StudentModel("Lê Văn C", "20200003"))
        studentList.add(StudentModel("Phạm Thị D", "20200004"))
        studentList.add(StudentModel("Hoàng Văn E", "20200005"))

        // Khởi tạo Adapter
        // Truyền hàm callback (xử lý khi bấm nút xóa) vào adapter
        adapter = StudentAdapter(this, studentList) { position ->
            deleteStudent(position)
        }
        listView.adapter = adapter

        // 1. Xử lý sự kiện nút Add
        btnAdd.setOnClickListener {
            val mssv = edtMSSV.text.toString().trim()
            val name = edtName.text.toString().trim()

            if (mssv.isNotEmpty() && name.isNotEmpty()) {
                studentList.add(StudentModel(name, mssv))
                adapter.notifyDataSetChanged()
                clearInput()
                Toast.makeText(this, "Đã thêm sinh viên", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Xử lý sự kiện click vào một dòng trong ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val student = studentList[position]
            // Đổ dữ liệu lên EditText
            edtMSSV.setText(student.mssv)
            edtName.setText(student.name)

            // Lưu lại vị trí đang chọn
            selectedPosition = position

            Toast.makeText(this, "Đang chọn: ${student.name}", Toast.LENGTH_SHORT).show()
            // Nếu ko cho sửa mssv thì thêm dòng này
            // edtMSSV.isEnabled = false
        }

        // 3. Xử lý sự kiện nút Update
        btnUpdate.setOnClickListener {
            if (selectedPosition == -1) {
                Toast.makeText(this, "Vui lòng chọn sinh viên để cập nhật", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mssv = edtMSSV.text.toString().trim()
            val name = edtName.text.toString().trim()

            if (mssv.isNotEmpty() && name.isNotEmpty()) {
                // Cập nhật dữ liệu tại vị trí đã chọn
                studentList[selectedPosition].name = name
                studentList[selectedPosition].mssv = mssv

                adapter.notifyDataSetChanged()

                // Reset trạng thái
                clearInput()
                selectedPosition = -1
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Thông tin không được để trống", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm xóa sinh viên (được gọi từ Adapter)
    private fun deleteStudent(position: Int) {
        // Nếu đang chọn sinh viên này để sửa thì reset form
        if (position == selectedPosition) {
            clearInput()
            selectedPosition = -1
        }
        // Nếu xóa item phía trên item đang chọn, cần giảm index selectedPosition
        if (position < selectedPosition) {
            selectedPosition--
        }

        studentList.removeAt(position)
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
    }

    private fun clearInput() {
        edtMSSV.setText("")
        edtName.setText("")
        edtMSSV.requestFocus()
    }
}

// --- CLASS ADAPTER TÙY CHỈNH ---
class StudentAdapter(
    private val context: AppCompatActivity,
    private val list: List<StudentModel>,
    private val onDeleteClick: (Int) -> Unit // Callback khi bấm nút xóa
) : BaseAdapter() {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false)
            holder = ViewHolder()
            holder.tvName = view.findViewById(R.id.tvName)
            holder.tvMSSV = view.findViewById(R.id.tvMSSV)
            holder.imgDelete = view.findViewById(R.id.imgDelete)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val student = list[position]
        holder.tvName.text = student.name
        holder.tvMSSV.text = student.mssv

        // Xử lý sự kiện bấm vào nút thùng rác
        holder.imgDelete.setOnClickListener {
            onDeleteClick(position) // Gọi ngược lại hàm deleteStudent ở MainActivity
        }

        return view
    }

    private class ViewHolder {
        lateinit var tvName: TextView
        lateinit var tvMSSV: TextView
        lateinit var imgDelete: ImageView
    }
}
