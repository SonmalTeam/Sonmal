package com.android.signlanguage.ui.model_training_data_formation

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.signlanguage.databinding.FragmentModelTrainingDataFormationBinding
import com.android.signlanguage.model.ml.HandTrackingModel
import com.google.mediapipe.components.PermissionHelper
import com.google.mediapipe.framework.AndroidAssetUtil


class ModelTrainingDataFormationFragment : Fragment() {

    companion object {
        fun newInstance() = ModelTrainingDataFormationFragment()
    }

    private lateinit var _viewModel: ModelTrainingDataFormationViewModel

    private lateinit var _previewDisplayView: SurfaceView
    private var _handTrackingModel: HandTrackingModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _handTrackingModel = HandTrackingModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentModelTrainingDataFormationBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this).get(ModelTrainingDataFormationViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        binding.save.setOnClickListener {
            val clipboard: ClipboardManager? =
                getSystemService(requireContext(), ClipboardManager::class.java)
            val clip = ClipData.newPlainText("ready signs", _viewModel.readySignsText)
            clipboard!!.setPrimaryClip(clip)
        }

        _previewDisplayView = binding.surfaceView

        _handTrackingModel!!.setupPreviewDisplayView(_previewDisplayView)
        AndroidAssetUtil.initializeNativeAssetManager(context)
        _handTrackingModel!!.initializeProcessor(context)
        _handTrackingModel!!.addCallback {
            _viewModel.handsCallback(it)
        }

        if (!PermissionHelper.cameraPermissionsGranted(activity))
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1)

        return binding.root
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}