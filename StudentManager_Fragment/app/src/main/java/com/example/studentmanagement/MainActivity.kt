
package com.example.studentmanagement

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmanagement.controller.AddStudentFragment
import com.example.studentmanagement.controller.FragmentSingleton
import com.example.studentmanagement.controller.StudentAdapter
import com.example.studentmanagement.database.DatabaseHelper
import com.example.studentmanagement.model.Student
import com.google.android.material.navigation.NavigationView

// MainActivity sẽ implement interface mới từ Adapter
class MainActivity : AppCompatActivity(), StudentAdapter.StudentAdapterListener {

    private lateinit var studentAdapter: StudentAdapter
    private lateinit var students: MutableList<Student>
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        setupUI()
        loadDataAndSetupRecyclerView()
        setupNavDrawer()
        setupSwipeToDelete()
    }

    private fun setupUI() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)
    }

    private fun loadDataAndSetupRecyclerView() {
        students = dbHelper.getAllStudentsAsList()
        studentAdapter = StudentAdapter(students, this)
        recyclerView = findViewById(R.id.recycler_view_students)
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showAddStudentFragment() {
        FragmentSingleton.getInstance().onStudentAddedListener = object : AddStudentFragment.OnStudentAddedListener {
            override fun onStudentAdded(name: String, studentId: String) {
                if (name.isBlank() || studentId.isBlank()) {
                    Toast.makeText(this@MainActivity, "Tên và MSSV không được để trống", Toast.LENGTH_SHORT).show()
                    return
                }
                val newStudent = Student(name, studentId)
                val newRowId = dbHelper.addStudent(newStudent)

                if (newRowId > -1) {
                    students.add(newStudent)
                    students.sortBy { it.studentName } // Sắp xếp lại
                    val newPosition = students.indexOf(newStudent)
                    studentAdapter.notifyItemInserted(newPosition)
                    recyclerView.scrollToPosition(newPosition)
                    Toast.makeText(this@MainActivity, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Lỗi: MSSV có thể đã tồn tại.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        navController.navigate(R.id.addStudentFragment)
    }

    // --- Implement các phương thức từ StudentAdapter.StudentAdapterListener ---

    /**
     * Được gọi khi người dùng nhấn Sửa từ item hoặc context menu.
     */
    override fun onStudentEditClicked(position: Int, student: Student) {
        // Gán listener là MainActivity, không cần tạo object mới
        FragmentSingleton.getInstance().studentActionListener = this
        FragmentSingleton.getInstance().student = student
        FragmentSingleton.getInstance().position = position
        navController.navigate(R.id.editStudentFragment)
    }

    /**
     * Được gọi khi người dùng nhấn Xóa từ context menu.
     */
    fun onStudentDelete(position: Int, student: Student) {
        // Hiển thị dialog xác nhận trước khi xóa
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sinh viên ${student.studentName}?")
            .setPositiveButton("Xóa") { _, _ ->
                handleStudentDelete(position, student)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // --- Logic xử lý CSDL và cập nhật UI ---

    /**
     * Hàm trung tâm để xử lý logic cập nhật sinh viên.
     */
    override fun onStudentUpdated(position: Int, updatedStudent: Student) {
        val count = dbHelper.updateStudent(updatedStudent)
        if (count > 0) {
            students[position] = updatedStudent
            studentAdapter.notifyItemChanged(position)
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStudentDeleteClicked(position: Int, student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sinh viên ${student.studentName}?")
            .setPositiveButton("Xóa") { _, _ ->
                handleStudentDelete(position, student)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    /**
     * Hàm trung tâm để xử lý logic xóa sinh viên.
     * Có thể được gọi từ nhiều nơi (vuốt, dialog).
     */
    private fun handleStudentDelete(position: Int, student: Student) {
        val count = dbHelper.deleteStudent(student.studentId)
        if (count > 0) {
            students.removeAt(position)
            studentAdapter.notifyItemRemoved(position)
            Toast.makeText(this, "Đã xóa sinh viên: ${student.studentName}", Toast.LENGTH_SHORT).show()
        } else {
            studentAdapter.notifyItemChanged(position)
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                handleStudentDelete(position, students[position])
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    // --- Các hàm thiết lập Menu và Navigation ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_add_student) {
            showAddStudentFragment()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }

    private fun setupNavDrawer() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.list_student_nav -> navController.navigate(R.id.listStudentFragment)
                R.id.add_student_nav -> showAddStudentFragment()
            }
            true
        }
    }
}
