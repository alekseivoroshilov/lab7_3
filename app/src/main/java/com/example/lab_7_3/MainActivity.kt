package com.example.lab_7_3

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val MSG_DOWNLOAD = 1
        const val MSG_SUCCESS = 2

        const val picUrl = "https://sun9-54.userapi.com/c846520/v846520339/204212/K7B_mWyOPmM.jpg"
        const val fileName = "lab_7_1"
    }

    private val messenger = Messenger(
        @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_SUCCESS -> updateTextView(msg.obj as String)
                    else -> super.handleMessage(msg)
                }
            }
        }
    )
    private var service: Messenger? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = Messenger(binder)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, PictureDownloader::class.java)

        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        findViewById<Button>(R.id.download_button).setOnClickListener {
            val msg = Message.obtain(null, MSG_DOWNLOAD, picUrl).apply {
                replyTo = messenger
            }
            service?.send(msg)

        }
        findViewById<Button>(R.id.clear_button).setOnClickListener {
            updateTextView("")
        }
    }

    fun updateTextView(message: String) {
        findViewById<TextView>(R.id.msg).text = message
    }

    override fun onDestroy() {
        super.onDestroy()

        service?.let { unbindService(serviceConnection) }
    }

}
