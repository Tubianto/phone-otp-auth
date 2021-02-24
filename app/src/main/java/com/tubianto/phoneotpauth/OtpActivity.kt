package com.tubianto.phoneotpauth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var verificationid:String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var editText: EditText
    private lateinit var buttonSignIn: Button
    private val mCallBack = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(s:String, forceResendingToken:PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            verificationid = s
        }
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null)
            {
                progressBar.visibility = View.VISIBLE
                verifyCode(code)
            }
        }
        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@OtpActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressbar)
        editText = findViewById(R.id.editTextCode)
        val phonenumber = intent.getStringExtra("phonenumber")
        sendVerificationCode(phonenumber)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View) {
                val code = editText.text.toString().trim()
                if ((code.isEmpty() || code.length < 6))
                {
                    editText.error = "Enter code..."
                    editText.requestFocus()
                    return
                }
                verifyCode(code)
            }
        })
    }
    private fun verifyCode(code:String) {
        val credential = PhoneAuthProvider.getCredential(verificationid, code)
        signInWithCredential(credential)
    }
    private fun signInWithCredential(credential:PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@OtpActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@OtpActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun sendVerificationCode(number:String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallBack
        )
    }
}
