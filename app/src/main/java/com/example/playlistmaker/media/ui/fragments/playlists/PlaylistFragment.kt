package com.example.playlistmaker.media.ui.fragments.playlists

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.data.TracksState
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.ui.PlaylistsBottomAdapter
import com.example.playlistmaker.media.ui.viewmodel.playlists.PlaylistViewModel
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment() {
    private val viewModel by viewModel<PlaylistViewModel>()
    private var binding: FragmentPlaylistBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewPlaylist: RecyclerView
    private lateinit var pl: Playlist
    private lateinit var trackAdapter: TrackAdapter
    private var sum: String = "0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CoordinatorLayout? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pl = arguments?.getParcelable<Playlist>("playlist")!!
        binding?.playlistName?.text = pl.name
        if (pl.num.toString().isEmpty()) {
            pl.num = 0
        }
        if (pl.description.isEmpty()) {
            binding?.description?.text = "2023"
        } else {
            binding?.description?.text = pl.description
        }
        if (pl.artworkUrl100.isEmpty()) {
            binding?.playlistCover?.visibility = View.GONE
            binding?.playlistPlaceholder?.visibility = View.VISIBLE
            binding?.buttonBack?.visibility = View.VISIBLE
        } else {
            binding?.playlistPlaceholder?.visibility = View.GONE
            binding?.playlistCover?.visibility = View.VISIBLE
            val uri = pl.artworkUrl100.toUri()
            Glide.with(requireActivity())
                .load(uri)
                .fitCenter()
                .into(binding?.playlistCover!!)

        }
        recyclerViewPlaylist = binding?.rvPlaylist!!
        val list: List<Playlist> = (listOf(pl))
        recyclerViewPlaylist.adapter = PlaylistsBottomAdapter(list) {
            clickAdapting(Track("", ' '.toString(), "", "", 0, "", "", "", "", "", false))
        }
        recyclerView = binding?.recyclerView!!
        trackAdapter = TrackAdapter({
            clickAdapting(it)
        },
            longClickListener = {
                suggestTrackDeleting(it, pl, 0, list.size)
            }
        )
        recyclerView.adapter = trackAdapter

        binding?.buttonBack?.setOnClickListener {
            findNavController().navigateUp()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
        viewModel.getTracks(pl.playlistId)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            Log.d("Render func", "I have started")
        }
        val bottomSheetContainerTracks = binding?.playlistSongs!!
        var bottomSheetBehaviorSettings =
            BottomSheetBehavior.from(binding?.playlistShare!!).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        binding?.buttonSharePlaylist?.setOnClickListener {
            viewModel.observeState().observe(viewLifecycleOwner) {
                var tracks = render(it)
                Log.d("Render func", "I have started")
                if (pl.num == 0.toLong()) {
                    val message = getString(R.string.message_no_tracks)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                } else {
                    var trackInfo =
                        "${getString(R.string.look_what_i_did)}\n${getString(R.string.playlist)} ${pl.name}\n${pl.description}\n${
                            getString(
                                R.string.quantityTracks
                            )
                        }${getString(R.string.tracks)} - ${pl.num}"
                    var i = 0
                    tracks.forEach { track ->
                        i += 1
                        val name = track.trackName
                        val duration = track.trackTimeMillis
                        trackInfo =
                            "$trackInfo\n$i. ${getString(R.string.naming)}: $name - ($duration)\n"
                    }

                    val intentSend = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, trackInfo)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Intent.createChooser(this, null)
                    }
                    requireContext().startActivity(intentSend, null)
                }
            }
        }
        binding?.buttonEdit?.setOnClickListener {
            binding?.playlistShare?.visibility = View.VISIBLE
            val bottomSheetContainerSettings = binding?.playlistShare
            bottomSheetBehaviorSettings =
                BottomSheetBehavior.from(bottomSheetContainerSettings!!).apply {
                    state = BottomSheetBehavior.STATE_COLLAPSED

                }
            binding?.share?.setOnClickListener {
                viewModel.observeState().observe(viewLifecycleOwner) {
                    var tracks = render(it)
                    Log.d("Render func", "I have started")
                    if (pl.num == 0.toLong()) {
                        val message = getString(R.string.message_no_tracks)
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    } else {
                        var trackInfo =
                            "${getString(R.string.look_what_i_did)}\n${getString(R.string.playlist)} ${pl.name}\n${pl.description}\n${
                                getString(
                                    R.string.quantityTracks
                                )
                            }${getString(R.string.tracks)} - ${pl.num}"
                        var i = 0
                        tracks.forEach { track ->
                            i += 1
                            val name = track.trackName
                            val duration = track.trackTimeMillis
                            trackInfo =
                                "$trackInfo\n$i. ${getString(R.string.naming)}: $name - ($duration)\n"
                        }

                        val intentSend = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, trackInfo)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            Intent.createChooser(this, null)
                        }
                        requireContext().startActivity(intentSend, null)
                    }
                }
            }
            binding?.edit?.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("playlist", pl)
                val navController = findNavController()
                navController.navigate(R.id.createPlaylistFragment, bundle)
            }
            binding?.delete?.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setBackground(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.background_dialog
                        )
                    )
                    .setTitle(Html.fromHtml("<font color='#000000'>${getString(R.string.question_to_delete_playlist)}"))
                    .setNegativeButton(getString(R.string.no)) { _, _ ->
                        return@setNegativeButton
                    }
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.deletePlaylist(pl)
                        findNavController().navigateUp()
                    }
                    .show()
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))

            }
        }

        val overlay = binding!!.overlay

        bottomSheetBehaviorSettings.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        recyclerViewPlaylist.visibility = View.VISIBLE
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainerTracks).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }

                    else -> {
                        binding?.overlay?.visibility = View.GONE
                        bottomSheetContainerTracks.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun render(state: TracksState): List<Track> {
        when (state) {
            is TracksState.Tracks -> {
                showContent(state.tracks, pl)
                return state.tracks
            }

            is TracksState.Empty -> {
                showEmpty()
                return emptyList()
            }
        }
    }

    private fun showContent(tracks: List<Track>, playlist: Playlist) {
        binding?.textNoTracks?.visibility = View.GONE
        Log.d("Tracks", "Are shown")
        binding?.recyclerView?.visibility = View.VISIBLE
        trackAdapter = TrackAdapter({
            clickAdapting(it)
        },
            longClickListener = {
                suggestTrackDeleting(it, playlist, tracks.indexOf(it), tracks.size)
            }
        )
        trackAdapter.setItems(tracks)
        recyclerView.adapter = trackAdapter
        //recyclerView.adapter?.notifyDataSetChanged()
        if (pl.num.toInt() == 2) {
            binding?.time?.text = "${viewModel.getTimeSum(tracks)} ${
                resources.getQuantityString(
                    R.plurals.minutes,
                    viewModel.getTimeSum(tracks).toInt()
                )
            }  ·  ${pl.num} трека"
        } else
            if (pl.num.toInt() == 3) {
                binding?.time?.text = "${viewModel.getTimeSum(tracks)} ${
                    resources.getQuantityString(
                        R.plurals.minutes,
                        viewModel.getTimeSum(tracks).toInt()
                    )
                }  ·  ${pl.num} трека"
            } else
                if (pl.num.toInt() == 4) {
                    binding?.time?.text = "${viewModel.getTimeSum(tracks)} ${
                        resources.getQuantityString(
                            R.plurals.minutes,
                            viewModel.getTimeSum(tracks).toInt()
                        )
                    }  ·  ${pl.num} трека"
                } else {
                    binding?.time?.text = "${viewModel.getTimeSum(tracks)} ${
                        resources.getQuantityString(
                            R.plurals.minutes,
                            viewModel.getTimeSum(tracks).toInt()
                        )
                    }  ·  ${pl.num} ${
                        resources.getQuantityString(
                            R.plurals.numberOfTracksAvailable,
                            pl.num.toInt()
                        )
            } "
        }
    }


    private fun deleteTrackByClick(item: Track, playlist: Playlist, pos: Int, size: Int) {
        viewModel.deleteTrack(item, playlist)
        recyclerView.adapter?.notifyItemRemoved(pos)
        val time =
            Integer.parseInt(item.trackTimeMillis[0].toString()) * 10 + Integer.parseInt(item.trackTimeMillis[1].toString())
        pl.num = pl.num - 1
        viewModel.observeNumState().observe(viewLifecycleOwner) {
            if (pl.num.toInt() == 2) {
                binding?.time?.text = "${it - time} ${
                    resources.getQuantityString(
                        R.plurals.minutes,
                        it.toInt()
                    )
                }  ·  ${pl.num} трека"
            } else
                if (pl.num.toInt() == 3) {
                    binding?.time?.text = "${it - time} ${
                        resources.getQuantityString(
                            R.plurals.minutes,
                            it.toInt()
                        )
                    }  ·  ${pl.num} трека"
                } else
                    if (pl.num.toInt() == 4) {
                        binding?.time?.text = "${it - time} ${
                            resources.getQuantityString(
                                R.plurals.minutes,
                                it.toInt()
                            )
                        }  ·  ${pl.num} трека"
                    } else {
                        binding?.time?.text = "${it - time} ${
                            resources.getQuantityString(
                                R.plurals.minutes,
                                it.toInt()
                            )
                        }  ·  ${pl.num} ${
                            resources.getQuantityString(
                                R.plurals.numberOfTracksAvailable,
                                pl.num.toInt()
                            )
                        } "
                    }
        }
    }

    private fun suggestTrackDeleting(
        track: Track,
        playlist: Playlist,
        pos: Int,
        size: Int
    ): String {
        var res: String = "null"
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_dialog
                )
            )
            .setTitle(Html.fromHtml("<font color='#000000'>${getString(R.string.question_to_delete)}"))
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                return@setNegativeButton
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteTrackByClick(track, playlist, pos, size)
                res = "yes"
            }
            .show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
        return res

    }

    private fun clickAdapting(item: Track) {
        Log.d("PlaylistFragment", "Click on the track")
        val bundle = Bundle()
        bundle.putParcelable("track", item)
        Log.d("track", "$item")
        val navController = findNavController()
        navController.navigate(R.id.fragment_to_playerFragment, bundle)
    }

    private fun showEmpty() {
        Log.d("Tracks", "ARE NOT shown")
        binding?.time?.text = "0 минут  ·  0 треков"
        binding?.recyclerView?.visibility = View.GONE
        binding?.textNoTracks?.visibility = View.VISIBLE
    }

}