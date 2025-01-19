import 'dart:io';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';
import 'package:csv/csv.dart';
import 'package:intl/intl.dart';
import '../models/sensor_data.dart';

class SaveDialog extends StatefulWidget {
  final SensorData sensorData;

  const SaveDialog({
    super.key,
    required this.sensorData,
  });

  @override
  State<SaveDialog> createState() => _SaveDialogState();
}

class _SaveDialogState extends State<SaveDialog> {
  final TextEditingController _nameController = TextEditingController();
  bool _saveAsImage = false;
  bool _saveAsCsv = false;

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  Future<void> _saveCsv() async {
    final directory = await getApplicationDocumentsDirectory();
    final fileName = '${_nameController.text}_${_getTimestamp()}.csv';
    final file = File('${directory.path}/$fileName');

    final rows = [
      widget.sensorData.getCsvHeaders(),
      widget.sensorData.toCsvRow(),
    ];

    final csvData = const ListToCsvConverter().convert(rows);
    await file.writeAsString(csvData);
    
    if (!mounted) return;
    
    await Share.shareXFiles(
      [XFile(file.path)],
      text: 'Datos del sensor INSECE',
    );
  }

  String _getTimestamp() {
    return DateFormat('dd-MM-yyyy_HH-mm-ss').format(DateTime.now());
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16.0),
      ),
      child: Container(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: 'Nombre de la captura',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16.0),
            CheckboxListTile(
              title: const Text('Guardar como imagen'),
              value: _saveAsImage,
              onChanged: (value) {
                setState(() {
                  _saveAsImage = value ?? false;
                });
              },
            ),
            CheckboxListTile(
              title: const Text('Enviar como CSV'),
              value: _saveAsCsv,
              onChanged: (value) {
                setState(() {
                  _saveAsCsv = value ?? false;
                });
              },
            ),
            const SizedBox(height: 16.0),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                TextButton(
                  onPressed: () => Navigator.pop(context),
                  style: TextButton.styleFrom(
                    backgroundColor: Colors.red,
                    foregroundColor: Colors.white,
                  ),
                  child: const Text('Cancelar'),
                ),
                TextButton(
                  onPressed: () async {
                    if (_saveAsCsv) {
                      await _saveCsv();
                    }
                    if (_saveAsImage) {
                      // TODO: Implementar captura de pantalla cuando sea necesario
                    }
                    if (mounted) {
                      Navigator.pop(context);
                    }
                  },
                  style: TextButton.styleFrom(
                    backgroundColor: Colors.blue,
                    foregroundColor: Colors.white,
                  ),
                  child: const Text('Guardar'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
