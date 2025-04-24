package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ob1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ob1)

        val ob2button = findViewById<Button>(R.id.continueButton)
//

        val homebutton = findViewById<Button>(R.id.button)
//

        ob2button.setOnClickListener {
            val intent = Intent(this@ob1, ob2::class.java)
            startActivity(intent)
        }

        homebutton.setOnClickListener {
            val intent = Intent(this@ob1, Home::class.java)
            startActivity(intent)
        }

//        button3.setOnClickListener {
//            val intent = Intent(this@ob1, ob2::class.java)
//            startActivity(intent)
//        }

    }
}