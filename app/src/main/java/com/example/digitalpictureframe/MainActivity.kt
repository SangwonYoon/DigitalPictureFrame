package com.example.digitalpictureframe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoButton : Button by lazy{
        findViewById(R.id.addPhotoButton)
    }

    private val startPhotoFrameModeButton : Button by lazy{
        findViewById(R.id.startPhotoFrameModeButton)
    }

    private val imageViewList: List<ImageView> by lazy{
        mutableListOf<ImageView>().apply{
            add(findViewById(R.id.ImageView11))
            add(findViewById(R.id.ImageView12))
            add(findViewById(R.id.ImageView13))
            add(findViewById(R.id.ImageView21))
            add(findViewById(R.id.ImageView22))
            add(findViewById(R.id.ImageView23))
        }
    }

    private val imageUriList : MutableList<Uri> = mutableListOf() // 갤러리에서 선택된 사진의 Uri를 저장할 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }

    private fun initAddPhotoButton(){
        addPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when{ // when문에 매개변수를 주지 않으면 if-else과 같이 사용할 수 있다.
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                        navigatePhotos() // 권한이 잘 부여되었을 때 갤러리에서 사진을 선택하는 기능
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> { // 사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환 / 사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환
                        showPermissionContextPopup()
                    }
                    else -> {
                        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000) // 매개변수로 주어진 배열의 권한들을 요청
                    }
                }
            }
        }
    }

    private fun initStartPhotoFrameModeButton(){
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this@MainActivity, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri -> // 리스트에 들어있는 데이터를 인덱스값과 함께 꺼냄
                intent.putExtra("photo$index", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size) // 사진을 몇개 꺼내야하는지 알려주기 위함
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult( // 권한을 요청했을 때 이용자가 어떤 선택을 했는지 확인하는 함수
        requestCode: Int, // 어떤 권한의 요청에 대한 request인지 확인할 수 있게 해주는 key값, 이 어플에서는 1000의 값만 가질것이다.
        permissions: Array<out String>,
        grantResults: IntArray // 요청이 거절되었으면 아무런 데이터를 갖지 않음, 요청을 수락하였을 경우 grantResults[0]의 값은 PackageManager.PERMISSION_GRANTED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1000 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // TODO 권한이 부여된 것입니다.
                    navigatePhotos()
                } else{
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                //
            }
        }
    }

    private fun navigatePhotos(){
        val intent = Intent(Intent.ACTION_GET_CONTENT) // 앨범을 호출하는 인텐트
        intent.type = "image/*" // intent에서 return받고자 하는 데이터의 형식을 image로 지정
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK){ // 정상적으로 사진이 선택되지 않은 경우
            return
        }

        when(requestCode){
            2000 -> {
                val selectedImageUri : Uri? = data?.data // Uri란? 인터넷 자원을 나타내는 고유 식별자(identifier)
                if(selectedImageUri != null){

                    if(imageUriList.size == 6){
                        Toast.makeText(this, "사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri) // ImageView에 선택된 사진을 set

                } else{
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup(){ // 교육용 팝업 확인 후 권한 팝업을 띄우는 함수
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    ) // 매개변수로 주어진 배열의 권한들을 요청
                }
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }
}