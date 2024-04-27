package server;

import packet.TftpPacket;
import packet.TftpRead;
import packet.TftpWrite;
import packet.TftpException;

import java.net.*;
import java.io.*;

public class TftpServer {

	public static void main(String argv[]) {
		try {
			//use port 6973
			DatagramSocket sock = new DatagramSocket(6973);
			System.out.println("Server Ready.  Port:  " + sock.getLocalPort());

			// Listen for requests
			while (true) {
				TftpPacket in = TftpPacket.receive(sock);
				// receive read request
				if (in instanceof TftpRead) {
					System.out.println("Read Request from " + in.getAddress());
					TftpServerRRQ r = new TftpServerRRQ((TftpRead) in);
				}
				// receive write request
				else if (in instanceof TftpWrite) {
					System.out.println("Write Request from " + in.getAddress());
					TftpServerWRQ w = new TftpServerWRQ((TftpWrite) in);
				}
			}
		} catch (SocketException e) {
			System.out.println("Server terminated(SocketException) " + e.getMessage());
		} catch (TftpException e) {
			System.out.println("Server terminated(TftpException)" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Server terminated(IOException)" + e.getMessage());
		}
	}
}