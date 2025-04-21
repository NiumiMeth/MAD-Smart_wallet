package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ob3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ob3)

        val home = findViewById<Button>(R.id.continueButton)
//        val homebutton = findViewById<Button>(R.id.button3)

        home.setOnClickListener {
            val intent = Intent(this@ob3, Home::class.java)
            startActivity(intent)
        }

    }
}