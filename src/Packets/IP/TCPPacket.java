package Packets.IP;




import Packets.IPPacket;
import java.util.BitSet;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author David
 */
public class TCPPacket extends IPPacket
{

    private int sourcePort;
    private int destinationPort;
    private byte[] sequenceNumber;
    public int acknowledgementNumber;
    private int dataOffset;  //Use this to determine options
    /*
     * Flags
     */
    private BitSet bitset;
    private boolean nsFlag;
    private boolean cwrFlag;
    private boolean eceFlag;
    private boolean urgFlag;
    private boolean ackFlag;
    private boolean pshFlag;
    private boolean rstFlag;
    private boolean synFlag;
    private boolean finFlag;
    private int windowSize;
    private byte[] checksum;
    private byte[] options;
    private byte[] tcpData;
    
    
    @Override
    public int getSourcePort()
    {
        return sourcePort;
    }

    @Override
    public int getDestinationPort()
    {
        return destinationPort;
    }
    
    public TCPPacket(byte[] packet)
    {
        super(packet);
        checksum = new byte[2];
        tcpData = new byte[totalLength - 20];

        sourcePort = readTwoFullBytes(34);
        destinationPort = readTwoFullBytes(36);
        sequenceNumber = getIPAddressBytes(38);
        acknowledgementNumber = readFourFullBytes(42);
        dataOffset = (packet[46] >> 4);
        bitset = BitSet.valueOf(getTwoBytes(46));
        nsFlag = bitset.get(8);
        cwrFlag = bitset.get(9);
        eceFlag = bitset.get(10);
        urgFlag = bitset.get(11);
        ackFlag = bitset.get(12);
        pshFlag = bitset.get(13);
        rstFlag = bitset.get(14);
        synFlag = bitset.get(15);
        finFlag = bitset.get(16);

        windowSize = readTwoFullBytes(48);
        for (int i = 0; i < 2; i++)
        {
            checksum[i] = packet[i + 50];
        }

        if (dataOffset > 5)
        {
            int tcpHeaderSize = dataOffset * 4;
            int optionNumberOfBytes = tcpHeaderSize - 20;
            options = new byte[optionNumberOfBytes];
            for (int i = 0; i < optionNumberOfBytes; i++)
            {
                options[i] = packet[i + 54];
            }
            System.arraycopy(packet, 54, options, 0, optionNumberOfBytes);
            for (int j = 0; j < packet.length - (54 + optionNumberOfBytes); j++)
            {
                tcpData[0] = packet[j + (54 + optionNumberOfBytes)];
            }
        }
        else
        {
            for (int k = 0; k < packet.length - 54; k++)
            {
                tcpData[0] = packet[k + 54];
            }
        }
    }

    private byte[] getTwoBytes(int startingByte)
    {
        byte[] tmpByte = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            tmpByte[i] = this.frame[i + startingByte];
        }
        return tmpByte;
    }

    @Override
    public void print()
    {
        super.print();
        System.out.println("------------------------------");
        System.out.println("TCP Header");
        System.out.println("------------------------------");
        System.out.println("Source Port: " + sourcePort);
        System.out.println("Destination Port: " + destinationPort);
        System.out.println("Sequence Number: " + sequenceNumber);
        System.out.println("Acknowledgement Number: " + acknowledgementNumber);
        System.out.println("Data Offset: " + dataOffset);

        System.out.println("NS Flag: " + nsFlag);
        System.out.println("CWR Flag: " + cwrFlag);
        System.out.println("ECE Flag: " + eceFlag);
        System.out.println("URG Flag: " + urgFlag);
        System.out.println("ACK Flag: " + ackFlag);
        System.out.println("PSH Flag: " + pshFlag);
        System.out.println("RST Flag: " + rstFlag);
        System.out.println("SYN Flag: " + synFlag);
        System.out.println("FIN Flag: " + finFlag);

        System.out.println("Window Size: " + windowSize);
        System.out.println("Checksum: " + checksum.toString());
        if (dataOffset > 5)
        {
            System.out.println("Options: " + options.toString());
        }
        System.out.println("Data: " + tcpData.toString());
        if (this.getClass().getName().equals("Packets.TCPPacket"))
        {
            System.out.println("############END OF LINE#############");
        }
    }
}
