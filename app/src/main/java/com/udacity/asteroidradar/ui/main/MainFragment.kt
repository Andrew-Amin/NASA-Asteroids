package com.udacity.asteroidradar.ui.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidsApiFilter
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModel.MainViewModelFactory(activity.application)).get(
            MainViewModel::class.java
        )
    }

    /**
     * RecyclerView Adapter for converting a list of Asteroids to cards.
     */
    private var asteroidsRecyclerAdapter: AsteroidsRecyclerAdapter? = null

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroidsList.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroids?.apply {
                asteroidsRecyclerAdapter?.asteroids = asteroids
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
        asteroidsRecyclerAdapter =
            AsteroidsRecyclerAdapter(AsteroidsRecyclerAdapter.OnClickAsteroid {
                viewModel.displayAsteroidDetails(it)
            })
        binding.asteroidRecycler.adapter = asteroidsRecyclerAdapter


        setListeners(binding)
        return binding.root
    }

    private fun setListeners(binding: FragmentMainBinding) {
        viewModel.asteroidsApiStatus.observe(viewLifecycleOwner, Observer<AsteroidsApiStatus> {
            if (it == AsteroidsApiStatus.FAILED)
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
        })

        viewModel.navigateToDetailScreen.observe(viewLifecycleOwner, Observer<Asteroid> {
            it?.let {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.navigationToDetailScreenDone()
            }
        })

        viewModel.pictureOfTheDay.observe(
            viewLifecycleOwner,
            Observer<PictureOfDay> { pictureOfDay ->
                pictureOfDay?.let {
                    if (pictureOfDay.mediaType == "image") {
                        Picasso.with(context)
                            .load(pictureOfDay.url)
                            .placeholder(R.drawable.placeholder_picture_of_day)
                            .error(R.drawable.placeholder_picture_of_day)
                            .into(binding.activityMainImageOfTheDay)
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilters(
            when (item.itemId) {
                R.id.show_next_week_menu -> AsteroidsApiFilter.WEEK_ASTEROIDS
                R.id.show_today_menu -> AsteroidsApiFilter.TODAY_ASTEROIDS
                else -> AsteroidsApiFilter.SAVED_ASTEROIDS
            }
        )
        return true
    }
}
