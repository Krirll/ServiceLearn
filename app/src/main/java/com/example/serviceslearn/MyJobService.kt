package com.example.serviceslearn

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyJobService : JobService() {

    private val coroutine = CoroutineScope(Dispatchers.Main)

    //при создании сервиса
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    //процесс выполнения всей работы сервиса
    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartCommand")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutine.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (i in 1 until 5) {
                        delay(1000)
                        log("Timer $i $page")
                    }
                    params?.completeWork(workItem) //этот метод означает что работа конкретного сервиса завершена
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, false)
            }
        }
        //1. true означает что сервис еще выполняется, сами завершим работу когда необходимо
        //асинхронная работа кода
        //2. false для синхронного кода, означает что сервис больше не выполняется
        //сервис сам завершит свою работу, и не нужно этого делать вручную
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onDestroy() {
        log("onDestroy")
        coroutine.cancel()
        super.onDestroy()
    }

    private fun log(message: String) {
        Log.d("MY_JOB_SERVICE", message)
    }

    companion object {

        const val JOB_ID = 1
        private const val PAGE = "page"

        /*fun newBundle(page: Int): PersistableBundle {
            return PersistableBundle().apply {
                putInt(PAGE, page)
            }
        }*/

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }

        }
    }
}