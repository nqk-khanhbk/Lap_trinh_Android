package vn.edu.hust.gmail_layout.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import vn.edu.hust.gmail_layout.R
import vn.edu.hust.gmail_layout.modle.Gmail

class GmailAdapter(private val listGmail: List<Gmail>) : BaseAdapter() {

    override fun getCount(): Int = listGmail.size

    override fun getItem(position: Int): Any = listGmail[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.email_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val gmail = listGmail[position]

        // Gán dữ liệu văn bản
        viewHolder.senderName.text = gmail.senderName
        viewHolder.time.text = gmail.time
        viewHolder.subject.text = gmail.subject
        viewHolder.description.text = gmail.description

        // Xử lý Avatar: Hiển thị chữ cái đầu của Tên người gửi
        viewHolder.avatarImage.setBackgroundColor(gmail.color) // Set màu nền
        viewHolder.avatarImage.setImageResource(0) // Xóa ảnh cũ nếu có

        // Lấy chữ cái đầu tiên để hiển thị (vì XML bạn dùng TextView đè lên ImageView hoặc chỉ dùng 1 trong 2)
        // Ở đây ta dùng mẹo: Ẩn ImageView đi, dùng TextView trong CardView để hiện chữ
        // Tuy nhiên, trong XML của bạn có cả ImageView và TextView chồng lên nhau.

        // Cách đơn giản nhất khớp với XML hiện tại:
        viewHolder.avatarImage.visibility = View.INVISIBLE // Ẩn ảnh thật
        viewHolder.avatarText.visibility = View.VISIBLE
        viewHolder.avatarText.text = gmail.senderName.first().toString().uppercase()

        // Set màu nền cho CardView (thay vì ImageView) để bo tròn màu
        (viewHolder.avatarText.parent as? androidx.cardview.widget.CardView)?.setCardBackgroundColor(gmail.color)

        return view
    }

    private class ViewHolder(view: View) {
        val senderName: TextView = view.findViewById(R.id.senderName)
        val time: TextView = view.findViewById(R.id.time)
        val subject: TextView = view.findViewById(R.id.subject)
        val description: TextView = view.findViewById(R.id.description)
        val avatarImage: ImageView = view.findViewById(R.id.avatar)
        // Bạn cần thêm ID cho TextView chữ cái trong XML nếu chưa có, ví dụ: avatarText
        // Tạm thời tôi find view bằng cách duyệt con của CardView hoặc bạn phải thêm ID vào XML
        // Để đơn giản, hãy thêm android:id="@+id/avatarText" vào TextView trong email_item.xml
        val avatarText: TextView = view.findViewById(R.id.avatarText)
    }
}
