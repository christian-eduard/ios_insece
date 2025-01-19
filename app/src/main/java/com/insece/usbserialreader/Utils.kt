package com.insece.usbserialreader

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileOutputStream
import java.io.OutputStream


fun shareCsvFile(context: Context, stringData: String, vararg numbers: Float) {

    if (numbers.size != 7) {
        throw IllegalArgumentException("Se requieren exactamente 7 números")
    }

    val timestamp = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault()).format(Date())
    val datestamp = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val hourstamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    val csvFile = File(context.cacheDir, "$stringData$timestamp.csv")
    try {
        val writer = FileWriter(csvFile)
        writer.append("Nombre;Fecha;Hora;Temp;Hum;PH;Cond;Nit;Fos;Pot\n")
        writer.append(stringData).append(";")
            .append(datestamp.toString()).append(";")
            .append(hourstamp.toString()).append(";")
            .append(numbers.joinToString(";") { String.format("%.2f", it) })
            .append("\n")
        writer.flush()
        writer.close()

    } catch (e: IOException) {
        e.printStackTrace()
        return
    }

    val fileUri: Uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        csvFile
    )
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir CSV"))

    csvFile.deleteOnExit()
}


fun saveScreenshot(context: Context, window: Window, imageName: String = generateImageName()) {
    // Verificar permisos para versiones de Android menores a 10
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                112 // Código de solicitud
            )
            return // Esperar a que el usuario conceda el permiso
        }
    }

    saveScreenshotWithPermissionGranted(context, window, imageName)
}

private fun saveScreenshotWithPermissionGranted(context: Context, window: Window, imageName: String) {
    // Captura la vista raíz de la aplicación
    val rootView: View = window.decorView.rootView
    rootView.isDrawingCacheEnabled = true

    // Crea el bitmap de la captura de pantalla
    val bitmap: Bitmap = Bitmap.createBitmap(rootView.drawingCache)
    rootView.isDrawingCacheEnabled = false

    val contentResolver = context.contentResolver

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Para Android 10 (API 29) y superior
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Insece")
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                // Notifica al usuario que la captura fue exitosa
                Toast.makeText(context, "Captura de pantalla guardada en la galería", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error al guardar la captura de pantalla", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Error al acceder al almacenamiento", Toast.LENGTH_LONG).show()
        }
    } else {
        // Para Android 9 (API 28) y versiones anteriores
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Insece")
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(context, "Error al crear el directorio", Toast.LENGTH_LONG).show()
            return
        }

        val file = File(directory, "$imageName.png")
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            // Añadir la imagen a la galería
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.png")
            }
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // Notifica al usuario que la captura fue exitosa
            Toast.makeText(context, "Captura de pantalla guardada en la galería", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar la captura de pantalla", Toast.LENGTH_LONG).show()
        }
    }
}

// Esta función debe llamarse para manejar la respuesta del usuario al permiso
fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (requestCode == 112) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permiso concedido, intentar guardar la captura de pantalla nuevamente
            saveScreenshotWithPermissionGranted(activity, activity.window, generateImageName())
        } else {
            Toast.makeText(activity, "Permiso de almacenamiento denegado", Toast.LENGTH_LONG).show()
        }
    }
}




fun generateImageName(): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "_$timestamp"
}