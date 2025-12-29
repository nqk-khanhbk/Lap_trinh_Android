package com.example.studentmanagement.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmanagement.R
import com.example.studentmanagement.model.Student

class StudentAdapter(
    private var students: MutableList<Student>,
    private val actionListener: StudentAdapterListener // Listener duy nhất
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    /**
     * Interface thống nhất để MainActivity implement.
     */
    interface StudentAdapterListener {
        fun onStudentEditClicked(position: Int, student: Student) // Khi nhấn Sửa
        fun onStudentDeleteClicked(position: Int, student: Student) // Khi nhấn Xóa
        fun onStudentUpdated(position: Int, updatedStudent: Student) // Khi EditFragment trả kết quả
    }

    // ... (Phần còn lại của Adapter giữ nguyên như phiên bản đúng trước đó) ...
    // (ViewHolder, onCreateViewHolder, onBindViewHolder, getItemCount)
    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.text_student_name)
        val studentId: TextView = itemView.findViewById(R.id.text_student_id)

        init {
            // Sự kiện click vào item để sửa
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    actionListener.onStudentEditClicked(position, students[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_student_item, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val currentStudent = students[position]
        holder.studentName.text = currentStudent.studentName
        holder.studentId.text = currentStudent.studentId

        // Thiết lập context menu khi nhấn giữ
        holder.itemView.setOnCreateContextMenuListener { menu, view, _ ->
            val activity = view.context as AppCompatActivity
            activity.menuInflater.inflate(R.menu.context_menu_student, menu)

            menu.findItem(R.id.menu_edit)?.setOnMenuItemClickListener {
                actionListener.onStudentEditClicked(position, currentStudent)
                true
            }
            menu.findItem(R.id.menu_delete)?.setOnMenuItemClickListener {
                actionListener.onStudentDeleteClicked(position, currentStudent)
                true
            }
        }
    }

    override fun getItemCount(): Int = students.size
}
