package com.britomartis.android.britobudget

import android.os.Bundle
import android.text.Layout
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    val TAG = "MyMessage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        //declare the animation
        val ttp = AnimationUtils.loadAnimation(this, R.anim.ttp);
        val ttb = AnimationUtils.loadAnimation(this, R.anim.ttb);

        val headertitle = findViewById(R.id.tite) as TextView
        val headerimage = findViewById(R.id.exit) as ImageView
        val sideimage = findViewById(R.id.slide) as ImageView

        //set the animation

        headerimage.startAnimation(ttp)
        headertitle.startAnimation(ttp)
        sideimage.startAnimation(ttb)


        btn_email.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                data = Uri.parse("mailto:")
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, "teambritomartis@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "FeedBack")
            }
            if (intent.resolveActivity(packageManager) != null) {
                intent.setPackage("com.google.android.gm")
                startActivity(intent)
            } else {
                Log.d(TAG, "No app available to send email.")
            }

        }

        exit.setOnClickListener { System.exit(-1) }

        chat.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }


}
