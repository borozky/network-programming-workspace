package networkinterface;

import java.net.*;
import java.util.*;
import static java.lang.System.out;

/**
 * Program that prints<br/>
 * - name of network interface</br>
 * - actual name <br/>
 * - hardware address (MAC)<br/>
 * - list of sub-interfaces<br/>
 * - list of host names and host addresses<br/>
 * 
 * @author Joshua Orozco
 */
public class ListNIFs 
{
    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        
        for (NetworkInterface netIf : Collections.list(nets)) {
        	
        	// display name and actual name
            out.printf("Display name: %s\n", netIf.getDisplayName());
            out.printf("Name: %s\n", netIf.getName());
            
            // MAC adddress in hex form, separated by ':'
            displayMACAddress(netIf);
            
            // subinterfaces
            displaySubInterfaces(netIf);
            
            // all hosts, include each host name and host address
            displayHostsInfo(netIf);
            
        	out.printf("\n");
        }
    }

    /**
     * Prints list of sub-interfaces' display and actual names
     * 
     * @param netIf
     * @throws SocketException
     */
    static void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
        Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();
        
        for (NetworkInterface subIf : Collections.list(subIfs)) {
            out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            out.printf("\tSub Interface Name: %s\n", subIf.getName());
        }
    }
    
    
    /**
     * Prints associated hosts' names and addresses
     * 
     * @param netIf
     * @throws SocketException
     */
    static void displayHostsInfo(NetworkInterface netIf) throws SocketException {
    	out.println("InetAddresses:");
    	
    	Enumeration<InetAddress> addresses = netIf.getInetAddresses();
    	while (addresses.hasMoreElements()) {
    		InetAddress address = addresses.nextElement();
    		String hostName = address.getHostName();
    		String hostAddress = address.getHostAddress();
    		
    		out.printf("  Hostname: %s\n", hostName);
    		out.printf("  Host address: %s\n", hostAddress);
    	}
    }
    
    
    /**
     * Displays MAC address in hex format, separated with ':'
     * 
     * @param netIf
     * @throws SocketException
     */
    static void displayMACAddress(NetworkInterface netIf) throws SocketException {
    	byte[] hardwareAddress = netIf.getHardwareAddress();
    	List<String> stringList = new ArrayList<>();
    	
    	if (hardwareAddress != null && hardwareAddress.length > 0) {
    		for (byte b : hardwareAddress) {
    			String hex = String.format("%02X", b);
    			stringList.add(hex);
    		}
    	}
    	
    	out.printf("MAC Address: %s\n", String.join(":", stringList));
    }
    
} 