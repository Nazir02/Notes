package com.example.notes


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.view.MAIN

class MainActivity : AppCompatActivity() {
    private lateinit var binder: ActivityMainBinding
    lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        navController = Navigation.findNavController(this, R.id.containerFragment)
        val navController = findNavController(R.id.containerFragment)
        setSupportActionBar(findViewById(R.id.toolBar))
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolBar)
                .setupWithNavController(navController, appBarConfiguration)
        MAIN = this
    }
}