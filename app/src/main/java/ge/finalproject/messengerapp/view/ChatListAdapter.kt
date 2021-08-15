package ge.finalproject.messengerapp.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.storage.FirebaseStorage
import ge.finalproject.messengerapp.R
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*


class ChatListAdapter (
    private val values: ArrayList<ChatHeader>,
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var numLoaded = 0

    lateinit var chatListener: OnItemClickListener;

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        chatListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_header_entry, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = values[position]

        if (position == numLoaded) {
            holder.isLoading.visibility=View.VISIBLE
            if (entry.profilePic == "") {
                holder.profilePic.setImageDrawable(context.resources.getDrawable(R.drawable.avatar_image_placeholder))
                setValues(holder, entry)
            } else {
                val storageRef = FirebaseStorage.getInstance().reference
                val pictRef = storageRef.child(entry.profilePic)
                pictRef.downloadUrl.addOnSuccessListener {
                    GlideApp.with(context)
                        .load(it)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .circleCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                holder.isLoading.visibility = View.GONE
                                Toast.makeText(context, "Connection failedd", Toast.LENGTH_SHORT)
                                    .show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                setValues(holder, entry)
                                return false
                            }
                        })
                        .into(holder.profilePic)
                }
            }
        }
//        holder.lastMessage.text = entry.lastMessage
    }

    override fun getItemCount(): Int = (numLoaded + 1).coerceAtMost(values.size)

    private fun setValues(holder: ViewHolder, entry: ChatHeader) {
        if (numLoaded != values.size) {
            (context as Activity).findViewById<RecyclerView>(R.id.chatHeaderList).post {
                notifyItemInserted(numLoaded)
            }
        }
        holder.isLoading.visibility = View.GONE
        holder.contactName.text = entry.nickname
        holder.time.text = getTime(entry.lastMessageTime)
        holder.lastMessage.text = entry.lastMessage
        numLoaded += 1

    }

    private fun getTime(time: Long): String {
        var diff = (System.currentTimeMillis() - time) / 1000
        if (diff < 3600) {
            return "${diff / 60} min"
        }
        diff /= 3600
        if (diff < 24) {
            return "$diff hour"
        }

        val formatter = SimpleDateFormat("dd MMM", Locale.US)
        return formatter.format(Date(time))
    }

    fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader) {
        var i = 0
        while (i < values.size) {
            if (values[i].chatId == chatId) {
                values.remove(values[i])
                values.add(0, chatHeader)
                (context as Activity).findViewById<RecyclerView>(R.id.chatHeaderList).post {
                    notifyItemInserted(0)
                    notifyItemRemoved(i)
                }
                break
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.timestamp)
        val contactName: TextView = view.findViewById(R.id.contactName)
        val isLoading: CircularProgressIndicator = view.findViewById(R.id.isLoading)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val profilePic: ImageView = view.findViewById(R.id.profilePic)

        init {
            itemView.setOnClickListener {
                if (chatListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        chatListener.onClick(adapterPosition)
                    }
                }
            }
        }
    }
}