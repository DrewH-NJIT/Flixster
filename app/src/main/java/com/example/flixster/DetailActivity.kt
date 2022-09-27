package com.example.flixster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import okhttp3.Headers


// note the %d for TRAILERS_URL

private const val TAG = "DetailActivity"

class DetailActivity : YouTubeBaseActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvOverview: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ytPlayerView: YouTubePlayerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        tvTitle = findViewById(R.id.tvTitle)
        tvOverview = findViewById(R.id.tvOverview)
        ratingBar = findViewById(R.id.rbVoteAverage)
        ytPlayerView = findViewById(R.id.player)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA) as Movie
        Log.i(TAG, "onCreate: $movie")
        tvTitle.text = movie.title
        tvOverview.text = movie.overview
        val rating = movie.voteAverage.toFloat()
        ratingBar.rating = rating
//        Toast.makeText(applicationContext,"hit1", Toast.LENGTH_SHORT).show()

        val client = AsyncHttpClient()
        val TRAILERS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=${getString(R.string.themoviedb_api_key)}"
        client.get(TRAILERS_URL.format(movie.movieId), object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure: $statusCode")
                Log.i(TAG, "onFailure: $response")
//                Toast.makeText(applicationContext,"hit2", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess: howdy")
//                Toast.makeText(applicationContext,"hit3", Toast.LENGTH_SHORT).show()

                val results = json.jsonObject.getJSONArray("results")
                if (results.length() == 0) {
                    Log.w(TAG, "onSuccess: No Movie Trailers Found. keyword = barnacle")
//                    Toast.makeText(applicationContext,"hit4", Toast.LENGTH_SHORT).show()

                    return
                }
                val movieTrailerJSON =
                    results.getJSONObject(0) // index 0 = the trailer index of the array.
                val youtubeKey = movieTrailerJSON.getString("key")
//                Toast.makeText(applicationContext,"hit5", Toast.LENGTH_SHORT).show()
                initializeYoutube(youtubeKey, rating)
            }

        })


    }

    private fun initializeYoutube(youtubeKey: String, rating: Float) {
         val YOUTUBE_API_KEY = getString(R.string.youtube_api_key)

        ytPlayerView.initialize(YOUTUBE_API_KEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {
                Log.i(TAG, "onInitializationSuccess: hi")

                if (rating.toInt() > 5) {
                    player?.loadVideo(youtubeKey)
                } else {
                    player?.cueVideo(youtubeKey)
                }

            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Log.i(TAG, "onInitializationFailure: ho")
            }

        })
    }
}