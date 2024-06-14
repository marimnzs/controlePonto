package com.ponto.controledeponto

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val requestLocationPermissionCode = 1
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    val pontoDeReferencia = LatLng(-22.8340787, -47.0552235) //coordenada na puc

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa o Firebase
        FirebaseApp.initializeApp(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Inicializa o Fused Location Provider Client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Configuração do pedido de localização
        locationRequest = LocationRequest.create().apply {
            interval = 1000000 // Intervalo de atualização de localização em milissegundos
            fastestInterval = 5000 // Intervalo mais rápido para atualização de localização
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Alta precisão
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    // Localização do usuário obtida com sucesso
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                    Toast.makeText(this@HomeActivity, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Log.e("Location", "Localização do usuário não disponível")
                    Toast.makeText(this@HomeActivity, "Localização do usuário não disponível", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Chama a função para obter a localização do usuário
        getLocation()

        initRecyclerView()

        binding.btnEditHours.setOnClickListener {
            val navegarEditHours = Intent(this, CheckInActivity::class.java)
            startActivity(navegarEditHours)
        }


        binding.btnCheckIn.setOnClickListener {
            if (auth.uid != null) {
                // Verifica se a permissão de localização está concedida
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                // Localização do usuário obtida com sucesso
                                val latitude = -22.8340787
                                val longitude = -47.0552235
                                Log.d("Location", "Latitude: $latitude, Longitude: $longitude")

                                // Verifica se a distância é menor ou igual a 200 metros do ponto de referência
                                val distancia = calcularDistancia(latitude, longitude)
                                if (distancia <= 200) {
                                    // Recupera o nome do usuário do Firestore antes de registrar o ponto
                                    db.collection("users").document(auth.uid!!)
                                        .get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            if (documentSnapshot.exists()) {
                                                val userName = documentSnapshot.getString("name")
                                                if (userName != null) {
                                                    // Registra o ponto
                                                    val currentTime = Calendar.getInstance().time
                                                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                                                    val currentDateTimeString = sdf.format(currentTime)

                                                    val horarioMap = hashMapOf(
                                                        "ponto" to currentDateTimeString,
                                                        "name" to userName
                                                    )

                                                    // Adicionando o documento na coleção "horarios" do usuário
                                                    db.collection("users").document(auth.uid!!)
                                                        .collection("horarios")
                                                        .add(horarioMap)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(
                                                                baseContext,
                                                                "Ponto registrado",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            getList()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(
                                                                baseContext,
                                                                "Não foi possível registrar ponto: ${e.message}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            getList()
                                                        }
                                                } else {
                                                    Log.e("Firestore", "Nome do usuário é nulo")
                                                    Toast.makeText(
                                                        baseContext,
                                                        "Nome do usuário não encontrado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                Log.e("Firestore", "Documento do usuário não encontrado")
                                                Toast.makeText(
                                                    baseContext,
                                                    "Documento do usuário não encontrado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Erro ao recuperar documento do usuário: ${e.message}")
                                            Toast.makeText(
                                                baseContext,
                                                "Erro ao recuperar documento do usuário: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "Você não está dentro do raio de 200 metros do ponto de referência",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Localização do usuário não disponível, solicitar atualizações
                                Log.d("Location", "Localização do usuário não disponível, solicitando atualizações")
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Falha ao obter a localização do usuário
                            Log.e("Location", "Erro ao obter a localização: ${exception.message}")
                            Toast.makeText(this, "Erro ao obter a localização: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Permissão de localização não concedida, solicita a permissão
                    requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), requestLocationPermissionCode)
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

        userCollection
            .orderBy("ponto", Query.Direction.DESCENDING) // Ordena por 'ponto' em ordem decrescente
            .get()
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
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Localização do usuário obtida com sucesso
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                    } else {
                        // Localização do usuário não disponível, solicitar atualizações
                        Log.d("Location", "Localização do usuário não disponível, solicitando atualizações")
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                    }
                }
                .addOnFailureListener { exception ->
                    // Falha ao obter a localização do usuário
                    Log.e("Location", "Erro ao obter a localização: ${exception.message}")
                    Toast.makeText(this, "Erro ao obter a localização: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Permissão de localização não concedida, solicita a permissão
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), requestLocationPermissionCode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestLocationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Pare as atualizações de localização quando a atividade for destruída
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun calcularDistancia(latitude: Double, longitude: Double): Float {
        val localizacaoUsuario = Location("")
        localizacaoUsuario.latitude = latitude
        localizacaoUsuario.longitude = longitude

        val localizacaoReferencia = Location("")
        localizacaoReferencia.latitude = pontoDeReferencia.latitude
        localizacaoReferencia.longitude = pontoDeReferencia.longitude

        return localizacaoUsuario.distanceTo(localizacaoReferencia)
    }
}
