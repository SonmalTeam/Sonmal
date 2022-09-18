package com.android.signlanguage.ui.lesson.new_sign

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.android.signlanguage.ViewModelInitListener
import com.android.signlanguage.databinding.FragmentNewSignBinding
import com.android.signlanguage.ui.lesson.Exercise

class NewSignFragment : Fragment(), ViewModelInitListener {

    companion object {
        private const val TAG = "NewSignFragment"

        private const val SIGN_BUNDLE = "sign"

        fun newInstance(sign: Char): NewSignFragment {
            val args = Bundle()
            args.putChar(SIGN_BUNDLE, sign)

            val fragment = NewSignFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val sign: Char
        get() = requireArguments().getChar(SIGN_BUNDLE)

    override var viewModelInitialized: ((viewModel: ViewModel) -> Unit)? = null

    private lateinit var _viewModel: NewSignViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewSignBinding.inflate(inflater, container, false)

        val factory = NewSignViewModelFactory(requireArguments().getChar(SIGN_BUNDLE))
        _viewModel = ViewModelProvider(this, factory).get(NewSignViewModel::class.java)
        viewModelInitialized?.invoke(_viewModel)

        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        return binding.root
    }
}