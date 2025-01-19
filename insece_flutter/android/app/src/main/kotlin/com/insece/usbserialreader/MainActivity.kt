package com.insece.usbserialreader

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.*
import android.os.Build
import com.hoho.android.usbserial.driver.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.IOException

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.insece.usbserialreader/usb"
    private var usbSerialPort: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    private val ACTION_USB_PERMISSION = "com.insece.usbserialreader.USB_PERMISSION"

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.apply { setupUsbSerial(this) }
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    setupDevice()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    dispose()
                }
            }
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "setupDevice" -> {
                    val success = setupDevice()
                    result.success(success)
                }
                "readData" -> {
                    val data = readData()
                    result.success(data)
                }
                "dispose" -> {
                    dispose()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }

        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(usbReceiver, filter)
        }
    }

    private fun setupDevice(): Boolean {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers: List<UsbSerialDriver> = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) return false

        val driver: UsbSerialDriver = availableDrivers[0]
        val device: UsbDevice = driver.device

        if (usbManager.hasPermission(device)) {
            return setupUsbSerial(device)
        } else {
            val permissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE)
            usbManager.requestPermission(device, permissionIntent)
        }
        return false
    }

    private fun setupUsbSerial(device: UsbDevice): Boolean {
        try {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val driver: UsbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(device)
            connection = usbManager.openDevice(device)
            val port = driver.ports[0]

            usbSerialPort = port
            usbSerialPort?.open(connection)
            usbSerialPort?.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun readData(): ByteArray? {
        val command = byteArrayOf(0x01, 0x03, 0x00, 0x00, 0x00, 0x07, 0x04, 0x08)
        val buffer = ByteArray(1024)
        var numBytesRead = 0

        usbSerialPort?.let { port ->
            try {
                port.write(command, 2000)
                Thread.sleep(100)
                numBytesRead = port.read(buffer, 500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return if (numBytesRead > 0) buffer.copyOf(numBytesRead) else null
    }

    private fun dispose() {
        try {
            usbSerialPort?.close()
            unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            usbSerialPort = null
            connection?.close()
            connection = null
        }
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
}
