package com.example.playlistmaker.player.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.ui.PlaylistsViewHolder

class BottomPlaylistsAdapter(private val playlists: List<Playlist>) :
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
    }
}
