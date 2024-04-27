package  server;

import packet.*;
import utility.*;

import java.net.*;
import java.io.*;


class TftpServerRRQ extends Thread {

	protected DatagramSocket sock;
	protected InetAddress host;
	protected int port;
	protected FileInputStream source;
	protected TftpPacket req;
	protected int timeoutLimit=5;
	protected String fileName;

	// initialize read request
	public TftpServerRRQ(TftpRead request) throws TftpException {
		try {
			req = request;
			//open new socket with random port num for tranfer
			sock = new DatagramSocket();
			sock.setSoTimeout(1000);
			fileName = request.fileName();

			host = request.getAddress();
			port = request.getPort();
			
			//create file object in parent folder
			File srcFile = new File("../"+fileName);
			/*System.out.println("procce checking");*/
			//check file
			if (srcFile.exists() && srcFile.isFile() && srcFile.canRead()) {
				source = new FileInputStream(srcFile);
				this.start(); //open new thread for transfer
			} else
				throw new TftpException("access violation");

		} catch (Exception e) {
			TftpError ePak = new TftpError(1, e.getMessage()); // error code 1
			try {
				ePak.send(host, port, sock);
			} catch (Exception f) {
			}

			System.out.println("Client start failed:  " + e.getMessage());
		}
	}
	//everything is fine, open new thread to transfer file
	public void run() {
		int bytesRead = TftpPacket.maxTftpPakLen;
		// handle read request
		if (req instanceof TftpRead) {
			try {
				for (int blkNum = 1; bytesRead == TftpPacket.maxTftpPakLen; blkNum++) {
					TftpData outPak = new TftpData(blkNum, source);
					/*System.out.println("send block no. " + outPak.blockNumber()); */
					bytesRead = outPak.getLength();
					/*System.out.println("bytes sent:  " + bytesRead);*/
					outPak.send(host, port, sock);
					/*System.out.println("current op code  " + outPak.get(0)); */
					
					//wait for the correct ack. if incorrect, retry up to 5 times
					while (timeoutLimit!=0) { 
						try {
							TftpPacket ack = TftpPacket.receive(sock);
							if (!(ack instanceof TftpAck)){throw new Exception("Client failed");}
							TftpAck a = (TftpAck) ack;
							
							if(a.blockNumber()!=blkNum){ //check ack
								throw new SocketTimeoutException("last packet lost, resend packet");}
							/*System.out.println("confirm blk num " + a.blockNumber()+" from "+a.getPort());*/
							break;
						} 
						catch (SocketTimeoutException t) {//resend last packet
							System.out.println("Resent blk " + blkNum);
							timeoutLimit--;
							outPak.send(host, port, sock);
						}
					} // end of while
					if(timeoutLimit==0){throw new Exception("connection failed");}
				}
				System.out.println("Transfer completed.(Client " +host +")" );
				System.out.println("Filename: "+fileName + "\nSHA1 checksum: "+ CheckSum.getChecksum("../"+fileName)+"\n");
			} catch (Exception e) {
				TftpError ePak = new TftpError(1, e.getMessage());

				try {
					ePak.send(host, port, sock);
				} catch (Exception f) {
				}

				System.out.println("Client failed:  " + e.getMessage());
			}
		}
	}
}