package com.example.mp3playerproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mp3playerproject.databinding.FragmentLikeListBinding

class LikeListFragment : Fragment() {
    lateinit var binding:FragmentLikeListBinding
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        refreshItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeListBinding.inflate(layoutInflater)
        refreshItem()
        return binding.root
    }

    fun refreshItem() {
        val dbOpenHelper = DBOpenHelper(mainActivity, MainActivity.DB_NAME, MainActivity.VERSION)
        var musicList = dbOpenHelper.selectMusicLike()
        binding.recyclerView.layoutManager = LinearLayoutManager(mainActivity)
        binding.recyclerView.adapter = LikeRecyclerAdapter(mainActivity,this, musicList)
    }
}