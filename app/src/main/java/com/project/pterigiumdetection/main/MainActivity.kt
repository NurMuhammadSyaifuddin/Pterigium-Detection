package com.project.pterigiumdetection.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.project.pterigiumdetection.databinding.ActivityMainBinding
import com.project.pterigiumdetection.detection.DetectionActivity
import com.project.pterigiumdetection.gone
import com.project.pterigiumdetection.visible

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expand()

        binding.btnCekMata.setOnClickListener {
            Intent(this, DetectionActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }

    private fun expand(){
        binding.apply {
            cardView.setOnClickListener {
                TransitionManager.beginDelayedTransition(layout, AutoTransition())
                isShowTextView(textViewAnswerPterigium)
            }
            cardView2.setOnClickListener {
                TransitionManager.beginDelayedTransition(layout2, AutoTransition())
                isShowTextView(textViewAnswerPenyebabPterigium)
            }
        }
    }

    private fun isShowTextView(textView: TextView){
        if (textView.visibility == View.GONE) textView.visible()
        else textView.gone()
    }
}