package vn.edu.hust.student_manage

import java.io.Serializable

data class StudentModel(
    var mssv: String,
    var name: String,
    var phone: String,
    var address: String
) : Serializable
