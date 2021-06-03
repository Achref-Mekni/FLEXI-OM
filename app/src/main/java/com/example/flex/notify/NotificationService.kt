package com.example.flex.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler

import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.flex.LoginActivity
import com.example.flex.R
import com.example.flex.nav_menu_fragments.TasksFragment
import java.util.*

class NotificationService : Service() {
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"
    var Your_X_SECS = 5
    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler: Handler = Handler()
    private fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        //timer.schedule(timerTask, 5000, Your_X_SECS * 1000) //
        timer!!.schedule(timerTask, 5000) //
    }

    private fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(Runnable { //TODO CALL NOTIFICATION FUNC
                    createNotification()
                })
            }
        }
    }
    private fun createNotification()
    {
        var builder = NotificationCompat.Builder(this, "alert-task")
            .setSmallIcon(R.drawable.ic_tasks_rip)
            .setContentTitle("Tasks Alert")
            .setContentText("you have "+LoginActivity.counter_TODO +" Tasks to do in the next 2 days & " +LoginActivity.counter_MISSED+" Overdue Tasks")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("you have "+LoginActivity.counter_TODO +" Tasks to do in the next 2 days & " +LoginActivity.counter_MISSED+" Overdue Tasks"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

}