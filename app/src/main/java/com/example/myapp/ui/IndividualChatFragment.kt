package com.example.myapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.myapp.R
import com.example.myapp.databinding.FragmentIndividualChatBinding
import com.example.myapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IndividualChatFragment : Fragment() {

    private lateinit var binding: FragmentIndividualChatBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIndividualChatBinding.inflate(inflater, container, false)

        binding.sndBtn.setOnClickListener {
            mainViewModel.sendMessage(binding.sndMessage.text.toString())
        }

        mainViewModel.messagesReceived.observe(viewLifecycleOwner) {
            binding.msgReceived.text = it
        }

        return binding.root
    }
}