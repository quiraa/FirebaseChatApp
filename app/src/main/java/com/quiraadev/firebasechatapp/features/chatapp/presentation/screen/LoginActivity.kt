package com.quiraadev.firebasechatapp.features.chatapp.presentation.screen

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quiraadev.firebasechatapp.R
import com.quiraadev.firebasechatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

	private val binding by viewBinding(ActivityLoginBinding::bind)

	private lateinit var googleSignInClient: GoogleSignInClient
	private lateinit var auth: FirebaseAuth

	override fun onStart() {
		super.onStart()
		// Check if user is signed in (non-null) and update UI accordingly.
		val currentUser = auth.currentUser
		updateUI(currentUser)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// * Configures Google Sign In

		val gso = GoogleSignInOptions
			.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getString(R.string.default_web_client_id))
			.requestEmail()
			.build()

		googleSignInClient = GoogleSignIn.getClient(this, gso)
		auth = Firebase.auth

		binding.signInButton.setOnClickListener {
			signIn()
		}
	}

	private fun signIn() {
		val signInIntent = googleSignInClient.signInIntent
		resultLauncher.launch(signInIntent)
	}

	private var resultLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
			if (result.resultCode == Activity.RESULT_OK) {
				val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
				try {
					val account = task.getResult(ApiException::class.java)
					Log.d(TAG, "FirebaseAuthWithGoogle" + account.id)
					firebaseAuthWithGoogle(tokenId = account.idToken!!)
				} catch (error: ApiException) {
					Log.d(TAG, "Google SignIn Failed")
				}
			}
		}

	private fun firebaseAuthWithGoogle(tokenId: String) {
		val credential = GoogleAuthProvider.getCredential(tokenId, null)
		auth.signInWithCredential(credential).addOnCompleteListener { task ->
			if (task.isSuccessful) {
				Log.d(TAG, "SignInWithCredential: Success")
				val user = auth.currentUser
				updateUI(currentUser = user)
			} else {
				Log.w(TAG, "SignInWithCredential: Failure", task.exception)
				updateUI(null)
			}
		}
	}

	private fun updateUI(currentUser: FirebaseUser?) {
		if (currentUser != null) {
			startActivity(Intent(this@LoginActivity, MainActivity::class.java))
			finish()
		}
	}

	companion object {
		private const val TAG = "LoginActivity"
	}
}