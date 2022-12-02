package com.example.myapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.R
import com.example.myapp.chat.ChatAdapter
import com.example.myapp.chat.FriendStatusReceiver
import com.example.myapp.databinding.FragmentChatsBinding
import com.example.myapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private val friendsAdapter by lazy {
        ChatAdapter {
            mainViewModel.selectedUser = it
            if(mainViewModel.connectToFriend()) {
                findNavController().navigate(R.id.action_ChatsFragment_to_IndividualChatFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Unable to connect to device: ${it.deviceName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        binding.chatList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = friendsAdapter
        }

        mainViewModel.friends.observe(viewLifecycleOwner) {
            friendsAdapter.gettingNewFriends(it)
        }

        mainViewModel.connectedToFriend.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_ChatsFragment_to_IndividualChatFragment)
            } else {
                Toast.makeText(context, "Unable to connect to your friend", Toast.LENGTH_LONG).show()
            }
        }

        mainViewModel.searchForUsersToChat()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(mainViewModel.friendStatusReceiver, FriendStatusReceiver.intentFilter)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(mainViewModel.friendStatusReceiver)
    }


}