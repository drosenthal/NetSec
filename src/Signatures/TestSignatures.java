/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Signatures;

import java.io.FileNotFoundException;

/**
 *
 * @author David
 */
public class TestSignatures
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        SignatureParser sigParser = new SignatureParser(args[0]);
    }
}
