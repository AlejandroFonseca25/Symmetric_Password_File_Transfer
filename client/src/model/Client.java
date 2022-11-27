package model;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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
			System.out.println("Connecting to " + serverIp + " on " + port + "...");
			Socket client = new Socket(serverIp, port);
			System.out.println("Connected to " + client.getRemoteSocketAddress());

			//inicia el proceso de diffie helman
			System.out.println("~~~~~~~~~~~~TRANSFERENCIA DE ARCHIVOS CON ESQUEMA DE CLAVE SIMETRICA~~~~~~~~~~~~");
			
			//File selection and sending, index 0 is the path, index 1 is the file name
			String[] fileInfo = fileManager.chooseFile();
			
			dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());

			System.out.println("por favor ingrese el numero primo p: ");
			// int p = Integer.parseInt(scanner.nextLine());
			int p = 17;
			System.out.println("por favor ingrese otro numero primo g: ");
			// int g = Integer.parseInt(scanner.nextLine());
			int g = 3;
			System.out.println("por favor ingrese su numero secreto: ");
			// int secret = Integer.parseInt(scanner.nextLine());
			int secret = 15;

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
			System.out.println("partnerResult: " + partnerResult);

			//se calcula la clave
			diffie.calculateSecretKey();
			System.out.println("clave compartida de diffie hellman: "+ diffie.getSecretKey());

			scanner.close();
			
			sendFile(fileInfo[0],fileInfo[1],diffie.getSecretKey());

			System.out.println(fileInfo[1] + " enviado con exito.");

		} catch (Exception e){
			System.out.println(e.toString());
		}
	}

	//tomado de: https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
	private static void sendFile(String path,String fileName,long key) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);

		byte[] fileData = new byte[(int)file.length()];
        fileInputStream.read(fileData);
		
		// AES encryption
		// BigInteger temp = BigInteger.valueOf(key);
		byte[] byteKey = hexToBytes("01010101010101010101010101010101");
		
		SecretKeySpec keySpec = new SecretKeySpec(byteKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encryptedFile = cipher.doFinal(fileData);
		fileOutputStream.write(encryptedFile);
        // send file size
        dataOutputStream.writeLong(file.length()); 
        // break file into chunks
        byte[] buffer = new byte[4*1024];
		long size = 4*1024;

		// System.out.println("ANTES DEL FOR");
		// for(int offset = 0; offset<(int)file.length();offset+=size){
		// 	System.out.println("offset: " + offset);
		// 	dataOutputStream.write(fileData,offset,(int)size);
        //     dataOutputStream.flush();
		// }

		// sends fileName
		dataOutputStream.writeUTF(fileName);

        while ((bytes=fileInputStream.read(buffer))!=-1){
            System.out.println("bytes " + bytes);
			dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }

        fileInputStream.close();
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
