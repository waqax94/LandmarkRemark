package com.waqas.landmarkremark.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waqas.landmarkremark.R
import com.waqas.landmarkremark.databinding.ActivitySplashBinding
import com.waqas.landmarkremark.infra.util.AppConstants
import com.waqas.landmarkremark.infra.util.SharedPrefs
import com.waqas.landmarkremark.presentation.home.HomeActivity
import com.waqas.landmarkremark.presentation.usersetup.UserSetupActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.TypeVariable

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presentNextActivity()
    }
    private fun goToNextActivity(){
        val prefs = SharedPrefs(applicationContext)
        if(prefs.get(AppConstants.USERNAME, String::class.java).isNotEmpty()){
            startActivity(Intent(this, HomeActivity::class.java))
        }
        else{
            startActivity(Intent(this, UserSetupActivity::class.java))
        }
        finish()
    }

    private fun presentNextActivity(){
        GlobalScope.launch(context = Dispatchers.Main) {
            delay(1000)
            goToNextActivity()
        }
    }
}