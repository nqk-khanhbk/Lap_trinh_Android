package vn.edu.hust.file_management

import android.app.AlertDialog
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(
    private val click: (File) -> Unit,
    private val rename: (File, String) -> Unit,
    private val delete: (File) -> Unit,
    private val copy: (File) -> Unit // THÊM: Callback cho hành động sao chép
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private var files = listOf<File>()

    fun submitList(list: List<File>) {
        files = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val name: TextView = view.findViewById(R.id.fileName)
        lateinit var file: File

        init {
            view.setOnClickListener { click(file) }
            view.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu.setHeaderTitle("Tùy chọn")
            menu.add(Menu.NONE, 1, 1, "Đổi tên").setOnMenuItemClickListener {
                renameFile()
                true
            }
            menu.add(Menu.NONE, 2, 2, "Xóa").setOnMenuItemClickListener {
                confirmDelete()
                true
            }
            // THÊM: Chỉ hiển thị menu Sao chép cho file
            if (file.isFile) {
                menu.add(Menu.NONE, 3, 3, "Sao chép").setOnMenuItemClickListener {
                    // Gọi lambda để MainActivity xử lý
                    copy(file)
                    true
                }
            }
        }

        // Các hàm renameFile() và confirmDelete() giữ nguyên như cũ
        private fun renameFile() {
            val input = EditText(itemView.context)
            input.setText(file.name)
            AlertDialog.Builder(itemView.context)
                .setTitle("Đổi tên")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val newName = input.text.toString()
                    if (newName.isNotEmpty() && newName != file.name) {
                        rename(file, newName)
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        private fun confirmDelete() {
            AlertDialog.Builder(itemView.context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa \"${file.name}\"?")
                .setPositiveButton("Xóa") { _, _ ->
                    delete(file)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.file = files[position]
        holder.name.text = files[position].name
    }

    override fun getItemCount() = files.size
}
