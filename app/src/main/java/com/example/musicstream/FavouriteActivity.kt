package com.example.musicstream

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.FavouriteAdapter
import com.example.musicstream.databinding.ActivityFavouriteBinding
import com.example.musicstream.models.SongModel
import com.example.musicstream.models.favourite
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var list : ArrayList<favourite>
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this@FavouriteActivity, MainActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@FavouriteActivity, PlaylistActivity::class.java))
        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@FavouriteActivity, FavouriteActivity::class.java))
        }
//        binding.searchBtn.setOnClickListener {
//            startActivity(Intent(this@FavouriteActivity, SearchActivity::class.java))
//        }

        pushDataFavourite()

    }

    private fun pushDataFavourite() {
        var userId = getUserId()
        FirebaseFirestore.getInstance().collection("favourite")
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener {
                val like = it.toObjects(favourite::class.java)
                setupCategoryRecyclerView(like)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "That bai", Toast.LENGTH_SHORT).show()
            }
    }

    fun setupCategoryRecyclerView(listLike: List<favourite>){
        favouriteAdapter = FavouriteAdapter(listLike)
        binding.playlistRV.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        binding.playlistRV.adapter = favouriteAdapter
    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }

    fun getUserId(): String?{
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }

    private fun addToPlaylist(id : String, playlistId: String ,playlistName:String) {
        if (playlistId.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("playlists").document(playlistId)
                .update("songs", FieldValue.arrayUnion(id))
                .addOnSuccessListener {
                    // Song added to playlist
                    Log.d("AddToPlaylist", "Song added to playlist: $id")
                    val intent = Intent(binding.root.context, PlaylistDetails::class.java)
                    intent.putExtra("playlistId", playlistId)
                    intent.putExtra("playlistName", playlistName)
                    binding.root.context.startActivity(intent)
                }
                .addOnFailureListener { e ->
                    // Log the exception or show some feedback to the user
                    Log.e("AddToPlaylist", "Error adding song to playlist", e)
                }
        } else {
            Log.e("AddToPlaylist", "Playlist ID is empty")
        }
    }

    fun showPlayerView(){
        binding.playerView.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        MyExoplayer.getCurrentSong()?.let {
            binding.playerView.visibility = View.VISIBLE
            binding.songTitleTextView.text = it.title
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32)) // bo tro goc anh
                ).into(binding.songCoverImageView)
        } ?: run{
            binding.playerView.visibility = View.GONE
        }
    }
}