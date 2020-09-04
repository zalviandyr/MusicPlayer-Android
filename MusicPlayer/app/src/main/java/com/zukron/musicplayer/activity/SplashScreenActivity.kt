package com.zukron.musicplayer.activity

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle

import com.zukron.musicplayer.R
import com.zukron.musicplayer.exo.ExoPlayerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        CoroutineScope(Main).launch {
            delay(3000)
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}