package Fragmentation;


import Fragmentation.Hole;
import Packets.*;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author David
 */
public final class Assembler implements Comparable<Assembler>
{

    private int assemblerID;
    private long timeOutTimeStamp;
    private boolean isArp = false;
    private boolean hasOverlap = false;
    private boolean isTooLarge = false;
    private boolean isComplete = false;
    boolean noMoreHoles;
    private int sid = -1;
    private IPPacket initialPacket;
    private IPPacket completePacket;
    private ARPFrame finalArpFrame;
    private ArrayList<IPPacket> fragmentList;
    private ArrayList<ARPFrame> arpSegment;
    private int[] dataValues;
    private ArrayList<Hole> holeDescriptorList;
    //Getters

    public int getAssemblerId()
    {
        return assemblerID;
    }

    public int getSid()
    {
        if (isTooLarge)
        {
            sid = 3;
        }
        else if (hasOverlap)
        {
            sid = 2;
        }
        else if (isArp)
        {
            sid = 0;
        }
        else
        {
            sid = 1;
        }
        return sid;
    }

    public IPPacket getFinalPacket()
    {
        return completePacket;
    }

    public ArrayList<IPPacket> getFragmentList()
    {
        return fragmentList;
    }

    //Constructor
    public Assembler(IPPacket packet)
    {
        assemblerID = ((packet.identification << 16) ^ packet.getSourceIP().hashCode()
                       ^ packet.getDestinationIP().hashCode() ^ packet.ipProtocol);
        fragmentList = new ArrayList<>();
        initialPacket = packet;
        holeDescriptorList = new ArrayList<>();
        holeDescriptorList.add(new Hole(0, 66000));
        setUpCompletePacket();
        addFragment(initialPacket);
        //Two minute timeout
        timeOutTimeStamp = System.currentTimeMillis() + 10000;
    }

    /*
     * CASE 1: FRAME IS AN ARP
     */
    public Assembler(ARPFrame arpFrame)
    {
        arpSegment = new ArrayList<>();
        arpSegment.add(finalArpFrame);
        finalArpFrame = arpFrame;
        sid = 0;
        isArp = true;
    }

    public void addFragment(IPPacket fragment)
    {
        if (fragment.isFirstAndLastPacket())
        {
            fragmentList.clear();
            holeDescriptorList.clear();
            holeDescriptorList.add(new Hole(0, 66000));
        }

        fragmentList.add(fragment);
        noMoreHoles = scanDataHoles(fragment);
        if (noMoreHoles)
        {
            dataValues = checkForOverlap();
            for (int i = 0; i < dataValues.length; i++)
            {
                if (dataValues[i] == 0)
                {
                    //value not set --> return false, reset dataValues(for memory)
                    dataValues = new int[1];
                }
            }
            assembleFragments();
            //run final calculations
        }
    }

    private int[] checkForOverlap()
    {
        int tempValues[];
        int lastFragIndex = -1;
        for (int i = 0; i < fragmentList.size(); i++)
        {
            if (fragmentList.get(i).isLastPacket() == true)
            {
                lastFragIndex = i;
                break;
            }
        }
        //use last fragment index to figure out size of packet
        IPPacket tempFrag = fragmentList.get(lastFragIndex);
        tempValues = new int[(tempFrag.fragmentOffset * 8) + tempFrag.data.length];
        for (int i = 0; i < tempValues.length; i++)
        {
            tempValues[i] = 0;
        }
        for (int i = 0; i < fragmentList.size(); i++)
        {
            tempFrag = fragmentList.get(i);
            int tempOffset = tempFrag.fragmentOffset * 8;
            int tempLength = tempFrag.data.length;
            for (int j = 0; j < tempLength; j++)
            {
                try
                {
                    if (tempValues[j + tempOffset] == 1)
                    {
                        hasOverlap = true;
                    }
                    tempValues[j + tempOffset] = 1;
                }
                catch (Exception e)
                {
                    System.err.println(fragmentList.size());
                    System.err.println(j);
                    System.err.println(tempValues.length);
                    System.err.println(tempOffset);
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        return tempValues;
    }

    private void assembleFragments()
    {
        //use dataValues.size to determine data.size
        completePacket.data = new byte[dataValues.length];

        //go though each fragment in order, adding to data
        //remember to check for older data --> keep older data over new data
        //		when placing data at an index --> change dataValues[index] = 0	
        IPPacket tempFrag;
        for (int i = 0; i < fragmentList.size(); i++)
        {
            tempFrag = fragmentList.get(i);
            int startOffset = tempFrag.fragmentOffset * 8;

            for (int j = 0; j < tempFrag.data.length; j++)
            {
                if (dataValues[j + startOffset] == 1)
                {
                    completePacket.data[j + startOffset] = tempFrag.data[j];
                }
                dataValues[j + startOffset] = 0;

            }

            
        }
        isComplete = true;
    }

    private void setUpCompletePacket()
    {
        completePacket = new IPPacket(initialPacket);
        completePacket.headerLength = 20;
        completePacket.fragmentOffset = 0;
        completePacket.flags = 0;
        completePacket.headerChecksum = 0;
    }

    private boolean scanDataHoles(IPPacket fragment)
    {
        for (int k = 0; k < holeDescriptorList.size(); k++)
        {
            Hole hole = holeDescriptorList.get(k);
            if (fragment.first > hole.last)
            {
                if (k == holeDescriptorList.size() - 1)//last hole
                {
                    isTooLarge = true; //larger than 64k or overwriting last packet
                }
                continue;
            }
            else if (fragment.last < hole.first)
            {
                continue;
            }
            else
            {
                holeDescriptorList.remove(hole);
                if (fragment.first > hole.first)
                {
                    holeDescriptorList.add(new Hole(hole.first, fragment.first - 1));
                }
                if (fragment.last < hole.last && fragment.flags == 1)
                {
                    holeDescriptorList.add(new Hole(fragment.last + 1, hole.last));
                }
                break;
            }
        }
        if (holeDescriptorList.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void print()
    {
        if (!isArp)
        {
            System.out.println("Assembler " + assemblerID + ":");
            System.out.println("\tSID: " + this.getSid());
            System.out.println("\tFinal Datagram: ");
            System.out.println("\t# of fragments: " + fragmentList.size());
        }
        else
        {
            System.out.println("Assembler " + assemblerID + ":");
            System.out.println("\tSID: " + this.getSid());
            System.out.println("\tFinal Datagram: ");
            System.out.println("\t# of fragments: " + arpSegment.size());
        }
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public boolean isTimedOut()
    {
        if ((System.currentTimeMillis() > timeOutTimeStamp) == true)
        {
            this.sid = 4;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int compareTo(Assembler o)
    {
        return (int) (o.timeOutTimeStamp - this.timeOutTimeStamp);
    }
}
