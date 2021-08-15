package ge.finalproject.messengerapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ge.finalproject.messengerapp.models.Message
import ge.finalproject.messengerapp.view.IChatView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(val view: IChatView, val chatId: String,val uiThread: View) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private lateinit var context: Context

    private var numLoaded = 0
    private var messagesToLoad = 0
    private var targetLoaded = 0
    private var timeLoaded = System.currentTimeMillis()
    private val values = ArrayList<Message?>().apply {
        add(null)
    }
    private var allLoaded = 0

    override fun getItemViewType(position: Int): Int {
        Log.d("EEE", "$numLoaded $allLoaded")
        if (values[position] == null) {
            return 2
        }
        return if (values[position]!!.sender == FirebaseAuth.getInstance().currentUser!!.uid) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var view: View
        if (viewType == 2) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_loading, parent, false)
            this.view.loadMessages(chatId, timeLoaded, 10, numLoaded)
            messagesToLoad = 10
            targetLoaded = numLoaded + 10
            return ViewHolder(view, true)
        }
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_sent_message, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_received_message, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val truePosition = numLoaded - 1 - position
        if (truePosition == -1) {
            return
        }
        val entry: Message = values[truePosition]!!

        holder.text.text = entry.message
        holder.time.text = getTime(-entry.time)
        if (truePosition > 0 && values[truePosition-1]!!.sender != entry.sender) {
            holder.marginBottom.layoutParams.height = (8 * view.getViewScale()).toInt()
        }

    }

    override fun getItemCount(): Int = numLoaded + 1 - allLoaded

//    @Synchronized
    fun onMessageLoaded(position: Int, message: Message) {
        Log.d("MMM", position.toString() + " " + message)
        while (values.size <= position) values.add(null)
        values[position] = message
        messagesToLoad --
        if (message.sender == "sys") {
            messagesToLoad -= (targetLoaded - position - 1)
            targetLoaded = position
            allLoaded = 1
        }
        if (messagesToLoad == 0) {
            var idx = numLoaded
            while (idx < targetLoaded) {
                uiThread.post {
                    notifyItemInserted(0)
                    numLoaded ++
                }
                idx ++
            }
//            numLoaded -= allLoaded
        }

    }

    fun onMessageAdded(message: Message) {
        values.add(0, message)
        numLoaded ++
        uiThread.post {
            notifyItemInserted(values.size - 1)
        }
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


    inner class ViewHolder(view: View, isLoading: Boolean = false) : RecyclerView.ViewHolder(view) {
        lateinit var time: TextView
        lateinit var text: TextView
        lateinit var marginBottom: Space

        init {
            if (!isLoading) {
                time = view.findViewById(R.id.messageTime)
                text = view.findViewById(R.id.messageText)
                marginBottom = view.findViewById(R.id.bottomMargin)
            }
        }

    }
}