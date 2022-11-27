package model;

import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.math.BigInteger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

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
			System.out.println("Esperando cliente en puerto..." + serverSocket.getLocalPort());
			Socket server = serverSocket.accept();
			System.out.println("Conectado a " + server.getRemoteSocketAddress());

			dataInputStream = new DataInputStream(server.getInputStream());
            dataOutputStream = new DataOutputStream(server.getOutputStream());
			scanner = new Scanner(System.in);
			
			System.out.println("~~~~~~~~~~~~TRANSFERENCIA DE ARCHIVOS CON ESQUEMA DE CLAVE SIMETRICA~~~~~~~~~~~~");
			//Lee numero secreto del servidor
			System.out.println("Por favor ingrese su numero secreto: ");
			// int b = Integer.parseInt(scanner.nextLine());
			int b = 13;

			//Lee numero primo del cliente
			int clientP = dataInputStream.readInt(); 
            System.out.println("por favor ingrese el numero primo p: " + clientP);

            //Lee numero de cliente
			int clientG = dataInputStream.readInt(); 
            System.out.println("por favor ingrese el numero primo g: " + clientG);

			//Lee clave publica del cliente
			int clientA = dataInputStream.readInt(); 
            System.out.println("Clave publica del cliente: " + clientA);

			diffie.setPartnerResult(clientA);
			diffie.setP(clientP);
			diffie.setG(clientG);
			diffie.setSecret(b);

			//Calcula clave publica del servidor
			diffie.calculateMyResult();

			//Calcula clave secreta para encriptar 
			diffie.calculateSecretKey();
			long secretKey = diffie.getSecretKey();
			System.out.println("secretKey: " + secretKey);

			//Envia clave publica del servidor al cliente
            dataOutputStream.writeInt(diffie.getMyResult());

			scanner.close();

			receiveFile(secretKey);

			System.out.println("Archivo recibido con exito.");
		} catch (Exception e){
			System.out.println(e.toString());
		}
	}

	//tomado de: https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
	private static void receiveFile(long key) throws Exception{
        int bytes = 0;
		System.out.println("entra al método");
		String fileName = dataInputStream.readUTF();
		System.out.println("lee el nombre del archivo");
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		System.out.println("2");
        
		//AES decryption
		// BigInteger temp = BigInteger.valueOf(key);
		// byte[] byteKey = temp.toByteArray();
		// SecretKeySpec keySpec = new SecretKeySpec(byteKey, "AES");
		// Cipher cipher = Cipher.getInstance("AES");
		// cipher.init(Cipher.DECRYPT_MODE, keySpec);
		// byte[] encryptedFile = cipher.doFinal(fileInputStream.read());s

        long size = dataInputStream.readLong();     // read file size
        System.out.println("lee el tamaño del archivo");
		byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
			System.out.println("entra al for");
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }

		System.out.println("desencripta");
		
		//the created file is loaded, decrypted and saved
		File file = new File(fileName);
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] fileData = new byte[(int)file.length()];
		System.out.println((int)file.length());
        fileInputStream.read(fileData);

		byte[] byteKey = hexToBytes("01010101010101010101010101010101");
		
		SecretKeySpec keySpec = new SecretKeySpec(byteKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] encryptedFile = cipher.doFinal(fileData);
		fileOutputStream = new FileOutputStream(fileName);
		fileOutputStream.write(encryptedFile);


        fileOutputStream.close();
    }

	public static byte[] hexToBytes(String str) {
		if (str==null) {
		   return null;
		} else if (str.length() < 2) {
		   return null;
		} else {
		   int len = str.length() / 2;
		   byte[] buffer = new byte[len];
		   for (int i=0; i<len; i++) {
			   buffer[i] = (byte) Integer.parseInt(
				  str.substring(i*2,i*2+2),16);
		   }
		   return buffer;
		}
  
	 }
}
