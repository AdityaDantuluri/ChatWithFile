import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;

public class MClient {

	public static void makeClient(int port, String serverAdr, int lPort) throws IOException{
		Socket socket = new Socket(serverAdr, port);
		BufferedReader inBuf = new BufferedReader(new InputStreamReader(System.in));
		
        nick(socket, inBuf);
        
        Runnable r1 = new FileServerThread(lPort, serverAdr);
        new Thread(r1).start();
        
		Runnable r2 = new ClientThread(socket, lPort, inBuf);
        new Thread(r2).start();
        
        try {            
            while (true) {
                try {
                	try{
                		BufferedReader in;
                    	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    	String response = in.readLine();
                    	if (response.charAt(1) == '!'){
                    		if(response.charAt(0) == 'm'){
                    			System.out.println(response.substring(2));
                    		}
                    		else if(response.charAt(0) == 'r'){
                    			String inf = response.substring(2);
                    			inf = inf.substring(inf.indexOf(' ')+1).trim();
                    			String file = inf.substring(0, inf.indexOf(' ')).trim();
                    			int sPort = Integer.parseInt(inf.substring(inf.indexOf(' ')+1).trim());
                    			//System.out.println("File:"+file);
                    			//System.out.println("Port: "+sPort);
                    			Socket outS = new Socket(serverAdr, sPort);
                    			PrintWriter out = new PrintWriter(new OutputStreamWriter(outS.getOutputStream(), StandardCharsets.UTF_8), true);
                    			out.println(file);
                    			
                    			File myFile = new File(file);
                    			byte[] bytes = new byte[8192];
                    			InputStream fIn = new FileInputStream(myFile);
                    	        OutputStream outB = outS.getOutputStream();
                    	        
                    			int count;
                    			while ((count = fIn.read(bytes)) > 0){
                    				outB.write(bytes, 0, count);
                    			}
                    			
                    			out.close();
                    			fIn.close();
                    			outB.close();
                    			outS.close();
                    		}
                    	}
                	}catch(Exception e){
                    	System.out.println("Server Disconnected\nShutting Down\n");
                        socket.close();
                        System.exit(0);
                    }
                	
                } finally {
                	
                }
            }
        }
        finally {
            socket.close();
        }
	}
	
	public static void nick(Socket socket, BufferedReader inBuf) throws IOException{
		BufferedReader in;
    	try{
        	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	String response = in.readLine();
        	System.out.println(response);
    	}catch(Exception e){
			e.printStackTrace();
        }
    	boolean key = true;
    	String inStr="Unnamed User";;
    	while( key ) {
    		
    		try {
					inStr = inBuf.readLine();
		    		inStr = inStr.trim();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if ( inStr.indexOf(" ") >= 0){
    			System.out.println("Nicknames cannot have spaces");
    			key= true;
    		}else{
    			key = false;
    		}
    	}
    	
		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			out.println(inStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


class ClientThread implements Runnable {
	Socket socket;
	int lPort;
	int port = 101;
	BufferedReader inBuf;
	public ClientThread(Socket s, int l, BufferedReader iB){
		lPort = l;
		socket = s;
		inBuf = iB;
	}
	
	public void run ()
   {
		while(true){
			try{
				menu();
				String inStr = readIn();
				if( inStr.length() != 1){
					System.out.println("Invalid Input");
				}
				else{
					
					if(inStr.charAt(0) == 'm' || inStr.charAt(0) == 'M'){
						message();
					}
					else if(inStr.charAt(0) == 'f' || inStr.charAt(0) == 'F'){
						request();
					}
					else if(inStr.charAt(0) == 'x' || inStr.charAt(0) == 'X'){
						exit();
					}
					else{
						System.out.println("Invalid Input");
					}
				}
		        try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			finally{
				
			}
		}
   }
	public void menu (){
		System.out.println("Enter an option ('m', 'f', 'x')\n  (M)essage (send)\n  (F)ile (request)\n e(X)it);");
	}
	
	public void message(){
		System.out.println("Enter your message:");
		String inStr = readIn();
		inStr = "m!" + inStr;
		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			out.println(inStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void request(){
		String owner; 
		String file;
		String outStr;
		System.out.println("Who owns the file?");
		owner = readIn();
		System.out.println("Which file do you want?");
		file = readIn();
		outStr = "r!" + owner + " " + file + " " + lPort;
		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			out.println(outStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void exit(){
		System.exit(1);;
	}
	
	public String readIn(){
		String inStr = null;
		try {
			inStr = inBuf.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inStr = inStr.trim();
		return inStr;
	}
}

class FileServerThread implements Runnable {
	Socket socket;
	int lPort;
	int port = 101;
	String serverAdr;
	ServerSocket listener;
	public FileServerThread(int l, String sA){
		serverAdr = sA;
		lPort = l;
		try {
			listener = new ServerSocket(lPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run ()
   {			
		while (true){
			try{
	    		String fileName;
				Socket socket = listener.accept();
				BufferedReader in;
	        	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        	fileName = in.readLine();
	        	

	            InputStream inF = null;
	            try {
	                inF = socket.getInputStream();
	            } catch (IOException ex) {
	            	
	            }
	            
	        	
	            OutputStream outF = null;
	        	try {
	                outF = new FileOutputStream(fileName);
	            } catch (FileNotFoundException ex) {
	                System.out.println("File not found. ");
	            }
	        	
	        	byte[] bytes = new byte[8192];

	            int count;
	            while ((count = inF.read(bytes)) > 0) {
	                outF.write(bytes, 0, count);
	            }
	            in.close();
	            inF.close();
	            outF.close();
	            socket.close();

	        	//System.out.println("File Recieved:"+fileName);
	    	}catch(Exception e){
				e.printStackTrace();
	        }
			
		}
		
   }
}
