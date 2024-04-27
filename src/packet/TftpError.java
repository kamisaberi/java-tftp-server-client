package packet;

/////////////////////////////////////////////////////////
//ERROR packet: put the right codes and error messages // 
//in the 'message'                                     //
/////////////////////////////////////////////////////////
public class TftpError extends TftpPacket {

    // Constructors
    protected TftpError() {
    }

    //Generate error packet
    public TftpError(int number, String message) {
        length = 4 + message.length() + 1;
        this.message = new byte[length];
        put(opOffset, tftpERROR);
        put(numOffset, (short) number);
        put(msgOffset, message, (byte) 0);
    }

    // Accessors
    public int number() {
        return this.get(numOffset);
    }

    public String message() {
        return this.get(msgOffset, (byte) 0);
    }
}
