import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class MServer {

	public static synchronized void makeServer(int port, String serverAdr) throws IOException{
		ServerSocket listener = new ServerSocket(port);
		Map<Socket,String> myMap = new ConcurrentHashMap<Socket,String>();
		Socket socket = null;
		try {
            while (true) {
            	try {
    				socket = listener.accept();
    				Runnable s = new sThread(socket, myMap);
    		        new Thread(s).start();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
        }
        
        finally {
            listener.close();
            socket.close();
        }
	}

}

class  sThread implements Runnable {
	Socket socket;
	ServerSocket sSoc;
	String nName;
	BufferedReader in;
	Map<Socket,String> myMap;
	public sThread(Socket s, Map<Socket,String> mM) throws IOException{
		
		socket = s;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		myMap = mM;
	}
	
	public synchronized void run ()
   {
		
    	try 
    	{PrintWriter ques = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
		ques.println("What would you like your nickname to be?\n");
		nName = in.readLine();
		ques.println("You are now Connected as: "+nName);
    	ques.println("Number of Other Users Currently Connected:"+myMap.size());
    	Iterator<Socket> it = myMap.keySet().iterator();
    	while(it.hasNext()){
    		try{
				PrintWriter out = new PrintWriter(new OutputStreamWriter(it.next().getOutputStream(), StandardCharsets.UTF_8), true);
				out.println("m!**"+nName+" has has joined");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	myMap.put(socket, nName);    	
        System.out.println("**"+nName+" has Joined");
			
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
		
		
		
		
		while (true) {
            try {
            	try{
                	String response = in.readLine();
                	if( (!(response.equals("null"))) && (!(response.equals(""))) ){
	                	String outS= "Message Corrupted";
	                	if(response.charAt(1) == '!' ){
	            			if(response.charAt(0) == 'm'){
	            				outS = "m!"+nName+":>"+response.substring(2);
	            				
	            				Iterator<Socket> it = myMap.keySet().iterator();
	            				while(it.hasNext()){
	            					Socket nSock = it.next();
	            					if(!myMap.get(nSock).equals(nName)){
	            						try{
		            						PrintWriter out = new PrintWriter(new OutputStreamWriter(nSock.getOutputStream(), StandardCharsets.UTF_8), true);
		            						out.println(outS);
		            					} catch (IOException e1) {
		            						// TODO Auto-generated catch block
		            						e1.printStackTrace();
		            					}
	            					}
	            		    		
	            		    	}
	            				
	            			}
	            			else if(response.charAt(0) == 'r'){
	            				outS = response;
	            				String user = response.substring(response.indexOf("!")+1, response.indexOf(" ")).trim();
	             				Socket sendS = getKeyByValue(myMap, user);
	            				PrintWriter out = new PrintWriter(new OutputStreamWriter(sendS.getOutputStream(), StandardCharsets.UTF_8), true);
        						out.println(outS);
	            			}
	            		}	                	           	 
        				System.out.println(outS);
                	}
            	}catch(Exception e){
            		
            		myMap.remove(socket);
                	Iterator<Socket> it = myMap.keySet().iterator();
            		while(it.hasNext()){
                		try{
            				PrintWriter out = new PrintWriter(new OutputStreamWriter(it.next().getOutputStream(), StandardCharsets.UTF_8), true);
            				out.println("m!**"+nName+" has disconnected");  
            			} catch (IOException e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			}
                	}  	
                	System.out.println("**"+nName+" has disconnected");
                	break;
                }
            } finally {
            	
            }
        }
   }
	
	public String setResponse(String response){
		String ret = "Message Corumpted";
		//String outS = nName+":>"+response;
		
		if(response.charAt(1) == '!' ){
			if(response.charAt(0) == 'm'){
				ret = nName+":>"+response.substring(2);
			}
			else if(response.charAt(0) == 'r'){
				ret = response;
			}
		}
		
		
		return ret;
	}
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
}
