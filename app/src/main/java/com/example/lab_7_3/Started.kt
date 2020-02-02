package com.example.lab_7_3

import android.app.DownloadManager
import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import java.io.File


class Started : Service() {
    private val broadcastActionKey = "com.example.lab_7_1.PIC_DOWNLOAD"
    private val broadcastMessageKey = "broadcastMessageKey"
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            coroutineScope.launch(Dispatchers.IO) {
                handleWork(intent)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun handleWork(intent: Intent) {
        val url = intent!!.getStringExtra(Intent.EXTRA_TEXT)
            var path = ""
            val string = download(url)
            val uri = Uri.parse(intent!!.getStringExtra(Intent.EXTRA_TEXT))
            val fileName = intent.getStringExtra("URL_PARAM")
            val cw = ContextWrapper(applicationContext)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            path = file.toString()


            if (path != "") {
                Intent().also { intent ->
                    intent.action = "com.example.lab7_start_service_app.IMAGE_DOWNLOAD_COMPLETE"
                    intent.putExtra("PATH_TO_IMAGE", path)
                    sendBroadcast(intent)
                }
            }
    }
    stopSelf()
    }

    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): {*/
    /*val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(intent!!.getStringExtra(Intent.EXTRA_TEXT))
        val fileName = intent.getStringExtra("URL_PARAM")
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val request = DownloadManager.Request(uri).apply {
            setTitle("StartedService download")
            setDescription("Downloading")
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setDestinationUri(Uri.fromFile(file))

        }
        downloadManager.enqueue(request)

        val responseIntent = Intent(broadcastActionKey).apply {
            action = broadcastActionKey
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            putExtra(broadcastMessageKey, file.absolutePath)
        }
        sendBroadcast(responseIntent)

    }
}
*/
    private fun download(url: String): String {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), MainActivity.fileName)
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Скаченное фото")
            setDescription("Downloading")
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setDestinationUri(Uri.fromFile(file))

        }
        downloadManager.enqueue(request)

        return file.absolutePath
    }
    private val parentJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
}
