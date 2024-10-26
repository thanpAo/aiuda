package com.example.aiuda

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
                    showImageSourceDialog() // Mostrar diálogo para elegir entre cámara o galería
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

    // Mostrar diálogo para que el usuario elija entre tomar foto o seleccionar de galería
    private fun showImageSourceDialog() {
        val options = arrayOf("Tomar Foto", "Seleccionar de Galería")
        AlertDialog.Builder(this)
            .setTitle("Elige una opción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera() // Opción para abrir la cámara
                    1 -> openGallery() // Opción para abrir la galería
                }
            }
            .show()
    }

    // Función para abrir la cámara
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForResultCamera.launch(cameraIntent)
    }

    // Función para abrir la galería
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResultGallery.launch(galleryIntent)
    }

    // Evento que procesa el resultado de la cámara y envía la vista previa de la foto al ImageView
    private val startForResultCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                val imageView = findViewById<ImageView>(R.id.search_icon)
                imageView.setImageBitmap(imageBitmap)
            }
        }

    // Evento que procesa el resultado de la galería y envía la imagen seleccionada al ImageView
    private val startForResultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageUri = intent?.data
                val imageView = findViewById<ImageView>(R.id.search_icon)
                imageView.setImageURI(imageUri)
            }
        }
}
