package com.example.expensenote_08

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class AimTrainerActivity : AppCompatActivity() {

    private var score = 0
    private var isPlaying = false
    private var timer: CountDownTimer? = null

    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStartAim: Button
    private lateinit var flGameArea: FrameLayout
    private lateinit var spinnerDifficulty: Spinner

    private val handler = Handler(Looper.getMainLooper())
    private var targetRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aim_trainer)

        tvScore = findViewById(R.id.tvScore)
        tvTimer = findViewById(R.id.tvTimer)
        btnStartAim = findViewById(R.id.btnStartAim)
        flGameArea = findViewById(R.id.flGameArea)
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty)
        val btnBackHub = findViewById<Button>(R.id.btnBackHub)

        // Setup difficulty levels
        val difficulties = arrayOf(
            "🟢 EASY (ง่าย - เป้าอยู่นิ่ง)",
            "🟡 MEDIUM (ปานกลาง - หายใน 1.5s)",
            "🔴 HARD (ยาก - หายใน 0.9s)",
            "🔥 INSANE (นรกไซเบอร์ - หายใน 0.6s)"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, difficulties)
        spinnerDifficulty.adapter = adapter

        btnStartAim.setOnClickListener {
            startGame()
        }

        btnBackHub.setOnClickListener {
            finish()
        }
    }

    private fun startGame() {
        score = 0
        isPlaying = true
        tvScore.text = "SCORE: $score"
        btnStartAim.visibility = View.GONE
        spinnerDifficulty.isEnabled = false
        flGameArea.removeAllViews()

        spawnTarget()

        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "TIME: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun spawnTarget() {
        if (!isPlaying) return

        handler.removeCallbacksAndMessages(null)
        flGameArea.removeAllViews()

        val difficulty = spinnerDifficulty.selectedItemPosition
        val (size, timeoutMillis) = when (difficulty) {
            0 -> Pair(150, 0L)          // Easy: infinite
            1 -> Pair(130, 1500L)       // Medium: 1.5s
            2 -> Pair(100, 900L)        // Hard: 0.9s
            else -> Pair(75, 600L)      // Insane: 0.6s
        }

        val target = View(this)
        val params = FrameLayout.LayoutParams(size, size)

        val maxX = (flGameArea.width - size).coerceAtLeast(100)
        val maxY = (flGameArea.height - size).coerceAtLeast(100)

        params.leftMargin = if (maxX > 0) Random.nextInt(0, maxX) else 50
        params.topMargin = if (maxY > 0) Random.nextInt(0, maxY) else 50

        target.layoutParams = params
        target.setBackgroundColor(if (Random.nextBoolean()) 0xFF00FFCC.toInt() else 0xFFFF007F.toInt())
        target.elevation = 8f

        target.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            score++
            tvScore.text = "SCORE: $score"
            spawnTarget()
        }

        flGameArea.addView(target)

        // If timeout is set, auto-respawn target (missed)
        if (timeoutMillis > 0) {
            targetRunnable = Runnable {
                if (isPlaying) {
                    spawnTarget()
                }
            }
            handler.postDelayed(targetRunnable!!, timeoutMillis)
        }
    }

    private fun endGame() {
        isPlaying = false
        timer?.cancel()
        handler.removeCallbacksAndMessages(null)
        flGameArea.removeAllViews()

        spinnerDifficulty.isEnabled = true
        btnStartAim.visibility = View.VISIBLE
        btnStartAim.text = "🎯 เล่นอีกครั้ง (SCORE: $score)"
        Toast.makeText(this, "🎯 เวลาหมด! ทำคะแนนได้ $score เป้า", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        handler.removeCallbacksAndMessages(null)
    }
}
