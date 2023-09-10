package android.tvz.hr.finalproject.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.tvz.hr.finalproject.view.LoginActivity
import android.tvz.hr.finalproject.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class PreloadScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_preload_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

            if(currentUser == null) {
                startActivity(Intent(this@PreloadScreenActivity, LoginActivity::class.java))
            }
            else {
                startActivity(Intent(this@PreloadScreenActivity, MainActivity::class.java))
            }

            finish()
        }, 1000)
    }
}