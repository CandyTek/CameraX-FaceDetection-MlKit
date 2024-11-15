package com.androchef.cameraxfacedetection

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androchef.cameraxfacedetection.camerax.CameraManager
import com.androchef.cameraxfacedetection.databinding.ActivityMainBinding
import com.androchef.cameraxfacedetection.face_detection.FaceStatus
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        createCameraManager()
        checkForPermission()
        onClicks()
        cameraManager.graphicOverlay.toggleSelector()

//        if (!tools.checkPermission(this, NEEDED_PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, 999)
//        }

        binding.btnRandomChooseHuman.setOnClickListener {
            val r = Random()
            val i: Int = r.nextInt(cameraManager.graphicOverlay.graphics.size)
            
//            msgbox(i.toString())
            // 获取 TextureView 的 bitmap
            val bitmap2 = binding.previewViewFinder.bitmap
            // 获取 TextureView 的变换矩阵
//            val matrix = cameraManager.graphicOverlay.graphics.get(i).calculateRect()
//            val matrix = cameraManager.graphicOverlay.face
            
            // 对 Bitmap 应用变换矩阵
            var bitmap3: Bitmap? = null
            bitmap2?.let {
//                bitmap3 = Bitmap.createBitmap(it, 0, 0, it.getWidth(), it.getHeight(), matrix, true)
            }
            binding.ivRandom.setImageBitmap(bitmap3)

            try {
                bitmap2?.recycle()
            } catch (e: Exception) {
            }

        }
    }
    
    fun msgbox(msg:String){
        AlertDialog.Builder(this)
            .setTitle("消息")
            .setMessage(msg)
            .setPositiveButton(getString(android.R.string.yes), null)
            .show()

    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS,
            )
        }
    }

    private fun onClicks() {
        binding.btnSwitch.setOnClickListener {
            cameraManager.changeCameraSelector()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            this,
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder,
            ::processPicture
        )
    }
    private fun processPicture(faceStatus: FaceStatus) {
        runOnUiThread {
            binding.tvCount.setText((cameraManager.graphicOverlay.graphics.size).toString())
        }
//        Log.e("facestatus","This is it ${faceStatus.name}")
//       when(faceStatus){}
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}
