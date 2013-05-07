
import Fragmentation.Assembler;
import Fragmentation.FragmentationFacility;
import Packets.ARPFrame;
import Packets.Frame;
import Packets.IPPacket;
import Signatures.Signature;
import Signatures.SignatureParser;
import Signatures.SignatureScanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
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
public class PacketParser implements Runnable
{

    SimplePacketDriver driver;
    List<byte[]> packetList;
    byte[] packet;
    int exitCount;
    private List<Frame> frameVector;
    Scanner fileScanner;
    private ArgumentHandler args;
    Map<String, String> argToClassDictionary;
    FragmentationFacility assemblerFacility;
    SignatureParser sigParser;

    public PacketParser(ArgumentHandler handler)
    {
        args = handler;
        args.print();
        argToClassDictionary = new HashMap<>();
        argToClassDictionary.put("eth", "Frame");
        argToClassDictionary.put("arp", "ARPFrame");
        argToClassDictionary.put("ip", "ARPFrame");
        argToClassDictionary.put("icmp", "ICMPPacket");
        argToClassDictionary.put("tcp", "TCPPacket");
        argToClassDictionary.put("udp", "UDPPacket");

        exitCount = args.count;
        frameVector = Collections.synchronizedList(new ArrayList<Frame>());
        assemblerFacility = new FragmentationFacility();
        sigParser = new SignatureParser(args.signatureFileName);
        if (args.readFileName.isEmpty())
        {
            driver = new SimplePacketDriver();
            //Get adapter names and print info
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
        }
        else
        {
            try
            {
                fileScanner = new Scanner(new File(args.readFileName));
                readFromFile();
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(PacketParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void run()
    {
        
        boolean exitBool = true;
        while (exitBool)
        {
            packet = driver.readPacket();
            determineFrameTypeAndSendToAssembler(packet);
            if (exitCount > 0)
            {
                exitCount--;
            }
            else
            {
                exitBool = false;
            }

        }
        new Thread(new PrintPacket()).start();
    }

    private void determineFrameTypeAndSendToAssembler(byte[] packetToRead)
    {
        switch (Frame.getFrameTypeHex(packetToRead))
        {
            case 0x0800:
                assemblerFacility.add(new IPPacket(packetToRead));
 //               frameVector.add(new IPPacket(packetToRead));
                break;
            case 0x0806:
                assemblerFacility.add(new ARPFrame(packetToRead));
//                frameVector.add(new IPPacket(packetToRead));
                break;
            default:
                frameVector.add(new Frame(packetToRead));
                break;
        }
    }

    private void readFromFile()
    {
        boolean exitBool = true;
        while (exitBool)
        {
            StringBuilder sb = new StringBuilder();
            String currentLine;
            boolean newLineNotReached = true;
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
                packet = hexStringToByteArray(normalizedHexString);
            }
            determineFrameTypeAndSendToAssembler(packet);
            if (exitCount > 0)
            {
                exitCount--;
            }
            else
            {
                exitBool = false;
            }
            
        }new Thread(new PrintPacket()).start();
    }

    private class PrintPacket implements Runnable
    {
        @Override
        public void run()
        {
            PriorityQueue<Assembler> timeoutQueue = assemblerFacility.getAssemblerQueue();
            Assembler currentAssembler;
            while (timeoutQueue.isEmpty() == false)
            {
                System.out.println(timeoutQueue.size());
                if (timeoutQueue.peek().isComplete())
                {
                    currentAssembler = timeoutQueue.poll();
                    currentAssembler.print();
                    for(Signature s : sigParser.getSignatureList())
                    {
                        SignatureScanner sigScanner = new SignatureScanner(s);
                        sigScanner.scanPacket(currentAssembler.getFinalPacket());
                    }
                }
                else if(timeoutQueue.peek().isTimedOut())
                {
                    System.err.println("Assembler timed out.");
                    timeoutQueue.poll().print();
                }
            }
//            boolean exitBool = true;
//            int oldSize = 0;
//            int newSize = 0;
//            while (exitBool)
//            {
//                newSize = frameVector.size();
//                if (newSize > oldSize)
//                {
//                    for (int i = oldSize; i < newSize; i++)
//                    {
//                        filterPrint(frameVector.get(i));
//                    }
//                }
//                oldSize = newSize;
//
//                if (exitCount != -1)
//                {
//                    if (newSize >= exitCount)
//                    {
//                        exitBool = false;
//                    }
//                }
//            }
//            System.out.println("PrintPacket thread ending at: " + frameVector.size());
        }

        private void filterPrint(Frame frame)
        {
            if (matchesSpecifiedType(frame)
                && withinSourcePortRange(frame)
                && withinDestinationPortRange(frame)
                && hasSourceAddress(frame)
                && hasDestinationAddress(frame)
                && matchesSourceAndDestination(frame)
                && matchesSourceOrDestination(frame))
            {
                frame.print();
            }
            else
            {
                System.out.println("Frame Skipped: does not match criteria filters");
            }
        }
    }

    private boolean matchesSpecifiedType(Frame frame)
    {
        if (!args.packetType.isEmpty()
            && frame.getClass().getName().equals(argToClassDictionary.get(args.packetType)))
        {
            return true;
        }
        else if (args.packetType.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean withinSourcePortRange(Frame frame)
    {
        if (args.sourcePortFilterStart == 0
            || args.sourcePortFilterEnd == 0)
        {
            return true;
        }
        else if (frame.getSourcePort() >= args.sourcePortFilterStart
                 && frame.getSourcePort() <= args.sourcePortFilterEnd)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean withinDestinationPortRange(Frame frame)
    {
        if (args.destinationPortFilterStart == 0
            || args.destinationPortFilterEnd == 0)
        {
            return true;
        }
        else if (frame.getDestinationPort() >= args.destinationPortFilterStart
                 && frame.getDestinationPort() <= args.destinationPortFilterEnd)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean hasSourceAddress(Frame frame)
    {
        if (frame.getSourceIP() == args.sourceAddressFilter)
        {
            return true;
        }
        else if (args.sourceAddressFilter == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean hasDestinationAddress(Frame frame)
    {
        if (frame.getDestinationIP() == args.destinationAddressFilter)
        {
            return true;
        }
        else if (args.destinationAddressFilter == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean matchesSourceAndDestination(Frame frame)
    {
        if (frame.getSourceIP() == args.sourceAndFilter
            && frame.getDestinationIP() == args.destinationAndFilter)
        {
            return true;
        }
        else if (args.sourceAndFilter == null
                 || args.destinationAndFilter == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean matchesSourceOrDestination(Frame frame)
    {
        if (frame.getSourceIP() == args.sourceOrFilter
            || frame.getDestinationIP() == args.destinationOrFilter)
        {
            return true;
        }
        else if (args.sourceOrFilter == null
                 || args.destinationOrFilter == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                  + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}