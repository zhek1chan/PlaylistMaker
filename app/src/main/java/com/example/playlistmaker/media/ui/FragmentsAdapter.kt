package com.example.playlistmaker.media.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.ui.fragments.FavouriteTracksFragment
import com.example.playlistmaker.media.ui.fragments.playlists.PlaylistsFragment

class FragmentsAdapter(
    parentFragment: Fragment
) :
    FragmentStateAdapter(parentFragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            FavouriteTracksFragment.newInstance()
        } else {
            PlaylistsFragment.newInstance()
        }
    }

}