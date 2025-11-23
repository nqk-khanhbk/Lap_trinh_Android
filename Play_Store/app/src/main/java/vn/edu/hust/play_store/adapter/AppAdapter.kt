package vn.edu.hust.play_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.play_store.R
import vn.edu.hust.play_store.model.AppModel

class AppAdapter(private val apps: List<AppModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_VERTICAL = 0
        const val TYPE_HORIZONTAL = 1
    }

    override fun getItemViewType(position: Int): Int {
        return apps[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_VERTICAL) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_vertical, parent, false)
            VerticalViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_horizontal, parent, false)
            HorizontalViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val app = apps[position]

        if (getItemViewType(position) == TYPE_VERTICAL) {
            val vHolder = holder as VerticalViewHolder
            vHolder.tvName.text = app.title
            vHolder.tvRating.text = app.rating
            vHolder.tvSize.text = app.size
            vHolder.imgIcon.setImageResource(app.imageResId)
        } else {
            val hHolder = holder as HorizontalViewHolder
            hHolder.tvName.text = app.title
            hHolder.tvRating.text = "${app.rating} ★"
            hHolder.imgIcon.setImageResource(app.imageResId)
        }
    }

    override fun getItemCount(): Int = apps.size

    // ViewHolder cho kiểu dọc
    class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgAppIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvAppName)
        val tvRating: TextView = itemView.findViewById(R.id.tvAppRating)
        val tvSize: TextView = itemView.findViewById(R.id.tvAppSize)
    }

    // ViewHolder cho kiểu ngang
    class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgAppIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvAppName)
        val tvRating: TextView = itemView.findViewById(R.id.tvAppRating)
    }
}
