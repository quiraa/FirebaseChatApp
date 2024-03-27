package com.quiraadev.firebasechatapp.features.chatapp.presentation.screen

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.quiraadev.firebasechatapp.R
import com.quiraadev.firebasechatapp.databinding.ActivityMainBinding
import com.quiraadev.firebasechatapp.features.chatapp.data.model.Message
import com.quiraadev.firebasechatapp.features.chatapp.presentation.adapter.FirebaseMessageAdapter
import java.util.Date

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private val binding by viewBinding(ActivityMainBinding::bind)

	private lateinit var auth: FirebaseAuth
	private lateinit var db: FirebaseDatabase

	private lateinit var adapter : FirebaseMessageAdapter

	private val requestNotificationPermissionLauncher =
		registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			if (isGranted) {
				Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
			} else {
				Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
			}
		}
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setSupportActionBar(binding.chatAppBar)
		if (Build.VERSION.SDK_INT >= 33) {
			requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
		}

		auth = Firebase.auth
		val firebaseUser = auth.currentUser

		db = Firebase.database

		val messageRef = db.reference.child(MESSAGES_CHILD)

		binding.sendButton.setOnClickListener {
			val friendlyMessage = Message(
				binding.messageEditText.text.toString(),
				firebaseUser?.displayName.toString(),
				firebaseUser?.photoUrl.toString(),
				Date().time.toString()
			)
			messageRef.push().setValue(friendlyMessage) { error, _ ->
				if (error != null) {
					Toast.makeText(this, getString(R.string.send_error) + error.message, Toast.LENGTH_SHORT).show()
				} else {
					Toast.makeText(this, getString(R.string.send_success), Toast.LENGTH_SHORT).show()
				}
			}
			binding.messageEditText.setText("")
		}

		if (firebaseUser == null) {
			startActivity(Intent(this, LoginActivity::class.java))
			finish()
			return
		}

		val manager = LinearLayoutManager(this)
		manager.stackFromEnd = true
		binding.messageRecyclerView.layoutManager = manager

		val options = FirebaseRecyclerOptions.Builder<Message>()
			.setQuery(messageRef, Message::class.java)
			.build()
		adapter = FirebaseMessageAdapter(options, firebaseUser.displayName)
		binding.messageRecyclerView.adapter = adapter
	}

	public override fun onResume() {
		super.onResume()
		adapter.startListening()
	}
	public override fun onPause() {
		adapter.stopListening()
		super.onPause()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = menuInflater
		inflater.inflate(R.menu.main_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.sign_out_menu -> {
				signOut()
				true
			}

			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun signOut() {
		auth.signOut()
		startActivity(Intent(this, LoginActivity::class.java))
		finish()
	}

	companion object {
		const val MESSAGES_CHILD = "messages"
	}
}