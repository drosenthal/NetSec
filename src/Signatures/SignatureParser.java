/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Signatures;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */

/*
 * Reads from a file of signatures and creates signature objects.
 */
public class SignatureParser
{

    private Scanner src;
    private FileReader fin;
    private ArrayList<Signature> signatureList;
    private int linePos;
    private int scannerPos;

    public ArrayList<Signature> getSignatureList()
    {
        return signatureList;
    }

    public SignatureParser(String fileName)
    {
        if (!fileName.isEmpty())
        {
            signatureList = new ArrayList<>();
            try
            {
                fin = new FileReader(fileName);
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(SignatureParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            src = new Scanner(fin);
            scanFileForSignatures();
        }
        else
        {
            System.err.println("No signature file specified.");
        }
    }

    private void scanFileForSignatures()
    {
        while (src.hasNextLine())
        {
            signatureList.add(new Signature());
            Signature currentSig = signatureList.get(linePos);
            scannerPos = 0;
            src.useDelimiter(" ");
            while (src.hasNext())
            {
                if (scannerPos < 7)
                {
                    String currentString = src.next();

                    switch (scannerPos)
                    {
                        case 0:
                            currentSig.action = currentString;
                            break;
                        case 1:
                            currentSig.protocol = currentString;
                            break;
                        case 2:
                            currentSig.srcIP = getCidrFromString(currentString);
                            break;
                        case 3:
                            currentSig.srcPort = getPorts(currentString);
                            break;
                        case 4:
                            currentSig.direction = currentString;
                            break;
                        case 5:
                            currentSig.dstIP = getCidrFromString(currentString);
                            break;
                        case 6:
                            currentSig.dstPort = getPorts(currentString);
                            break;
                        default:
                            break;
                    }
                }
                else
                {
                    src.useDelimiter(";");
                    String currentString = src.next();
                    String[] option = currentString.split(":");
                    option[0] = option[0].replaceAll("[^a-zA-Z0-9]", "");
                    if (option.length > 1) // to account for the last ) and return carriage
                    {
                        option[1] = option[1].replaceAll("[^a-zA-Z0-9+*! ]", "");
                        switch (option[0])
                        {
                            case "msg":
                                currentSig.msg = option[1];
                                break;
                            case "logto":
                                currentSig.logTo = option[1];
                                break;
                            case "ttl":
                                currentSig.ttl = option[1];
                                break;
                            case "tos":
                                currentSig.tos = option[1];
                                break;
                            case "id":
                                currentSig.id = option[1];
                                break;
                            case "fragoffset":
                                currentSig.fragOffset = option[1];
                                break;
                            case "ipoption":
                                currentSig.ipOption = option[1];
                                break;
                            case "fragbits":
                                currentSig.fragBits = option[1];
                                break;
                            case "dsize":
                                currentSig.dSize = option[1];
                                break;
                            case "flags":
                                currentSig.flags = option[1];
                                break;
                            case "seq":
                                currentSig.seq = option[1];
                                break;
                            case "ack":
                                currentSig.ack = option[1];
                                break;
                            case "itype":
                                currentSig.iType = option[1];
                                break;
                            case "icode":
                                currentSig.iCode = option[1];
                                break;
                            case "content":
                                currentSig.content = option[1];
                                break;
                            case "sameip":
                                currentSig.sameIp = option[1];
                            case "sid":
                                currentSig.sid = option[1];
                                break;
                            default:
                                System.err.println("Unknown Option: " + option[0]);
                                break;
                        }
                    }
                }
                scannerPos++;
            }
        }
    }

    private String[] getCidrFromString(String inputString)
    {
        String ipAddresses[];
        if (!inputString.equals("any"))
        {
            ipAddresses = inputString.split(",");

            ipAddresses[0] = ipAddresses[0].replaceAll("[^a-zA-Z0-9./]", "");
            if (ipAddresses.length > 1)
            {
                ipAddresses[ipAddresses.length - 1] =
                ipAddresses[ipAddresses.length - 1].replaceAll("[^a-zA-Z0-9./]", "");
            }
        }
        else
        {
            ipAddresses = new String[1];
            ipAddresses[0] = "0.0.0.0/0";
        }
        return ipAddresses;
    }

    private String[] getPorts(String inputString)
    {
        String ports[];
        if (inputString.equals("any"))
        {
            ports = new String[2];
            ports[0] = "0";
            ports[1] = "65535";
        }
        else if (inputString.startsWith(":"))
        {
            ports = new String[2];
            ports[0] = "0";
            ports[1] = inputString.replaceAll("[^a-zA-Z0-9./]", "");
        }
        else if (inputString.endsWith(":"))
        {
            ports = new String[2];
            ports[0] = inputString.replaceAll("[^a-zA-Z0-9./]", "");
            ports[1] = "65535";
        }
        else
        {
            ports = inputString.split(":");
            ports[0] = ports[0].replaceAll("[^a-zA-Z0-9./]", "");
            if (ports.length > 1)
            {
                ports[ports.length - 1] =
                ports[ports.length - 1].replaceAll("[^a-zA-Z0-9./]", "");
            }

        }
        return ports;
    }
}
