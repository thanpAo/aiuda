package com.example.aiuda

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aiuda.fragments.ListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fragments.HomeFragment
import fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Listener para manejar la selección de items en la barra de navegación
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(ListFragment())
                    true
                }
                R.id.nav_profile -> {
                    openCamera()
                    true
                }
                else -> false
            }
        }
        // Cargar el fragmento inicial
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    // Función para reemplazar el fragmento actual
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    // Función para abrir la cámara
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForResult.launch(cameraIntent)
    }

    // Evento que procesa el resultado de la cámara y envía la vista previa de la foto al ImageView
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                val imageView = findViewById<ImageView>(R.id.search_icon)
                imageView.setImageBitmap(imageBitmap)
            }
        }
}
