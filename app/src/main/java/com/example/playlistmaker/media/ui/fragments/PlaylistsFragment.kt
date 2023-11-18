package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.ui.PlaylistsAdapter
import com.example.playlistmaker.media.data.PlaylistsState
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var binding: FragmentPlaylistsBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylist.setOnClickListener {
            Log.d("NewPlaylist", "tap tap")
            findNavController().navigate(R.id.createPlaylistFragment)
        }

        viewModel.fillData()
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(
            requireContext(), /*Количество столбцов*/
            2
        ) //ориентация по умолчанию — вертикальная
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Playlists -> showContent(state.playlist)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showContent(playlist: List<Playlist>) {
        binding.emptyLibrary.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = PlaylistsAdapter(playlist)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.emptyLibrary.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}