import 'dart:async';
import 'package:flutter/material.dart';
import '../models/sensor_data.dart';
import '../services/usb_serial_service.dart';
import '../widgets/measurement_box.dart';
import '../widgets/save_dialog.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final UsbSerialService _usbService = UsbSerialService();
  bool _isConnected = false;
  late Timer _timer;
  SensorData _sensorData = SensorData();

  @override
  void initState() {
    super.initState();
    _setupUsb();
    _startReading();
  }

  Future<void> _setupUsb() async {
    bool connected = await _usbService.setupDevice();
    setState(() {
      _isConnected = connected;
    });
  }

  void _startReading() {
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) async {
      if (_isConnected) {
        final rawData = await _usbService.readData();
        if (rawData != null) {
          final processedData = _usbService.processData(rawData);
          setState(() {
            _sensorData = SensorData.fromList(processedData);
          });
        }
      }
    });
  }

  @override
  void dispose() {
    _timer.cancel();
    _usbService.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_isConnected ? 'Conectado' : 'Esperando conexión...'),
        backgroundColor: _isConnected ? Colors.green : Colors.red,
      ),
      body: OrientationBuilder(
        builder: (context, orientation) {
          return orientation == Orientation.portrait
              ? _buildPortraitLayout()
              : _buildLandscapeLayout();
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          showDialog(
            context: context,
            builder: (context) => SaveDialog(sensorData: _sensorData),
          );
        },
        backgroundColor: Colors.blue,
        child: const Icon(Icons.save),
      ),
    );
  }

  Widget _buildPortraitLayout() {
    return GridView.count(
      crossAxisCount: 2,
      childAspectRatio: 1.5,
      padding: const EdgeInsets.all(8.0),
      children: _buildMeasurementBoxes(),
    );
  }

  Widget _buildLandscapeLayout() {
    return GridView.count(
      crossAxisCount: 4,
      childAspectRatio: 1.2,
      padding: const EdgeInsets.all(8.0),
      children: _buildMeasurementBoxes(),
    );
  }

  List<Widget> _buildMeasurementBoxes() {
    return [
      MeasurementBox(
        title: 'INSECE',
        value: 0,
        unit: '',
        backgroundColor: MeasurementColors.insece,
      ),
      MeasurementBox(
        title: 'Temperatura',
        value: _sensorData.temperature,
        unit: '°C',
        backgroundColor: MeasurementColors.temperature,
      ),
      MeasurementBox(
        title: 'Humedad',
        value: _sensorData.humidity,
        unit: '%',
        backgroundColor: MeasurementColors.humidity,
      ),
      MeasurementBox(
        title: 'Nitrógeno',
        value: _sensorData.nitrogen,
        unit: 'mg/Kg',
        backgroundColor: MeasurementColors.nitrogen,
      ),
      MeasurementBox(
        title: 'pH',
        value: _sensorData.ph,
        unit: '',
        backgroundColor: MeasurementColors.ph,
      ),
      MeasurementBox(
        title: 'Fósforo',
        value: _sensorData.phosphorus,
        unit: 'mg/Kg',
        backgroundColor: MeasurementColors.phosphorus,
      ),
      MeasurementBox(
        title: 'Conductividad',
        value: _sensorData.conductivity,
        unit: 'μS/cm',
        backgroundColor: MeasurementColors.conductivity,
      ),
      MeasurementBox(
        title: 'Potasio',
        value: _sensorData.potassium,
        unit: 'mg/Kg',
        backgroundColor: MeasurementColors.potassium,
      ),
    ];
  }
}
