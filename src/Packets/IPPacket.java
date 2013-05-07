package Packets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class IPPacket extends Frame
{

    public byte[] entireHeader;
    public byte[] ipHeader;
    public byte[] data;
    public byte[] entireFrame;
    public int version;
    public int headerLength;
    public byte differentiatedServices;
    public int totalLength;
    public int identification;
    public int flags;
    public int fragmentOffset;
    public int timeToLive;
    public int ipProtocol;
    public long headerChecksum;
    private long computedChecksum;
    public InetAddress sourceIP;
    public InetAddress destinationIP;
    private byte[] tmpByte = new byte[4];
    public int first;
    public int last;
    //TODO: add OPTIONS for IP Header!!!!!

    public boolean isLastPacket()
    {
        if (this.flags != 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isFirstPacket()
    {
        return (this.fragmentOffset == 0) ? true : false;
    }

    public boolean isFirstAndLastPacket()
    {
        return (this.isFirstPacket() && this.isLastPacket()) ? true : false;
    }

    @Override
    public InetAddress getSourceIP()
    {
        return sourceIP;
    }

    @Override
    public InetAddress getDestinationIP()
    {
        return destinationIP;
    }
    //TODO: change values to use methods in frame
    //i.e. getmacaddressbytes and readtwofullbytes

    public IPPacket(IPPacket existingPacket)
    {
        super(existingPacket.entireFrame); //this is sloppy
        this.entireFrame = existingPacket.entireFrame;
        this.entireHeader = existingPacket.entireHeader;
        this.ipHeader = existingPacket.ipHeader;
        this.version = existingPacket.version;
        this.headerLength = existingPacket.headerLength;
        this.differentiatedServices = existingPacket.differentiatedServices;
        this.totalLength = existingPacket.totalLength;
        this.identification = existingPacket.identification;
        this.flags = existingPacket.flags;
        this.fragmentOffset = existingPacket.fragmentOffset;
        this.timeToLive = existingPacket.timeToLive;
        this.ipProtocol = existingPacket.ipProtocol;
        this.headerChecksum = existingPacket.headerChecksum;
        this.computedChecksum = existingPacket.computedChecksum;
        this.sourceIP = existingPacket.sourceIP;
        this.destinationIP = existingPacket.destinationIP;
        this.data = existingPacket.data;
        this.first = existingPacket.first;
        this.last = existingPacket.last;
    }

    public IPPacket(byte[] packetRead)
    {
        super(packetRead);
        this.entireFrame = packetRead; //TODO: change to frame bytes only
        entireHeader = new byte[34];
        System.arraycopy(packetRead, 0, entireHeader, 0, 34);

        ipHeader = new byte[20];
        System.arraycopy(packetRead, 14, ipHeader, 0, 20);
        version = entireHeader[14] >> 4;
        headerLength = ((entireHeader[14] & 0x0F) * 32) / 8;
        differentiatedServices = entireHeader[15];
        totalLength = ((entireHeader[16] & 0xff) << 8) | (entireHeader[17] & 0xff);
        identification = ((entireHeader[18] & 0xff) << 8) | (entireHeader[19] & 0xff);
        flags = (entireHeader[20] >> 5);
        fragmentOffset = ((entireHeader[20] & 0x1F) << 8) | (entireHeader[21] & 0xff);
        timeToLive = entireHeader[22];
        ipProtocol = entireHeader[23];
        headerChecksum = readTwoFullBytes(24);
        computedChecksum = calculateChecksum(ipHeader, 0, 10, ipHeader.length);
        try
        {
            for (int i = 0; i < 4; i++)
            {
                tmpByte[i] = entireHeader[i + 26];
            }

            sourceIP = InetAddress.getByAddress(tmpByte);
            for (int j = 0; j < 4; j++)
            {
                tmpByte[j] = entireHeader[j + 30];
            }
            destinationIP = InetAddress.getByAddress(tmpByte);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(IPPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
        data = new byte[totalLength - headerLength];
        if (this.getClass().getName().equals("Packets.IPPacket"))
        {

            for (int i = 0; i < data.length; i++)
            {
                data[i] = packetRead[i + 34];
            }

        }

        first = this.fragmentOffset * 8;
        last = this.fragmentOffset * 8 + this.data.length - 1;

    }

    public static int getPacketType(byte[] packet)
    {
        return packet[23];
    }

    private String getIPProtocolName(int protocol)
    {
        switch (protocol)
        {
            case 1:
                return "ICMP";
            case 6:
                return "TCP";
            case 17:
                return "UDP";
            default:
                return "Unknown";
        }
    }

    public boolean hasCorrectChecksum()
    {
        if (computedChecksum != headerChecksum)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private long calculateChecksum(
            byte[] headerData,
            int startOffset,
            int checksumOffset,
            int length)
    {
        int total = 0;
        int i = startOffset;
        int imax = checksumOffset;

        while (i < imax)
        {
            total += (((headerData[i++] & 0xff) << 8) | (headerData[i++] & 0xff));
        }

        // Skip existing checksum. 	( packet[10] and packet[11] )
        i = checksumOffset + 2;

        imax = length - (length % 2);

        while (i < imax)
        {
            total += (((headerData[i++] & 0xff) << 8) | (headerData[i++] & 0xff));
        }

        if (i < length)
        {
            total += ((headerData[i] & 0xff) << 8);
        }

        // Fold to 16 bits
        while ((total & 0xffff0000) != 0)
        {
            total = (total & 0xffff) + (total >>> 16);
        }

        total = (~total & 0xffff);


        return total;
    }

    @Override
    public void print()
    {
        super.print();
        System.out.println("Frame type: IP");
        System.out.println("------------------------------");
        System.out.println("IP Header");
        System.out.println("------------------------------");
        System.out.println("Version: " + version);
        System.out.println("Header Length: " + headerLength);
        System.out.println("Differentiated Service: " + differentiatedServices);
        System.out.println("Total Length: " + totalLength);
        System.out.println("Identification: " + identification);
        System.out.println("Flags: " + flags);
        System.out.println("Fragment Offset: " + fragmentOffset);
        System.out.println("Time to live: " + timeToLive);
        System.out.println("Protocol: " + getIPProtocolName(ipProtocol));
        System.out.println("Header Checksum: " + headerChecksum);
        System.out.println("Source IP: " + sourceIP);
        System.out.println("Destination IP: " + destinationIP);
        if (this.getClass().getName().equals("Packets.IPPacket"))
        {
            System.out.println("############END OF LINE#############");
        }
    }
}
