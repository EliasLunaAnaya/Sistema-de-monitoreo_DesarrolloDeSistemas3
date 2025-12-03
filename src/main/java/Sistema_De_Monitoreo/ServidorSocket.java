
package Sistema_De_Monitoreo;

import java.io.*;
import java.net.*;
import java.sql.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class ServidorSocket {
    private static final String CLAVE = "1234567890123456"; // 16 caracteres para AES
    private static final String rutaBD = "jdbc:sqlite:monitorDB.db";

    public static void main(String[] args) {
        inicializarDB();
        try (ServerSocket serverSocket = new ServerSocket(5000)) { //Socket en el puerto 5000
            System.out.println("Servidor escuchando en puerto 5000...");
            while (true) {
                Socket cliente = serverSocket.accept();
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Este metodo es el que se encarga de insertar datos o realizar una consulta en la base de datos
    private static void manejarCliente(Socket cliente) {
        try (ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream())) {

            //Desencriptar el mensaje
            String mensajeEncriptado = (String) ois.readObject();
            String mensaje = desencriptar(mensajeEncriptado);

            //Si el mensaje empieza con insert...
            if (mensaje.startsWith("INSERT")) {
                String[] partes = mensaje.split("\n");
                if (partes.length < 4) {
                    oos.writeObject(encriptar("ERROR: formato incorrecto"));
                    return;
                }
                int x = Integer.parseInt(partes[1]);
                int y = Integer.parseInt(partes[2]);
                int z = Integer.parseInt(partes[3]);
                insertarDB(x, y, z);
                oos.writeObject(encriptar("OK"));
            } else if (mensaje.startsWith("CONSULTA")) {
                String[] partes = mensaje.split("\n");
                String filtro = partes.length > 1 ? partes[1] : "";
                String datos = obtenerDatos(filtro);
                oos.writeObject(encriptar(datos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Inicializa la db en caso de no estar creada
    private static void inicializarDB() {
        try (Connection conn = DriverManager.getConnection(rutaBD);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS datos_sensor (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "z INTEGER NOT NULL," +
                    "fecha_de_captura TEXT NOT NULL," +
                    "hora_de_captura TEXT NOT NULL)");
            System.out.println("Base de datos inicializada en: " + rutaBD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Metodo para insertar en la base de datos
    private static void insertarDB(int x, int y, int z) throws SQLException {
        Connection conn = DriverManager.getConnection(rutaBD);
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO datos_sensor(x,y,z,fecha_de_captura,hora_de_captura) VALUES(?,?,?,?,?)"
        );
        String fecha = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        ps.setInt(1, x);
        ps.setInt(2, y);
        ps.setInt(3, z);
        ps.setString(4, fecha);
        ps.setString(5, hora);
        ps.executeUpdate();

        System.out.println("X: " + x + " ,y: " + y + " ,z: " + z);


        conn.close();
    }

    //Metodo para realizar la consuslta en la base de datos
    private static String obtenerDatos(String filtro) throws SQLException {
        Connection conn = DriverManager.getConnection(rutaBD);
        String sql;
        PreparedStatement ps;

        filtro = filtro.trim();
        System.out.println("Filtro recibido: " + filtro);

        if (filtro.isEmpty()) {
            sql = "SELECT x,y,z FROM datos_sensor";
            ps = conn.prepareStatement(sql);
        } else if (filtro.startsWith("FROM")) {
            String[] partes = filtro.replace("FROM", "").trim().split(" ");
            if (partes.length == 1) {
                // Solo fecha inicial
                sql = "SELECT x,y,z FROM datos_sensor WHERE fecha_de_captura >= ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, partes[0]);
            } else if (partes.length == 2) {
                // Fecha inicial + hora inicial
                sql = "SELECT x,y,z FROM datos_sensor WHERE (fecha_de_captura > ?) OR (fecha_de_captura = ? AND hora_de_captura >= ?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, partes[0]);
                ps.setString(2, partes[0]);
                ps.setString(3, partes[1]);
            } else {
                conn.close();
                return "";
            }
        } else if (filtro.startsWith("BETWEEN")) {
            String[] partes = filtro.replace("BETWEEN", "").trim().split(" AND ");
            if (partes.length == 2) {
                String[] inicio = partes[0].split(" ");
                String[] fin = partes[1].split(" ");

                // Fecha inicial y fecha final
                if (inicio.length == 1 && fin.length == 1) {
                    sql = "SELECT x,y,z FROM datos_sensor WHERE fecha_de_captura BETWEEN ? AND ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, inicio[0]);
                    ps.setString(2, fin[0]);

                    // Fecha inicial, hora inicial, fecha final
                } else if (inicio.length == 2 && fin.length == 1) {
                    sql = "SELECT x,y,z FROM datos_sensor WHERE (fecha_de_captura > ? OR (fecha_de_captura = ? AND hora_de_captura >= ?)) AND fecha_de_captura <= ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, inicio[0]);
                    ps.setString(2, inicio[0]);
                    ps.setString(3, inicio[1]);
                    ps.setString(4, fin[0]);

                    // Fecha inicial, hora inicial, fecha final y hora final
                } else if (inicio.length == 2 && fin.length == 2) {
                    sql = "SELECT x,y,z FROM datos_sensor WHERE (fecha_de_captura > ? OR (fecha_de_captura = ? AND hora_de_captura >= ?)) AND (fecha_de_captura < ? OR (fecha_de_captura = ? AND hora_de_captura <= ?))";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, inicio[0]);
                    ps.setString(2, inicio[0]);
                    ps.setString(3, inicio[1]);
                    ps.setString(4, fin[0]);
                    ps.setString(5, fin[0]);
                    ps.setString(6, fin[1]);
                } else {
                    conn.close();
                    return "";
                }
            } else {
                conn.close();
                return "";
            }
        } else {
            conn.close();
            return "";
        }

        ResultSet rs = ps.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append(rs.getInt("x")).append(",")
                    .append(rs.getInt("y")).append(",")
                    .append(rs.getInt("z")).append(";");
        }
        conn.close();
        return sb.toString();
    }

    private static String encriptar(String texto) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return java.util.Base64.getEncoder().encodeToString(cipher.doFinal(texto.getBytes()));
    }

    private static String desencriptar(String textoEncriptado) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = java.util.Base64.getDecoder().decode(textoEncriptado);
        return new String(cipher.doFinal(bytes));
    }
}
