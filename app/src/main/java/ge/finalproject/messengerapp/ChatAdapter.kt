package ge.finalproject.messengerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context

    private var numLoaded = 0

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var view: View
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_sent_message, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_received_message, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 16

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


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}