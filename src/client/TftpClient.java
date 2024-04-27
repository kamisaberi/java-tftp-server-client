package  client;

import packet.TftpException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TftpClient {
	public static void main(String argv[]) throws TftpException, Exception {
		String host = "";
		String fileName = "";
		String mode="octet"; //default mode
		String type="";
		try {
			// Process command line
			if (argv.length == 0)
				throw new Exception("--Usage-- \nocter mode:  TFTPClient [host] [Type(R/W?)] [filename] \nother mode:  TFTPClient [host] [Type(R/W?)] [filename] [mode]" );
			//use default mode(octet)
			if(argv.length == 3){
				host =argv[0];
			    type = argv[argv.length - 2];
			    fileName = argv[argv.length - 1];}
			//use other modes
			else if(argv.length == 4){
				host = argv[0];
				mode =argv[argv.length-1];
				type = argv[argv.length - 3];
				fileName = argv[argv.length - 2];
			}
			else throw new Exception("wrong command. \n--Usage-- \nocter mode:  TFTPClient [host] [Type(R/W?)] [filename] \nother mode:  TFTPClient [host] [Type(R/W?)] [filename] [mode]");
			
			
			InetAddress server = InetAddress.getByName(host);
			
			//process read request
			if(type.matches("R")){
				TftpClientRRQ r = new TftpClientRRQ(server, fileName, mode);}
			//process write request
			else if(type.matches("W")){
				TftpClientWRQ w = new TftpClientWRQ(server, fileName, mode);
			}
			else{throw new Exception("wrong command. \n--Usage-- \nocter mode:  TFTPClient [host] [Type(R/W?)] [filename] \nother mode:  TFTPClient [host] [Type(R/W?)] [filename] [mode]");}
			
		} catch (UnknownHostException e) {
			System.out.println("Unknown host " + host);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}