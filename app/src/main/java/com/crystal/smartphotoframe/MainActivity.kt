package com.crystal.smartphotoframe

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val addPhotoButton: Button by lazy{
        findViewById(R.id.addPhotoButton)
    }
    private val startPhotoFrameModeButton:Button by lazy{
        findViewById(R.id.startPhotoFrameModeButton)
    }
    private val imageViewList: List<ImageView> by lazy{
        mutableListOf<ImageView>().apply{
            add(findViewById(R.id.imageView1_1))
            add(findViewById(R.id.imageView1_2))
            add(findViewById(R.id.imageView1_3))
            add(findViewById(R.id.imageView2_1))
            add(findViewById(R.id.imageView2_2))
            add(findViewById(R.id.imageView2_3))
        }
    }
    private val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init add photo, 권한 받아오기
        initAddPhotoButton()
        initStringPhotoFrameModeButton()

    }

    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            when{
                //권한이 부여되어있는지 확인
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //권한이 잘 부여되었을 때.
                    //갤러리에서 사진을 선택하는 기능
                    navigatePhotos()
                }

                //교육용 팝업 띄우고, 결과에 따른 권한 팝업 띄움.
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 권한 팝업을 띄우는 기능
                    showPermissionContextPopup()

                } else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                    //callback-> onRequestPermissionResult
                }
            }
        }
    }

    private fun initStringPhotoFrameModeButton() {
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed{index, uri ->
                intent.putExtra("photo${index}", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }
    }

    //requestPermissions 의 콜백
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //권한이 부여됨.
                    Toast.makeText(this, "권한이 부여됨.", Toast.LENGTH_SHORT).show()
                    navigatePhotos()
                }else{
                    //권한이 부여되지 않음.
                    Toast.makeText(this, "권한이 부여되지 않았습니다.", Toast.LENGTH_LONG).show()
                }
            }else -> {
                //requestCode 다른 것 아직 없음.
            }
        }
    }

    private fun navigatePhotos() {
        //SAF 사용자 친화적이면서 구현도 간단
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( resultCode != Activity.RESULT_OK) {
            return
        }
        when(requestCode){
            2000->{
                val selectedImageURI:Uri? = data?.data
                if(selectedImageURI != null){
                    if(imageUriList.size == imageViewList.size){
                        Toast.makeText(this, "사진이 꽉 찼습니다.", Toast.LENGTH_LONG).show()
                    }

                    imageUriList.add(selectedImageURI)
                    imageViewList[imageUriList.size-1].setImageURI(selectedImageURI)
                }else{
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
                }

            }else ->{
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                //callback-> onRequestPermissionResult
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

}