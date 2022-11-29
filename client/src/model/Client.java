package model;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**

 * This class represents the Client side for the secure file transfer using diffieHellman algorithm.

 */
public class Client {
	private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
	private static Scanner scanner;
	
	public static void main (String[] args) {
		try {
			//Connection values
			int port = 8088;
			String serverIp = "localhost";

			scanner = new Scanner(System.in);
			
			//Diffie Hellman
			DiffieHellman diffie = new DiffieHellman();
			
			//File Manager creation
			FileManager fileManager = new FileManager();

			//Connection process
			System.out.println("Conectando a " + serverIp + " a traves del puerto " + port + "...");
			Socket client = new Socket(serverIp, port);
			System.out.println("Conectado a " + client.getRemoteSocketAddress());

			//inicia el proceso de diffie helman
			System.out.println("~~~~~~~~~~~~TRANSFERENCIA DE ARCHIVOS CON ESQUEMA DE CLAVE SIMETRICA : CLIENTE~~~~~~~~~~~~");
			
			System.out.println("Seleccionar el archivo a transferir...");

			//File selection and sending, index 0 is the path, index 1 is the file name
			String[] fileInfo = fileManager.chooseFile();
			
			System.out.println("Archivo cargado exitosamente.");

			dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());

			System.out.println("Por favor ingrese el numero primo p: ");
			int p = Integer.parseInt(scanner.nextLine());
			System.out.println("Por favor ingrese el numero generador g: ");
			int g = Integer.parseInt(scanner.nextLine());
			System.out.println("por favor ingrese su numero secreto: ");
			int secret = Integer.parseInt(scanner.nextLine());

			diffie.setP(p);
			diffie.setG(g);
			diffie.setSecret(secret);
			diffie.calculateMyResult();

			//enviar información al servidor
			dataOutputStream.writeInt(p);
			dataOutputStream.writeInt(g);
			dataOutputStream.writeInt(diffie.getMyResult());
			
			//recibo el resultado del servidor
			int partnerResult = dataInputStream.readInt();
			diffie.setPartnerResult(partnerResult);

			//se calcula la clave
			diffie.calculateSecretKey();

			System.out.println("~~~~~~~~~~~~VALORES DE DIFFIE HELLMAN~~~~~~~~~~~~");
			System.out.println("p = " + diffie.getP());
			System.out.println("g = " + diffie.getG()); 
			System.out.println("Numero secreto (s) = " + diffie.getSecret());
			System.out.println("g^s mod p = " + diffie.getMyResult());
			System.out.println("g^s mod p del servidor = " + diffie.getPartnerResult());
			System.out.println("Llave secreta = " + diffie.getSecretKey());

			scanner.close();
			
			sendFile(fileInfo[0],fileInfo[1],diffie.getSecretKey());

			System.out.println(fileInfo[1] + " enviado con exito.");

		} catch (Exception e){
			System.out.println(e.toString());
		}
	}

	/**
	* Sends the already encrypted file to the communication partner. 
	* <p>
	* The file is created using the path given, then the file name is sent
	* to the communication partner for identification purposes.
	* Then, the file is encrypted with the secret key calculated in the 
	* Diffie-Hellman algorithm. Finally, the encrypted file is sent in 4KB packages
	* to the communication partner.
	*
	* @param  path 	   an absolute URL giving the base location of the image
	* @param  fileName the location of the image, relative to the url argument
	* @param  key 	   Diffie hellman's own private key
	*/
	//tomado de: https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
	private static void sendFile(String path,String fileName,long key) throws Exception{
        int bytes = 0;
		String momentaneumFile = "encrypted "+fileName;

        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
		FileOutputStream fileOutputStream = new FileOutputStream(momentaneumFile);

		byte[] fileData = new byte[(int)file.length()];
        fileInputStream.read(fileData);
		
		// AES encryption
		// BigInteger temp = BigInteger.valueOf(key);
		byte[] byteKey = longToBytes(key);
		
		SecretKeySpec keySpec = new SecretKeySpec(byteKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encryptedFile = cipher.doFinal(fileData);
		fileOutputStream.write(encryptedFile);
        // send file size

		file = new File(momentaneumFile);
		dataOutputStream.writeUTF(fileName);
        dataOutputStream.writeLong(file.length()); 


		fileInputStream = new FileInputStream(file);
        // break file into chunks
        byte[] buffer = new byte[4*1024];
		long size = 4*1024;
		

        while ((bytes=fileInputStream.read(buffer))!=-1){
			dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }

		//file = new File(momentaneumFile);
		//file.deleteOnExit();

        fileInputStream.close();
		fileOutputStream.close();
    }


	/**
	* Calculates the array of bytes of length 16 that represents the number given to the method as parameter
	* <p>
	* The result of this method is going to be used ad 
	* the key for the encrypt of the file
    *
	* @param  x a log that is the key for encryption
	* @return      the array of bytes of length 16 that represents x
	*/
	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(x);
		return buffer.array();
	}
}
