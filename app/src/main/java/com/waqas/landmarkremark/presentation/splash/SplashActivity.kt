package com.waqas.landmarkremark.presentation.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waqas.landmarkremark.R
import com.waqas.landmarkremark.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}