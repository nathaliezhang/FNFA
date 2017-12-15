package com.example.nzhang.proto_festival

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by mel on 15/12/2017.
 */

class AnimActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim)
        val intent : Intent = Intent(this, MainActivity::class.java)
        android.os.Handler().postDelayed(
                { startActivity(intent) },
                3000)
    }
}
