package com.tubianto.phoneotpauth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var buttonContinue: Button
    private var phoneCode: String = "62"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ccp.setOnCountryChangeListener { selectedCountry ->
            phoneCode = selectedCountry.phoneCode
        }
        editText = findViewById(R.id.editTextPhone)
        buttonContinue = findViewById(R.id.buttonContinue)
        buttonContinue.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View) {
                val code = phoneCode
                val number = editText.text.toString().trim()
                if (number.isEmpty() || number.length < 10)
                {
                    editText.error = "Valid number is required"
                    editText.requestFocus()
                    return
                }
                val phonenumber = "+$code$number"
                val intent = Intent(this@RegisterActivity, OtpActivity::class.java)
                intent.putExtra("phonenumber", phonenumber)
                startActivity(intent)
            }
        })
    }
    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
