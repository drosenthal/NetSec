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
public class UDPPacket extends IPPacket
{

    private int sourcePortNumber;
    private int destinationPortNumber;
    private int length;
    private int checksum;
    private byte[] udpData;

    @Override
    public int getSourcePort()
    {
        return sourcePortNumber;
    }

    @Override
    public int getDestinationPort()
    {
        return destinationPortNumber;
    }
    public UDPPacket(byte[] packet)
    {
        super(packet);

        sourcePortNumber = readTwoFullBytes(34);
        destinationPortNumber = readTwoFullBytes(36);
        length = readTwoFullBytes(38);
        checksum = readTwoFullBytes(40);
        udpData = new byte[length - 8];
        for (int i = 0; i < data.length-42; i++)
        {
            udpData[i] = packet[i + 42];
        }
    }

    @Override
    public void print()
    {
        super.print();
        System.out.println("------------------------------");
        System.out.println("UDP Header");
        System.out.println("------------------------------");
        System.out.println("Source Port Number: " + sourcePortNumber);
        System.out.println("Destination Port Number " + destinationPortNumber);
        System.out.println("Length: " + length);
        System.out.println("Checksum: " + checksum);
        System.out.println("Data: " + data);
        if (this.getClass().getName().equals("Packets.UDPPacket"))
        {
            System.out.println("############END OF LINE#############");
        }

    }
}
