/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Signatures;

/**
 *
 * @author David
 */
public class Signature
{

    public String action;
    public String protocol;
    public String[] srcIP;
    public String[] srcPort;
    public String direction;
    public String[] dstIP;
    public String[] dstPort;
    /*
     * OPTIONS
     */
    public String msg = "";
    public String logTo = "";
    public String ttl = "";
    public String tos = "";
    public String id = "";
    public String fragOffset = "";
    public String ipOption = "";
    public String fragBits = "";
    public String dSize = "";
    public String flags = "";
    public String seq = "";
    public String ack = "";
    public String iType = "";
    public String iCode = "";
    public String content = "";
    public String sameIp = "";
    public String sid = "";

    public Signature()
    {
        System.out.println("New signature created.");
    }
}
