package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap


/**
 * A public interface that exposes the [AsteroidsApiService] methods
 */
interface AsteroidsApiService {


    @GET("neo/rest/v1/feed?api_key=$API_KEY")
    fun getAsteroidsDataAsync(
        @QueryMap queryParameters: Map<String,String>,
    ): Deferred<String>

    @GET("planetary/apod?api_key=$API_KEY")
    fun getImageOfTheDayAsync(): Deferred<PictureOfDay?>
}