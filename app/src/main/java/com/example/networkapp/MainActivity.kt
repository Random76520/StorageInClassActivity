package com.example.networkapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

private const val AUTO_SAVE_KEY = "auto_save"
private const val COMIC_PREFERENCES = "comic_preferences"
private const val COMIC_TITLE_KEY = "title"
private const val COMIC_DESCRIPTION_KEY = "description"

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    private lateinit var preferences: SharedPreferences
    private var autoSave = false
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        preferences = getSharedPreferences(COMIC_PREFERENCES, MODE_PRIVATE)
        autoSave = preferences.getBoolean(AUTO_SAVE_KEY, false)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        if (autoSave) {
            loadComic()
        }

    }

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {showComic(it)}, {
            })
        )
    }

    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)

        if (autoSave) {
            saveComic(comicObject)
        }
    }

    private fun saveComic(comicObject: JSONObject) {
        val editor = preferences.edit()
        editor.putString(COMIC_TITLE_KEY, comicObject.getString("title"))
        editor.putString(COMIC_DESCRIPTION_KEY, comicObject.getString("alt"))
        editor.apply()
    }

    private fun loadComic() {
        titleTextView.text = preferences.getString(COMIC_TITLE_KEY, "")
        descriptionTextView.text = preferences.getString(COMIC_DESCRIPTION_KEY, "")
    }
}