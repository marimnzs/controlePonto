package com.ponto.controledeponto

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivityCheckInBinding
import java.util.*

class CheckInActivity : AppCompatActivity() {
    //alterar horarios
    private lateinit var binding: ActivityCheckInBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val selectedDays = mutableListOf<String>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            checkboxSegunda.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Segunda-feira")
                } else {
                    selectedDays.remove("Segunda-feira")
                }
            }
            checkboxTerca.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Terça-feira")
                } else {
                    selectedDays.remove("Terça-feira")
                }
            }
            checkboxQuarta.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Quarta-feira")
                } else {
                    selectedDays.remove("Quarta-feira")
                }
            }
            checkboxQuinta.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Quinta-feira")
                } else {
                    selectedDays.remove("Quinta-feira")
                }
            }
            checkboxSexta.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Sexta-feira")
                } else {
                    selectedDays.remove("Sexta-feira")
                }
            }
            checkboxSabado.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Sábado")
                } else {
                    selectedDays.remove("Sábado")
                }
            }
            checkboxDomingo.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add("Domingo")
                } else {
                    selectedDays.remove("Domingo")
                }
            }
        }

        val startTimeEditText = binding.tvInitial
        val endTimeEditText = binding.tvFinal

        // Definir entrada de tipo numérico para os campos de horário
        startTimeEditText.inputType = InputType.TYPE_CLASS_NUMBER
        endTimeEditText.inputType = InputType.TYPE_CLASS_NUMBER

        // Consulta os dados do Firestore e preenche os campos na tela
        fetchAndDisplayData()

        binding.btnConfirm.setOnClickListener {
            if (auth.uid != null) {
                val startTime = startTimeEditText.text.toString()
                val endTime = endTimeEditText.text.toString()

                Log.d("Auth", "UID do usuário: ${auth.uid}")

                val diasDeTrabalho = hashMapOf<String, Any>(
                    "diasSelecionados" to selectedDays,
                    "horarioInicial" to startTime,
                    "horarioFinal" to endTime
                )

                // Verificar se já existe um documento "diasDeTrabalho" para o usuário
                db.collection("users").document(auth.uid!!)
                    .collection("diasDeTrabalho").document("dados")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Se o documento existir, atualize os dados
                            db.collection("users").document(auth.uid!!)
                                .collection("diasDeTrabalho").document("dados")
                                .update(diasDeTrabalho)
                                .addOnSuccessListener {
                                    Toast.makeText(baseContext, "Horário atualizado", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        baseContext,
                                        "Não foi possível atualizar o horário",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            // Se o documento não existir, crie um novo
                            db.collection("users").document(auth.uid!!)
                                .collection("diasDeTrabalho").document("dados")
                                .set(diasDeTrabalho)
                                .addOnSuccessListener {
                                    Toast.makeText(baseContext, "Horário adicionado", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        baseContext,
                                        "Não foi possível adicionar o horário",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error getting document: ", exception)
                        Toast.makeText(baseContext, "Erro ao acessar dados", Toast.LENGTH_LONG).show()
                    }
            } else {
                Log.e("Auth", "UID do usuário é nulo")
                // Lidar com o caso em que o UID do usuário é nulo
            }
        }
    }

    private fun fetchAndDisplayData() {
        if (auth.uid != null) {
            // Consulta os dados no Firestore
            db.collection("users").document(auth.uid!!)
                .collection("diasDeTrabalho").document("dados")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Se o documento existir, preencha os campos na tela com os dados recuperados
                        val data = document.data
                        if (data != null) {
                            val selectedDays = data["diasSelecionados"] as List<String>
                            val startTime = data["horarioInicial"] as String
                            val endTime = data["horarioFinal"] as String

                            // Preencha os checkboxes com os dias selecionados
                            selectedDays.forEach { day ->
                                when (day) {
                                    "Segunda-feira" -> binding.checkboxSegunda.isChecked = true
                                    "Terça-feira" -> binding.checkboxTerca.isChecked = true
                                    "Quarta-feira" -> binding.checkboxQuarta.isChecked = true
                                    "Quinta-feira" -> binding.checkboxQuinta.isChecked = true
                                    "Sexta-feira" -> binding.checkboxSexta.isChecked = true
                                    "Sábado" -> binding.checkboxSabado.isChecked = true
                                    "Domingo" -> binding.checkboxDomingo.isChecked = true
                                }
                            }

                            // Preencha os campos de horário inicial e final
                            binding.tvInitial.setText(startTime)
                            binding.tvFinal.setText(endTime)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting document: ", exception)
                    Toast.makeText(baseContext, "Erro ao acessar dados", Toast.LENGTH_LONG).show()
                }
        } else {
            Log.e("Auth", "UID do usuário é nulo")
            // Lidar com o caso em que o UID do usuário é nulo
        }
    }
}