package com.example.loginfirebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.loginfirebase.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAuthBinding
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()

        mBinding.authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        val email:String? = prefs.getString("email", null)
        val provider:String? = prefs.getString("provider", null)

        if(email != null && provider != null){
            mBinding.authLayout.visibility = View.INVISIBLE
            showProfile(email,ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        title = "Autenticación"
        mBinding.btnSignUp.setOnClickListener {
            if (mBinding.etEmail.text!!.isNotEmpty() && mBinding.etPassword.text!!.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(mBinding.etEmail.text.toString(),
                    mBinding.etPassword.text.toString())
                    .addOnCompleteListener{

                        if (it.isSuccessful){
                            showProfile(it.result?.user?.email?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                }
            }

        }

        mBinding.btnLogin.setOnClickListener {
            if (mBinding.etEmail.text!!.isNotEmpty() && mBinding.etPassword.text!!.isNotEmpty()){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(mBinding.etEmail.text.toString(),
                    mBinding.etPassword.text.toString())
                    .addOnCompleteListener{

                        if (it.isSuccessful){
                            showProfile(it.result?.user?.email?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }

                    }
            }
        }

        mBinding.btnGoogle.setOnClickListener {
            // Configuración

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient: GoogleSignInClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)

        }

    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun showProfile(email: String, provider: ProviderType){

        val homeIntent: Intent= Intent(this,ProfileActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){
            val task: Task<GoogleSignInAccount> =GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential:AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(){
                        if (it.isSuccessful){
                            showProfile(account.email ?: "", ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }

                    }

                }

            }catch (e:ApiException){
                showAlert()
            }



        }

    }
}