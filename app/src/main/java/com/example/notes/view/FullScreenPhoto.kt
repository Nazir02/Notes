package com.example.notes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.notes.R
import kotlinx.android.synthetic.main.activity_photo__view.*

class FullScreenPhoto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo__view)
        val photo=intent.getStringExtra("photo")
        touch_Image_View.setImageURI(photo?.toUri())
       fullScrenBack.setOnClickListener {
           super.onBackPressed()
       }
    }

}