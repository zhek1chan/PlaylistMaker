package com.example.playlistmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.ui.TrackAdapter
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel by viewModel<SearchViewModel>()
    private var isClickAllowed = true
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var historyRecycler: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyRecycler = binding.searchHistoryRecyclerView
        searchViewModel.getStateLiveData().observe(viewLifecycleOwner) { stateLiveData ->
            when (stateLiveData) {
                is SearchScreenState.DefaultSearch -> defaultSearch()
                is SearchScreenState.ConnectionError -> connectionError()
                is SearchScreenState.Loading -> loading()
                is SearchScreenState.NothingFound -> nothingFound()
                is SearchScreenState.SearchIsOk -> searchIsOk(stateLiveData.data)
                is SearchScreenState.SearchWithHistory -> searchWithHistory(stateLiveData.historyData)
                else -> {
                    connectionError()
                }
            }
        }

        trackAdapter = TrackAdapter(
            clickListener = {
                if (isClickAllowed) {
                    clickAdapting(it)
                }
            },
            longClickListener = {})

        historyAdapter = TrackAdapter(
            clickListener = {
                if (isClickAllowed) {
                    clickAdapting(it)
                }
            },
            longClickListener = {})
        isClickAllowed = false
        clickDebounceManager()
        onEditorFocus()
        onSearchTextChange()
        onClearIconClick()
        clearIconVisibilityChanger()
        startSearchByEnterPress()

        recyclerView = binding.searchResultsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = trackAdapter


        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyRecycler.adapter = historyAdapter
        binding.clearButton.setOnClickListener {
            makeHistoryLLGone()
            searchViewModel.clearHistory()
        }
        showHistoryTracks()
    }

    // метод восстанавливает поисковой запрос после пересоздания
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_USER_INPUT, "")
        }
    }

    private lateinit var searchText: String

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    private fun showHistoryTracks() {
        if (searchViewModel.provideHistory().value?.isNotEmpty() != false) {
            searchViewModel.provideHistory().value?.let { historyAdapter.setItems(it) }
            makeHistoryLLVisible()
        }
    }

    private fun clickDebounceManager() {
        GlobalScope.launch { clickDebounce() }
    }

    private suspend fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = false
        }
        return current
    }

    private fun clickAdapting(item: Track) {
        Log.d("SearchFragment", "Click on the track")
        isClickAllowed = false
        searchViewModel.addItem(item)
        val bundle = Bundle()
        bundle.putParcelable("track", item)
        Log.d("track", "$item")
        val navController = findNavController()
        navController.navigate(R.id.Fragment_to_playerFragment, bundle)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search() {
        searchViewModel.searchRequesting(binding.inputEditText.text.toString())
    }
    private fun searchDebounce() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            search()
        }
    }

    private fun onEditorFocus() {
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty() && searchViewModel.provideHistory().value?.isNotEmpty() == true) {
                searchViewModel.clearTrackList()
            } else {
                makeHistoryLLGone()
            }
        }
    }

    private fun onSearchTextChange() {
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.inputEditText.hasFocus() && p0?.isEmpty() == true && searchViewModel.provideHistory().value?.isNotEmpty() == true) {
                    searchViewModel.clearTrackList()
                } else {
                    makeHistoryLLGone()
                }
                if (binding.inputEditText.text.isNotEmpty()) {
                    searchText = binding.inputEditText.text.toString()
                    searchDebounce()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startSearchByEnterPress() {
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    searchText = binding.inputEditText.text.toString()
                    search()
                    trackAdapter.notifyDataSetChanged()
                }
            }
            false
        }
    }

    private fun onClearIconClick() {
        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val keyboard =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(
                binding.inputEditText.windowToken,
                0
            )
            binding.inputEditText.clearFocus()
            searchViewModel.clearTrackList()
            showHistoryTracks()
        }
    }

    private fun clearIconVisibilityChanger() {
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun defaultSearch() {
        makeHistoryLLGone()
        recyclerView.visibility = View.VISIBLE
        binding.nothingFoundPlaceholder.visibility = View.GONE
        binding.netProblemPlaceholder.visibility = View.GONE
        Log.d("DefaultSearch", "DefaultSearch was started")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        makeHistoryLLGone()
        recyclerView.visibility = View.GONE
        binding.nothingFoundPlaceholder.visibility = View.GONE
        binding.netProblemPlaceholder.visibility = View.GONE
        trackAdapter.notifyDataSetChanged()
        Log.d("Loading", "Loading was started")
    }

    private fun searchIsOk(data: List<Track>) {
        binding.loadingIndicator.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        binding.nothingFoundPlaceholder.visibility = View.GONE
        binding.netProblemPlaceholder.visibility = View.GONE
        binding.clearButton.visibility - View.GONE
        trackAdapter.setItems(data)
        makeHistoryLLGone()
        Log.d("SearchIsOk", "Loading has been end")
    }

    private fun nothingFound() {
        binding.loadingIndicator.visibility = View.GONE
        binding.clearButton.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        binding.nothingFoundPlaceholder.visibility = View.VISIBLE
        binding.netProblemPlaceholder.visibility = View.GONE
        makeHistoryLLGone()
        Log.d("NothingFound", "NothingFound")
    }

    private fun connectionError() {
        binding.loadingIndicator.visibility = View.GONE
        binding.netProblemPlaceholder.visibility = View.VISIBLE
        binding.nothingFoundPlaceholder.visibility = View.GONE
        recyclerView.visibility = View.GONE
        binding.updateButton.setOnClickListener { search() }
        makeHistoryLLGone()
        Log.d("ConnectionError", "Connection Error")
    }

    private fun searchWithHistory(historyData: List<Track>) {
        if (historyData.isEmpty()) {
            defaultSearch()
            return
        }
        binding.clearButton.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        binding.nothingFoundPlaceholder.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.netProblemPlaceholder.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        historyAdapter.setItems(historyData)
        historyAdapter.notifyDataSetChanged()
        makeHistoryLLVisible()
        Log.d("searchWithHistory", "SearchWithHistory was started")
    }

    private fun makeHistoryLLGone() {
        binding.historyLayout.visibility = View.GONE
    }

    private fun makeHistoryLLVisible() {
        binding.historyLayout.visibility = View.VISIBLE
    }


    companion object {
        private const val key = "track"
        private const val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}