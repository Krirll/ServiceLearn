package com.example.serviceslearn

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log

class MyIntentService2 : IntentService(NAME) {

    //при создании сервиса
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent")
        val page = intent?.getIntExtra(PAGE, 0) ?: 0
        for (i in 1 until 5) {
            Thread.sleep(1000)
            log("Timer $i $page")
        }
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    private fun log(message: String) {
        Log.d("MY_INTENT_SERVICE_2", message)
    }

    companion object {

        private const val PAGE = "page"
        private const val NAME = "MyIntentService"

        fun newIntent(context: Context, page: Int): Intent =
            Intent(context, MyIntentService2::class.java).apply {
                putExtra(PAGE, page)
            }
    }
}