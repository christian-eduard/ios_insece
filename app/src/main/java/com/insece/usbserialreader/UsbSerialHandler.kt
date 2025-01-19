package com.insece.usbserialreader

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException

class UsbSerialHandler(private val context: Context, private val onUsbConnectionChanged: (Boolean) -> Unit) {

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var usbSerialPort: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null

    private val usbPermissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE)

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.apply {
                                setupUsbSerial(this)
                                onUsbConnectionChanged(true)
                            }
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (device != null && device == usbSerialPort?.driver?.device) {
                        close()
                        onUsbConnectionChanged(false)
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        if (usbManager.hasPermission(it)) {
                            setupUsbSerial(it)
                            onUsbConnectionChanged(true)
                        } else {
                            usbManager.requestPermission(it, usbPermissionIntent)
                        }
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter(ACTION_USB_PERMISSION).apply {
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED)
        }else {
            context.registerReceiver(usbReceiver, filter)
        }
    }

    fun setupDevice() {
        val availableDrivers: List<UsbSerialDriver> = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            return
        }

        val driver: UsbSerialDriver = availableDrivers[0]
        val device: UsbDevice = driver.device

        if (usbManager.hasPermission(device)) {
            setupUsbSerial(device)
            onUsbConnectionChanged(true)
        } else {
            usbManager.requestPermission(device, usbPermissionIntent)
        }
    }

    private fun setupUsbSerial(device: UsbDevice) {
        val driver: UsbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(device)
        connection = usbManager.openDevice(device)
        val port = driver.ports[0]

        usbSerialPort = port
        usbSerialPort?.open(connection)
        usbSerialPort?.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
    }

    private fun sendReadCommand() {
        val command = byteArrayOf(0x01, 0x03, 0x00, 0x00, 0x00, 0x07, 0x04, 0x08)
        usbSerialPort?.let { port ->
            if (port.isOpen) {
                try {
                    port.write(command, 2000)
                } catch (e: IOException) {
                    // Manejar errores de escritura
                }
            }
        }
    }

    fun readData(): ByteArray? {
        sendReadCommand()
        Thread.sleep(100)

        val buffer = ByteArray(1024)
        var numBytesRead = 0
        usbSerialPort?.let { port ->
            try {
                numBytesRead = port.read(buffer, 500)
            } catch (e: Exception) {
                // Manejar errores de lectura
            }
        }
        return if (numBytesRead > 0) buffer.copyOf(numBytesRead) else null
    }

    fun close() {
        try {
            usbSerialPort?.close()
            context.unregisterReceiver(usbReceiver)
        } catch (e: IOException) {
            // Manejar errores de cierre
        } finally {
            usbSerialPort = null
            connection?.close()
            connection = null

        }
    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.insece.usbserialreader.USB_PERMISSION"
    }
}