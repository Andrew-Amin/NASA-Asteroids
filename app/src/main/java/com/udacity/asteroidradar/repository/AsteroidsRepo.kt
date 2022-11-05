package com.udacity.asteroidradar.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidsApi
import com.udacity.asteroidradar.api.AsteroidsApiFilter
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.toAsteroidList
import com.udacity.asteroidradar.toAsteroidDatabaseObjects
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepo(private val database: AsteroidRadarDatabase) {

    private val calender = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
    private val now = dateFormatter.format(calender.time)

    /**
     * A asteroids List that can be shown on the screen.
     */
    val allAsteroids = Transformations.map(database.asteroidRadarDao.getAllAsteroids()) {
        it.toAsteroidList()
    }
    val weekAsteroids = Transformations.map(database.asteroidRadarDao.getWeekAsteroidsWithData(now)) {
        it.toAsteroidList()
    }
    val todayAsteroids = Transformations.map(database.asteroidRadarDao.getAsteroidsOfSpecificData(now)) {
        it.toAsteroidList()
    }

    /**
     * Fetch asteroids from internet an store them in the database
     */
    suspend fun refreshAsteroidsData(asteroidSearchFilter: AsteroidsApiFilter) {
        val queryParams = hashMapOf<String, String>()
        queryParams["start_date"] = now
        if (asteroidSearchFilter == AsteroidsApiFilter.TODAY_ASTEROIDS)
            queryParams["end_date"] = now

        if (asteroidSearchFilter != AsteroidsApiFilter.SAVED_ASTEROIDS) {
            val asteroidsResponse = AsteroidsApi.instance.getAsteroidsDataAsync(queryParams).await()
            val asteroidsList = parseAsteroidsJsonResult(JSONObject(asteroidsResponse))
            database.asteroidRadarDao.saveAsteroids(*asteroidsList.toAsteroidDatabaseObjects())
        }
    }

    /**
     * Fetch ImageOfTheDay to be displayed in the home screen
     */
    suspend fun getImageOfTheDay(): PictureOfDay? {
        return AsteroidsApi.instance.getImageOfTheDayAsync().await()
    }

    /**
     * Fetch ImageOfTheDay to be displayed in the home screen
     */

    fun deletePreviousAsteroids() {
        database.asteroidRadarDao.deleteAsteroidsBeforeDate(now)
    }
}