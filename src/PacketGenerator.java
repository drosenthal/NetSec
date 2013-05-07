
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author David
 */
public class PacketGenerator
{

    byte[] packetToSend;
    Scanner fileScanner;
    ArgumentHandler handler;
    File readFile;
    SimplePacketDriver driver;

    public PacketGenerator(ArgumentHandler _handler)
    {
        //Set up Driver
        driver = new SimplePacketDriver();
        String[] adapters = driver.getAdapterNames();
        System.out.println("Number of adapters: " + adapters.length);
        for (int i = 0; i < adapters.length; i++)
        {
            System.out.println("Device name in Java =" + adapters[i]);
        }
        if (driver.openAdapter(adapters[0]))
        {
            System.out.println("Adapter is open: " + adapters[0]);
        }


        handler = _handler;
        readFile = new File(handler.readFileName);
        try
        {
            fileScanner = new Scanner(readFile);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(PacketParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendPacket();
    }

    private void sendPacket()
    {
        StringBuilder sb = new StringBuilder();
        String currentLine;
        boolean newLineNotReached = true;
        for (int i = 0; i < handler.count; i++)
        {
            while (newLineNotReached)
            {
                if (fileScanner.hasNextLine())
                {
                    currentLine = fileScanner.nextLine();
                    if (!currentLine.equals(""))
                    {
                        sb.append(currentLine);
                    }
                    else
                    {
                        newLineNotReached = false;
                    }
                }
            }
            String normalizedHexString = sb.toString().replaceAll(" ", "");
            if (!normalizedHexString.isEmpty())
            {
                packetToSend = PacketParser.hexStringToByteArray(normalizedHexString);
                System.out.println(packetToSend);
                driver.sendPacket(packetToSend);
            }
        }
    }
}
