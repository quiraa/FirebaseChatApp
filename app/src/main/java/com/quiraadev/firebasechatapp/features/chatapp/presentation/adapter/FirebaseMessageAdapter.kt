package com.quiraadev.firebasechatapp.features.chatapp.presentation.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.quiraadev.firebasechatapp.R
import com.quiraadev.firebasechatapp.databinding.ItemMessageBinding
import com.quiraadev.firebasechatapp.features.chatapp.data.model.Message

class FirebaseMessageAdapter(
	options: FirebaseRecyclerOptions<Message>,
	private val currentUserName: String?,
) : FirebaseRecyclerAdapter<Message, FirebaseMessageAdapter.MessageViewHolder>(options) {

	inner class MessageViewHolder(private val binding: ItemMessageBinding) :
		RecyclerView.ViewHolder(binding.root) {
			fun bindView(item: Message) {
				binding.tvMessage.text = item.text
				setTextColor(item.name, binding.tvMessage)
				binding.tvMessenger.text = item.name
				Glide.with(itemView.context)
					.load(item.photoUrl)
					.circleCrop()
					.into(binding.ivMessenger)
				if (item.timeStamp != null) {
					binding.tvTimestamp.text = DateUtils.getRelativeTimeSpanString(item.timeStamp.toLong())
				}
			}

		private fun setTextColor(userName: String?, textView: TextView) {
			if (currentUserName == userName && userName != null) {
				textView.setBackgroundResource(R.drawable.rounded_message_blue)
			} else {
				textView.setBackgroundResource(R.drawable.rounded_message_yellow)
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): FirebaseMessageAdapter.MessageViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.item_message, parent, false)
		val binding = ItemMessageBinding.bind(view)
		return MessageViewHolder(binding)
	}

	override fun onBindViewHolder(
		holder: FirebaseMessageAdapter.MessageViewHolder,
		position: Int,
		model: Message
	) {
		holder.bindView(model)
	}
}