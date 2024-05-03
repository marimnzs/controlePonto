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

        binding.btnConfirm.setOnClickListener {
            if (auth.uid != null) {

                val startTime = startTimeEditText.text.toString()
                val endTime = endTimeEditText.text.toString()

                Log.d("Auth", "UID do usuário: ${auth.uid}")

                val diasDeTrabaho = hashMapOf<String, Any>(
                    "diasSelecionados" to selectedDays,
                    "horarioInicial" to startTime,
                    "horarioFinal" to endTime
                )

                db.collection("users").document(auth.uid!!)
                    .collection("diasDeTrabalho")
                    .add(diasDeTrabaho)
                    .addOnSuccessListener {
                        Toast.makeText(baseContext, "Horário adicionado", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            baseContext,
                            "Nao foi possivel adicionar horário",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                Log.e("Auth", "UID do usuário é nulo")
                // Lidar com o caso em que o UID do usuário é nulo
            }
        }
    }
}