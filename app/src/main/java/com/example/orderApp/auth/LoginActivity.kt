package com.example.orderApp.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderApp.activities.OrderStatusActivity
import com.example.orderApp.databinding.ActivityLogin2Binding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    lateinit var loginActivity:ActivityLogin2Binding
    var pd: ProgressDialog? = null
    var googleSignInClient: GoogleSignInClient? = null
    private lateinit var sharedPreferences: SharedPreferences

    private var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivity=ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(loginActivity.root)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
//                                Log.w(AcceptOrderActivity.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                val editor = sharedPreferences.edit()
                editor.putString("fcmToken",token)
                editor.putString("jwtToken",token)
                editor.apply()
            })

        // Check if the user is already logged in
        if (isLoggedIn()) {
            navigateToAcceptOrderActivity()
            return
        }

        pd = ProgressDialog(this)
        loginActivity.buttonLogin.setOnClickListener {
            val email = loginActivity.editTextEmail.text.toString().trim()
            val password = loginActivity.editTextPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
               loginUser(email,password)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun navigateToAcceptOrderActivity() {
        val intent = Intent(this, OrderStatusActivity::class.java)
        startActivity(intent)
        finish()
    }

   fun loginUser(email: String, password: String) {
       val baseUrl = "http://api.freeorder.co.uk/api/auth"
       val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
       urlBuilder?.addQueryParameter("email", email)
       urlBuilder?.addQueryParameter("password", password)
       val url = urlBuilder?.build().toString()
       pd!!.setMessage("Please Wait!...")
       pd?.setCanceledOnTouchOutside(false)
       pd!!.show()
       val client = OkHttpClient()
       Log.e("UserLoginManager", "$email $password")

       val request = Request.Builder()
           .url(url)
           .get()
           .addHeader("FCMToken", sharedPreferences.getString("fcmToken", "")!!)
           .build()


       client.newCall(request).enqueue(object : Callback {
           override fun onFailure(call: Call, e: IOException) {
               Log.e("UserLoginManager", "Failed to make API request: ${e.message}")
               // Handle failure, show error to the user, etc.
           }

           override fun onResponse(call: Call, response: Response) {
               val responseData = response.body?.string()
               try {
                   val jsonObject = JSONObject(responseData)
                   val status = response.code
                   if (status == 200) {
                       // Rest of your code

                       FirebaseMessaging.getInstance().token.addOnCompleteListener(
                           OnCompleteListener { task ->
                               if (!task.isSuccessful) {
                                   // Log.w(AcceptOrderActivity.TAG, "Fetching FCM registration token failed", task.exception)
                                   return@OnCompleteListener
                               }
                               // Get new FCM registration token
                               val token = task.result
                               var tokens = jsonObject.getString("token")
                               var userId = jsonObject.getString("userId")
                               Log.d("adasdd", tokens)
                               val editor = sharedPreferences.edit()
                               editor.putBoolean("isLoggedIn", true)
                               editor.putString("token", tokens)
                               editor.putString("userId", userId)
                               editor.putString("email", email)
                               editor.putString("password", password)
                               Log.d("emailPassword",email+password)
                               editor.putString("fcm", token)
                               editor.apply()
                               // Navigate to AcceptOrderActivity
                               navigateToAcceptOrderActivity()
                               pd?.dismiss()
                           })

                   } else {
                       // Rest of your code
                       // Login failed, handle the error
                       val errors = jsonObject.getJSONObject("errors")
                       val emailErrors = errors.getJSONArray("email")
                       val passwordErrors = errors.getJSONArray("password")
                       pd?.dismiss()
                       Toast.makeText(this@LoginActivity, "email or password are incorrect!", Toast.LENGTH_SHORT).show()
                       // Log or display the error messages
                       for (i in 0 until emailErrors.length()) {
                           Log.e("UserLoginManager", "Email error: ${emailErrors.getString(i)}")
                       }
                       for (i in 0 until passwordErrors.length()) {
                           Log.e("UserLoginManager", "Password error: ${passwordErrors.getString(i)}")
                       }
                   }
               } catch (e: JSONException) {
                   runOnUiThread(Runnable {
                       pd?.dismiss()
                       Toast.makeText(this@LoginActivity, "email or password are incorrect!", Toast.LENGTH_SHORT).show()
                   })
                   Log.e("UserLoginManager", "Error parsing JSON response: ${e.message}")
                   // Handle parsing error, show error to the user, etc.
               }
           }
       })
   }

    override fun onBackPressed() {
        finishAffinity()
    }
}