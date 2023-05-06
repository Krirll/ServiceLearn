package com.example.serviceslearn

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.serviceslearn.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = (service as? MyForegroundService.LocalBinder) ?: return
            val foregroundService = binder.getService()
            foregroundService.onProgressChanged = { progress ->
                binding.progressActivity.progress = progress
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.defaultService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 25))
        }

        //начиная с API 26 нужно уведомлять пользователя о том, что сервис работает в фоне
        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newIntent(this)
            )
            //showNotification()
            //Активити может подписаться на сервис
            //и в момент подписки мы получим экземпляр IBinder (см. метод onBind MyForegroundService)
        }

        binding.intentService.setOnClickListener {
            startService(MyIntentService.newIntent(this))
        }

        binding.jobScheduler.setOnClickListener {
            //указываем какой сервис используем
            val componentName = ComponentName(this, MyJobService::class.java)
            //устанавливаем все необходимые ограничения
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                //устанавливаем флаг, для того чтобы сервис выполнялся только во время зарядки
                .setRequiresCharging(true)
                //флаг на то, что сервис должен выполняться только с включенным wifi
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                //.setExtras(MyJobService.newBundle(page++))
                .build()
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler./*schedule останавливает работу предыдущего сервиса*/
                        enqueue(jobInfo, JobWorkItem(intent))
            } //enqueue позволяет складываеть вызову сервисов в очередь
            else {
                startService(MyIntentService2.newIntent(this, page++))
            }

        }

        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, page++)
        }

        binding.workManager.setOnClickListener {
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.enqueueUniqueWork(
                MyWorker.WORK_NAME,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(page++)
            )
        }

        binding.alarmManager.setOnClickListener {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND, 15)
            val intent = AlarmReceiver.newIntent(this)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onStart() {
        super.onStart()
        bindService(
            MyForegroundService.newIntent(this),
            serviceConnection,
            0
        )
    }

    /*private fun showNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        notificationManager.notify(1, notification)
    }*/

    companion object {

        //private const val CHANNEL_ID = "channel_id"
        //private const val CHANNEL_NAME = "channel_name"
    }
}