package com.example.giao_dien.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.giao_dien.R
import com.example.giao_dien.databinding.ActivitySettingBinding
import com.example.giao_dien.ui.language.LanguageActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Language Option
        binding.layoutLanguage.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java).apply {
                putExtra(LanguageActivity.EXTRA_IS_FROM_SETTING, true)
            }
            startActivity(intent)
        }

        // Rate Option
        binding.layoutRate.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.msg_rate_thank_you), Toast.LENGTH_SHORT).show()
            }
        }

        // Share Option
        binding.layoutShare.setOnClickListener {
            val shareText = getString(R.string.share_app_text_format, packageName)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
        }

        // Feedback Option
        binding.layoutFeedback.setOnClickListener {
            val feedbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@example.com"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject))
            }
            try {
                startActivity(Intent.createChooser(feedbackIntent, getString(R.string.share_chooser_title)))
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.msg_error_no_email_client), Toast.LENGTH_SHORT).show()
            }
        }

        // Privacy Policy Option
        binding.layoutPrivacy.setOnClickListener {
            val privacyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            try {
                startActivity(privacyIntent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.msg_error_unable_open_link), Toast.LENGTH_SHORT).show()
            }
        }
    }
}