package android.tvz.hr.finalproject.view

import android.content.Intent
import android.os.Bundle
import android.tvz.hr.finalproject.R
import android.tvz.hr.finalproject.databinding.ActivityLoginBinding
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

lateinit var binding: ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.bind(findViewById(R.id.root))

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val isValidated : Boolean = validateData(email, password)

            if(!isValidated) {
                return@setOnClickListener
            }

            loginInFirebase(email, password)
        }
    }

    fun returnToRegister(view: android.view.View) {
        startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
    }

    fun loginInFirebase(email: String, password: String) {

        val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            if(task.isSuccessful) {
                if(firebaseAuth.currentUser?.isEmailVerified == true) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                else {
                    Snackbar.make(
                        findViewById(R.id.root),
                        resources.getString(R.string.email_ver),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                Snackbar.make(
                    findViewById(R.id.root),
                    task.exception?.localizedMessage.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }


    fun validateData(email : String, password : String) : Boolean {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = resources.getString(R.string.email_invalid)
            return false
        }
        if(password.length < 6) {
            binding.passwordEditText.error = resources.getString(R.string.passwordEditText)
            return false
        }

        return true
    }
}