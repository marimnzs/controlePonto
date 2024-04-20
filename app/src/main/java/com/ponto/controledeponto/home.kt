package com.ponto.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ponto.controledeponto.databinding.ActivityHomeBinding

class home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnEditHours.setOnClickListener {
            val navergarEditHours = Intent(this, CheckInActivity::class.java)

            startActivity(navergarEditHours)
        }
    }
}