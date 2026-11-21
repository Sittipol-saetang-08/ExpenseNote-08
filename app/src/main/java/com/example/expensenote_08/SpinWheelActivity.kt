package com.example.expensenote_08

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class SpinWheelActivity : AppCompatActivity() {

    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_wheel)

        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val tvRouletteResult = findViewById<TextView>(R.id.tvRouletteResult)
        val tvColorName = findViewById<TextView>(R.id.tvColorName)

        val btnRed = findViewById<Button>(R.id.btnRed)
        val btnBlack = findViewById<Button>(R.id.btnBlack)
        val btnBackHub = findViewById<Button>(R.id.btnBackHub)

        fun spinRoulette(userGuessedRed: Boolean) {
            btnRed.isEnabled = false
            btnBlack.isEnabled = false

            tvColorName.text = "🌀 กำลังหมุนวงล้อรูเล็ตควอนตัม..."
            tvMessage.text = "ลุ้นระทึก..."

            // Rotate animation effect (3 full rotations = 1080 degrees)
            val rotateAnimator = ObjectAnimator.ofFloat(tvRouletteResult, "rotation", 0f, 1080f)
            rotateAnimator.duration = 1200 // 1.2 seconds

            rotateAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Generate final result after spin
                    val roll = Random.nextInt(0, 37)
                    val winningColor: String
                    val winningDisplay: String

                    when {
                        roll == 0 -> {
                            winningColor = "GREEN"
                            winningDisplay = "🟢 0 (GREEN JACKPOT!)"
                        }
                        roll % 2 == 1 -> {
                            winningColor = "RED"
                            winningDisplay = "🔴 $roll (RED)"
                        }
                        else -> {
                            winningColor = "BLACK"
                            winningDisplay = "⚫ $roll (BLACK)"
                        }
                    }

                    tvRouletteResult.text = winningDisplay
                    tvColorName.text = "ผลลัพธ์การหมุนรูเล็ต"

                    val userChoice = if (userGuessedRed) "RED" else "BLACK"

                    if (winningColor == "GREEN") {
                        score += 5
                        tvMessage.text = "🌟 แจ็กพอตแตก! ออกสีเขียว (+5 คะแนน)"
                        Toast.makeText(this@SpinWheelActivity, "JACKPOT GREEN! +5 คะแนน", Toast.LENGTH_LONG).show()
                    } else if (winningColor == userChoice) {
                        score++
                        tvMessage.text = "🎉 ถูกต้อง! ออกสี $userChoice ชนะรับ +1 คะแนน"
                        Toast.makeText(this@SpinWheelActivity, "ทายถูก! +1 คะแนน", Toast.LENGTH_SHORT).show()
                    } else {
                        score = maxOf(0, score - 1)
                        tvMessage.text = "❌ ผิด! ผลลัพธ์คือ $winningColor (คุณทาย $userChoice)"
                        Toast.makeText(this@SpinWheelActivity, "ทายผิด! -1 คะแนน", Toast.LENGTH_SHORT).show()
                    }

                    tvScore.text = "SCORE: $score"

                    btnRed.isEnabled = true
                    btnBlack.isEnabled = true
                }
            })

            rotateAnimator.start()
        }

        btnRed.setOnClickListener {
            spinRoulette(true)
        }

        btnBlack.setOnClickListener {
            spinRoulette(false)
        }

        btnBackHub.setOnClickListener {
            finish()
        }
    }
}
