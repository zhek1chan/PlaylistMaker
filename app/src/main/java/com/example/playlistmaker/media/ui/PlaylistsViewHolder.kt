package com.example.playlistmaker.media.ui

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.Playlist

class PlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val pic: ImageView = itemView.findViewById(R.id.emptyList)
    private val name: TextView = itemView.findViewById(R.id.name)
    private val num: TextView = itemView.findViewById(R.id.num)

    fun bind(albums: Playlist) {
        if (Uri.parse(albums.artworkUrl100).toString().isEmpty()) {
            pic.setImageDrawable(itemView.resources.getDrawable(R.drawable.placeholder))
        } else {
            val cornerPixelSize =
                itemView.resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius)
            Glide.with(itemView)
                .load(Uri.parse(albums.artworkUrl100))
                .transform(CenterCrop(), RoundedCorners(cornerPixelSize), CenterCrop())
                .into(pic)
        }
        name.text = albums.name

        if (albums.num.toInt() == 2) {
            num.text = albums.num.toString() + " " + "трека"
        } else
            if (albums.num.toInt() == 3) {
                num.text = albums.num.toString() + " " + "трека"
            } else
                if (albums.num.toInt() == 4) {
                    num.text = albums.num.toString() + " " + "трека"
                } else {
                    num.text = albums.num.toString() + " " + itemView.resources.getQuantityString(
                        R.plurals.numberOfSongsAvailable,
                        albums.num.toInt()
                    )
                }
    }
}