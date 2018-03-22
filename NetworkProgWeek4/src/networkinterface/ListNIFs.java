package networkinterface;

import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class ListNIFs 
{
    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        
        for (NetworkInterface netIf : Collections.list(nets)) {
            out.printf("Display name: %s\n", netIf.getDisplayName());
            out.printf("Name: %s\n", netIf.getName());
            displayMACAddress(netIf);
            displaySubInterfaces(netIf);
            displayHostsInfo(netIf);
        	out.printf("\n");
        }
    }

    
    static void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
        Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();
        
        for (NetworkInterface subIf : Collections.list(subIfs)) {
            out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            out.printf("\tSub Interface Name: %s\n", subIf.getName());
        }
    }
    
    
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