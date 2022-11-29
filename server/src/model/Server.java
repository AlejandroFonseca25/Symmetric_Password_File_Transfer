package model;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.io.*;
import java.math.BigInteger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
/**

 * This class represents the Server side for the secure file transfer using diffieHellman algorithm.

 */
public class Server {
	private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
	private static Scanner scanner;
	public static void main (String[] args) {
		try {
			//Connection values
			int port = 8088;
			
			//Diffie Hellman
			DiffieHellman diffie = new DiffieHellman();
			
			//Connection process
			System.out.println("Iniciando servidor...");
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Esperando cliente en puerto " + serverSocket.getLocalPort() + "...");
			Socket server = serverSocket.accept();
			System.out.println("Conectado a " + server.getRemoteSocketAddress());

			dataInputStream = new DataInputStream(server.getInputStream());
            dataOutputStream = new DataOutputStream(server.getOutputStream());
			scanner = new Scanner(System.in);
			
			System.out.println("~~~~~~~~~~~~TRANSFERENCIA DE ARCHIVOS CON ESQUEMA DE CLAVE SIMETRICA : SERVIDOR~~~~~~~~~~~~");
			//Lee numero secreto del servidor
			System.out.println("Por favor ingrese su numero secreto: ");
			int b = Integer.parseInt(scanner.nextLine());
			// int b = 13;

			//Lee numero primo del cliente
			int clientP = dataInputStream.readInt(); 

            //Lee numero de cliente
			int clientG = dataInputStream.readInt(); 

			//Lee clave publica del cliente
			int clientA = dataInputStream.readInt(); 

			diffie.setPartnerResult(clientA);
			diffie.setP(clientP);
			diffie.setG(clientG);
			diffie.setSecret(b);

			//Calcula clave publica del servidor
			diffie.calculateMyResult();

			//Calcula clave secreta para encriptar 
			diffie.calculateSecretKey();
			long secretKey = diffie.getSecretKey();

			//Envia clave publica del servidor al cliente
            dataOutputStream.writeInt(diffie.getMyResult());

			System.out.println("~~~~~~~~~~~~VALORES DE DIFFIE HELLMAN~~~~~~~~~~~~");
			System.out.println("p = " + diffie.getP());
			System.out.println("g = " + diffie.getG()); 
			System.out.println("Numero secreto (s) = " + diffie.getSecret());
			System.out.println("g^s mod p = " + diffie.getMyResult());
			System.out.println("g^s mod p del cliente = " + diffie.getPartnerResult());
			System.out.println("Llave secreta = " + diffie.getSecretKey());

			scanner.close();

			receiveFile(secretKey);

			System.out.println("Archivo recibido y desencriptado con exito.");
		} catch (Exception e){
			System.out.println(e.toString());
		}
	}

		/**
	* Receives the encrypted file sent by the communication partner and decrypts it. 
	* <p>
	* The file is recieved in 4KB packages and assigned the name also sent by the communication partner,
	* After successful loading, the file is decrypted with the secret key calculated in the 
	* Diffie-Hellman algorithm and put into this folders' path.
	*
	* @param  key 	   Diffie hellman's own private key
	*/
	//tomado de: https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
	private static void receiveFile(long key) throws Exception{
        int bytes = 0;
		String fileName = dataInputStream.readUTF();
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        

        long size = dataInputStream.readLong();     // read file size
		byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }

		//the created file is loaded, decrypted and saved
		File file = new File(fileName);
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] fileData = new byte[(int)file.length()];
        fileInputStream.read(fileData);

		byte[] byteKey = longToBytes(key);
		
		SecretKeySpec keySpec = new SecretKeySpec(byteKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] encryptedFile = cipher.doFinal(fileData);
		fileOutputStream = new FileOutputStream(fileName);
		fileOutputStream.write(encryptedFile);

        fileOutputStream.close();
    }

	/**
	* Calculates the array of bytes of length 16 that represents the number given to the method as parameter
	* <p>
	* The result of this method is going to be used ad 
	* the key for the decription of the file
    *
	* @param  x a log that is the key for decryption
	* @return      the array of bytes of length 16 that represents x
	*/
	 public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(x);
		return buffer.array();
	}
}
