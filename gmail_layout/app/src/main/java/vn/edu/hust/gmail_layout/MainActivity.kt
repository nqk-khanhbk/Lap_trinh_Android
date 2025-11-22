package vn.edu.hust.gmail_layout

import android.graphics.Color
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import vn.edu.hust.gmail_layout.adapter.GmailAdapter
import vn.edu.hust.gmail_layout.modle.Gmail

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R.id.listView)

        // Tạo dữ liệu giả
        val gmails = listOf(
            Gmail("Edurila.com", "12:34 PM", "$19 Only (First 10 spots)", "Are you looking to Learn Web Designing...", Color.parseColor("#F44336")),
            Gmail("Chris Abad", "11:22 AM", "Help make Campaign Monitor better", "Let us know your thoughts! No Images...", Color.parseColor("#E91E63")),
            Gmail("Tuto.com", "11:04 AM", "8h de formation gratuite", "Photoshop, SEO, Blender, CSS, WordPress...", Color.parseColor("#9C27B0")),
            Gmail("Support", "10:26 AM", "Société Ovh : suivi de vos services", "SAS OVH - http://www.ovh.com 2 rue K...", Color.parseColor("#673AB7")),
            Gmail("Matt from Ionic", "10:00 AM", "The New Ionic Creator Is Here!", "Announcing the all-new Creator, build...", Color.parseColor("#3F51B5")),
            Gmail("Google Cloud", "09:15 AM", "Google Cloud Platform", "Welcome to Google Cloud Platform...", Color.parseColor("#2196F3")),
            Gmail("LinkedIn", "08:30 AM", "You have a new connection", "John Doe wants to connect with you...", Color.parseColor("#03A9F4"))
        )

        // Gán Adapter
        val adapter = GmailAdapter(gmails)
        listView.adapter = adapter
    }
}
