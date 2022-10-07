package com.d202.sonmal.ui.call

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.d202.sonmal.adapter.DialAdapter
import com.d202.sonmal.databinding.FragmentDialBinding
import com.d202.sonmal.ui.call.viewmodel.DialViewModel
import com.d202.sonmal.utils.sharedpref.SettingsPreference

private const val TAG ="DialFragment"
class DialFragment : Fragment() {
    private lateinit var binding: FragmentDialBinding
    private val viewModel: DialViewModel by viewModels()
    private lateinit var dialAdapter: DialAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

        binding.apply {
            lifecycleOwner = this@DialFragment
            vm = viewModel
            dialAdapter = DialAdapter()
            dialAdapter.onClickDialListener = object : DialAdapter.OnClickDialListener {
                override fun onClick(dial: String) {
                    tvPhone.setText("${tvPhone.text}${dial}")
                }
            }
            ivUndo.setOnClickListener {
                if(tvPhone.text.isNotEmpty()){
                    tvPhone.setText(tvPhone.text.substring(0, tvPhone.text.length - 1))
                }
            }
            ivCall.setOnClickListener {
                if(tvPhone.text.isNotEmpty()) {
                    call(tvPhone.text.toString())
                }
            }
            ivContacts.setOnClickListener {
                resultLauncher.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI))
            }
            ivBack.setOnClickListener {
                viewModel.dialVisibility(true)
            }
            recyclerDial.apply {
                adapter = dialAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            val cursor = requireContext().contentResolver.query(
                it.data!!.data!!,
                arrayOf<String>(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                ),
                null,
                null,
                null
            )
            if (cursor!!.moveToFirst()) {
                val name = cursor.getString(0)
                val phone = cursor.getString(1)
                binding.tvPhone.setText(phone)
            }
        }
    }


    private fun call(number: String){
        SettingsPreference().setUseCall(true)
        requireContext().startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${number}")))
    }
}
