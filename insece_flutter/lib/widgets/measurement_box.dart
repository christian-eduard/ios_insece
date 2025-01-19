import 'package:flutter/material.dart';

class MeasurementBox extends StatelessWidget {
  final String title;
  final double value;
  final String unit;
  final Color backgroundColor;

  const MeasurementBox({
    super.key,
    required this.title,
    required this.value,
    required this.unit,
    required this.backgroundColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(4.0),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(8.0),
        border: Border.all(color: Colors.black, width: 1),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            spreadRadius: 1,
            blurRadius: 3,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.black,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Text(
            '${value.toStringAsFixed(1)}$unit',
            style: const TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: Colors.black,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}

class MeasurementColors {
  static const Color insece = Color(0xFF000000);
  static const Color temperature = Color(0xFFEC0708);
  static const Color humidity = Color(0xFF06BFC0);
  static const Color nitrogen = Color(0xFF457CAA);
  static const Color ph = Color(0xFF5CDD5E);
  static const Color phosphorus = Color(0xFFE9D3C1);
  static const Color conductivity = Color(0xFFEC9B08);
  static const Color potassium = Color(0xFFECD237);
}
