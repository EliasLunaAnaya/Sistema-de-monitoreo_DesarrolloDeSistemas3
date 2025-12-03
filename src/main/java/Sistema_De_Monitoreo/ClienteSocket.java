
package Sistema_De_Monitoreo;

import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class ClienteSocket {
    private static final String CLAVE = "1234567890123456";

    private static Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;


    public static void enviarDatos(int x, int y, int z) {
        try (Socket socket = new Socket("localhost", 5000); //Creación del socket
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); //Envia objetos

             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            String mensaje = "INSERT\n" + x + "\n" + y + "\n" + z;
            oos.writeObject(encriptar(mensaje)); //Encriptar el mensaje

            String respuestaEncriptada = (String) ois.readObject();
            String respuesta = desencriptar(respuestaEncriptada);
            System.out.println("Respuesta del servidor: " + respuesta);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Este metodo consulta TODOS los datos sin filtro
    public static String consultarDatos() {
        return consultarDatos("");
    }

    //Este metodo consulta los datos con el filtro de la vista historico
    public static String consultarDatos(String filtro) {
        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            String mensaje = filtro.isEmpty() ? "CONSULTA" : "CONSULTA\n" + filtro;
            oos.writeObject(encriptar(mensaje));

            String respuestaEncriptada = (String) ois.readObject();
            return desencriptar(respuestaEncriptada);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




    //Metodos para encriptar y desencriptar datos
    private static String encriptar(String texto) throws Exception {
        Cipher cipher = Cipher.getInstance("AES"); //Objeto Cipher que implementa AES
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES"); //Convierte el string CLAVE en bytes y lo mete en el objeto SecretKeySpec
        cipher.init(Cipher.ENCRYPT_MODE, key); //Cipher en modo ENCRYPT_MODE
        return java.util.Base64.getEncoder().encodeToString(cipher.doFinal(texto.getBytes())); //Devuelve el texto cifrado en formato base64
    }
    private static String desencriptar(String textoEncriptado) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key); //Inicializa el cipher en modo DECRYPT_MODE para poder desencriptar el mensaje
        byte[] bytes = java.util.Base64.getDecoder().decode(textoEncriptado); //Convierte el texto cifrado en base65
        return new String(cipher.doFinal(bytes)); //Convierte los bytes descifrados a string
    }
}

