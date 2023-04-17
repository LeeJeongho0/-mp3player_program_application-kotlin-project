package com.example.mp3playerproject

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.mp3playerproject.databinding.ActivityPlayBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding:ActivityPlayBinding
    val ALBUM_IMAGE_SIZE = 90
    var mediaPlayer: MediaPlayer? = null
    lateinit var musicData: MusicData
    private var playList: MutableList<Parcelable>? = null
    private var currentPosition: Int = 0
    var mp3playerJob: Job? = null
    var pauseFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playList = intent.getParcelableArrayListExtra("parcelableList")
        currentPosition = intent.getIntExtra("position", 0)
        musicData = playList?.get(currentPosition) as MusicData
        dataSet(musicData)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnList -> {
                mp3playerJob?.cancel()
                mediaPlayer?.stop()
                finish()
            }
            R.id.btnPlay -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.pause()
                    binding.btnPlay.setImageResource(R.drawable.play)
                    pauseFlag = true
                } else {
                    playMusic()
                }
            }
            R.id.btnNext ->{
                mediaPlayer?.stop()
                mp3playerJob?.cancel()
                binding.seekBar.progress = 0
                binding.playDuration.text = "00:00"

                val nextPosition = currentPosition + 1
                if(nextPosition == playList?.size!!){
                    currentPosition = 0
                    musicData = playList?.get(currentPosition) as MusicData
                }else{
                    currentPosition = nextPosition
                    musicData = playList?.get(currentPosition) as MusicData
                }
                dataSet(musicData)
                mediaPlayer?.start()
                binding.btnPlay.setImageResource(R.drawable.pause_button)

                // 코루틴으로 음악을 재생
                val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                mp3playerJob = backgroundScope.launch {
                    while (mediaPlayer!!.isPlaying) {
                        var currentPosition = mediaPlayer?.currentPosition!!
                        // 코루틴속에서 화면의 값을 변동시키고자 할 때 runOnUiThread 사용
                        var strCurrentPosition = SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                        runOnUiThread {
                            binding.seekBar.progress = currentPosition
                            binding.playDuration.text = strCurrentPosition
                        }
                        try {
                            delay(1000)
                            binding.seekBar.incrementProgressBy(1000)
                        } catch (e: java.lang.Exception) {
                            Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                        }
                    }

                }
            }
            R.id.btnPrevious -> {
                mediaPlayer?.stop()
                mp3playerJob?.cancel()
                binding.seekBar.progress = 0
                binding.playDuration.text = "00:00"

                val previousPosition = currentPosition - 1
                if(previousPosition < 0){
                    currentPosition = playList!!.size - 1
                    musicData = playList?.get(currentPosition) as MusicData
                }else{
                    currentPosition = previousPosition
                    musicData = playList?.get(currentPosition) as MusicData
                }
                dataSet(musicData)
                mediaPlayer?.start()
                binding.btnPlay.setImageResource(R.drawable.pause_button)


                // 코루틴으로 음악을 재생
                val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                mp3playerJob = backgroundScope.launch {
                    while (mediaPlayer!!.isPlaying) {
                        var currentPosition = mediaPlayer?.currentPosition!!
                        // 코루틴속에서 화면의 값을 변동시키고자 할 때 runOnUiThread 사용
                        var strCurrentPosition = SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                        runOnUiThread {
                            binding.seekBar.progress = currentPosition
                            binding.playDuration.text = strCurrentPosition
                        }
                        try {
                            delay(1000)
                            binding.seekBar.incrementProgressBy(1000)
                        } catch (e: java.lang.Exception) {
                            Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                        }
                    }
                }
            }
        }
    }

    fun dataSet(musicData: MusicData){
        binding.albumTitle.text = musicData.title
        binding.albumArtist.text = musicData.artist
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(musicData.duration)
        binding.playDuration.text = "00:00"
        val bitmap = musicData.getAlbumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.music_video_24)
        }
        // 음악 파일객체 가져옴
        mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri())
        // 이벤트 처리(일시정지, 실행, 돌아가기, 정지, 시크바 조절)
        binding.btnList.setOnClickListener(this)
        binding.btnPlay.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.btnPrevious.setOnClickListener(this)
        binding.seekBar.max = mediaPlayer!!.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun playMusic(){
        mediaPlayer?.start()
        binding.btnPlay.setImageResource(R.drawable.pause_button)
        pauseFlag = false

        // 코루틴으로 음악을 재생
        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        mp3playerJob = backgroundScope.launch {
            while (mediaPlayer!!.isPlaying) {
                var currentPosition = mediaPlayer?.currentPosition!!
                // 코루틴속에서 화면의 값을 변동시키고자 할 때 runOnUiThread 사용
                var strCurrentPosition = SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                runOnUiThread {
                    binding.seekBar.progress = currentPosition
                    binding.playDuration.text = strCurrentPosition
                }
                try {
                    delay(1000)
                    binding.seekBar.incrementProgressBy(1000)
                } catch (e: java.lang.Exception) {
                    Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                }
            }
            if (pauseFlag == false) {
                runOnUiThread {
                    binding.seekBar.progress = 0
                    binding.btnPlay.setImageResource(R.drawable.play)
                    binding.playDuration.text = "00:00"
                }
            }
        }
    }
}