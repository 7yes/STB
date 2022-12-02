package com.example.myapp.chat

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.ChatItemBinding

class ChatAdapter(
    private val friendsList: MutableList<WifiP2pDevice> = mutableListOf(),
    private val tryToConnect: (WifiP2pDevice) -> Unit
) : RecyclerView.Adapter<ItemViewHolder>() {

    fun gettingNewFriends(newFriends: List<WifiP2pDevice>) {
        if (newFriends != friendsList) {
            friendsList.clear()
            friendsList.addAll(newFriends)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(friendsList[position], tryToConnect)

    override fun getItemCount(): Int = friendsList.size
}

class ItemViewHolder(
    private val binding: ChatItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(friend: WifiP2pDevice, connect: (WifiP2pDevice) -> Unit) {
        binding.frName.text = friend.deviceName
        binding.frAddress.text = friend.deviceAddress
        binding.frStatus.text = if(friend.status == 1) "ONLINE" else "OFFLINE"

        binding.startChatBtn.setOnClickListener {
            connect(friend)
        }
    }
}