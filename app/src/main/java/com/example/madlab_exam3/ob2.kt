package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ob2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ob2)

        val ob3button = findViewById<Button>(R.id.continueButton)
     val homebutton = findViewById<Button>(R.id.button)

        ob3button.setOnClickListener {
            val intent = Intent(this@ob2, ob3::class.java)
            startActivity(intent)
        }

        homebutton.setOnClickListener {
            val intent = Intent(this@ob2, Home::class.java)
            startActivity(intent)
        }

    }
}