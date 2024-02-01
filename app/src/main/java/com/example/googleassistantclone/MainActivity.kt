package com.example.googleassistantclone

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googleassistantclone.UI_Utils.Utils
import com.example.googleassistantclone.assistant.AssistantActivity
import com.example.googleassistantclone.assistant.ExploreActivity
import com.example.googleassistantclone.functions.GoogleLensActivity

class MainActivity : AppCompatActivity() {

    lateinit var hiGoogle: ImageView
    lateinit var explore: ImageView
    lateinit var googleLens: ImageView
    lateinit var googleAssistant: ImageView
    private val REQUEST_PERMISSION_CODE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.setCustomActionBar(supportActionBar,this)
        hiGoogle = findViewById(R.id.hi_google)
        explore = findViewById(R.id.explore)
        googleLens = findViewById(R.id.google_lens)
        googleAssistant = findViewById(R.id.google_assistant)

        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)!=PERMISSION_GRANTED){
            checkPermission()
        }

        hiGoogle.setOnClickListener {
            startActivity(Intent(this,AssistantActivity::class.java))
        }

        explore.setOnClickListener {
            startActivity(Intent(this,ExploreActivity::class.java))
        }

        googleLens.setOnClickListener {
            startActivity(Intent(this,GoogleLensActivity::class.java))
        }

        googleAssistant.setOnClickListener {
            startActivity(Intent(this,AssistantActivity::class.java))
        }

    }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSION_CODE && grantResults.isNotEmpty()){
            if(grantResults[0] == PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT)
            }
        }
    }

}