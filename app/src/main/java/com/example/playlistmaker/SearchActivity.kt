package com.example.playlistmaker

import android.annotation.SuppressLint
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.ItunesApi
import com.example.playlistmaker.data.ItunesResponse
import com.example.playlistmaker.ui.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button
    private lateinit var searchEditText: EditText

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesApi = retrofit.create(ItunesApi::class.java)
    private lateinit var placeholderTitle: TextView

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        placeholderTitle = findViewById(R.id.placeholderTitle)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageView>(R.id.backButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView = findViewById(R.id.rvTracks)
        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter()
        recyclerView.adapter = adapter

        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard(searchEditText)
            clearButton.visibility = View.GONE
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.GONE
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearButton.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.isNotEmpty()) {
                    search()
                }
                true
            } else false
        }

        refreshButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        itunesApi.search(searchText).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                if (response.code() == 200) {
                    val results = response.body()?.results ?: emptyList()
                    if (results.isNotEmpty()) {
                        showResults(results)
                    } else {
                        showPlaceholder(PlaceholderType.NOTHING_FOUND)
                    }
                } else {
                    showPlaceholder(PlaceholderType.NETWORK_ERROR)
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                showPlaceholder(PlaceholderType.NETWORK_ERROR)
            }
        })
    }

    private fun showResults(results: List<com.example.playlistmaker.data.Track>) {
        placeholderLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.tracks.clear()
        adapter.tracks.addAll(results)
        adapter.notifyDataSetChanged()
    }

    private fun showPlaceholder(type: PlaceholderType) {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE

        when (type) {
            PlaceholderType.NOTHING_FOUND -> {
                placeholderImage.setImageResource(R.drawable.ntfound)
                placeholderTitle.text = getString(R.string.nothing_found)
                placeholderMessage.visibility = View.GONE
                refreshButton.visibility = View.GONE
            }
            PlaceholderType.NETWORK_ERROR -> {
                placeholderImage.setImageResource(R.drawable.nowifi)
                placeholderTitle.text = getString(R.string.network_error_title)
                placeholderMessage.text = getString(R.string.network_error_message)
                placeholderMessage.visibility = View.VISIBLE
                refreshButton.visibility = View.VISIBLE
            }
        }
    }
    private enum class PlaceholderType {
        NOTHING_FOUND,
        NETWORK_ERROR
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString("SEARCH_TEXT", "")
        if (savedText.isNotEmpty()) {
            searchEditText.setText(savedText)
            searchEditText.setSelection(savedText.length)
        }
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}