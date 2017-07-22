import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatClient {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		if( !( args.length == 4 && args[0].equals("-l") && args[2].equals("-p") ) ){
			System.out.println("Invalid Input");
			return;
		}
		int lPort = Integer.parseInt(args[1]);
		int sPort = Integer.parseInt(args[3]);
		String serverAdr = InetAddress.getLocalHost().getHostAddress();
		
		try{
			System.out.println("Setting up Client");
			MClient.makeClient(sPort, serverAdr, lPort);
		}catch(Exception e){
			System.out.println("Invalid Input\n");
			System.exit(0);
		}

	}

}
