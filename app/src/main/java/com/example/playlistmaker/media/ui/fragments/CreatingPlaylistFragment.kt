package com.example.playlistmaker.media.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CreatingAlbumAlertBinding
import com.example.playlistmaker.databinding.FragmentPlaylistCreatingBinding
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.ui.viewmodel.CreatingPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreatingPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistCreatingBinding
    private val viewModel by viewModel<CreatingPlaylistViewModel>()
    private var playlist = Playlist(0, "", "", "", 0, 0)
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
        var uriString = ""
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if ((nameText.isNotEmpty()) || (descriptionText.isNotEmpty()) || (uriString.isNotEmpty())) {
                val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                    .setBackground(
                        ContextCompat.getDrawable(
                            requireContext(),
                            android.R.color.background_dark
                        )
                    )
                    .setTitle(Html.fromHtml("<font color='#FFFFFF'>${getString(R.string.exit_question)}</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>${getString(R.string.all_data_would_be_lost)}</font>"))
                    .setPositiveButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }.setNegativeButton(getString(R.string.finish)) { dialog, _ ->
                        findNavController().navigateUp()
                    }.show()
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setAllCaps(false)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else findNavController().navigateUp()
        }
        binding.backIcon.setOnClickListener {
            if ((nameText.isNotEmpty()) || (descriptionText.isNotEmpty()) || (uriString.isNotEmpty())) {
                val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                    .setBackground(
                        ContextCompat.getDrawable(
                            requireContext(),
                            android.R.color.background_dark
                        )
                    )
                    .setTitle(Html.fromHtml("<font color='#FFFFFF'>${getString(R.string.exit_question)}</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>${getString(R.string.all_data_would_be_lost)}</font>"))
                    .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.cancel()
                    }.setNegativeButton(getString(R.string.finish)) { dialog, which ->
                        findNavController().navigateUp()
                    }.show()
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setAllCaps(false)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else findNavController().navigateUp()
        }

        onNameTextChange()
        onDescriptionTextChange()

        binding.createPlaylist.setOnClickListener {
            viewModel.onCreateClick(playlist)
            val customSnackBar = Snackbar.make(binding.snackBar, "", 2000)
            val layout = customSnackBar.view as Snackbar.SnackbarLayout
            val bind: CreatingAlbumAlertBinding = CreatingAlbumAlertBinding.inflate(layoutInflater)
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
            requestPermission()
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
        binding.albumCoverageAdd.setOnClickListener {
            requestPermission()
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    var nameText = ""
    var descriptionText = ""
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
}