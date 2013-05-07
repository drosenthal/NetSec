package Packets;




import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ARPFrame extends Frame
{

    private int hardwareType;
    private int protocolType;
    private int hardwareSize;
    private int protocolSize;
    private int opCode;
    private byte[] senderMac;
    private InetAddress senderIP;
    private byte[] targetMac;
    private InetAddress targetIP;

    //TODO: program the OPTIONS field
    
    public ARPFrame(byte[] frameRead)
    {
        super(frameRead);
        hardwareType = readTwoFullBytes(14);
        protocolType = readTwoFullBytes(16);
        hardwareSize = frame[18];
        protocolSize = frame[19];
        opCode = readTwoFullBytes(20);
        senderMac = getMacAddressBytes(22);
        targetMac = getMacAddressBytes(32);
        try
        {
            senderIP = InetAddress.getByAddress(getIPAddressBytes(28));
            targetIP = InetAddress.getByAddress(getIPAddressBytes(38));
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(ARPFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//TODO: program gethardwaretypename
    private String getHardwareTypeName(int hType)
    {
        return "";
    }
//TODO: program getprotocoltypename
    private String getProtocolTypeName(int pType)
    {
        return "";
    }
    
//TODO: program getOpCodeTypeName
    private String getOpCodeTypeName(int oCode)
    {
        return "";
    }
    
    @Override
    public void print()
    {
        super.print();
        System.out.println("Frame type: ARP");
        System.out.println("------------------------------");
        System.out.println("ARP Header");
        System.out.println("------------------------------");
        System.out.println("Hardware Type: " + hardwareType);
        System.out.println("Protocol Type: " + protocolType);
        System.out.println("Hardware Size: " + hardwareSize);
        System.out.println("Protocol Size: " + protocolSize);
        System.out.println("Op Code: " + opCode);
        System.out.println("Sender Mac: ");
        printMacAddress(senderMac);
        System.out.println("Sender IP: " + senderIP);
        System.out.println("Target Mac: ");
        printMacAddress(targetMac);
        System.out.println("Target IP: " + targetIP);
        if (this.getClass().getName().equals("Packets.ARPFrame"))
        {
            System.out.println("############END OF LINE#############");
        }
    }
}
