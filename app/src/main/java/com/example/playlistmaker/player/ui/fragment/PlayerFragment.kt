package com.example.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CreatingAlbumAlertBinding
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.media.data.PlaylistsState
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.ui.PlaylistsBottomAdapter
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.player.ui.view_model.FragmentPlayerViewModel
import com.example.playlistmaker.player.ui.view_model.FragmentPlayerViewModel.Companion.PLAYER_BUTTON_PRESSING_DELAY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private lateinit var playerState: PlayerState
    private val viewModel by viewModel<FragmentPlayerViewModel>()
    private lateinit var binding: FragmentPlayerBinding
    private var url: String = ""
    private var buttonChangerJob: Job? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = arguments?.getParcelable<Track>("track")!!
        binding.songNamePlayerActivity.text = track?.trackName ?: "Unknown Track"
        binding.bandNamePlayerActivity.text = track?.artistName ?: "Unknown Artist"
        binding.durationTrackValuePlayerActivity.text = track?.trackTimeMillis ?: "00:00"
        binding.albumValuePlayerActivity.text = track?.collectionName ?: "Unknown Album"
        binding.yearValuePlayerActivity.text = (track?.releaseDate ?: "Year").take(4)
        binding.genreValuePlayerActivity.text = track?.primaryGenreName ?: "Unknown Genre"
        binding.countryValuePlayerActivity.text = track?.country ?: "Unknown Country"
        val getImage = (track?.artworkUrl100 ?: "Unknown Cover").replace(
            "100x100bb.jpg",
            "512x512bb.jpg"
        )
        if (getImage != "Unknown Cover") {
            getImage.replace("100x100bb.jpg", "512x512bb.jpg")
            Glide.with(this)
                .load(getImage)
                .placeholder(R.drawable.song_cover)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius)))
                .into(binding.albumsCoverPlayerActivity)
        }
        url = track?.previewUrl ?: return
        playerState = PlayerState.STATE_PREPARED
        viewModel.createPlayer(url) {
            preparePlayer()
        }

        binding.backButtonPlayerActivity.setOnClickListener {
            findNavController().navigateUp()
        }
        updateButton()
        binding.playButtonPlayerActivity.setOnClickListener {
            if (viewModel.playerStateGetter() == PlayerState.STATE_PLAYING) viewModel.pause() else viewModel.play()
            updateButton()
        }
        binding.pauseButtonPlayerActivity.setOnClickListener {
            viewModel.pause()
            updateButton()
        }
        viewModel.putTime().observe(requireActivity()) { timer ->
            binding.trackTimePlayerActivity.text = timer
            if ((timer != "00:00") and (playerState != PlayerState.STATE_PAUSED)) Log.d(
                "TrackTimer",
                timer
            )
            if (timer == "00:00") {
                updateButton()
            }
        }
        buttonChangerJob?.start()!!


        val bottomSheetContainer = binding.playlistsBottomSheet

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.plusButtonPlayerActivity.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.newPlaylist.setOnClickListener {
            Log.d("NewPlaylist", "tap tap")
            findNavController().navigate(R.id.createPlaylistFragment)
        }
        viewModel.onLikedCheck(track).observe(requireActivity()) { likeIndicator ->
            if (!likeIndicator) {
                changeLikeButton(track)
                track.isFavourite = false
            } else {
                track.isFavourite = true
                binding.likeButtonPlayerActivity.visibility = View.GONE
                binding.pressedLikeButtonPlayerActivity.visibility = View.VISIBLE
                binding.pressedLikeButtonPlayerActivity.setOnClickListener {
                    Log.d("Press on dislike", ":)")
                    viewModel.onLikeClick(track)
                    changeLikeButton(track)
                }
            }
        }


        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.loadPlaylists()
        viewModel.observeState().observe(requireActivity()) {
            render(track, it)
        }
        val overlay = binding.overlay

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        recyclerView.visibility = View.VISIBLE
                        overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    override fun onPause() {
        updateButton()
        super.onPause()
        viewModel.pause()
    }

    private fun preparePlayer() {
        binding.playButtonPlayerActivity.isEnabled = true
        binding.albumPlayerActivity.visibility = View.VISIBLE
        binding.pauseButtonPlayerActivity.visibility = View.GONE
    }

    //логика смены кнопок Pause & Play
    private fun playerButtonChanger() {
        playerState = viewModel.playerStateGetter()
        when (playerState) {
            PlayerState.STATE_DEFAULT -> {
                binding.playButtonPlayerActivity.visibility = View.VISIBLE
                binding.pauseButtonPlayerActivity.visibility = View.GONE
            }

            PlayerState.STATE_PREPARED -> {
                binding.playButtonPlayerActivity.visibility = View.VISIBLE
                binding.pauseButtonPlayerActivity.visibility = View.GONE
            }

            PlayerState.STATE_PAUSED -> {
                binding.playButtonPlayerActivity.visibility = View.VISIBLE
                binding.pauseButtonPlayerActivity.visibility = View.GONE
            }

            PlayerState.STATE_PLAYING -> {
                binding.pauseButtonPlayerActivity.visibility = View.VISIBLE
                binding.playButtonPlayerActivity.visibility = View.GONE
            }
        }
    }

    private fun updateButton() {
        buttonChangerJob = lifecycleScope.launch {
            delay(PLAYER_BUTTON_PRESSING_DELAY)
            playerButtonChanger()
        }
    }

    private fun changeLikeButton(track: Track) {
        track.isFavourite = false
        binding.likeButtonPlayerActivity.visibility = View.VISIBLE
        binding.pressedLikeButtonPlayerActivity.visibility = View.GONE
        binding.likeButtonPlayerActivity.setOnClickListener {
            Log.d("Press on like button", ":)")
            viewModel.onLikeClick(track)
            track.isFavourite = true
            binding.likeButtonPlayerActivity.visibility = View.GONE
            binding.pressedLikeButtonPlayerActivity.visibility = View.VISIBLE
        }
    }

    private fun render(track: Track, state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Playlists ->
                showContent(track, state.playlist)

            is PlaylistsState.Empty ->
                showEmpty()
        }
    }

    private fun showContent(track: Track, playlist: List<Playlist>) {
        recyclerView.adapter = PlaylistsBottomAdapter(playlist) {
            Log.d("ClickAdapting", "Launched")
            playlistClickAdapting(track, it)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun showEmpty() {
        binding.recyclerView.visibility = View.GONE
    }
    private fun playlistClickAdapting(track: Track, playlist: Playlist) {
        val customSnackBar = Snackbar.make(binding.snackBar, "", 3000)
        val layout = customSnackBar.view as Snackbar.SnackbarLayout
        val bind: CreatingAlbumAlertBinding = CreatingAlbumAlertBinding.inflate(layoutInflater)
        val booleanType = viewModel.addTrackToPlaylist(track, playlist)
        if (booleanType == true) {
            recyclerView.adapter?.notifyDataSetChanged()
            viewModel.loadPlaylists()
            viewModel.observeState().observe(requireActivity()) {
                render(track, it)
            }
            BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
            bind.text.setText("Трек '${track.trackName}' добавлен в плейлист '${playlist.name}'")
            layout.setPadding(0, 0, 0, 0)
            layout.addView(bind.root, 0)
            customSnackBar.show()
        } else {
            bind.text.setText("Трек '${track.trackName}' уже был добавлен в плейлист '${playlist.name}'")
            layout.setPadding(0, 0, 0, 0)
            layout.addView(bind.root, 0)
            customSnackBar.show()
        }
    }

}

