package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.media.data.FavTracksState
import com.example.playlistmaker.media.ui.viewmodel.FavouriteTracksViewModel
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {
    private val favouritesViewModel by viewModel<FavouriteTracksViewModel>()
    private lateinit var binding: FragmentFavouriteTracksBinding
    private var adapter: TrackAdapter? = null
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderPicture: ImageView
    private lateinit var favouriteTracks: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(
            clickListener = {
                onTrackClickDebounce(it)
            },
            longClickListener = {})

        placeholderMessage = binding.placeholderMessage
        placeholderPicture = binding.emptyLibrary
        favouriteTracks = binding.favouriteTracksRV
        favouriteTracks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favouriteTracks.adapter = adapter

        favouritesViewModel.fillData()

        favouritesViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        favouriteTracks.adapter = null
    }

    private fun onTrackClickDebounce(item: Track) {
        favouritesViewModel.addItem(item)
        val bundle = Bundle()
        bundle.putParcelable("track", item)
        val navController = findNavController()
        navController.navigate(R.id.Fragment_fav_to_playerFragment, bundle)
    }

    private fun render(state: FavTracksState) {
        when (state) {
            is FavTracksState.FavTracks -> showContent(state.tracks)
            is FavTracksState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        favouriteTracks.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderPicture.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        favouriteTracks.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        placeholderPicture.visibility = View.GONE
        adapter?.setItems(tracks)
        adapter?.notifyDataSetChanged()
    }

    companion object {
        private const val key = "track"
        fun newInstance() = FavouriteTracksFragment()
    }
}