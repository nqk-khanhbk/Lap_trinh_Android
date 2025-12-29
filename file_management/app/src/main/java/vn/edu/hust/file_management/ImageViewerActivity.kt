package vn.edu.hust.file_management

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ImageViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val path = intent.getStringExtra("path") ?: return
        val imageView = findViewById<ImageView>(R.id.imageView)
        val bitmap = BitmapFactory.decodeFile(File(path).absolutePath)
        imageView.setImageBitmap(bitmap)
    }
}