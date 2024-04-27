package packet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

////////////////////////////////////////////////////////
//DATA packet: put the right code in the message; read// 
//file for sending; write file after receiving        //
////////////////////////////////////////////////////////
public final class TftpData extends TftpPacket {

    // Constructors
    protected TftpData() {
    }

    public TftpData(int blockNumber, FileInputStream in) throws IOException {
        this.message = new byte[maxTftpPakLen];
        // manipulate message
        this.put(opOffset, tftpDATA);
        this.put(blkOffset, (short) blockNumber);
        // read the file into packet and calculate the entire length
        length = in.read(message, dataOffset, maxTftpData) + 4;
    }

    // Accessors

    public int blockNumber() {
        return this.get(blkOffset);
    }

    /*
     * public void data(byte[] buffer) { buffer = new byte[length-4];
     *
     * for (int i=0; i<length-4; i++) buffer[i]=message[i+dataOffset]; }
     */

    // File output
    public int write(FileOutputStream out) throws IOException {
        out.write(message, dataOffset, length - 4);

        return (length - 4);
    }
}
