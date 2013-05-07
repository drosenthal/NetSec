
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Scanner;
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 *
 * @author David
 */
public class NetSecMain
{

    public static FileOutputStream fos;
    public static PrintWriter pw;
    public static TeeOutputStream tos;
    public static Scanner scan;

    public static void main(String[] args) throws UnknownHostException, FileNotFoundException
    {
        ArgumentHandler handler = new ArgumentHandler(args);
        if (!handler.outputFileName.isEmpty())
        {
            fos = new FileOutputStream(handler.outputFileName);
            tos = new TeeOutputStream(fos, System.out); //Splits the file stream to console and file
            PrintStream ps = new PrintStream(tos);
            System.setOut(ps);
        }
        scan = new Scanner(System.in);

        System.out.println("1 - Packet Parser\n"
                           + "2 - Packet Generator");
        int num = scan.nextInt();
        scan.close();

        switch (num)
        {
            case 1:
                PacketParser parser = new PacketParser(handler);

                if (parser.driver != null)
                {
                    new Thread(parser).start();
                }
                break;

            case 2:
                PacketGenerator generator = new PacketGenerator(handler);
                break;
            default:
                System.out.println("Not a valid choice.");
                break;

        }


    }
}
