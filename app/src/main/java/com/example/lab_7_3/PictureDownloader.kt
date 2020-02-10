package com.example.lab_7_3

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import java.io.File
import android.provider.MediaStore
import android.provider.MediaStore.Images

class PictureDownloader : Service() {

    private val messenger = Messenger(
        @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MainActivity.MSG_DOWNLOAD -> {
                        val path = download(msg.obj as String)
                        val sender = msg.replyTo
                        sender.send(Message.obtain(null, MainActivity.MSG_SUCCESS, path))
                    }
                    else -> super.handleMessage(msg)
                }
            }
        }
    )



    private fun download(url: String): String {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), MainActivity.fileName)
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("(Bound) Скаченное фото")
            setDescription("Downloading")
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setDestinationUri(Uri.fromFile(file))

        }
        downloadManager.enqueue(request)

        return file.absolutePath
    }


    override fun onBind(intent: Intent?): IBinder? = messenger.binder

}