package com.example.serviceslearn

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService: Service() {

    //при создании сервиса
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    //процесс выполнения всей работы сервиса
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        val start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        CoroutineScope(Dispatchers.Main).launch {
            for (i in start until start + 5) {
                delay(1000)
                log("Timer $i")
            }
            stopSelf()
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(message: String) {
        Log.d("MY_DEFAULT_SERVICE", message)
    }

    companion object {

        private const val EXTRA_START = "start"

        fun newIntent(context: Context, start: Int): Intent =
            Intent(context, MyService::class.java).apply {
                putExtra(EXTRA_START, start)
            }
    }
}