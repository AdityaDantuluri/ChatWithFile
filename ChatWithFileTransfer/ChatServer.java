import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatServer {

	public static void main(String[] args) throws UnknownHostException {
		if(args.length != 1){
			System.out.println("Invalid Input");
			return;
		}
		
		int lPort = Integer.parseInt(args[0]);
		String serverAdr = InetAddress.getLocalHost().getHostAddress();
		
		try{
			System.out.println("Setting up Server");
			MServer.makeServer(lPort, serverAdr);
		}catch(Exception e){
			System.out.println("Invalid Input\n");
			System.exit(0);
		}

	}

}
