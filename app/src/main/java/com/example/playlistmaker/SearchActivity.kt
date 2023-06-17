package com.example.playlistmaker

import TrackAdapter
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.history.LinkedRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.playlistmaker.network.ITunesApi
import com.example.playlistmaker.network.client.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

typealias TrackList = ArrayList<Track>

private enum class ResponseState {
    SUCCESS,
    NOTHING_FOUND,
    ERROR
}

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var problemsLayout: LinearLayout
    private lateinit var problemsText: TextView
    private lateinit var problemsIcon: ImageView
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var cleanHistoryButton: Button
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var linkedRepository: LinkedRepository<Track>

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val TRACKS = "TRACKS"
        private const val API_URL = "https://itunes.apple.com"
        private const val PREFS = "my_prefs"
        private const val QUERY = "searchQuery"
        private const val TRACKS_LIST = "TRACKS_LIST"
        private const val RESPONSE_STATE = "responseState"
        private const val MAX_HISTORY_SIZE = 10
        private const val HISTORY = "history"
    }

    //Retrofit конвертер
    private val retrofit = Retrofit.Builder()
        .baseUrl(SearchActivity.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)

    override fun onPause() {
        super.onPause()
        linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onStop() {
        super.onStop()
        linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onResume() {
        super.onResume()
        linkedRepository.restoreFromSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<ImageView>(R.id.arrow_back)
        recyclerView = findViewById(R.id.search_results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_icon)
        problemsLayout = findViewById(R.id.problems_layout)
        problemsText = findViewById(R.id.search_placeholder_text)
        problemsIcon = findViewById(R.id.problems_image)
        refreshButton = findViewById(R.id.refresh_button)
        loadingIndicator = findViewById(R.id.loading_indicator)
        cleanHistoryButton = findViewById(R.id.clear_button)

        // История поиска
        historyLayout = findViewById(R.id.history_layout)
        hideHistoryLayout()
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        linkedRepository = LinkedRepository(MAX_HISTORY_SIZE)
        linkedRepository.restoreFromSharedPreferences(PREFS, HISTORY, this)

        // ADAPTERS
        historyAdapter = TrackAdapter(linkedRepository)
        trackAdapter = TrackAdapter(linkedRepository)
        recyclerView.adapter = trackAdapter
        historyRecyclerView.adapter = historyAdapter


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    search(searchEditText.text.toString())
                }
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    makeClearButtonInvisible()
                } else {
                    makeClearButtonVisible()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString(SEARCH_QUERY, ""))
        }

        val sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val searchQuery = sharedPref.getString(QUERY, "")
        val json = sharedPref.getString(TRACKS_LIST, ResponseState.SUCCESS.name)
        val state = ResponseState.valueOf(
            sharedPref.getString(RESPONSE_STATE, ResponseState.SUCCESS.name)
                ?: ResponseState.SUCCESS.name
        )

        //логика отображения кнопки очистить текст
        if (searchQuery.isNullOrEmpty()) {
            makeClearButtonInvisible()
        } else {
            makeClearButtonVisible()
        }
        searchEditText.setText(searchQuery)
        //GSON
        val gson = Gson()
        val type = object : TypeToken<TrackList>() {}.type
        if (json.isNullOrEmpty()) {
            showProblemsLayout(responseState = ResponseState.NOTHING_FOUND)
        } else {
            if (searchEditText.text.isNullOrEmpty()) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
            }
            val gson = Gson()
            val type = object : TypeToken<TrackList>() {}.type
        }

        //логика вывода ошибок
        if (state != ResponseState.SUCCESS) {
            showProblemsLayout(state)
        }

        searchEditText.addTextChangedListener(simpleTextWatcher)

        searchEditText.addTextChangedListener { text ->
            if ((text.isNullOrEmpty()) && (linkedRepository.getSize() > 0)) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
                trackAdapter.setTracks(null)
            }
        }

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if ((hasFocus) && (searchEditText.text.isEmpty()) && (linkedRepository.getSize() > 0)) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
            }
        }

        // прослушиватель нажатия на кнопку "очистить историю"
        cleanHistoryButton.setOnClickListener {
            linkedRepository.clear()
            linkedRepository.clearSharedPreferences(PREFS, HISTORY, this)
            hideHistoryLayout()
        }

        //логика кнопки очистить текст
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0) // скрыть клавиатуру
            trackAdapter.setTracks(null)
            val sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(RESPONSE_STATE, ResponseState.SUCCESS.name)
            editor.apply()
            searchEditText.requestFocus()
            showHistoryLayout()
        }

        //логика кнопки назад
        backButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(QUERY, searchEditText.text.toString())
            val gson = Gson()
            val json = gson.toJson(trackAdapter.getTracks())
            editor.putString(TRACKS_LIST, json)
            editor.apply()
            this.finish()
        }
        refreshButton.setOnClickListener {
            search(searchEditText.text.toString())
        }

    }

    private fun makeClearButtonInvisible() {
        clearButton.visibility = View.GONE
    }

    private fun makeClearButtonVisible() {
        clearButton.visibility = View.VISIBLE
        clearButton.background = getDrawable(R.drawable.is_baseline_clear)
        searchEditText.background = getDrawable(R.drawable.search_field)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchEditText.text.toString())
        outState.putParcelableArrayList(TRACKS, trackAdapter.getTracks())
        outState.putParcelableArrayList(HISTORY, historyAdapter.getTracks())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        searchEditText.setText(searchQuery)
        linkedRepository.restoreFromSharedPreferences(PREFS, HISTORY, this)
        historyAdapter = TrackAdapter(linkedRepository)
        trackAdapter = TrackAdapter(linkedRepository)
        // TODO когда метод getParcelableArrayList уберут, надо будет использвать сериализацию в json и восстоновление из json
        val restoredTracks = savedInstanceState.getParcelableArrayList<Track>(TRACKS)
        if (restoredTracks != null) {
            trackAdapter.setTracks(restoredTracks)
        }
        val restoredHistory = savedInstanceState.getParcelableArrayList<Track>(HISTORY)
        if (restoredHistory != null) {
            historyAdapter.setTracks(restoredHistory)
        }
        historyRecyclerView.adapter = historyAdapter
        recyclerView.adapter = trackAdapter
    }

    //логика поиска
    private fun search(searchQuery: String) {
        trackAdapter.setTracks(null)
        loadingIndicator.visibility = View.VISIBLE
        hideHistoryLayout()
        recyclerView.visibility = View.GONE
        val call = itunesService.search(searchQuery)
        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                val responseState = when {
                    response.isSuccessful && response.body()?.results?.isNotEmpty() == true -> ResponseState.SUCCESS
                    response.isSuccessful -> ResponseState.NOTHING_FOUND
                    else -> ResponseState.ERROR
                }
                val sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(RESPONSE_STATE, responseState.name)
                editor.apply()
                when (responseState) {
                    ResponseState.SUCCESS -> {
                        hideHistoryLayout()
                        loadingIndicator.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        val sharedPreferences =
                            getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString(TRACKS_LIST, response.body()?.results!!.toString())
                        trackAdapter.setTracks(response.body()?.results!!)
                    }

                    ResponseState.NOTHING_FOUND -> showProblemsLayout(responseState)
                    ResponseState.ERROR -> showProblemsLayout(responseState)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                val responseState = ResponseState.ERROR
                showProblemsLayout(responseState)
            }
        })
    }

    private fun showHistoryLayout() {
        historyLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        problemsLayout.visibility = View.GONE
        cleanHistoryButton.visibility = View.VISIBLE
        val gson = Gson()
        val json = gson.toJson(linkedRepository.get(reversed = true))
        val type = object : TypeToken<TrackList>() {}.type
        val restoredTracks: TrackList = gson.fromJson(json, type)
        historyAdapter.setTracks(restoredTracks)
    }

    private fun hideHistoryLayout() {
        recyclerView.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        cleanHistoryButton.visibility = View.VISIBLE


    }

    private fun showProblemsLayout(responseState: ResponseState) {
        recyclerView.visibility = View.GONE
        problemsLayout.visibility = View.VISIBLE
        when (responseState.name) {
            ResponseState.SUCCESS.name -> {
                recyclerView.visibility = View.VISIBLE
                problemsLayout.visibility = View.GONE
                refreshButton.visibility = View.GONE
            }
            ResponseState.ERROR.name -> {
                recyclerView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                problemsText.text = getString(R.string.no_internet)
                problemsIcon.setImageResource(R.drawable.no_internet)
                refreshButton.visibility = View.VISIBLE
                refreshButton.setOnClickListener {
                    search(searchEditText.text.toString())
                }
            }
            ResponseState.NOTHING_FOUND.name -> {
                recyclerView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                problemsText.text = getString(R.string.nothing_found)
                problemsIcon.setImageResource(R.drawable.nothing_found)
                refreshButton.visibility = View.GONE
            }
        }
    }
}
