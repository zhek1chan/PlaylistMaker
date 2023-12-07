package com.example.playlistmaker.media.ui.fragments.playlists

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CreatingAlbumAlertBinding
import com.example.playlistmaker.databinding.FragmentPlaylistCreatingBinding
import com.example.playlistmaker.media.data.TracksState
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.ui.viewmodel.playlists.CreatingPlaylistViewModel
import com.example.playlistmaker.player.domain.Track
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreatingPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistCreatingBinding
    private val viewModel by viewModel<CreatingPlaylistViewModel>()
    private var playlist = Playlist(0, "", "", "", 0, 0)
    private var track = Track("", "", "", "", 0, "", "", "", "", "")
    private lateinit var tracks: List<Track>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistCreatingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var pl = arguments?.getParcelable<Playlist>("playlist")!!
        if (pl.name.isNotEmpty()) {
            viewModel.observeState().observe(viewLifecycleOwner) {
                render(it)
            }
            var nameText = pl.name
            var descriptionText = pl.description
            playlist.name = pl.name
            playlist.description = pl.description
            playlist.trackId = pl.trackId
            playlist.playlistId = pl.playlistId
            viewModel.getTracks(playlist.playlistId)
            playlist.num = pl.num
            binding.nameOfAlbum.setText(nameText)
            binding.descriptionOfAlbum.setText(descriptionText)
            binding.saveText.setText(getString(R.string.save))
            binding.naming.setText(getString(R.string.edit))
            var uriString = pl.artworkUrl100
            if (uriString.isNotEmpty()) {
                binding.albumCoverageAdd.visibility = View.GONE
                playlist.artworkUrl100 = uriString
                Glide.with(requireActivity())
                    .load(uriString.toUri())
                    .centerCrop()
                    .transform(
                        CenterCrop(),
                        RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius))
                    )
                    .into(binding.albumCoverage)
            }
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                if ((nameText.isNotEmpty()) || (descriptionText.isNotEmpty()) || (uriString.isNotEmpty())) {
                    val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                        .setBackground(
                            ContextCompat.getDrawable(
                                requireContext(),
                                android.R.color.background_dark
                            )
                        )
                        .setTitle(Html.fromHtml("<font color='#FFFFFF'>${getString(R.string.edit_exit_question)}</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>${getString(R.string.all_data_would_be_lost)}</font>"))
                        .setPositiveButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.cancel()
                        }.setNegativeButton(getString(R.string.finish)) { dialog, _ ->
                            val bundle = Bundle()
                            bundle.putParcelable("playlist", playlist)
                            val navController = findNavController()
                            navController.navigate(R.id.playlistFragment, bundle)
                        }.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setAllCaps(false)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable("playlist", playlist)
                    val navController = findNavController()
                    navController.navigate(R.id.playlistFragment, bundle)
                }
            }
            binding.backIcon.setOnClickListener {
                if ((nameText.isNotEmpty()) || (descriptionText.isNotEmpty()) || (uriString.isNotEmpty())) {
                    val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                        .setBackground(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.color.white
                            )
                        )
                        .setTitle(Html.fromHtml("<font color='#000000'>${getString(R.string.exit_question)}</font>"))
                        .setMessage(Html.fromHtml("<font color='#000000'>${getString(R.string.all_data_would_be_lost)}</font>"))
                        .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                            dialog.cancel()
                        }.setNegativeButton(getString(R.string.finish)) { dialog, which ->
                            val bundle = Bundle()
                            bundle.putParcelable("playlist", playlist)
                            val navController = findNavController()
                            navController.navigate(R.id.playlistFragment, bundle)
                        }.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setAllCaps(false)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable("playlist", playlist)
                    val navController = findNavController()
                    navController.navigate(R.id.playlistFragment, bundle)
                }
            }

            editOnNameTextChange()
            editOnDescriptionTextChange()
            val pickMedia =
                registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    //обрабатываем событие выбора пользователем фотографии
                    uriString = uri.toString()
                    if (uri != null) {
                        binding.albumCoverageAdd.visibility = View.GONE
                        playlist.artworkUrl100 = uri.toString()
                        viewModel.saveImageToPrivateStorage(uri)
                        Glide.with(requireActivity())
                            .load(uri)
                            .centerCrop()
                            .transform(
                                CenterCrop(),
                                RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius))
                            )
                            .into(binding.albumCoverage)
                        binding.createPlaylist.isClickable = true
                        iAmBigButton(playlist.name)
                        binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist_active);
                    } else {
                        binding.albumCoverageAdd.visibility = View.VISIBLE
                        Glide.with(binding.albumCoverage)
                            .load(R.drawable.creating_album_cover)
                            .centerCrop()
                            .transform(
                                CenterCrop(),
                                RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius))
                            )
                            .into(binding.albumCoverage)
                        Log.d("PhotoPicker", "No media selected")
                        pl.artworkUrl100 = uriString
                    }
                }
            //по нажатию на кнопку pickImage запускаем photo picker
            binding.albumCoverage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            }
            binding.albumCoverageAdd.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } else {
            var uriString = ""
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                if ((nameText.isNotEmpty()) || (descriptionText.isNotEmpty()) || (uriString.isNotEmpty())) {
                    val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                        .setBackground(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.color.white
                            )
                        )
                        .setTitle(Html.fromHtml("<font color='#000000'>${getString(R.string.exit_question)}</font>"))
                        .setMessage(Html.fromHtml("<font color='#000000'>${getString(R.string.all_data_would_be_lost)}</font>"))
                        .setPositiveButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.cancel()
                        }.setNegativeButton(getString(R.string.finish)) { dialog, _ ->
                            findNavController().navigateUp()
                        }.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setAllCaps(false)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                } else findNavController().navigateUp()
            }
            binding.backIcon.setOnClickListener {
                if ((nameText.isNotEmpty()) || (descriptionText.isNotEmpty()) || (uriString.isNotEmpty())) {
                    val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                        .setBackground(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.color.white
                            )
                        )
                        .setTitle(Html.fromHtml("<font color='#000000'>${getString(R.string.exit_question)}</font>"))
                        .setMessage(Html.fromHtml("<font color='#000000'>${getString(R.string.all_data_would_be_lost)}</font>"))
                        .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                            dialog.cancel()
                        }.setNegativeButton(getString(R.string.finish)) { dialog, which ->
                            findNavController().navigateUp()
                        }.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setAllCaps(false)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
                } else findNavController().navigateUp()
            }

            onNameTextChange()
            onDescriptionTextChange()

            binding.createPlaylist.setOnClickListener {
                val list = listOf<Track>(track)
                viewModel.onCreateClick(playlist, list)
                val customSnackBar = Snackbar.make(binding.snackBar, "", 2000)
                val layout = customSnackBar.view as Snackbar.SnackbarLayout
                val bind: CreatingAlbumAlertBinding =
                    CreatingAlbumAlertBinding.inflate(layoutInflater)
                bind.text.setText("Плейлист $nameText создан")
                layout.setPadding(0, 0, 0, 0)
                layout.addView(bind.root, 0)
                customSnackBar.show()
                findNavController().navigateUp()
            }
            val pickMedia =
                registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    //обрабатываем событие выбора пользователем фотографии
                    uriString = uri.toString()
                    if (uri != null) {
                        binding.albumCoverageAdd.visibility = View.GONE
                        playlist.artworkUrl100 = uri.toString()
                        viewModel.saveImageToPrivateStorage(uri)
                        Glide.with(requireActivity())
                            .load(uri)
                            .centerCrop()
                            .transform(
                                CenterCrop(),
                                RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius))
                            )
                            .into(binding.albumCoverage)
                    } else {
                        binding.albumCoverageAdd.visibility = View.VISIBLE
                        Glide.with(binding.albumCoverage)
                            .load(R.drawable.creating_album_cover)
                            .centerCrop()
                            .transform(
                                CenterCrop(),
                                RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_album_cover_corner_radius))
                            )
                            .into(binding.albumCoverage)
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
            //по нажатию на кнопку pickImage запускаем photo picker
            binding.albumCoverage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            binding.albumCoverageAdd.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    var nameText = ""
    var descriptionText = ""
    var nText: String = ""
    var dText: String = ""
    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Tracks -> {
                tracks = state.tracks
            }

            is TracksState.Empty -> {
                tracks = emptyList()
            }
        }
    }

    private fun editOnNameTextChange() {
        binding.nameOfAlbum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nText = binding.nameOfAlbum.text.toString()
                if (nText.isNotEmpty()) {
                    playlist.name = nText
                    binding.createPlaylist.isClickable = true
                    binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist_active)
                    iAmBigButton(nText)
                } else {
                    binding.createPlaylist.isClickable = false
                    binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                playlist.name = nText
            }
        })
    }

    private fun editOnDescriptionTextChange() {
        binding.descriptionOfAlbum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                dText = binding.descriptionOfAlbum.text.toString()
                if (dText.isNotEmpty()) {
                    playlist.description = dText
                    iAmBigButton(nText)
                    binding.createPlaylist.isClickable = true
                    binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist_active);
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                playlist.description = dText
            }
        })
    }

    private fun onNameTextChange() {
        binding.nameOfAlbum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nameText = binding.nameOfAlbum.text.toString()
                saveText(nameText, descriptionText)
                if (nameText.isNotEmpty()) {
                    binding.createPlaylist.isClickable = true
                    binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist_active);
                } else {
                    binding.createPlaylist.isClickable = false
                    binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist);
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun onDescriptionTextChange() {
        binding.descriptionOfAlbum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                descriptionText = binding.descriptionOfAlbum.text.toString()
                Log.d("Input", "$descriptionText")
                saveText(nameText, descriptionText)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun saveText(n: String, d: String) {
        playlist.name = n
        playlist.description = d
    }

    companion object {
        const val album = "myalbum"
        const val jpg = "first_cover.jpg"
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
            255
        )
    }

    fun iAmBigButton(nameText: String) {
        binding.createPlaylist.setOnClickListener {
            val newPlaylist = playlist
            Log.d("Playlist CHECK", "$newPlaylist")
            viewModel.savePlayList(newPlaylist, tracks)
            val customSnackBar = Snackbar.make(binding.snackBar, "", 2000)
            val layout = customSnackBar.view as Snackbar.SnackbarLayout
            val bind: CreatingAlbumAlertBinding = CreatingAlbumAlertBinding.inflate(layoutInflater)
            bind.text.setText("Плейлист $nameText сохранён")
            layout.setPadding(0, 0, 0, 0)
            layout.addView(bind.root, 0)
            customSnackBar.show()
            val bundle = Bundle()
            bundle.putParcelable("playlist", playlist)
            val navController = findNavController()
            findNavController().popBackStack(R.id.playlistFragment, true)
            navController.navigate(R.id.playlistFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().popBackStack(R.id.createPlaylist, true)
    }
}