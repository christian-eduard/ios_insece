# Readme 


### Funciones :
- Leer los datos de una sonda que se conecta mediante 485 - modbus al dispositivo.
- Registrar los datos como captura de pantalla.
- Enviar una copia de los datos (en formato CSV) que esten registrados a traves del boton de compartir del dispositivo.

### Archivos :
*Layouts* : Archivo principal donde he creado las interfaces principales.
*Composables*: Archivo con composables diversos de kotlin que he creado para separar un poco las interfaces. Es igual que el archivo layouts pero en vez de vistas enteras con componentes pequeños.
*Utils*: Archivo donde pongo funciones sueltas que no encajaban mejor en ningun otro archivo.
*UsbSerialHandler*: Archivo que gestiona la conexion USB y abre el puerto de serie cuando detecta que se conecta.
*MainActivity*:  Archivo principal de la aplicacion. Punto de entrada de codigo.
