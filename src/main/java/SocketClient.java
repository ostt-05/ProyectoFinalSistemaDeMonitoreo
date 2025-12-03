import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in; // Nuevo reader
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static boolean conectar() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            // Inicializamos el lector aquí
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (Exception e) {
            System.err.println("No se pudo conectar: " + e.getMessage());
            return false;
        }
    }

    public static void enviarDatosSensor(int x, int y, int z) {
        if (socket != null && !socket.isClosed()) {
            LocalDateTime ahora = LocalDateTime.now();
            String payload = x + "," + y + "," + z + "," +
                    ahora.toLocalDate() + "," + ahora.format(TIME_FORMATTER);
            out.println(SecurityUtil.encriptar("INSERT:" + payload));
        }
    }

    // Nuevo método Síncrono para pedir historial
    // Sincronizado para evitar conflictos con el hilo del monitor si se cruzaran
    public static synchronized String pedirHistorial(String fecha, String horaInicio, String horaFin) {
        try {
            if (socket == null || socket.isClosed()) return null;

            // 1. Enviar Petición
            String request = "GET_HISTORY:" + fecha + "," + horaInicio + "," + horaFin;
            out.println(SecurityUtil.encriptar(request));

            // 2. Esperar Respuesta (Bloqueante)
            String respuestaEncriptada = in.readLine();

            if (respuestaEncriptada != null) {
                return SecurityUtil.desencriptar(respuestaEncriptada);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void desconectar() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}