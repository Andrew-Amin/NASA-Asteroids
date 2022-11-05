package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidsApiFilter
import com.udacity.asteroidradar.database.getAsteroidRadarDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class AsteroidsApiStatus {
    LOADING, LOADED, FAILED
}

class MainViewModel(app: Application) : AndroidViewModel(app) {
    /**----------- Initializations -----------------------*/
    private val database = getAsteroidRadarDatabase(app.applicationContext)
    private val repo = AsteroidsRepo(database)

    /**----------- AsteroidsApiFilter LiveData -----------------------*/
    private val _searchFilter = MutableLiveData<AsteroidsApiFilter>()

    /**----------- AsteroidsListLiveData -----------------------*/
    val asteroidsList : LiveData<List<Asteroid>> = Transformations.switchMap(_searchFilter){
        when(it){
            AsteroidsApiFilter.WEEK_ASTEROIDS -> repo.weekAsteroids
            AsteroidsApiFilter.TODAY_ASTEROIDS -> repo.todayAsteroids
            else -> repo.allAsteroids
        }
    }

    /**----------- AsteroidsApiStatusLiveData -----------------------*/
    private val _asteroidsApiStatus = MutableLiveData<AsteroidsApiStatus>()
    val asteroidsApiStatus: LiveData<AsteroidsApiStatus> get() = _asteroidsApiStatus

    /**----------- PictureOfTheDayLiveData -----------------------*/
    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay> get() = _pictureOfTheDay

    /**----------- DetailNavigation -----------------------*/
    private val _navigateToDetailScreen = MutableLiveData<Asteroid>()
    val navigateToDetailScreen: LiveData<Asteroid> get() = _navigateToDetailScreen

    /**----------- ViewModel helper Methods -----------------------*/
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToDetailScreen.postValue(asteroid)
    }

    fun navigationToDetailScreenDone() {
        _navigateToDetailScreen.postValue(null)
    }

    fun updateFilters(asteroidsApiFilter: AsteroidsApiFilter) {
        _searchFilter.postValue(asteroidsApiFilter)
    }

    /**----------- ViewModel Private Methods -----------------------*/
    private suspend fun getPictureOfDay() {
        repo.getImageOfTheDay()?.let {
            _pictureOfTheDay.postValue(it)
        }
    }


    private suspend fun refreshAsteroidsData(searchFilter: AsteroidsApiFilter) {
        try {
            _asteroidsApiStatus.postValue(AsteroidsApiStatus.LOADING)
            repo.refreshAsteroidsData(searchFilter)
            _asteroidsApiStatus.postValue(AsteroidsApiStatus.LOADED)
        } catch (e: Exception) {
            _asteroidsApiStatus.postValue(AsteroidsApiStatus.FAILED)
        }
    }

    init {
        _searchFilter.postValue(AsteroidsApiFilter.SAVED_ASTEROIDS)
        viewModelScope.launch(Dispatchers.IO) {
            getPictureOfDay()
            refreshAsteroidsData(AsteroidsApiFilter.WEEK_ASTEROIDS)
        }
    }


    /**----------- Factory for constructing MainViewModel with parameter -----------*/
    class MainViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}