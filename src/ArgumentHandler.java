import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.cli.*;

/**
 *
 * @author David
 */
public class ArgumentHandler 
{
    public int count = -1;
    public String readFileName = "";
    public String signatureFileName = "";
    public String outputFileName = "";
    public String packetType = "";
    public boolean headerInfoOnly = false;
    public InetAddress sourceAddressFilter;
    public InetAddress destinationAddressFilter;
    public InetAddress sourceOrFilter;
    public InetAddress destinationOrFilter;
    public InetAddress sourceAndFilter;
    public InetAddress destinationAndFilter;
    public int sourcePortFilterStart;
    public int sourcePortFilterEnd;
    public int destinationPortFilterStart;
    public int destinationPortFilterEnd;
    
    public ArgumentHandler(String args[]) throws UnknownHostException{
            //Prints usage statements
        HelpFormatter formatter = new HelpFormatter();
        Option packetCounter = OptionBuilder.withArgName("count")
                        .hasArg()
                        .withDescription("Exit after receiving count packets.")
                        .create('c');
        
        Option signatureFile = OptionBuilder.withArgName("filename")
                        .hasArg()
                        .withDescription("File from which to read signatures.")
                        .create('s');
        
        Option readFile = OptionBuilder.withArgName("filename")
                        .hasArg()
                        .withDescription("Read packets from specified file.")
                        .create('r');
        
        Option outputFile = OptionBuilder.withArgName("filename")
                        .hasArg()
                        .withDescription("Save output to filename.")
                        .create('o');
        
        Option packetFilter = OptionBuilder.withArgName("type")
                        .hasArg()
                        .withDescription("Prints only packets of the specified"
                + "type where type is one of: eth, arp, ip, icmp, tcp, or udp.")
                        .create('t');
        
        Option sourceAddress = OptionBuilder.withArgName("saddress")
                        .hasArg()
                        .withDescription("Print only packets with source address"
                + "equal to saddress")
                        .create("src");
        
        Option destinationAddress = OptionBuilder.withArgName("daddress")
                        .hasArg()
                        .withDescription("Print only packets with destination address"
                + "equal to daddress")
                        .create("dst");
        
        Option sord = OptionBuilder.withArgName("saddress daddress")
                        .hasArgs(2)
                        .withDescription("Print only packets where the source address"
                + " matches saddress or the desination address matches daddress.")
                        .create("sord");
        
        Option sandd = OptionBuilder.withArgName("saddress daddress")
                        .hasArgs(2)
                        .withDescription("Print only packets where the source address"
                + " matches saddress and the desination address matches daddress.")
                        .create("sandd");
        
        Option sport = OptionBuilder.withArgName("port1 port2")
                        .hasArgs(2)
                        .withDescription("Print only packets where the source port is "
                + "in the range port1-port2.")
                        .create("sport");
        
        Option dport = OptionBuilder.withArgName("port1 port2")
                        .hasArgs(2)
                        .withDescription("Print only packets where the destination port "
                + "is in the range port1-port2.")
                        .create("dport");
        
        Option headerInfoOnlySwitch = new Option("h", "Print header info only as specified by -t");
        Option help = new Option("help", "Displays this help menu.");
        Options options = new Options();

        options.addOption(packetCounter);
        options.addOption(readFile);
        options.addOption(signatureFile);
        options.addOption(outputFile);
        options.addOption(packetFilter);
        options.addOption(headerInfoOnlySwitch);
        options.addOption(sourceAddress);
        options.addOption(destinationAddress);
        options.addOption(sord);
        options.addOption(sandd);
        options.addOption(sport);
        options.addOption(dport);
        options.addOption(help);
        
        
        // create the parser
        CommandLineParser parser = new GnuParser();
        try
        {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("c"))
            {
                this.count = Integer.parseInt(line.getOptionValue("c"));
            }
            
            if (line.hasOption("r"))
            {
                this.readFileName = line.getOptionValue("r");
            }
            if (line.hasOption("s"))
            {
                this.signatureFileName = line.getOptionValue("s");
            }
            if (line.hasOption("o"))
            {
                this.outputFileName = line.getOptionValue("o");
            }
            if (line.hasOption("t"))
            {
                this.packetType = line.getOptionValue("t");
                if (line.hasOption("h"))
                {
                    this.headerInfoOnly = true;
                }
            }
            
            if (line.hasOption("src"))
            {
                this.sourceAddressFilter = InetAddress.getByName(line.getOptionValue("src"));
            }
            if (line.hasOption("dst"))
            {
                this.destinationAddressFilter = InetAddress.getByName(line.getOptionValue("dst"));
            }
            if (line.hasOption("sord"))
            {
                String [] values = line.getOptionValues("sord");
                this.sourceOrFilter = InetAddress.getByName(values[0]);
                this.destinationOrFilter = InetAddress.getByName(values[1]);
            }
            if (line.hasOption("sandd"))
            {
                String [] values = line.getOptionValues("sandd");
                this.sourceAndFilter = InetAddress.getByName(values[0]);
                this.destinationAndFilter = InetAddress.getByName(values[1]);
            }
            if (line.hasOption("sport"))
            {
                String [] values = line.getOptionValues("sport");
                this.sourcePortFilterStart = Integer.parseInt(values[0]);
                this.sourcePortFilterEnd = Integer.parseInt(values[1]);
            }
            if (line.hasOption("dport"))
            {
                String [] values = line.getOptionValues("sport");
                this.destinationPortFilterStart = Integer.parseInt(values[0]);
                this.destinationPortFilterEnd = Integer.parseInt(values[1]);
            }
            if (line.hasOption("help"))
            {
                formatter.printHelp("Network Parser", options);
            }
        }
        catch (ParseException exp)
        {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }   
    }
    public void print()
    {
        System.out.println("----- Arguments -----");
        System.out.println("Packet counter: " + this.count);
        System.out.println("Read File Name: " + this.readFileName);
        System.out.println("Signature File Name:" + this.signatureFileName);
        System.out.println("Output File Name: " + this.outputFileName);
        System.out.println("Packet type filter: " + this.packetType);
        System.out.println("Header info only?: " + this.headerInfoOnly);
        System.out.println("Source Address Filter: " + this.sourceAddressFilter);
        System.out.println("Destination Address Filter: " + this.destinationAddressFilter);
        System.out.println("Source OR Filter: " + this.sourceOrFilter);
        System.out.println("Destination OR Filter: " + this.destinationOrFilter);
        System.out.println("Source AND Filter: " + this.sourceAndFilter);
        System.out.println("Destination AND Filter: " + this.destinationAndFilter);
        System.out.println("Source Port Filter Starting Port: " + this.sourcePortFilterStart);
        System.out.println("Source Port Filter Ending Port: " + this.sourcePortFilterEnd);
        System.out.println("Destination Port Filter Starting Port: " + this.destinationPortFilterStart);
        System.out.println("Destination Port Filter Ending Port: " + this.destinationPortFilterEnd);
        System.out.println("----- End Arguments -----\n" );
        
    }
}

