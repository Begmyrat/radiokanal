package com.example.radiokanal

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.radiokanal.adapter.PagerAdapter
import com.example.radiokanal.databinding.ActivityMainBinding
import com.example.radiokanal.fragments.FragmentRadio
import com.example.radiokanal.fragments.FragmentTv
import com.example.radiokanal.service.MediaCaptureService
import com.google.android.exoplayer2.offline.DownloadService.startForeground
import com.google.android.exoplayer2.util.NotificationUtil.createNotificationChannel


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var isRecording = false
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PagerAdapter(this, null, binding.viewPager)
            .apply {
                addFragment(FragmentRadio(), "Radio")
                addFragment(FragmentTv(), "TV")
            }.attach()

        binding.navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.radio -> {
                    binding.viewPager.currentItem = 0
                }
                R.id.tv -> {
                    binding.viewPager.currentItem = 1
                }
            }
            true
        }


        binding.fab.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab ->{
                Toast.makeText(applicationContext, "fab is clicked", Toast.LENGTH_LONG).show()
                clickRecordButton()
            }
        }
    }

    private fun clickRecordButton(){
//        if(checkPermissions()){
            if(!isRecording){
                isRecording = true
                startCapturing()
            }else{
                isRecording = false
                stopCapturing()
            }
//        }
    }

    private fun stopCapturing() {
        Toast.makeText(applicationContext, "stop recording", Toast.LENGTH_LONG).show()
        ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, MediaCaptureService::class.java).apply {
            action = MediaCaptureService.ACTION_STOP
        })
    }

    private fun startCapturing() {
        Toast.makeText(applicationContext, "start recording", Toast.LENGTH_LONG).show()
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission()
        } else {
            startMediaProjectionRequest()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 2
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            //3
            ActivityCompat.requestPermissions(this, permissions, 0)

            return false;
        }
        return true
    }

    private fun startMediaProjectionRequest() {
        // 1
        mediaProjectionManager = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        // 2
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), MEDIA_PROJECTION_REQUEST_CODE)

    }

//    private val MEDIA_PROJECTION_REQUEST_CODE = 13

    companion object {
        private const val MEDIA_PROJECTION_REQUEST_CODE = 13
    }

    private fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    "Permissions to capture audio granted.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext, "Permissions to capture audio denied.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 1
        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {

            // 2
            if (resultCode == Activity.RESULT_OK) {

                // 3
                Toast.makeText(
                    applicationContext,
                    "MediaProjection permission obtained. Foreground service will start to capture audio.",
                    Toast.LENGTH_SHORT
                ).show()

                val audioCaptureIntent = Intent(applicationContext, MediaCaptureService::class.java).apply {
                    action = MediaCaptureService.ACTION_START
                    putExtra(MediaCaptureService.EXTRA_RESULT_DATA, data!!)
                }
                ContextCompat.startForegroundService(applicationContext, audioCaptureIntent)
            }

        } else {

            // 4
            Toast.makeText(
                applicationContext, "Request to get MediaProjection denied.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

