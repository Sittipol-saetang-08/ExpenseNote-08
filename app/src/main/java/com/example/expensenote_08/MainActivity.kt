package com.example.expensenote_08

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val expenseList = mutableListOf<String>()
    private val PREF_NAME = "quantum_expense_prefs"
    private val KEY_EXPENSES = "quantum_expense_list"
    private var total: Double = 0.0

    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var btnSecretGame: TextView
    private lateinit var tvHistory: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvLastItem: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etAmount = findViewById(R.id.etAmount)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        btnSecretGame = findViewById(R.id.btnSecretGame)
        tvHistory = findViewById(R.id.tvHistory)
        tvTotal = findViewById(R.id.tvTotal)
        tvLastItem = findViewById(R.id.tvLastItem)

        // ตั้งค่าหมวดหมู่การใช้จ่ายในชีวิตประจำวัน (สไตล์ไซเบอร์)
        val categories = arrayOf(
            "🍜 อาหารและเครื่องดื่ม",
            "🚗 เดินทางและขนส่ง",
            "🛍 ช้อปปิ้งและเสื้อผ้า",
            "💡 บิลและค่าใช้จ่ายบ้าน",
            "🎬 บันเทิงและสันทนาการ",
            "📦 อื่นๆ"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter

        // โหลดข้อมูลประวัติควอนตัมที่บันทึกไว้
        loadExpenses()
        updateHistoryDisplay()

        // ปุ่มลับเปิดเมนูอาร์เคด (Arcade Hub Menu)
        btnSecretGame.setOnClickListener {
            val intent = Intent(this, ArcadeHubActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "⚡ [QUANTUM ARCADE] ยินดีต้อนรับสู่โซนเกม", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val description = etDescription.text.toString().trim()

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "⚡ [NEURAL SYNCED] กรุณากรอกจำนวนก่อนบันทึก", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "⚡ [NEURAL SYNCED] กรุณากรอกจำนวนเงินเป็นตัวเลขเท่านั้น", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            total += amount

            // ตรวจสอบช่องคำอธิบาย หากไม่ได้กรอกให้ใช้คำว่า "ไม่ระบุ"
            val finalDescription = if (description.isEmpty()) "ไม่ระบุ" else description

            // จัดรูปแบบรายการพร้อมหมวดหมู่และคำอธิบาย
            val itemText = "• [$selectedCategory] ฿$amountStr - $finalDescription"

            expenseList.add(0, itemText) // เพิ่มรายการใหม่ไว้ด้านบนสุด
            saveExpenses()
            updateHistoryDisplay()

            // แสดงยอดรวมและรายการล่าสุดสั้นๆ ในรูปแบบ Kotlin String Extension
            tvTotal.text = "ยอดรวมวันนี้: %.2f บาท".format(total)
            tvLastItem.text = "รายการล่าสุด: $finalDescription - %.2f บาท".format(amount)

            Toast.makeText(this, "⚡ [NEURAL SYNCED] บันทึกข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()

            // เคลียร์ฟอร์ม
            etAmount.text.clear()
            etDescription.text.clear()
            etAmount.clearFocus()
        }

        // ปุ่มล้างข้อมูล (Purge Matrix)
        btnClear.setOnClickListener {
            expenseList.clear()
            saveExpenses()
            total = 0.0

            etAmount.text.clear()
            etDescription.text.clear()

            tvLastItem.text = "รายการล่าสุด: -"
            tvTotal.text = "ยอดรวมวันนี้: 0.00 บาท"

            updateHistoryDisplay()
            Toast.makeText(this, "🗑 [PURGE COMPLETE] ล้างข้อมูลทั้งหมดเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveExpenses() {
        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val combined = expenseList.joinToString("|||")
        prefs.edit().putString(KEY_EXPENSES, combined).apply()
    }

    private fun loadExpenses() {
        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val combined = prefs.getString(KEY_EXPENSES, "") ?: ""
        expenseList.clear()
        if (combined.isNotEmpty()) {
            expenseList.addAll(combined.split("|||"))
        }
    }

    private fun calculateTotal(): Double {
        var sum = 0.0
        val regex = Regex("""฿(\d+(?:\.\d+)?)""")
        for (item in expenseList) {
            val match = regex.find(item)
            if (match != null) {
                sum += match.groupValues[1].toDoubleOrNull() ?: 0.0
            }
        }
        return sum
    }

    private fun updateHistoryDisplay() {
        total = calculateTotal()
        if (expenseList.isEmpty()) {
            tvHistory.text = "[ WAITING FOR NEURAL INPUT... ]"
            tvTotal.text = "ยอดรวมวันนี้: 0.00 บาท"
            tvLastItem.text = "รายการล่าสุด: - "
        } else {
            tvHistory.text = expenseList.joinToString("\n")
            tvTotal.text = "ยอดรวมวันนี้: %.2f บาท".format(total)
        }
    }
}
