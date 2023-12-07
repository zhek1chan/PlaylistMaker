package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.SearchResultItemBinding
import com.example.playlistmaker.player.domain.Track

class TrackAdapter(
    private val clickListener: TrackClick,
    private val longClickListener: LongDurationPress
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var items: List<Track> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(SearchResultItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(items[position])
            notifyDataSetChanged()
        }
        holder.itemView.setOnLongClickListener {
            longClickListener.onLongClick(items[position])
            notifyDataSetChanged()
            return@setOnLongClickListener true
        }
        getPos(position)


    }

    fun getPos(l: Int): Int {
        return l
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun interface TrackClick {
        fun onClick(track: Track)
    }

    fun interface LongDurationPress {
        fun onLongClick(track: Track)
    }


    fun setItems(items: List<Track>) {
        this.items = items
        notifyDataSetChanged()
    }
}