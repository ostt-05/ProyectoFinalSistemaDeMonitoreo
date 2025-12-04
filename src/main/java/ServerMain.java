import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("--- SERVIDOR INICIADO ---");
        DatabaseManager.inicializarBD();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Esperando clientes en puerto " + PUERTO + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress());
                new Thread(() -> manejarCliente(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manejarCliente(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String mensaje = SecurityUtil.desencriptar(inputLine);

                if (mensaje == null) continue;

                if (mensaje.startsWith("INSERT:")) {

                    String datos = mensaje.substring(7);
                    String[] partes = datos.split(",");
                    if (partes.length == 5) {
                        DatabaseManager.insertarDatos(
                                Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), Integer.parseInt(partes[2]),
                                partes[3], partes[4]
                        );
                    }
                }
                else if (mensaje.startsWith("GET_HISTORY:")) {
                    // vas a recibir backshots de "GET_HISTORY:YYYY-MM-DD,HH:mm:ss,HH:mm:ss"
                    System.out.println("Petición de historial recibida.");
                    String params = mensaje.substring(12); // quita el pendejo "GET_HISTORY:"
                    String[] partes = params.split(",");

                    if (partes.length == 3) {
                        String fecha = partes[0];
                        String hInicio = partes[1];
                        String hFin = partes[2];


                        java.util.List<String> resultados = DatabaseManager.consultarHistorial(fecha, hInicio, hFin);


                        String respuestaRaw = String.join(";", resultados);


                        if (respuestaRaw.isEmpty()) respuestaRaw = "NO_DATA";
                        String respuestaEncriptada = SecurityUtil.encriptar(respuestaRaw);
                        out.println(respuestaEncriptada);
                        System.out.println("Datos históricos enviados (" + resultados.size() + " registros).");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado.");
        }
    }
}