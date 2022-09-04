package com.project.pterigiumdetection.detection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.pterigiumdetection.R
import com.project.pterigiumdetection.databinding.ActivityDetectionBinding

class DetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}