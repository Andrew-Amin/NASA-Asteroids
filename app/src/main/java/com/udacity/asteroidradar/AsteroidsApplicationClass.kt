package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.worker.RefreshAsteroidsDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidsApplicationClass : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    //to separate the initialization from the main thread
    private fun delayedInitialization() {
        applicationScope.launch {
            setupFetchAsteroidsWorker()
        }
    }

    private fun setupFetchAsteroidsWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) //connected to a wifi
            .setRequiresBatteryNotLow(true) // battery is not low
            .setRequiresCharging(true) // the device is charging
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true) // check if the device is idle for the worker request to run
                }
            }.build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshAsteroidsDataWorker>(
                1,
                TimeUnit.DAYS
            ) // do it one time a day
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshAsteroidsDataWorker.WORK_NAME, // unique worker name
            ExistingPeriodicWorkPolicy.KEEP, // does not replace the running worker
            repeatingRequest
        )
    }

    override fun onCreate() {
        super.onCreate()
        delayedInitialization()
    }
}