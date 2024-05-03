package com.ponto.controledeponto

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnEditHours.setOnClickListener {
            val navegarEditHours = Intent(this, CheckInActivity::class.java)

            startActivity(navegarEditHours)
        }

        binding.btnCheckIn.setOnClickListener {
            if (auth.uid != null) {
                val currentTime = Calendar.getInstance().time
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val currentDateTimeString = sdf.format(currentTime)

                Log.d("Auth", "UID do usuário: ${auth.uid}")

                val horarioMap = hashMapOf(
                    "ponto" to currentDateTimeString,
                )
                db.collection("users").document(auth.uid!!)
                    .collection("horarios")
                    .add(horarioMap)
                    .addOnSuccessListener{
                        Toast.makeText(baseContext, "Ponto registrado", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(baseContext, "Nao foi possivel registrar ponto", Toast.LENGTH_LONG).show()
                    }
            } else {
                Log.e("Auth", "UID do usuário é nulo")
                // Lidar com o caso em que o UID do usuário é nulo
            }
        }

    }
}