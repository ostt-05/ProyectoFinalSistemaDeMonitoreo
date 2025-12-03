import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

public class ArduinoManager {
    private SerialPort activePort;
    private Thread readingThread;
    private boolean reading = false;
    private final DataListener listener;

    public ArduinoManager(DataListener listener) {
        this.listener = listener;
    }

    public SerialPort[] getAvailablePorts() {
        return SerialPort.getCommPorts();
    }

    public boolean connect(String portDescriptor) {
        activePort = SerialPort.getCommPort(portDescriptor);
        activePort.setBaudRate(9600);
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (activePort.openPort()) {
            startReading();
            return true;
        }
        return false;
    }

    public void disconnect() {
        reading = false;
        if (activePort != null) {
            activePort.closePort();
        }
    }

    private void startReading() {
        reading = true;
        readingThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(activePort.getInputStream())) {
                while (scanner.hasNextLine() && reading) {
                    String line = scanner.nextLine();
                    parseData(line);
                }
            } catch (Exception e) {
                listener.onError("Error leyendo puerto: " + e.getMessage());
            }
        });
        readingThread.start();
    }

    // Parsea el formato: "x:10,y:20,z:30"
    private void parseData(String line) {
        try {
            // Limpiar espacios y separar por comas
            String[] parts = line.trim().split(",");
            int x = 0, y = 0, z = 0;

            for (String part : parts) {
                String[] val = part.split(":");
                if (val.length == 2) {
                    if (val[0].trim().equals("x")) x = Integer.parseInt(val[1].trim());
                    if (val[0].trim().equals("y")) y = Integer.parseInt(val[1].trim());
                    if (val[0].trim().equals("z")) z = Integer.parseInt(val[1].trim());
                }
            }
            listener.onDataReceived(x, y, z);

        } catch (NumberFormatException e) {
            System.err.println("Error parseando datos: " + line);
        }
    }
}