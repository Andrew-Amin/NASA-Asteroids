package com.udacity.asteroidradar

import android.os.Parcelable
import com.udacity.asteroidradar.database.AsteroidDatabaseObject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable


/**Convert Array Of Asteroids Database Object to an Array of Asteroid*/
fun ArrayList<Asteroid>.toAsteroidDatabaseObjects(): Array<AsteroidDatabaseObject> {
    return map {
        AsteroidDatabaseObject(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}