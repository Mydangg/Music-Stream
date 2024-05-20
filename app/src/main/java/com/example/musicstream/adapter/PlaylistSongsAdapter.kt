package com.example.musicstream.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.MyExoplayer
import com.example.musicstream.PlayerActivity
import com.example.musicstream.databinding.SongListItemRecyclerRowBinding
import com.example.musicstream.models.SongModel

class PlaylistSongsAdapter(private val songs: List<SongModel>) : RecyclerView.Adapter<PlaylistSongsAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: SongListItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongModel) {
            binding.songTitleTextView.text = song.title
            binding.songSubtitleTextView.text = song.subtitle
            Glide.with(binding.songCoverImageView).load(song.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32)) // bo tro goc anh
                )
                .into(binding.songCoverImageView)
            binding.root.setOnClickListener {
                MyExoplayer.startPlaying(binding.root.context,song)
                it.context.startActivity(Intent(it.context, PlayerActivity::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }
}