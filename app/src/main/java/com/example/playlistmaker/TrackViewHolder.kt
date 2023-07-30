package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
    ) {

    private val albumsCover: ImageView = itemView.findViewById(R.id.track_cover)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val bandName: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private var trackID: String = ""
    private var country: String = ""

    fun bind(item: Track) {
        trackName.text = item.trackName
        bandName.text = item.artistName
        trackID = item.trackId
        country = item.country
        val cornerPixelSize =
            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
        trackTime.text = DateUtils.formatTime(item.trackTimeMillis)
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.song_cover)
            .centerCrop()
            .transform(RoundedCorners(cornerPixelSize))
            .into(albumsCover)
    }
}