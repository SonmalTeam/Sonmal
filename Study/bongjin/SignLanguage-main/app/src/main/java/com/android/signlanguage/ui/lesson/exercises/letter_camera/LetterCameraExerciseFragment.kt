package com.android.signlanguage.ui.lesson.exercises.letter_camera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.signlanguage.R
import com.android.signlanguage.ViewModelInitListener
import com.android.signlanguage.databinding.FragmentLetterCameraExerciseBinding
import com.android.signlanguage.model.ml.HandTrackingModel
import com.android.signlanguage.model.ml.SignDetectionModelLoader
import com.android.signlanguage.ui.lesson.Exercise
import com.android.signlanguage.ui.lesson.ExerciseRules
import com.google.mediapipe.components.PermissionHelper
import com.google.mediapipe.framework.AndroidAssetUtil
import kotlinx.coroutines.*


class LetterCameraExerciseFragment : Fragment(), ViewModelInitListener, Exercise {

    companion object : ExerciseRules {
        private const val TAG = "LetterCameraExerciseFragment"

        override val unlockedSignsRequired: Int = 1

        private const val SIGN_BUNDLE = "sign"

        fun newInstance(sign: Char): LetterCameraExerciseFragment {
            val args = Bundle()
            args.putChar(SIGN_BUNDLE, sign)
            val fragment = LetterCameraExerciseFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var _viewModel: LetterCameraExerciseViewModel
    override var viewModelInitialized: ((viewModel: ViewModel) -> Unit)? = null

    private lateinit var _previewDisplayView: SurfaceView
    private var _handTrackingModel: HandTrackingModel? = null

    private lateinit var _binding: FragmentLetterCameraExerciseBinding

    var hideWrongSignMessageAnimator: ValueAnimator? = null
    var extendWrongSignTextAnimator: ValueAnimator? = null

    override val sign: Char
        get() = requireArguments().getChar(SIGN_BUNDLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _handTrackingModel = HandTrackingModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLetterCameraExerciseBinding.inflate(inflater, container, false)

        val factory = LetterCameraExerciseViewModelFactory(sign)
        _viewModel = ViewModelProvider(this, factory).get(LetterCameraExerciseViewModel::class.java)
        _viewModel.signDetectionModel =
            SignDetectionModelLoader().load(requireActivity().assets, "model.tflite")
        viewModelInitialized?.invoke(_viewModel)

        _binding.lifecycleOwner = this
        _binding.viewModel = _viewModel

        _previewDisplayView = SurfaceView(context)
        _previewDisplayView.alpha = 1f
        _binding.viewGroup.addView(_previewDisplayView, 0)
        _handTrackingModel!!.setupPreviewDisplayView(_previewDisplayView)
        AndroidAssetUtil.initializeNativeAssetManager(context)
        _handTrackingModel!!.initializeProcessor(context)
        _handTrackingModel!!.addCallback {
            _viewModel.handsCallback(it)
        }

        _viewModel.isCameraAccessible.observe(viewLifecycleOwner) {
            if (it)
                _viewModel.isLoading.value = true
        }

        _viewModel.rightPrediction = {
            MainScope().launch {
                showRightSignMessage()
            }
        }

        _viewModel.wrongPrediction = {
            vibrate(150)
            MainScope().launch {
                showWrongSignMessage(it)
            }
        }

        _handTrackingModel!!.isCameraLoaded.observe(viewLifecycleOwner) {
            if (_viewModel.isCameraAccessible.value == true)
                _viewModel.isLoading.value = !it
        }

        if (PermissionHelper.cameraPermissionsGranted(activity))
            _viewModel.isCameraAccessible.value = true
        else
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1)

        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        _handTrackingModel!!.initializeConverter()
        if (PermissionHelper.cameraPermissionsGranted(activity)) {
            _handTrackingModel!!.startCamera(requireActivity())
        }
    }

    override fun onPause() {
        super.onPause()
        _handTrackingModel!!.closeConverter()
    }

    override fun onDestroy() {
        super.onDestroy()
        _handTrackingModel!!.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewModel.signDetectionModel.close()
    }

    private fun vibrate(ms: Long) {
        val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(ms)
        }
    }

    private fun showRightSignMessage() {
        _binding.wrongSignImage.clearAnimation()
        _binding.wrongSignTextView.clearAnimation()
        hideWrongSignMessageAnimator?.cancel()
        extendWrongSignTextAnimator?.cancel()
        hideWrongSignMessage(0) {
            _binding.rightSignImage.apply {
                alpha = 0f
                visibility = View.VISIBLE

                animate()
                    .alpha(1f)
                    .setDuration(150)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            showRightSignText(250) {
                                MainScope().launch {
                                    delay(750)
                                    _viewModel.finish()
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun showWrongSignMessage(sign: Char) {
        _binding.wrongSignImage.clearAnimation()
        _binding.wrongSignTextView.clearAnimation()
        hideWrongSignMessageAnimator?.cancel()
        extendWrongSignTextAnimator?.cancel()
        hideWrongSignMessage(0) {

            _binding.wrongSignImage.apply {
                alpha = 0f
                visibility = View.VISIBLE

                animate()
                    .alpha(1f)
                    .setDuration(150)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            showWrongSignText(sign, 250) {
                                hideWrongSignMessage(2500, null)
                            }
                        }
                    })
            }
        }
    }

    private fun hideWrongSignMessage(startDelay: Long, endCallback: (() -> ImageView)?) {
        hideWrongSignMessageAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            duration = 150
            setStartDelay(startDelay)
            addUpdateListener {
                _binding.wrongSignImage.alpha = animatedValue as Float
                _binding.wrongSignTextView.alpha = animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    _binding.wrongSignImage.visibility = View.GONE
                    _binding.wrongSignTextView.visibility = View.GONE
                    endCallback?.invoke()
                }
            })
        }
        hideWrongSignMessageAnimator!!.start()
    }

    private fun showWrongSignText(sign: Char, startDelay: Long, endCallback: () -> Unit) {
        val view = _binding.wrongSignTextView
        view.text = getString(R.string.showing_sign_message, sign)
        view.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetWidth = view.measuredWidth

        view.apply {
            layoutParams.width = 0
            alpha = 1f
            visibility = View.VISIBLE
        }

        extendWrongSignTextAnimator = ValueAnimator.ofInt(0, targetWidth).apply {
            interpolator = AccelerateInterpolator()
            duration = 300
            setStartDelay(startDelay)
            addUpdateListener {
                val layoutParams = view.layoutParams
                layoutParams.width = (targetWidth * animatedFraction).toInt()
                view.layoutParams = layoutParams
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val layoutParams = view.layoutParams
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    endCallback.invoke()
                }
            })
        }
        extendWrongSignTextAnimator!!.start()
    }

    private fun showRightSignText(startDelay: Long, endCallback: () -> Unit) {
        val view = _binding.rightSignTextView
        view.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetWidth = view.measuredWidth

        view.apply {
            layoutParams.width = 0
            alpha = 1f
            visibility = View.VISIBLE
        }

        val extendRightSignTextAnimator = ValueAnimator.ofInt(0, targetWidth).apply {
            interpolator = AccelerateInterpolator()
            duration = 300
            setStartDelay(startDelay)
            addUpdateListener {
                val layoutParams = view.layoutParams
                layoutParams.width = (targetWidth * animatedFraction).toInt()
                view.layoutParams = layoutParams
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val layoutParams = view.layoutParams
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    endCallback.invoke()
                }
            })
        }
        extendRightSignTextAnimator!!.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionHelper.cameraPermissionsGranted(activity))
            _viewModel.isCameraAccessible.value = true
    }
}