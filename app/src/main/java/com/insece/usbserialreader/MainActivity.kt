package com.insece.usbserialreader

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    private lateinit var usbSerialHandler: UsbSerialHandler
    private var job: Job? = null
    private var isUsbConnected by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbSerialHandler = UsbSerialHandler(this) { connected ->
            isUsbConnected = connected
        }
        usbSerialHandler.setupDevice()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SerialDataDisplay()
                }
            }
        }
        startReading()
    }

    @Composable
    fun SerialDataDisplay() {
        var data by remember { mutableStateOf("Esperando datos...") }
        var dataList by remember { mutableStateOf(mutableListOf<Int>()) }

        LaunchedEffect(Unit) {
            while (true) {
                val newData = usbSerialHandler.readData()
                newData?.let {
                    val dataString = it.joinToString(" ") { byte -> "%02X".format(byte) }
                    if (dataString.isNotEmpty()) {
                        data = "Datos recibidos: $dataString"
                        dataList =  processDataStringToBigEndianDecimal(dataString)
                    }
                }
                delay(1000) // Leemos cada segundo # MODIFICADO DANI
            }
        }

        if (isUsbConnected) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LandscapeLayout(
                    status ="Conexion establecida" ,
                    tempValue = dataList.getOrNull(1)?.div(10f) ?: 0f,
                    moisValue = dataList.getOrNull(0)?.div(10f) ?: 0f,
                    nitValue = dataList.getOrNull(4)?.div(1f) ?: 0f,
                    phValue = dataList.getOrNull(3)?.div(10f) ?: 0f,
                    fosValue = dataList.getOrNull(5)?.div(1f) ?: 0f,
                    conValue = dataList.getOrNull(2)?.div(1f) ?: 0f,
                    potValue = dataList.getOrNull(6)?.div(1f) ?: 0f
                )
            } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                PortraitLayout(
                    status = "Conexion establecida",
                    tempValue = dataList.getOrNull(1)?.div(10f) ?: 0f,
                    moisValue = dataList.getOrNull(0)?.div(10f) ?: 0f,
                    nitValue = dataList.getOrNull(4)?.div(1f) ?: 0f,
                    phValue = dataList.getOrNull(3)?.div(10f) ?: 0f,
                    fosValue = dataList.getOrNull(5)?.div(1f) ?: 0f,
                    conValue = dataList.getOrNull(2)?.div(1f) ?: 0f,
                    potValue = dataList.getOrNull(6)?.div(1f) ?: 0f
                )
            }
        }
        else{
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LandscapeLayout(
                    status = "Esperando conexion...",
                    tempValue = 0 / 10f,
                    moisValue = 0 / 10f,
                    nitValue = 0 / 1f,
                    phValue = 0 / 10f,
                    fosValue = 0 / 1f,
                    conValue = 0 / 1f,
                    potValue = 0 / 1f
                )
            } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                PortraitLayout(
                    status = "Esperando conexion...",
                    tempValue = 0 / 10f,
                    moisValue = 0 / 10f,
                    nitValue = 0 / 1f,
                    phValue = 0 / 10f,
                    fosValue = 0 / 1f,
                    conValue = 0 / 1f,
                    potValue = 0 / 1f
                )
            }
        }
    }


    private fun startReading() {
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(100)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        usbSerialHandler.close()
    }


    fun processDataStringToBigEndianDecimal(dataString: String): MutableList<Int> {
        // Dani : Dividimos la cadena por espacios para obtener los bytes como String
        val dataBytes = dataString.split(" ")

        // Dani : El tercer byte (índice 2) nos dice cuántos bytes de datos hay
        val numberOfDataBytes = dataBytes[2].toInt(16)

        // Dani : Tomamos los bytes de datos a partir del cuarto byte (índice 3)
        val data = dataBytes.subList(3, 3 + numberOfDataBytes)

        // Dani : Emparejamos los bytes y los transformamos a decimal (big endian)
        val bigEndianData = mutableListOf<Int>()

        for (i in data.indices step 2) {
            if (i + 1 < data.size) {
                // Dani :  Convertimos el par de bytes en decimal sin invertir (big endian)
                val bigEndianValue = (data[i].toInt(16) shl 8) + data[i + 1].toInt(16)
                bigEndianData.add(bigEndianValue)
            }
        }

        return bigEndianData
    }
}