package com.example.expensenote_08

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ArcadeHubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arcade_hub)

        val btnGameCard = findViewById<Button>(R.id.btnGameCard)
        val btnGameWheel = findViewById<Button>(R.id.btnGameWheel)
        val btnGameAim = findViewById<Button>(R.id.btnGameAim)
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)

        btnGameCard.setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }

        btnGameWheel.setOnClickListener {
            startActivity(Intent(this, SpinWheelActivity::class.java))
        }

        btnGameAim.setOnClickListener {
            startActivity(Intent(this, AimTrainerActivity::class.java))
        }

        btnBackToMain.setOnClickListener {
            finish()
        }
    }
}
