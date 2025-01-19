import Foundation
import ORSSerial
import SwiftUI

class SerialPortManager: NSObject, ObservableObject, ORSSerialPortDelegate {
    private var serialPort: ORSSerialPort?
    private var readTimer: Timer?
    
    @Published var temperature: Float = 0.0
    @Published var moisture: Float = 0.0
    @Published var nitrogen: Float = 0.0
    @Published var ph: Float = 0.0
    @Published var phosphorus: Float = 0.0
    @Published var conductivity: Float = 0.0
    @Published var potassium: Float = 0.0
    @Published var isConnected: Bool = false
    
    override init() {
        super.init()
        setupSerialPort()
        startReadTimer()
    }
    
    private func setupSerialPort() {
        // Buscar puerto serial disponible
        if let availablePort = ORSSerialPortManager.shared().availablePorts.first {
            serialPort = ORSSerialPort(path: availablePort.path)
            serialPort?.baudRate = 9600
            serialPort?.delegate = self
            serialPort?.open()
        }
    }
    
    private func startReadTimer() {
        readTimer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
            self?.sendReadCommand()
        }
    }
    
    private func sendReadCommand() {
        // Comando de lectura: mismo protocolo que Android
        let command: [UInt8] = [0x01, 0x03, 0x00, 0x00, 0x00, 0x07, 0x04, 0x08]
        let data = Data(command)
        serialPort?.send(data)
    }
    
    func serialPort(_ serialPort: ORSSerialPort, didReceive data: Data) {
        // Procesar datos recibidos
        let bytes = [UInt8](data)
        if bytes.count >= 3 {
            let numberOfDataBytes = Int(bytes[2])
            let dataBytes = Array(bytes[3..<min(3 + numberOfDataBytes, bytes.count)])
            
            // Procesar datos en formato big-endian
            var index = 0
            while index < dataBytes.count - 1 {
                let value = (Int(dataBytes[index]) << 8) + Int(dataBytes[index + 1])
                
                // Asignar valores según el índice
                DispatchQueue.main.async {
                    switch index / 2 {
                    case 0: self.moisture = Float(value) / 10.0
                    case 1: self.temperature = Float(value) / 10.0
                    case 2: self.conductivity = Float(value)
                    case 3: self.ph = Float(value) / 10.0
                    case 4: self.nitrogen = Float(value)
                    case 5: self.phosphorus = Float(value)
                    case 6: self.potassium = Float(value)
                    default: break
                    }
                }
                index += 2
            }
        }
    }
    
    func serialPortWasOpened(_ serialPort: ORSSerialPort) {
        DispatchQueue.main.async {
            self.isConnected = true
        }
    }
    
    func serialPortWasClosed(_ serialPort: ORSSerialPort) {
        DispatchQueue.main.async {
            self.isConnected = false
        }
    }
    
    func saveAsImage(fileName: String) {
        // Implementar guardado como imagen
        let timestamp = DateFormatter.localizedString(from: Date(), dateStyle: .medium, timeStyle: .medium)
        // Aquí iría la lógica de captura y guardado de imagen
    }
    
    func saveAsCSV(fileName: String) {
        let timestamp = DateFormatter.localizedString(from: Date(), dateStyle: .medium, timeStyle: .medium)
        let csvString = """
        Fecha,Temperatura,Humedad,Nitrógeno,PH,Fósforo,Conductividad,Potasio
        \(timestamp),\(temperature),\(moisture),\(nitrogen),\(ph),\(phosphorus),\(conductivity),\(potassium)
        """
        
        if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let fileURL = dir.appendingPathComponent("\(fileName).csv")
            do {
                try csvString.write(to: fileURL, atomically: true, encoding: .utf8)
            } catch {
                print("Error al guardar CSV: \(error)")
            }
        }
    }
    
    deinit {
        readTimer?.invalidate()
        serialPort?.close()
    }
}
