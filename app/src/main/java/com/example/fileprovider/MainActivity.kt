package com.example.fileprovider

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.fileprovider.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    // Variable per al View Binding
    private lateinit var binding: ActivityMainBinding

    // Variable global per al fitxer on guardarem la foto
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflem el layout mitjançant View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurem el botó per fer fotos
        binding.btnTakeFoto.setOnClickListener {
            // Creem un Intent per obrir l'aplicació de càmera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { camIntent ->
                // Comprovem que hi hagi una activitat capaç de gestionar l'Intent
                camIntent.resolveActivity(packageManager)?.also {
                    // Creem el fitxer temporal on guardarem la foto
                    createPhotoFile()
                    // Obtenim un Uri segur a partir del File via FileProvider
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.fileprovider.fileprovider",
                        file
                    )
                    // Li diem a la càmera que guardi la imatge en aquest Uri
                    camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                }
            }
            // Llancem la càmera i esperem el resultat
            startForResult.launch(intent)
        }
    }

    // Mètode que crea un fitxer temporal per a la foto
    private fun createPhotoFile() {
        // Directori privat a l'emmagatzematge extern per a imatges
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Creem un fitxer amb nom "IMG_<timestamp>_.jpg"
        file = File.createTempFile(
            "IMG_${System.currentTimeMillis()}_",
            ".jpg",
            dir
        )
    }

    // Callback que rep el resultat de la càmera
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            // Si la càmera ha tornat OK
            if (result.resultCode == Activity.RESULT_OK) {
                // Convertim el fitxer en un Bitmap i el mostrem
                val imageBitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.miniatureFoto.setImageBitmap(imageBitmap)
            }
        }
}
