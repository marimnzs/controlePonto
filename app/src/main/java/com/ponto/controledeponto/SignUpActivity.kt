package com.ponto.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()
            val name: String = binding.etName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                createUserWithEmailAndPassword(email, password, name)

            } else {
                Toast.makeText(this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmailAndPassword:Success")
                val user = auth.currentUser
                print({auth.uid})
                user?.let {
                    val uid = user.uid
                    createUserDocument(uid, name)
                }
                // Navegar para LoginActivity após sucesso na criação da conta
                val navegarSignInActivity = Intent(this, LoginActivity::class.java)
                startActivity(navegarSignInActivity)
                finish() // Opcional: fechar a atividade atual
            } else {
                Log.w(TAG, "createUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Authentication Failure: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Log.e(TAG, "createUserWithEmailAndPassword:Error", it)
            Toast.makeText(baseContext, "Authentication Error: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createUserDocument(uid: String, name: String) {
        val user = hashMapOf(
            "name" to name,
            "uid" to uid
        )
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User document successfully written!")
                Toast.makeText(baseContext, "User document successfully written!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing user document", e)
                Toast.makeText(baseContext, "Error writing document: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }
}
