package vn.edu.hust.play_store

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.play_store.adapter.CategoryAdapter
import vn.edu.hust.play_store.model.AppModel
import vn.edu.hust.play_store.model.CategoryModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvMain: RecyclerView = findViewById(R.id.rvMain)

        // 1. Tạo dữ liệu giả
        // List DỌC (Type = 0) - Cho phần "Suggested for you"
        // Cấu trúc: Tên, Rating, Size, Icon, Type
        val listSuggested = listOf(
            AppModel("Mech Assemble: Zombie", "4.8", "624 MB", R.drawable.anh1, 0),
            AppModel("MU: Hồng Hoả Đạo", "4.8", "339 MB", R.drawable.anh2, 0),
            AppModel("War Inc: Rising", "4.9", "231 MB", R.drawable.anh3, 0),
            AppModel("Gunbound Mobile", "4.5", "120 MB", R.drawable.anh4, 0)
        )

        // List NGANG (Type = 1) - Cho phần "Recommended for you"
        val listRecommended = listOf(
            AppModel("Suno - AI Music", "4.9", "50 MB", R.drawable.anh1, 1),
            AppModel("Claude by AI", "4.8", "32 MB", R.drawable.anh2, 1),
            AppModel("DramaBox Movies", "4.7", "88 MB", R.drawable.anh1, 1),
            AppModel("Chat GPT", "4.6", "45 MB", R.drawable.anh2, 1),
            AppModel("Tiktok", "4.5", "100 MB", R.drawable.anh4, 1)
        )

        // List các Category (Nhóm)
        val categories = listOf(
            CategoryModel("Suggested for you", listSuggested),
            CategoryModel("Recommended for you", listRecommended),
            CategoryModel("New & Updated", listSuggested), // Tái sử dụng list dọc
            CategoryModel("Offline Games", listRecommended) // Tái sử dụng list ngang
        )

        // 2. Thiết lập Adapter cho RecyclerView cha (Dọc)
        val categoryAdapter = CategoryAdapter(this, categories)
        rvMain.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMain.adapter = categoryAdapter
    }
}
