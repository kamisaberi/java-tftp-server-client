package packet;

import java.net.*;
import java.io.*;

//////////////////////////////////////////////////////////////////////////////
//GENERAL packet: define the packet structure, necessary members and methods// 
//of TFTP packet. To be extended by other specific packet(read, write, etc) //
//////////////////////////////////////////////////////////////////////////////
public class TftpPacket {

    // TFTP constants
    public static int tftpPort = 69;
    public static int maxTftpPakLen = 516;
    public static int maxTftpData = 512;

    // Tftp opcodes
    protected static final short tftpRRQ = 1;
    protected static final short tftpWRQ = 2;
    protected static final short tftpDATA = 3;
    protected static final short tftpACK = 4;
    protected static final short tftpERROR = 5;

    // Packet Offsets
    protected static final int opOffset = 0;

    protected static final int fileOffset = 2;

    protected static final int blkOffset = 2;
    protected static final int dataOffset = 4;

    protected static final int numOffset = 2;
    protected static final int msgOffset = 4;

    // The actual packet for UDP transfer
    protected byte[] message;
    protected int length;

    // Address info (required for replies)
    protected InetAddress host;
    protected int port;

    // Constructor
    public TftpPacket() {
        message = new byte[maxTftpPakLen];
        length = maxTftpPakLen;
    }

    // Methods to receive packet and convert it to yhe right type(data/ack/read/...)
    public static TftpPacket receive(DatagramSocket sock) throws IOException {
        TftpPacket in = new TftpPacket(), retPak = new TftpPacket();
        //receive data and put them into in.message
        DatagramPacket inPak = new DatagramPacket(in.message, in.length);
        sock.receive(inPak);

        //Check the opcode in message, then cast the message into the corresponding type
        switch (in.get(0)) {
            case tftpRRQ:
                retPak = new TftpRead();
                break;
            case tftpWRQ:
                retPak = new TftpWrite();
                break;
            case tftpDATA:
                retPak = new TftpData();
                break;
            case tftpACK:
                retPak = new TftpAck();
                break;
            case tftpERROR:
                retPak = new TftpError();
                break;
        }
        retPak.message = in.message;
        retPak.length = inPak.getLength();
        retPak.host = inPak.getAddress();
        retPak.port = inPak.getPort();

        return retPak;
    }

    //Method to send packet
    public void send(InetAddress ip, int port, DatagramSocket s) throws IOException {
        s.send(new DatagramPacket(message, length, ip, port));
    }

    // DatagramPacket like methods
    public InetAddress getAddress() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getLength() {
        return length;
    }

    // Methods to put opcode, blkNum, error code into the byte array 'message'.
    protected void put(int at, short value) {
        message[at++] = (byte) (value >>> 8);  // first byte
        message[at] = (byte) (value % 256);    // last byte
    }

    @SuppressWarnings("deprecation")
    //Put the filename and mode into the 'message' at 'at' follow by byte "del"
    protected void put(int at, String value, byte del) {
        value.getBytes(0, value.length(), message, at);
        message[at + value.length()] = del;
    }

    protected int get(int at) {
        return (message[at] & 0xff) << 8 | message[at + 1] & 0xff;
    }

    protected String get(int at, byte del) {
        StringBuffer result = new StringBuffer();
        while (message[at] != del) result.append((char) message[at++]);
        return result.toString();
    }
}


