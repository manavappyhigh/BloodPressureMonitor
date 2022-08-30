package com.theappschef.bloodpressuremonitor.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.theappschef.bloodpressuremonitor.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Thread.sleep(2000)

        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }else {
            startActivity(Intent(this@SplashActivity, FirebaseAuthenticationActivity::class.java))
        }
    }
}