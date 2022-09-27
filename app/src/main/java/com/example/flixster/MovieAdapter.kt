package com.example.flixster

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Pair.create
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.core.util.Pair
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


const val MOVIE_EXTRA = "MOVIE_EXTRA"

class MovieAdapter(
    private val context: Context,
    private val movies: List<Movie>,
    private var orientation: Int,
    private val activity: Activity
) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie, orientation)
    }

    override fun getItemCount() = movies.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val ivPoster = itemView.findViewById<ImageView>(R.id.ivPoster)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvOverview = itemView.findViewById<TextView>(R.id.tvOverview)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(movie: Movie, orientation: Int) {
            tvTitle.text = movie.title
            tvOverview.text = movie.overview

            val radius = 30
            val margin = 10

            Glide.with(context).load(
                if (orientation == Configuration.ORIENTATION_PORTRAIT) movie.posterImageUrl else movie.backdropImageUrl
            ).placeholder(R.drawable.placeholder_small
            ).transform(RoundedCorners(radius),
            ).into(ivPoster) // this is where i should detect orientation

        }

        override fun onClick(v: View) {
            val movie = movies[adapterPosition]
//            Toast.makeText(context, movie.title, Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DetailActivity::class.java)

            val p1: Pair<View, String> = Pair.create(ivPoster as View, "profile")
            val p2: Pair<View, String> = Pair.create(tvOverview as View, "overview")
            val p3: Pair<View, String> = Pair.create(tvTitle as View, "title")

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                p1,p2,p3
            )

            intent.putExtra(MOVIE_EXTRA, movie)
            context.startActivity(intent, options.toBundle())

        }
    }
}
