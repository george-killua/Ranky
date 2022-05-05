package com.killua.ranky.features.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.killua.data.models.Club
import com.killua.ranky.R
import com.killua.ranky.databinding.RvClubItemBinding


class ClubsAdapter(private val onClick: (Club) -> Unit) :
    ListAdapter<Club, ClubsAdapter.ClubViewHolder>(ClubDiffCallback) {

    class ClubViewHolder(binding: RvClubItemBinding, val onClick: (Club) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        private val ivClubImageView = binding.ivItemClubimage
        private val tvClubName: TextView = binding.tvItemClubname
        private val tvClubCountry: TextView = binding.tvItemClubcountry
        private val tvClubValue: TextView = binding.tvItemClubvalue
        private var currentClub: Club? = null

        init {
            itemView.setOnClickListener {
                currentClub?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(club: Club) {
            currentClub = club
            Glide.with(ivClubImageView.context)
                .load(club.image)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivClubImageView)
            tvClubName.text = club.name
            tvClubCountry.text = club.country
            tvClubValue.text = itemView.context.getString(R.string.millions, club.value)

        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = RvClubItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ClubViewHolder(view, onClick)
    }

    /* Gets current club and uses it to bind view. */
    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = getItem(position)
        holder.bind(club)

    }
}

object ClubDiffCallback : DiffUtil.ItemCallback<Club>() {
    override fun areItemsTheSame(oldItem: Club, newItem: Club): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Club, newItem: Club): Boolean {
        return oldItem.id == newItem.id
    }
}