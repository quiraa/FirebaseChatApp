package com.quiraadev.firebasechatapp.features.chatapp.presentation.screen

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quiraadev.firebasechatapp.R
import com.quiraadev.firebasechatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private val binding by viewBinding(ActivityMainBinding::bind)

	private lateinit var auth: FirebaseAuth
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setSupportActionBar(binding.chatAppBar)

		auth = Firebase.auth
		val firebaseUser = auth.currentUser

		if (firebaseUser == null) {
			startActivity(Intent(this, LoginActivity::class.java))
			finish()
			return
		}

		binding

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
}