package com.ponto.controledeponto
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.ponto.controledeponto.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        val user = FirebaseAuth.getInstance().currentUser


        binding.btnLogin.setOnClickListener{
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()


            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                Log.d(TAG, "createUserWithEmailAndPassword:Sucess")
                val user = auth.currentUser
            } else {
                Log.w(TAG, "createUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Authentication Failure", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        val navegarHome = Intent(this, HomeActivity::class.java )
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if (task.isSuccessful){
                Log.d(TAG, "signInUserWithEmailAndPassword:Sucess")
                Toast.makeText(baseContext, "Bem vindo de volta!", Toast.LENGTH_LONG).show()
                startActivity(navegarHome)
//                val user = auth.currentUser
            } else {
                Log.w(TAG, "signInUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Usuário incorreto", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }



}