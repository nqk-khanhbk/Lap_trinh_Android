package vn.edu.hust.play_store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.play_store.R
import vn.edu.hust.play_store.model.CategoryModel

class CategoryAdapter(
    private val context: Context,
    private val categories: List<CategoryModel>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvCategoryTitle)
        val rvApps: RecyclerView = itemView.findViewById(R.id.rvApps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvTitle.text = category.title

        // QUAN TRỌNG: Kiểm tra loại app để set LayoutManager cho đúng
        val isVerticalList = category.apps.isNotEmpty() && category.apps[0].type == 0

        val appAdapter = AppAdapter(category.apps)

        if (isVerticalList) {
            // Nếu là list dọc (Suggested) -> Hiển thị dọc
            holder.rvApps.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else {
            // Nếu là list ngang (Recommended) -> Hiển thị ngang
            holder.rvApps.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        holder.rvApps.adapter = appAdapter
    }

    override fun getItemCount(): Int = categories.size
}
