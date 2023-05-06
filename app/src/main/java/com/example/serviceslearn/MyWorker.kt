package com.example.serviceslearn

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class MyWorker(
    context: Context,
    private val workerParameters: WorkerParameters
): Worker(context, workerParameters) {

    override fun doWork(): Result {
        log("doWork")
        val page = workerParameters.inputData.getInt(PAGE, 0)
        for (i in 1 until 5) {
            Thread.sleep(1000)
            log("Timer $i $page")
        }
        return Result.success()
    }

    private fun log(message: String) {
        Log.d("MY_WORKER", message)
    }

    override fun onStopped() {
        log("onStopped")
        super.onStopped()
    }

    companion object {

        private const val PAGE = "page"
        const val WORK_NAME = "work name"

        fun makeRequest(page: Int): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<MyWorker>().apply {
                setInputData(workDataOf(PAGE to page))
                setConstraints(makeConstrains())
            }.build()

        private fun makeConstrains(): Constraints =
            Constraints.Builder()
                //.setRequiresCharging(true)
                //.setRequiredNetworkType(NetworkType.TEMPORARILY_UNMETERED)
                .build()
    }
}