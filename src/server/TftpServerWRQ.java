package  server;

import packet.*;
import utility.*;

import java.net.*;
import java.io.*;


class TftpServerWRQ extends Thread {

	protected DatagramSocket sock;
	protected InetAddress host;
	protected int port;
	protected FileOutputStream outFile;
	protected TftpPacket req;
	protected int timeoutLimit = 5;
	//protected int testloss=0;
	protected File saveFile;
	protected String fileName;

	// Initialize read request
	public TftpServerWRQ(TftpWrite request) throws TftpException {
		try {
			req = request;
			sock = new DatagramSocket(); // new port for transfer
			sock.setSoTimeout(1000);

			host = request.getAddress();
			port = request.getPort();
			fileName = request.fileName();
			//create file object in parent folder
			saveFile = new File("../"+fileName);

			if (!saveFile.exists()) {
				outFile = new FileOutputStream(saveFile);
				TftpAck a = new TftpAck(0);
				a.send(host, port, sock); // send ack 0 at first, ready to
											// receive
				this.start();
			} else
				throw new TftpException("access violation, file exists");

		} catch (Exception e) {
			TftpError ePak = new TftpError(1, e.getMessage()); // error code 1
			try {
				ePak.send(host, port, sock);
			} catch (Exception f) {
			}

			System.out.println("Client start failed:" + e.getMessage());
		}
	}

	public void run() {
		/*int bytesRead = TFTPpacket.maxTftpPakLen;*/
		// handle write request
		if (req instanceof TftpWrite) {
			try {
				for (int blkNum = 1, bytesOut = 512; bytesOut == 512; blkNum++) {
					while (timeoutLimit != 0) {
						try {
							TftpPacket inPak = TftpPacket.receive(sock);
							//check packet type
							if (inPak instanceof TftpError) {
								TftpError p = (TftpError) inPak;
								throw new TftpException(p.message());
							} else if (inPak instanceof TftpData) {
								TftpData p = (TftpData) inPak;
								/*System.out.println("incoming data " + p.blockNumber());*/
								// check blk num
								if (/*testloss==20||*/p.blockNumber() != blkNum) { //expect to be the same
									//System.out.println("loss. testloss="+testloss+"timeoutLimit="+timeoutLimit);
									//testloss++;
									throw new SocketTimeoutException();
								}
								//write to the file and send ack
								bytesOut = p.write(outFile);
								TftpAck a = new TftpAck(blkNum);
								a.send(host, port, sock);
								//testloss++;
								break;
							}
						} catch (SocketTimeoutException t2) {
							System.out.println("Time out, resend ack");
							TftpAck a = new TftpAck(blkNum - 1);
							a.send(host, port, sock);
							timeoutLimit--;
						}
					}
					if(timeoutLimit==0){throw new Exception("Connection failed");}
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
				saveFile.delete();
			}
		}
	}
}