/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Signatures;

import Packets.IP.ICMPPacket;
import Packets.IP.TCPPacket;
import Packets.IPPacket;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.util.SubnetUtils;

/**
 *
 * @author David
 */
public class SignatureScanner
{

    private Signature sig;
    private IPPacket packet;
    private FileWriter outFile;
    private PrintWriter out;

    public SignatureScanner(Signature signature)
    {
        sig = signature;
        //set up output file for logto, then format message with msg and sid of signature

        try
        {
            if (!sig.logTo.isEmpty())
            {
                outFile = new FileWriter(sig.logTo);

            }
            else
            {
                outFile = new FileWriter("IDSLog.log");
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(SignatureScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
        out = new PrintWriter(outFile);
    }

    public void scanPacket(IPPacket packet)
    {
        switch (packet.ipProtocol)
        {
            case 1:
                if (sig.protocol.equals("icmp"))
                {
                    ICMPPacket icmpPacket = new ICMPPacket(packet.entireFrame);
                    if (checkPacketBasics(icmpPacket) && checkICMPPacketFlags(icmpPacket)
                        && checkIPPacketFlags())
                    {
                        out.println("ICMP Packet is flagged:" + sig.msg);
                    }
                    else
                    {
                        out.println("ICMP Packet is clear");
                    }
                }
                out.close();
                break;

            case 6:
                if (sig.protocol.equals("tcp"))
                {
                    TCPPacket tcpPacket = new TCPPacket(packet.entireFrame);
                    if (checkPacketBasics(tcpPacket) && checkTCPPacketFlags(tcpPacket)
                        && checkIPPacketFlags())
                    {
                        out.println("TCP Packet is flagged" + sig.msg);
                    }
                    else
                    {
                        out.println("TCP Packet is clear");
                    }
                }
                out.close();
                break;
            default:
                this.packet = new IPPacket(packet.entireFrame);
                if (checkPacketBasics(this.packet) && checkIPPacketFlags())
                {
                    out.println("IP Packet is flagged" + sig.msg);
                }
                else
                {
                    out.println("IP Packet is clear");
                }
                out.close();
                break;
        }

    }

    private boolean checkPacketBasics(IPPacket packet)
    {
        return checkSrcPortRanges(packet.getSourcePort())
               && checkDstPortRanges(packet.getDestinationPort())
               && checkDataFlow();
    }

    private boolean checkSrcIPRange(InetAddress packetAddress)
    {

        SubnetUtils utils;
        boolean finalResult = true;
        for (String s : sig.srcIP)
        {
            utils = new SubnetUtils(s);
            finalResult =
            finalResult || utils.getInfo().isInRange(packetAddress.toString());
        }

        return finalResult;
    }

    private boolean checkDstIPRange(InetAddress packetAddress)
    {

        SubnetUtils utils;
        boolean finalResult = true;
        for (String s : sig.dstIP)
        {
            utils = new SubnetUtils(s);
            finalResult =
            finalResult || utils.getInfo().isInRange(packetAddress.toString());
        }

        return finalResult;
    }

    private boolean checkDataFlow()
    {
        if (sig.direction.equals("->"))
        {
            if (checkSrcIPRange(packet.getSourceIP())
                && checkDstIPRange(packet.getDestinationIP()))
            {
                return true;
            }
        }
        else if (sig.direction.equals("<>"))
        {
            if (checkSrcIPRange(packet.getSourceIP())
                && checkDstIPRange(packet.getDestinationIP())
                || checkSrcIPRange(packet.getDestinationIP())
                   && checkDstIPRange(packet.getSourceIP()))
            {
                return true;
            }
        }
        else
        {
            return false;
        }
        return false;
    }

    private boolean checkSrcPortRanges(int port)
    {
        int startPort = Integer.parseInt(sig.srcPort[0]);
        int endPort = Integer.parseInt(sig.srcPort[1]);
        if (port > startPort && port < endPort)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkDstPortRanges(int port)
    {
        int startPort = Integer.parseInt(sig.dstPort[0]);
        int endPort = Integer.parseInt(sig.dstPort[1]);
        if (port > startPort && port < endPort)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkTCPPacketFlags(TCPPacket tcpPacket)
    {
        if (checkFlags(tcpPacket) && checkSequenceNumber(tcpPacket)
            && checkAckValue(tcpPacket))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkICMPPacketFlags(ICMPPacket icmpPacket)
    {
        if (checkIType(icmpPacket) && checkICode(icmpPacket))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkIPPacketFlags()
    {
        if (checkTTL() && checkTOS() && checkID() && checkFragOffset()
            && checkIPOption() && checkFragBits() && checkDSize()
            && checkContent() && checkSameIP())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkTTL()
    {
        if (!sig.ttl.isEmpty())
        {
            if (packet.timeToLive == Integer.parseInt(sig.ttl))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkTOS()
    {
        if (!sig.tos.isEmpty())
        {
            //Needs implementing
            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean checkID()
    {
        if (!sig.id.isEmpty())
        {
            if (packet.identification == Integer.parseInt(sig.id))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkFragOffset()
    {
        if (!sig.fragOffset.isEmpty())
        {
            if (packet.fragmentOffset == Integer.parseInt(sig.fragOffset))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkIPOption()
    {
        if (!sig.ipOption.isEmpty())
        {
            //Needs implementing
            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean checkFragBits()
    {
        if (!sig.fragBits.isEmpty())
        {
            //Needs implementing
            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean checkDSize()
    {
        if (!sig.dSize.isEmpty())
        {
            if (packet.data.length == Integer.parseInt(sig.dSize))
            {
                return true;
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean checkFlags(TCPPacket tcpPacket)
    {
        if (!sig.flags.isEmpty())
        {
            if (tcpPacket.flags == Integer.parseInt(sig.flags))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkSequenceNumber(TCPPacket tcpPacket)
    {
        if (!sig.seq.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkAckValue(TCPPacket tcpPacket)
    {
        if (!sig.ack.isEmpty())
        {
            if (tcpPacket.acknowledgementNumber == Integer.parseInt(sig.ack))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkIType(ICMPPacket icmpPacket)
    {
        if (!sig.iType.isEmpty())
        {
            if (icmpPacket.type == Integer.parseInt(sig.iType))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkICode(ICMPPacket icmpPacket)
    {
        if (!sig.iCode.isEmpty())
        {
            if (icmpPacket.code == Integer.parseInt(sig.iCode))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean checkContent()
    {
        if (!sig.content.isEmpty())
        {
            //Needs implementing
            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean checkSameIP()
    {
        if (!sig.sameIp.isEmpty())
        {
            if (packet.getSourceIP().equals(packet.getDestinationIP()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
