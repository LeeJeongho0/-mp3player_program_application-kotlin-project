package com.example.mp3playerproject

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3playerproject.databinding.ListRecyclerBinding
import java.text.SimpleDateFormat

class LikeRecyclerAdapter(val context: Context, val likeListFragment:LikeListFragment ,val musicList:MutableList<MusicData>?):RecyclerView.Adapter<LikeRecyclerAdapter.CustomViewHolder>() {
    val ALBUM_IMAGE_SIZE = 90

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ListRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = musicList?.size?:0

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        // 이미지, artist, title, duration binding
        val bitmap = musicList?.get(position)?.getAlbumBitmap(context, ALBUM_IMAGE_SIZE)
        if(bitmap != null){
            binding.ivAlbumArt.setImageBitmap(bitmap)
        }else{
            binding.ivAlbumArt.setImageResource(R.drawable.music_video_24)
        }
        binding.tvArtist.text = musicList?.get(position)?.artist
        binding.tvTitle.text = musicList?.get(position)?.title
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(musicList?.get(position)?.duration)
        when(musicList?.get(position)?.likes){
            0 -> binding.ivItemLike.setImageResource(R.drawable.favorite_unlike_24)
            1 -> binding.ivItemLike.setImageResource(R.drawable.favorite_like_24)
        }
        // 아이템항목 클릭 시 PlayActivity MusicData 전달
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context,PlayActivity::class.java)
            val parcelableList:ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            intent.putExtra("parcelableList", parcelableList)
            intent.putExtra("position", position)
            binding.root.context.startActivity(intent)
        }
        binding.ivItemLike.setOnClickListener {
            when(musicList?.get(position)?.likes){
                0 -> {
                    musicList?.get(position)?.likes = 1
                    binding.ivItemLike.setImageResource(R.drawable.favorite_like_24)
                }
                1 -> {
                    musicList?.get(position)?.likes = 0
                    binding.ivItemLike.setImageResource(R.drawable.favorite_unlike_24)
                }
            }
            val db = DBOpenHelper(context, MainActivity.DB_NAME, MainActivity.VERSION)
            var errorFlag = db.updateLike(musicList!!.get(position))
            if(errorFlag){
                Toast.makeText(context,"updateLike 실패", Toast.LENGTH_SHORT).show()
            }else{
                this.notifyDataSetChanged()
                likeListFragment.refreshItem()
            }
        }
    }
    inner class CustomViewHolder(val binding: ListRecyclerBinding): RecyclerView.ViewHolder(binding.root)
}