package Packets;




import java.net.InetAddress;



/**
 *
 * @author David
 */
public class Frame
{

    protected byte[] frame;
    private byte[] frameDstMac;
    private byte[] frameSrcMac;
    private byte[] frameType;

    public byte[] getFrameInBytes()
    {
        return frame;
    }
    public Frame(byte[] frameRead)
    {
        frame = frameRead;
        frameDstMac = new byte[6];
        frameSrcMac = new byte[6];
        frameType = new byte[2];

        frameDstMac = getMacAddressBytes(0);
        frameSrcMac = getMacAddressBytes(6);


        for (int i = 0; i < 2; i++)
        {
            frameType[i] = frame[i + 12];
        }
    }

    public void print()
    {

        System.out.println("\n###############BEGIN################");
        System.out.println("------------------------------");
        System.out.println("Ethernet Frame Header:");
        System.out.println("------------------------------");
        System.out.println("Destination Mac Address: ");
        printMacAddress(frameDstMac);
        System.out.println("Source Mac Address: ");
        printMacAddress(frameSrcMac);
        if (isNotIpOrArp())
        {
            System.out.println("Frame Type: Ethernet");
        }
        if (this.getClass().getName().equals("Packets.Frame"))
        {
            System.out.println("############END OF LINE#############");
        }
    }

    protected void printMacAddress(byte[] address)
    {
        for (int i = 0; i < address.length; i++)
        {
            System.out.print(
                    String.format("%02X%s", address[i], (i < address.length - 1) ? "-" : ""));
        }
        System.out.println();
    }

    private boolean isNotIpOrArp()
    {
        if (readTwoFullBytes(12) != 0x0800
            && readTwoFullBytes(12) != 0x0806)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected int readTwoFullBytes(int startingByte)
    {
        return ((this.frame[startingByte] & 0xff) << 8 | this.frame[startingByte + 1] & 0xff);
    }

    protected final byte[] getIPAddressBytes(int startingByte)
    {
        byte[] tmpByte = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            tmpByte[i] = this.frame[i + startingByte];
        }
        return tmpByte;
    }
    protected int readFourFullBytes(int startingByte)
    {
        return ((this.frame[startingByte] & 0xff) << 24 | 
                (this.frame[startingByte + 1] & 0xff) << 16 | 
                (this.frame[startingByte + 2] & 0xff) << 8 | 
                 this.frame[startingByte + 3] & 0xff);
    }
    protected final byte[] getMacAddressBytes(int startingByte)
    {
        byte[] tmpByte = new byte[6];
        for (int i = 0; i < 6; i++)
        {
            tmpByte[i] = this.frame[i + startingByte];
        }
        return tmpByte;
    }

    public static int getFrameTypeHex(byte[] frame)
    {
        return ((frame[12] & 0xff) << 8) | (frame[13] & 0xff);
    }
    public int getSourcePort()
    {
        return 0;
    }

    public int getDestinationPort()
    {
        return 0;
    }

    public InetAddress getSourceIP()
    {
        return null;
    }

    public InetAddress getDestinationIP()
    {
        return null;
    }
}
