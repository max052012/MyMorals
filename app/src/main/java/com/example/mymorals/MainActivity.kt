// MainActivity.kt
package com.example.mymorals

import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var resultText: TextView
    private lateinit var nextButton: Button

    private var currentQuestionIndex = 0
    private lateinit var questions: JSONArray
    private var languageCode = "ar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(languageCode)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnArabic).setOnClickListener {
            languageCode = "ar"
            setLocale("ar")
        }

        findViewById<Button>(R.id.btnEnglish).setOnClickListener {
            languageCode = "en"
            setLocale("en")
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            showGameScreen()
        }
    }

    private fun setLocale(code: String) {
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    private fun showGameScreen() {
        setContentView(R.layout.activity_question)

        questionText = findViewById(R.id.questionText)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        resultText = findViewById(R.id.resultText)
        nextButton = findViewById(R.id.btnNext)

        questions = JSONArray(loadJSONFromAsset("questions.json"))

        loadQuestion()

        nextButton.setOnClickListener {
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.length()
            resultText.text = ""
            loadQuestion()
        }
    }

    private fun loadQuestion() {
        val q = questions.getJSONObject(currentQuestionIndex)

        val question = if (languageCode == "ar") q.getString("question_ar") else q.getString("question_en")
        val options = if (languageCode == "ar") q.getJSONArray("options_ar") else q.getJSONArray("options_en")
        val correctIndex = q.getInt("correct_index")

        questionText.text = question
        btnOption1.text = options.getString(0)
        btnOption2.text = options.getString(1)
        btnOption3.text = options.getString(2)

        val buttons = listOf(btnOption1, btnOption2, btnOption3)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (index == correctIndex) {
                    resultText.text = getString(R.string.correct)
                } else {
                    resultText.text = getString(R.string.wrong)
                }
            }
        }
    }

    private fun loadJSONFromAsset(fileName: String): String {
        val inputStream: InputStream = assets.open(fileName)
        return inputStream.bufferedReader().use { it.readText() }
    }
}
