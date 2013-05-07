package Packets.IP;

import Packets.IPPacket;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
public class ICMPPacket extends IPPacket
{
    public int type;
    public int code;
    private int checksum;
    private byte[] restOfHeader;
    public ICMPPacket(byte[] packet)
    {
        super(packet);
        type = packet[34];
        code = packet[35];
        checksum = readTwoFullBytes(36);
        restOfHeader = getIPAddressBytes(38);
    }
    
    @Override
    public void print()
    {
        super.print();
        System.out.println("------------------------------");
        System.out.println("ICMP Header");
        System.out.println("------------------------------");
        System.out.println("Type: " + type);
        System.out.println("Code: " + code);
        System.out.println("Checksum: " + checksum);
        System.out.println("Rest of Header: " + restOfHeader);
        if (this.getClass().getName().equals("Packets.ICMPPacket"))
        {
            System.out.println("############END OF LINE#############");
        }
    }
}
