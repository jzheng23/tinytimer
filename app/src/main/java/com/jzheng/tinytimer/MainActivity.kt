package com.jzheng.tinytimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jzheng.tinytimer.data.DeviceInfo
import com.jzheng.tinytimer.ui.navigation.Navigation
import com.jzheng.tinytimer.ui.theme.TimerTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                Navigation()
            }
        }
        DeviceInfo.getAllInfo().forEach { (key, value) ->
            Log.d("Device info", "$key: $value")
        }

        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Authentication", "signInAnonymously:success")
                } else {
                    Log.w("Authentication", "signInAnonymously:failure", task.exception)
                }
            }
    }


}










