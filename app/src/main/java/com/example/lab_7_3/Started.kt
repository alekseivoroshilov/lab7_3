package com.example.lab_7_3

import android.app.DownloadManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File


class StartedService : IntentService("StartedService") {
    private val broadcastActionKey = "com.example.lab_7_1.PIC_DOWNLOAD"
    private val broadcastMessageKey = "broadcastMessageKey"



    override fun onHandleIntent(intent: Intent?) {
        val url = intent!!.getStringExtra(Intent.EXTRA_TEXT)
        val fileName = intent.getStringExtra("URL_PARAM")
        print(url + "\n" + fileName)
        val path = download(url, fileName)

        val responseIntent = Intent(broadcastActionKey).apply {
            action = broadcastActionKey
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            putExtra(broadcastMessageKey, path)
            Log.i("Started, response", "broadcast sent")
        }

        sendBroadcast(responseIntent)
        Log.i("Started, stopping", "service stopped")
        stopSelf()
    }



    private fun download(url: String, fileName: String): String {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("(Started) Скаченное фото")
            setDescription("Downloading")
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setDestinationUri(Uri.fromFile(file))

        }
        downloadManager.enqueue(request)
        Log.i("Downloaded", "Sent the path")
        return file.absolutePath
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Started service", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Started service", "onDestroy")
    }
}
