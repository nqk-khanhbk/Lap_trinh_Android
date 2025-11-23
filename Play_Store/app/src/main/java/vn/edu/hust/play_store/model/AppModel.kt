package vn.edu.hust.play_store.model

data class AppModel(
    val title: String,
    val rating: String,
    val size: String,
    val imageResId: Int,
    val type: Int = 0
)
