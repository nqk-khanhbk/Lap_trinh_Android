package vn.edu.hust.file_management

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileAdapter
    private var currentDir: File = Environment.getExternalStorageDirectory()
    private var fileToCopy: File? = null // THÊM: Biến lưu trữ file đang chờ được sao chép

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadFiles(currentDir)
            } else {
                Toast.makeText(this, "Quyền truy cập bị từ chối.", Toast.LENGTH_LONG).show()
            }
        }

    // THÊM: Trình khởi chạy để nhận kết quả từ màn hình cài đặt quyền (Android 11+)
    private val manageStorageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadFiles(currentDir)
                } else {
                    Toast.makeText(this, "Quyền quản lý tệp bị từ chối.", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FileAdapter(
            click = { file -> onItemClick(file) },
            rename = { file, newName -> renameFile(file, newName) },
            delete = { file -> deleteFile(file) },
            copy = { file -> copyFile(file) } // THÊM: Khởi tạo callback sao chép
        )
        recyclerView.adapter = adapter

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                Toast.makeText(this, "Cần cấp quyền quản lý tất cả các tệp.", Toast.LENGTH_LONG).show()
                manageStorageResultLauncher.launch(intent)
            } else {
                loadFiles(currentDir)
            }
        } else { // Android 10-
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                loadFiles(currentDir)
            }
        }
    }

    // --- Tích hợp Option Menu ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // THÊM: Hiển thị tùy chọn "Dán" nếu có file đang chờ sao chép
        val pasteItem = menu.findItem(R.id.menu_paste_file)
        if (pasteItem == null && fileToCopy != null) {
            menu.add(Menu.NONE, R.id.menu_paste_file, Menu.NONE, "Dán vào đây")
                .setIcon(android.R.drawable.ic_menu_save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        } else {
            pasteItem?.isVisible = fileToCopy != null
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create_folder -> { createFolder(); true }
            R.id.menu_create_file -> { createTextFile(); true }
            R.id.menu_paste_file -> { pasteFile(); true } // THÊM: Xử lý sự kiện dán
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --- Các hàm logic chính (đã cập nhật và bổ sung) ---

    private fun loadFiles(dir: File) {
        title = dir.path
        val files = dir.listFiles()?.toList()?.sortedBy { it.name } ?: emptyList()
        adapter.submitList(files)
        invalidateOptionsMenu() // THÊM: Gọi để cập nhật menu (hiện/ẩn nút Dán)
        recyclerView.scrollToPosition(0)
    }


    override fun onBackPressed() {
        // KIỂM TRA: Đường dẫn hiện tại có phải là thư mục gốc của bộ nhớ ngoài không
        if (currentDir.absolutePath != Environment.getExternalStorageDirectory().absolutePath) {
            // SỬA LỖI: Tách logic kiểm tra null để tránh lỗi biên dịch
            val parent = currentDir.parentFile
            if (parent != null) {
                currentDir = parent
                loadFiles(currentDir)
            } else {
                // Nếu không có thư mục cha, xử lý như khi đang ở thư mục gốc
                super.onBackPressed()
            }
        } else {
            // Nếu đang ở thư mục gốc, gọi hành động back mặc định của hệ thống (thoát ứng dụng)
            super.onBackPressed()
        }
    }


    // Các hàm còn lại giữ nguyên hoặc đã được cập nhật
    private fun onItemClick(file: File) {
        if (file.isDirectory) {
            currentDir = file
            loadFiles(file)
        } else {
            when (file.extension.lowercase()) {
                "txt" -> {
                    val intent = Intent(this, TextViewerActivity::class.java)
                    intent.putExtra("path", file.absolutePath)
                    startActivity(intent)
                }
                "jpg", "jpeg", "png", "bmp" -> {
                    val intent = Intent(this, ImageViewerActivity::class.java)
                    intent.putExtra("path", file.absolutePath)
                    startActivity(intent)
                }
                else -> Toast.makeText(this, "Không hỗ trợ mở file định dạng .${file.extension}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createFolder() {
        val input = EditText(this).apply { hint = "Tên thư mục" }
        AlertDialog.Builder(this)
            .setTitle("Tạo thư mục mới")
            .setView(input)
            .setPositiveButton("Tạo") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    File(currentDir, name).mkdir()
                    loadFiles(currentDir)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun createTextFile() {
        val input = EditText(this).apply { hint = "Tên file (không cần .txt)" }
        AlertDialog.Builder(this)
            .setTitle("Tạo file văn bản mới")
            .setView(input)
            .setPositiveButton("Tạo") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()){
                    File(currentDir, "$name.txt").writeText("Nội dung ban đầu")
                    loadFiles(currentDir)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun renameFile(file: File, newName: String) {
        val newFile = File(file.parent, newName)
        if (file.renameTo(newFile)) {
            Toast.makeText(this, "Đổi tên thành công", Toast.LENGTH_SHORT).show()
            loadFiles(currentDir)
        } else {
            Toast.makeText(this, "Đổi tên thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFile(file: File) {
        val success = if (file.isDirectory) file.deleteRecursively() else file.delete()
        if (success) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
            loadFiles(currentDir)
        } else {
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    // THÊM: Các hàm xử lý Sao chép và Dán
    private fun copyFile(file: File) {
        fileToCopy = file
        invalidateOptionsMenu() // Cập nhật lại menu để hiển thị nút "Dán"
        Toast.makeText(this, "Đã sao chép: ${file.name}", Toast.LENGTH_SHORT).show()
    }

    private fun pasteFile() {
        fileToCopy?.let { sourceFile ->
            val destFile = File(currentDir, sourceFile.name)
            if (destFile.exists()) {
                Toast.makeText(this, "Tệp đã tồn tại trong thư mục này.", Toast.LENGTH_SHORT).show()
                return
            }
            try {
                sourceFile.copyTo(destFile)
                Toast.makeText(this, "Dán thành công!", Toast.LENGTH_SHORT).show()
                fileToCopy = null // Xóa trạng thái sao chép
                loadFiles(currentDir)
            } catch (e: IOException) {
                Toast.makeText(this, "Lỗi khi dán tệp: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}


