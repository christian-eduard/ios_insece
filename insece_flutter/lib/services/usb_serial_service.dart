import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class UsbSerialService {
  static const platform = MethodChannel('com.insece.usbserialreader/usb');
  bool _isConnected = false;
  Timer? _timer;

  Future<bool> setupDevice() async {
    try {
      final bool result = await platform.invokeMethod('setupDevice');
      _isConnected = result;
      return result;
    } on PlatformException catch (e) {
      print('Error setting up device: ${e.message}');
      return false;
    }
  }

  Future<List<int>?> readData() async {
    if (!_isConnected) return null;

    try {
      final List<dynamic>? result = await platform.invokeMethod('readData');
      return result?.cast<int>();
    } on PlatformException catch (e) {
      print('Error reading data: ${e.message}');
      return null;
    }
  }

  List<double> processData(List<int> rawData) {
    List<double> processedData = List.filled(7, 0.0);
    if (rawData.length < 19) return processedData;

    for (int i = 0; i < 7; i++) {
      int index = 3 + (i * 2);
      if (index + 1 < rawData.length) {
        int value = (rawData[index] << 8) | rawData[index + 1];
        
        switch (i) {
          case 0: // Humedad
          case 1: // Temperatura
          case 3: // pH
            processedData[i] = value / 10.0;
            break;
          default:
            processedData[i] = value.toDouble();
        }
      }
    }
    return processedData;
  }

  void startReading(Function(List<double>) onDataReceived) {
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) async {
      if (_isConnected) {
        final rawData = await readData();
        if (rawData != null) {
          final processedData = processData(rawData);
          onDataReceived(processedData);
        }
      }
    });
  }

  void dispose() {
    _timer?.cancel();
    platform.invokeMethod('dispose');
  }
}
