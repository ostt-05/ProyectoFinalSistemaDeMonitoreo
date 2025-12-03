import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// Ya no necesitamos imports de LocalDate/LocalTime aquí porque vienen como String

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:monitorBD.db";

    public static void inicializarBD() {
        // La creación de la tabla sigue igual, cumpliendo con [cite: 16]
        String sql = "CREATE TABLE IF NOT EXISTS datos_sensor (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "x INTEGER NOT NULL," +
                "y INTEGER NOT NULL," +
                "z INTEGER NOT NULL," +
                "fecha_de_captura TEXT NOT NULL," +
                "hora_de_captura TEXT NOT NULL);";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Base de datos lista.");
        } catch (SQLException e) {
            System.err.println("Error BD: " + e.getMessage());
        }
    }

    // --- METODO MODIFICADO ---
    public static void insertarDatos(int x, int y, int z, String fecha, String hora) {
        String sql = "INSERT INTO datos_sensor(x, y, z, fecha_de_captura, hora_de_captura) VALUES(?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);
            // Insertamos los valores recibidos del cliente
            pstmt.setString(4, fecha); // [cite: 22]
            pstmt.setString(5, hora);  // [cite: 23]

            pstmt.executeUpdate();
            System.out.println("Guardado: " + fecha + " " + hora + " -> [" + x + "," + y + "," + z + "]");

        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
        }
    }
    public static List<String> consultarHistorial(String fecha, String horaInicio, String horaFin) {
        List<String> resultados = new ArrayList<>();
        // Consulta SQL para filtrar por fecha y rango de hora
        String sql = "SELECT x, y, z, hora_de_captura FROM datos_sensor " +
                "WHERE fecha_de_captura = ? " +
                "AND hora_de_captura >= ? AND hora_de_captura <= ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fecha);
            pstmt.setString(2, horaInicio);
            pstmt.setString(3, horaFin);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String hora = rs.getString("hora_de_captura");

                // Formato compacto para enviar por red: "x:y:z:hora"
                resultados.add(x + ":" + y + ":" + z + ":" + hora);
            }
        } catch (SQLException e) {
            System.err.println("Error consultando historial: " + e.getMessage());
        }
        return resultados;
    }
}