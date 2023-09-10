package android.tvz.hr.finalproject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.tvz.hr.finalproject.view.LoginActivity
import android.tvz.hr.finalproject.R
import android.tvz.hr.finalproject.databinding.ActivityCreateAccountBinding
import android.util.Patterns
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_create_account)

        binding = ActivityCreateAccountBinding.bind(findViewById(R.id.root))

        binding.registerBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            val isValidated : Boolean = validateData(email, password, confirmPassword)

            if(!isValidated) {
                return@setOnClickListener
            }

            registerInFirebase(email, password)

        }
    }

    fun returnToLogin(view : android.view.View) {
        startActivity(Intent(this@CreateAccountActivity, LoginActivity::class.java))
    }

    fun registerInFirebase(email: String, password: String) {

        val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@CreateAccountActivity) { task ->
            if (task.isSuccessful) {
                Snackbar.make(findViewById(R.id.root), resources.getString(R.string.succ_reg), Snackbar.LENGTH_SHORT).show()
                firebaseAuth.currentUser?.sendEmailVerification()
                firebaseAuth.signOut()
                finish()
            } else {
                Snackbar.make(findViewById(R.id.root), task.exception?.localizedMessage.toString(), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun validateData(email : String, password : String, confirmPassword : String) : Boolean {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = resources.getString(R.string.email_invalid)
            return false
        }
        if(password.length < 6) {
            binding.passwordEditText.error = resources.getString(R.string.passwordEditText)
            return false
        }
        if(confirmPassword != password) {
            binding.confirmPasswordEditText.error = resources.getString(R.string.confirmPasswordEditText)
            return false
        }

        return true
    }
}