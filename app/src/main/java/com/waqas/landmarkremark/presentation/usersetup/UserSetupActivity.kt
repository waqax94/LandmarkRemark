package com.waqas.landmarkremark.presentation.usersetup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waqas.landmarkremark.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setup)
    }
}