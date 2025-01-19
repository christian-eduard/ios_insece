class SensorData {
  final double temperature;
  final double humidity;
  final double nitrogen;
  final double ph;
  final double phosphorus;
  final double conductivity;
  final double potassium;

  SensorData({
    this.temperature = 0.0,
    this.humidity = 0.0,
    this.nitrogen = 0.0,
    this.ph = 0.0,
    this.phosphorus = 0.0,
    this.conductivity = 0.0,
    this.potassium = 0.0,
  });

  factory SensorData.fromList(List<double> data) {
    return SensorData(
      humidity: data[0],      // Humedad
      temperature: data[1],   // Temperatura
      conductivity: data[2],  // Conductividad
      ph: data[3],           // pH
      nitrogen: data[4],      // Nitrógeno
      phosphorus: data[5],    // Fósforo
      potassium: data[6],     // Potasio
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'Temperatura': temperature,
      'Humedad': humidity,
      'Nitrógeno': nitrogen,
      'pH': ph,
      'Fósforo': phosphorus,
      'Conductividad': conductivity,
      'Potasio': potassium,
    };
  }

  List<String> toCsvRow() {
    return [
      temperature.toString(),
      humidity.toString(),
      nitrogen.toString(),
      ph.toString(),
      phosphorus.toString(),
      conductivity.toString(),
      potassium.toString(),
    ];
  }

  List<String> getCsvHeaders() {
    return [
      'Temperatura (°C)',
      'Humedad (%)',
      'Nitrógeno (mg/Kg)',
      'pH',
      'Fósforo (mg/Kg)',
      'Conductividad (μS/cm)',
      'Potasio (mg/Kg)',
    ];
  }
}
