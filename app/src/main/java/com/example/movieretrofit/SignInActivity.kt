package com.example.movieretrofit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.movieretrofit.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val isLoggedIn = getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
            .getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            startMainActivity()
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            auth = Firebase.auth
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    fbAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api Exeption")
            }
        }
        binding.bSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val sigInClient = getClient()
        launcher.launch(sigInClient.signInIntent)
    }

    private fun fbAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("MyLog", "Google sign in done")
                // После успешной авторизации сохраняем информацию в SharedPreferences
                getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("IS_LOGGED_IN", true)
                    .apply()

                startMainActivity()
            } else {
                Log.d("MyLog", "Google sign in error")
            }
        }
    }

    private fun startMainActivity() {
        //if (auth.currentUser != null){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}