package vn.edu.hust.gmail_layout.modle

data class Gmail(
    val senderName: String,
    val time: String,
    val subject: String,
    val description: String,
    val color: Int // Màu nền cho Avatar
)
