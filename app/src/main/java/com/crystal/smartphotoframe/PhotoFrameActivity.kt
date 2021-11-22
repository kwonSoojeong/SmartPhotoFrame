package com.crystal.smartphotoframe

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity: AppCompatActivity() {

    private var currentPosition = 0
    private val photoList = mutableListOf<Uri>()
    private val backgroundPhotoImageView : ImageView by lazy{
        findViewById(R.id.backgroundPhotoImageView)
    }
    private val photoImageView: ImageView by lazy{
        findViewById(R.id.photoImageView)
    }
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        val size = intent.getIntExtra("photoListSize", 0)
        for(i in 0..size){
            intent.getStringExtra("photo$i")?.let{
                //null이 아닐때만 수행
                photoList.add(Uri.parse(it))
            }
        }
//        startTimer()
        //OnCreate에서 TImer를 실행하면  onStop()이여도 timer가 동작하기떄문에 주기에 맞게 관리해줘야한다
            // onStart(), onStop() 참고
    }

    private fun startTimer(){
        timer = timer(period = 5 * 1000){
            runOnUiThread {
                Log.d("PhotoFrame", "run timer, +5s")
                val current = currentPosition
                //마지막 사진일경우 1번으로 돌아와야한다.
                val next = if(photoList.size <= currentPosition + 1) 0 else currentPosition + 1
                backgroundPhotoImageView.setImageURI(photoList[current])
                photoImageView.alpha = 0f //완전 투명, 보이지 않음.
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(100)
                    .start()

                currentPosition = next;
            }
        }
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
        Log.d("PhotoFrame", "OnStop, timer cancel")
    }

    override fun onStart() {
        super.onStart()
        startTimer()
        Log.d("PhotoFrame", "OnStart, timer Start")
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        Log.d("PhotoFrame", "OnDestroy, timer destroy")
    }
}