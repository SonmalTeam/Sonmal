package com.android.signlanguage.ui.detailed_signs

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.signlanguage.databinding.ActivityDetailedSignsBinding

class DetailedSignsActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = DetailedSignsActivity()
    }

    private lateinit var _viewModel: DetailedSignsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailedSignsBinding.inflate(layoutInflater)

        _viewModel = ViewModelProvider(this).get(DetailedSignsViewModel::class.java)

        _viewModel.signsDrawables.observe(this) {
            binding.signsList.adapter = DetailedSignsAdapter(it)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

}