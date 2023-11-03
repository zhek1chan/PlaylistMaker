package com.example.playlistmaker.media.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CreatingAlbumAlertBinding
import com.example.playlistmaker.databinding.FragmentPlaylistCreatingBinding
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.ui.viewmodel.CreatingPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


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

        binding.backIcon.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.exit_question))
                .setMessage(getString(R.string.all_data_would_be_lost))
                .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.cancel()
                }.setNegativeButton(getString(R.string.finish)) { dialog, which ->
                    findNavController().navigateUp()
                }.show()
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
                if (uri != null) {
                    binding.albumCoverage.setImageURI(uri)
                    binding.albumCoverageAdd.visibility = View.GONE
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        //по нажатию на кнопку pickImage запускаем photo picker
        binding.albumCoverage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            val filePath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )
            val file = File(filePath, "first_cover.jpg")
            binding.albumCoverage.setImageURI(file.toUri())
            binding.albumCoverageAdd.visibility = View.GONE
        }
        binding.albumCoverageAdd.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            val filePath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )
            val file = File(filePath, "first_cover.jpg")
            binding.albumCoverage.setImageURI(file.toUri())
            binding.albumCoverageAdd.visibility = View.GONE
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
                binding.createPlaylist.isClickable = true
                binding.createPlaylist.setBackgroundResource(R.drawable.button_create_playlist_active);
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun onDescriptionTextChange() {
        binding.nameOfAlbum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                descriptionText = binding.nameOfAlbum.text.toString()
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

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        playlist.artworkUrl100 = uri.toString()
    }
}