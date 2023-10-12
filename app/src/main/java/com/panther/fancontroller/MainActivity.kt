package com.panther.fancontroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.panther.fancontroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var remoteView: RemoteView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        remoteView = binding.remoteView

        remoteView.setOnClickListener {
            remoteView.updateToWords()
        }

        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            remoteView.setTextMode(isChecked)
            updateFanStateText()
        }
    }

    private fun updateFanStateText() {
        val fanSpeed = remoteView.getFanSpeed()
        if (binding.btnSwitch.isChecked) {
            resources.getString(fanSpeed.labelWord)
        } else {
            resources.getString(fanSpeed.labelNumber)
        }
    }
}