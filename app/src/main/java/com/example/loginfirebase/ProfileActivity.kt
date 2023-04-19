package com.example.loginfirebase

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loginfirebase.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}

class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Setup
        val bundle:Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")

        setup(email = email ?:"", provider = provider ?:"")

        //Guardar el providerType
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

    }

    private fun setup(email:String, provider: String) {
        title = "Inicio"
        mBinding.tvEmail.text=email
        mBinding.tvTypeAuth.text = provider

        mBinding.btnLogout.setOnClickListener {

            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}