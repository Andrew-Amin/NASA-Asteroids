package com.udacity.asteroidradar.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidItemLayoutBinding

class AsteroidsRecyclerAdapter(private val onClickAsteroid: OnClickAsteroid) :
    RecyclerView.Adapter<AsteroidsRecyclerAdapter.AsteroidViewHolder>() {

    class AsteroidViewHolder(var binding: AsteroidItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * The asteroids that our Adapter will show
     */
    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(
            AsteroidItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = asteroids.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = asteroids[position]
        holder.itemView.setOnClickListener {
            onClickAsteroid.onClick(item)
        }
        holder.bind(item)
    }

    /**
     * Click listener for Videos. By giving the block a name it helps a reader understand what it does.
     * We use this class to pass the click listener from the view to the viewHolder
     */

    class OnClickAsteroid(val asteroidBlock: (Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = asteroidBlock(asteroid)
    }
}

