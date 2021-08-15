package ge.finalproject.messengerapp.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.storage.FirebaseStorage
import ge.finalproject.messengerapp.R
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader


class UserListAdapter() : RecyclerView.Adapter<UserListAdapter.UserListItemViewHolder>() {
    private lateinit var context: Context
    var numLoaded = 0
    var list = ArrayList<UserHeader?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListItemViewHolder {
        context = parent.context
        var view = LayoutInflater.from(context).inflate(R.layout.user_header_entry, parent, false)
        return UserListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserListItemViewHolder, position: Int) {
        var item = list[position]
        if (position == numLoaded) {
            holder.isLoading.visibility = View.VISIBLE
            if (item?.profilePicture.toString() == "") {
                holder.profilePicture.setImageDrawable(context.resources.getDrawable(R.drawable.avatar_image_placeholder))
                setValues(holder, item)
            } else {

                val filename = item?.profilePicture
                val storageRef = FirebaseStorage.getInstance().getReference("$filename")
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        Glide.with(context)
                            .load(it)
                            .circleCrop()
                            .placeholder(R.drawable.avatar_image_placeholder)
                            .addListener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    holder.isLoading.visibility = View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    setValues(holder, item)
                                    return false
                                }
                            })
                            .into(holder.profilePicture)
                    }

            }
        }
    }

    override fun getItemCount(): Int = (numLoaded + 1).coerceAtMost(list.size)

    private fun setValues(holder: UserListItemViewHolder, item: UserHeader?) {
        if (numLoaded != list.size) {
            (context as Activity).findViewById<RecyclerView>(R.id.userHeaderList).post {
                notifyItemInserted(numLoaded)
            }
        }
        holder.isLoading.visibility = View.GONE
        holder.tvNickname.text = item?.nickname
        holder.tvJob.text = item?.job
        numLoaded += 1
    }
    class UserListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var profilePicture: ImageView = view.findViewById(R.id.profile_picture)
        var tvNickname: TextView = view.findViewById(R.id.nickname_tv)
        var tvJob: TextView = view.findViewById(R.id.job_tv)
        var isLoading: CircularProgressIndicator = view.findViewById(R.id.isLoading)
    }

}
