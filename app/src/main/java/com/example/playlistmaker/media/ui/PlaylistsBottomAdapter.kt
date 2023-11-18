package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.Playlist

class PlaylistsBottomAdapter(
    private val playlists: List<Playlist>,
    private val clickListener: PlaylistClick
) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adding_to_playlist_item, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(playlists[position])
            notifyDataSetChanged()
        }
    }
    fun interface PlaylistClick {
        fun onClick(playlist: Playlist)
    }
}