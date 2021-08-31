package com.waqas.landmarkremark.presentation.usersetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waqas.landmarkremark.R
import com.waqas.landmarkremark.databinding.ActivityUserSetupBinding
import com.waqas.landmarkremark.infra.util.AppConstants
import com.waqas.landmarkremark.infra.util.SharedPrefs
import com.waqas.landmarkremark.presentation.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        login()
    }

    private fun login() {
        binding.signInButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            if(validateUsername(username)){
                setUsernameError(null)
                val prefs = SharedPrefs(applicationContext)
                prefs.put(AppConstants.USERNAME,username)
                goToHomeActivity()
            }
        }
    }

    private fun setUsernameError(e: String?){
        binding.usernameInput.error = e
    }

    private fun validateUsername(username: String): Boolean{
        if(username.isEmpty()){
            setUsernameError(getString(R.string.username_error))
            return false
        }
        return true
    }

    private fun goToHomeActivity(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}