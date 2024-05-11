package com.ponto.controledeponto

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Inicializa o Fused Location Provider Client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Chama a função para obter a localização do usuário
        getLocation()

        initRecyclerView()

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
            }
        }

    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        getList() // Chama a função para obter os dados do Firestore
    }

    private fun displayDataOnRecyclerView(list: MutableList<String>) {
        binding.recyclerView.adapter = Adapter(list)
    }

    private fun getList() {
        val list = mutableListOf<String>()

        val userCollection = db.collection("users").document(auth.uid!!).collection("horarios")

        userCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val ponto = document.getString("ponto")
                    if (ponto != null) {
                        list.add(ponto)
                    }
                }

                displayDataOnRecyclerView(list)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                Toast.makeText(baseContext, "Não foi possível carregar os dados", Toast.LENGTH_LONG).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLocation() {
        // Verifica se a permissão de localização está concedida
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permissão de localização concedida, solicita a localização do usuário
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    // Localização do usuário obtida com sucesso
                    if (location != null) {
                        // Use a localização aqui
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                    } else {
                        // Localização do usuário não disponível
                        Log.e("Location", "Localização do usuário não disponível")
                        Toast.makeText(this, "Localização do usuário não disponível", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Permissão de localização não concedida, solicita a permissão
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão de localização concedida, chama a função para obter a localização do usuário
                getLocation()
            } else {
                // Permissão de localização negada, exibe uma mensagem de erro
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}