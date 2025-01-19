import SwiftUI
import ORSSerial

struct ContentView: View {
    @StateObject private var serialManager = SerialPortManager()
    @State private var showingSaveSheet = false
    @State private var fileName = ""
    @State private var saveAsImage = false
    @State private var saveAsCSV = false
    
    var body: some View {
        GeometryReader { geometry in
            if geometry.size.width > geometry.size.height {
                // Landscape layout
                HStack {
                    MeasurementView(title: "Insece", value: 0, unit: "", color: Color.black)
                    MeasurementView(title: "Temperatura", value: serialManager.temperature, unit: "°C", color: Color(red: 0.93, green: 0.03, blue: 0.03))
                    MeasurementView(title: "Humedad", value: serialManager.moisture, unit: "%", color: Color(red: 0.02, green: 0.75, blue: 0.75))
                    MeasurementView(title: "Nitrógeno", value: serialManager.nitrogen, unit: "mg/Kg", color: Color(red: 0.27, green: 0.49, blue: 0.67))
                    MeasurementView(title: "PH", value: serialManager.ph, unit: "", color: Color(red: 0.36, green: 0.87, blue: 0.37))
                    MeasurementView(title: "Fósforo", value: serialManager.phosphorus, unit: "mg/Kg", color: Color(red: 0.91, green: 0.83, blue: 0.76))
                    MeasurementView(title: "Conductividad", value: serialManager.conductivity, unit: "μS/cm", color: Color(red: 0.93, green: 0.61, blue: 0.03))
                    MeasurementView(title: "Potasio", value: serialManager.potassium, unit: "mg/Kg", color: Color(red: 0.93, green: 0.82, blue: 0.22))
                }
            } else {
                // Portrait layout
                VStack {
                    HStack {
                        MeasurementView(title: "Insece", value: 0, unit: "", color: Color.black)
                        MeasurementView(title: "Temperatura", value: serialManager.temperature, unit: "°C", color: Color(red: 0.93, green: 0.03, blue: 0.03))
                    }
                    HStack {
                        MeasurementView(title: "Humedad", value: serialManager.moisture, unit: "%", color: Color(red: 0.02, green: 0.75, blue: 0.75))
                        MeasurementView(title: "Nitrógeno", value: serialManager.nitrogen, unit: "mg/Kg", color: Color(red: 0.27, green: 0.49, blue: 0.67))
                    }
                    HStack {
                        MeasurementView(title: "PH", value: serialManager.ph, unit: "", color: Color(red: 0.36, green: 0.87, blue: 0.37))
                        MeasurementView(title: "Fósforo", value: serialManager.phosphorus, unit: "mg/Kg", color: Color(red: 0.91, green: 0.83, blue: 0.76))
                    }
                    HStack {
                        MeasurementView(title: "Conductividad", value: serialManager.conductivity, unit: "μS/cm", color: Color(red: 0.93, green: 0.61, blue: 0.03))
                        MeasurementView(title: "Potasio", value: serialManager.potassium, unit: "mg/Kg", color: Color(red: 0.93, green: 0.82, blue: 0.22))
                    }
                }
            }
            
            // Save Button
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    Button(action: {
                        showingSaveSheet = true
                    }) {
                        Image(systemName: "square.and.arrow.down")
                            .font(.system(size: 25))
                            .foregroundColor(.white)
                            .frame(width: 60, height: 60)
                            .background(Color.blue)
                            .clipShape(Circle())
                            .padding()
                    }
                }
            }
        }
        .sheet(isPresented: $showingSaveSheet) {
            SaveSheet(
                fileName: $fileName,
                saveAsImage: $saveAsImage,
                saveAsCSV: $saveAsCSV,
                onSave: {
                    if saveAsImage {
                        serialManager.saveAsImage(fileName: fileName)
                    }
                    if saveAsCSV {
                        serialManager.saveAsCSV(fileName: fileName)
                    }
                    fileName = ""
                    showingSaveSheet = false
                },
                onCancel: {
                    fileName = ""
                    showingSaveSheet = false
                }
            )
        }
    }
}

struct MeasurementView: View {
    let title: String
    let value: Float
    let unit: String
    let color: Color
    
    var body: some View {
        VStack {
            Text(title)
                .font(.headline)
                .foregroundColor(.white)
            Text(String(format: "%.1f", value) + unit)
                .font(.title)
                .foregroundColor(.white)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(color)
    }
}

struct SaveSheet: View {
    @Binding var fileName: String
    @Binding var saveAsImage: Bool
    @Binding var saveAsCSV: Bool
    let onSave: () -> Void
    let onCancel: () -> Void
    
    var body: some View {
        NavigationView {
            Form {
                TextField("Nombre del archivo", text: $fileName)
                Toggle("Guardar como imagen", isOn: $saveAsImage)
                Toggle("Guardar como CSV", isOn: $saveAsCSV)
                
                HStack {
                    Button("Cancelar", role: .cancel) {
                        onCancel()
                    }
                    Spacer()
                    Button("Guardar") {
                        onSave()
                    }
                    .disabled(fileName.isEmpty || (!saveAsImage && !saveAsCSV))
                }
            }
            .navigationTitle("Guardar datos")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
