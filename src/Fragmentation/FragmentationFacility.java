package Fragmentation;


import Packets.ARPFrame;
import Packets.IPPacket;
import java.util.PriorityQueue;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author David
 */
public class FragmentationFacility
{

    private PriorityQueue<Assembler> assemblerQueue;

    public PriorityQueue<Assembler> getAssemblerQueue()
    {
        return assemblerQueue;
    }
    //Constructor

    public FragmentationFacility()
    {
        assemblerQueue = new PriorityQueue<>();
    }

    public void add(ARPFrame arpFrame)
    {
        assemblerQueue.add(new Assembler(arpFrame));
        System.out.println("Arp Queue created.");
    }

    public void add(IPPacket ipPacket)
    {
        if (ipPacket.hasCorrectChecksum() == true)
        {
            int count = 1;
            if (!assemblerQueue.isEmpty())
            {
                for (Assembler a : assemblerQueue)
                {
                    int assemblerHash = ((ipPacket.identification << 16) ^ ipPacket.getSourceIP().hashCode()
                                         ^ ipPacket.getDestinationIP().hashCode() ^ ipPacket.ipProtocol);
                    //An assembler exists for this signature
                    if (a.getAssemblerId() == assemblerHash && !(a.isComplete()))
                    {
                        a.addFragment(ipPacket);
                        System.out.println("Fragment Added to existing Queue: " + assemblerHash);
                        break;
                    }
                    //An assembler does NOT exist for this signature
                    else if (count == assemblerQueue.size())
                    {
                        assemblerQueue.add(new Assembler(ipPacket));
                        System.out.println("New Queue " + assemblerHash + " created.");
                        break;
                    }
                    //Signature doesn't exist and the loop has NOT checked all 
                    //assemblers for a match
                    else
                    {
                        count++;
                    }
                }
            }
            else
            {
                assemblerQueue.add(new Assembler(ipPacket));
                System.out.println("New Queue Created.");
            }
        }
    }
}
