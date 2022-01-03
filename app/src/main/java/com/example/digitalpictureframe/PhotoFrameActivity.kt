package com.example.digitalpictureframe

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity: AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private var currentPosition = 0 // 현재 보여지고 있는 사진의 position값을 저장하는 변수

    private var timer : Timer? = null

    private val photoImageView : ImageView by lazy{
        findViewById(R.id.photoImageView)
    }

    private val backgroundPhotoImageView : ImageView by lazy{
        findViewById(R.id.backgroundPhotoImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        Log.d("photoFrame", "onCreate")

        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent(){ // intent를 통해 넘겨진 String을 Uri로 변환하여 리스트에 저장하는 함수
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size){
            intent.getStringExtra("photo$i")?.let{
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer(){
        timer = timer(period = 5000){
            runOnUiThread{ // timer 블록 내부는 main Thread에서 동작하는 것이 아니기 때문에 Ui 조작을 위해서는 runOnUiThread() 함수를 이용해야함

                Log.d("photoFrame", "사진 변경")

                val current = currentPosition
                val next = if(photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundPhotoImageView.setImageURI(photoList[current])

                photoImageView.alpha = 0f // alpha는 투명도를 의미
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate() // View에 애니메이션 효과를 주기 위한 함수
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    override fun onStop() {
        super.onStop()

        Log.d("photoFrame", "onStop")

        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()

        Log.d("photoFrame", "onStart")

        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("photoFrame", "onDestroy")

        timer?.cancel()
    }

}