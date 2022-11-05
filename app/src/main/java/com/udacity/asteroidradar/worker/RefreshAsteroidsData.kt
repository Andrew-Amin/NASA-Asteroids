package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.AsteroidsApiFilter
import com.udacity.asteroidradar.database.getAsteroidRadarDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepo
import java.lang.Exception

class RefreshAsteroidsDataWorker(applicationContext: Context, parameters: WorkerParameters) :
    CoroutineWorker(applicationContext, parameters) {

    companion object{
        const val WORK_NAME = "Refresh_Asteroids_Data_Worker"
    }
    override suspend fun doWork(): Result {
        val database = getAsteroidRadarDatabase(applicationContext)
        val repo = AsteroidsRepo(database)
        return try {
            repo.refreshAsteroidsData(AsteroidsApiFilter.TODAY_ASTEROIDS)
            repo.deletePreviousAsteroids()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}