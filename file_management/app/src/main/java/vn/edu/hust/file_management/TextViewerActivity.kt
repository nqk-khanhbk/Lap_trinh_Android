package vn.edu.hust.file_management
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class TextViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        val path = intent.getStringExtra("path") ?: return
        val textView = findViewById<TextView>(R.id.textContent)
        textView.text = File(path).readText()
    }
}
