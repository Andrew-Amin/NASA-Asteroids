package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

private lateinit var DATABASE_INSTANCE: AsteroidRadarDatabase

@Dao
interface AsteroidRadarDao {

    /**get all saved Asteroids from the database.*/
    @Query("select * from asteroid_radar_records order by closeApproachDate")
    fun getAllAsteroids(): LiveData<List<AsteroidDatabaseObject>>

    /**get all saved Asteroids with a specific date from the database.*/
    @Query("select * from asteroid_radar_records where closeApproachDate=:date order by closeApproachDate")
    fun getAsteroidsOfSpecificData(date: String): LiveData<List<AsteroidDatabaseObject>>


    /**get all saved Asteroids with a specific date from the database.*/
    @Query("select * from asteroid_radar_records where distanceFromEarth>:d order by closeApproachDate")
    fun test(d: Double): LiveData<List<AsteroidDatabaseObject>>

    /**get all saved Asteroids grater than a date from the database.*/
    @Query("select * from asteroid_radar_records where closeApproachDate>=:date order by closeApproachDate")
    fun getWeekAsteroidsWithData(date: String): LiveData<List<AsteroidDatabaseObject>>

    /**delete previous Asteroids.*/
    @Query("delete from asteroid_radar_records where closeApproachDate<:date")
    fun deleteAsteroidsBeforeDate(date: String)

    /**Insert Array of Asteroids into database.*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAsteroids(vararg Asteroids: AsteroidDatabaseObject)

}

@Database(entities = [AsteroidDatabaseObject::class], version = 1, exportSchema = false)
abstract class AsteroidRadarDatabase : RoomDatabase() {
    abstract val asteroidRadarDao: AsteroidRadarDao
}

fun getAsteroidRadarDatabase(context: Context): AsteroidRadarDatabase {
    /**
     * synchronized(lock)
     * Ensures we only initialize it once by using synchronized.
     * Only one thread may enter a synchronized block at a time.
     * */
    synchronized(AsteroidRadarDatabase::class.java) {
        if (!::DATABASE_INSTANCE.isInitialized) {
            DATABASE_INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidRadarDatabase::class.java,
                "AsteroidRadarDatabase"
            )
                /**Wipes and rebuilds instead of migrating if no Migration object.*/
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return DATABASE_INSTANCE
}