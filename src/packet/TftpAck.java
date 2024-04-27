package packet;

/////////////////////////////////////////////////////////
//ACK packet: put the right opcode and block number in // 
//the 'message'                                        //
/////////////////////////////////////////////////////////
public final class TftpAck extends TftpPacket {

    // Constructors
    protected TftpAck() {
    }

    //Generate ack packet
    public TftpAck(int blockNumber) {
        length = 4;
        this.message = new byte[length];
        put(opOffset, tftpACK);
        put(blkOffset, (short) blockNumber);
    }

    // Accessors
    public int blockNumber() {
        return this.get(blkOffset);
    }
}
