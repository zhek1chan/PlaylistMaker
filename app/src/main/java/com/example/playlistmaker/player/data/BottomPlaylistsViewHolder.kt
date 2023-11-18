package com.example.playlistmaker.player.data

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.Playlist

class BottomPlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val pic: ImageView = itemView.findViewById(R.id.emptyList)
    private val name: TextView = itemView.findViewById(R.id.name)
    private val num: TextView = itemView.findViewById(R.id.num)

    fun bind(albums: Playlist) {
        val cornerPixelSize =
            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
        Glide.with(itemView)
            .load(Uri.parse(albums.artworkUrl100))
            .placeholder(R.drawable.song_cover)
            .centerCrop()
            .transform(RoundedCorners(cornerPixelSize))
            .into(pic)
        name.text = albums.name
        num.text = albums.num.toString()
    }
}
