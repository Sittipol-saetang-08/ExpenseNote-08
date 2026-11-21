package com.example.expensenote_08

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class CardGameActivity : AppCompatActivity() {

    private var currentCardValue = 7
    private var currentSuitIndex = 0

    private var mysteryCardValue = 5
    private var mysterySuitIndex = 1

    private var score = 0
    private var isGuessed = false

    private val suits = arrayOf("♠ SPADES", "♥ HEARTS", "♦ DIAMONDS", "♣ CLUBS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)

        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val tvCurrentCard = findViewById<TextView>(R.id.tvCurrentCard)
        val tvCurrentSuit = findViewById<TextView>(R.id.tvCurrentSuit)
        val tvMysteryCard = findViewById<TextView>(R.id.tvMysteryCard)
        val tvMysterySuit = findViewById<TextView>(R.id.tvMysterySuit)

        val btnHigher = findViewById<Button>(R.id.btnHigher)
        val btnLower = findViewById<Button>(R.id.btnLower)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnBack = findViewById<Button>(R.id.btnBack)

        fun startNewRound() {
            isGuessed = false
            currentCardValue = mysteryCardValue
            currentSuitIndex = mysterySuitIndex

            mysteryCardValue = Random.nextInt(2, 15)
            mysterySuitIndex = Random.nextInt(4)

            tvCurrentCard.text = getCardDisplayValue(currentCardValue)
            tvCurrentSuit.text = suits[currentSuitIndex]

            tvMysteryCard.text = "?"
            tvMysterySuit.text = "[ FACE DOWN ]"
            tvScore.text = "SCORE: $score"
            tvMessage.text = "ไพ่ใบถัดไปจะ [สูงกว่า] หรือ [ต่ำกว่า] ${getCardDisplayValue(currentCardValue)}?"

            btnHigher.visibility = View.VISIBLE
            btnLower.visibility = View.VISIBLE
            btnNext.visibility = View.GONE
        }

        // Initialize first round
        currentCardValue = Random.nextInt(2, 15)
        currentSuitIndex = Random.nextInt(4)
        mysteryCardValue = Random.nextInt(2, 15)
        mysterySuitIndex = Random.nextInt(4)
        startNewRound()

        fun makeGuess(isUserGuessingHigher: Boolean) {
            if (isGuessed) return
            isGuessed = true

            // Reveal mystery card
            tvMysteryCard.text = getCardDisplayValue(mysteryCardValue)
            tvMysterySuit.text = suits[mysterySuitIndex]

            val isHigher = mysteryCardValue > currentCardValue
            val isEqual = mysteryCardValue == currentCardValue

            val correct = if (isEqual) {
                true // Equal counts as correct
            } else if (isUserGuessingHigher) {
                isHigher
            } else {
                !isHigher
            }

            if (correct) {
                score++
                tvMessage.text = "🎉 ถูกต้อง! เปิดไพ่ได้ ${getCardDisplayValue(mysteryCardValue)} (${if(isHigher)"สูงกว่า" else if(isEqual)"เท่ากัน" else "ต่ำกว่า"})"
                Toast.makeText(this, "ชนะ! +1 คะแนน", Toast.LENGTH_SHORT).show()
            } else {
                score = maxOf(0, score - 1)
                tvMessage.text = "❌ ผิด! เปิดไพ่ได้ ${getCardDisplayValue(mysteryCardValue)} (${if(isHigher)"สูงกว่า" else "ต่ำกว่า"})"
            }

            tvScore.text = "SCORE: $score"
            btnHigher.visibility = View.GONE
            btnLower.visibility = View.GONE
            btnNext.visibility = View.VISIBLE
        }

        btnHigher.setOnClickListener { makeGuess(true)
        }
        btnLower.setOnClickListener { makeGuess(false) }
        btnNext.setOnClickListener { startNewRound() }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getCardDisplayValue(value: Int): String {
        return when (value) {
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            14 -> "A"
            else -> value.toString()
        }
    }
}
