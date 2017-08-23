package org.tsg.inspector;

import java.util.*;
import java.io.*;

import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.IPv6;
import org.onlab.packet.Ip4Address;
import org.onlab.packet.Ip6Address;
import org.onlab.packet.TCP;
import org.onlab.packet.UDP;
import org.onlab.packet.MacAddress;

//used as the key in the key-map
public class SourceDestType {
		
	String EthType;
	String Protocol;
	String MACSrc;
	String MACDst;
	String IPSrc;
	String IPDst;
	String PortSrc;
	String PortDst;
	
	private static boolean ipv6enabled = false;
	
	private static String ipv4Title = "ETH TYPE|PROTOCOL|  SRC MAC ADDRESS |  SRC IP ADDRESS |SRC PORT|  DST MAC ADDRESS |  DST IP ADDRESS |DST PORT|PACKET COUNT|  TOTAL BYTES  |AVG PACKET SIZE|\n";
	private static String ipv6Title = "ETH TYPE|PROTOCOL|  SRC MAC ADDRESS |     SRC IP ADDRESS      |SRC PORT|  DST MAC ADDRESS |     DST IP ADDRESS      |DST PORT|PACKET COUNT|  TOTAL BYTES  |AVG PACKET SIZE|\n";   	
	private static String ipv4Row = "%-8d|%-8d|%-18d|%-17d|%-8d|%-18d|%-17d|%-8d|%-12d|%-15d|%-15d\n";
	private static String ipv6Row = "%-8d|%-8d|%-18d|%-25d|%-8d|%-18d|%-25d|%-8d|%-12d|%-15d|%-15d\n";
	private static String ipv4Key = "%-8s|%-8s|%-18s|%-17s|%-8s|%-18s|%-17s|%-8s|";
	private static String ipv6Key = "%-8s|%-8s|%-18s|%-25s|%-8s|%-18s|%-25s|%-8s|";
	private static String ipValue = "%-12d|%-15d|%-15d\n";  
	private static String ipv4Split = "-----------------------------------------------------------------------------------------------------------------------------------------------------------\n--------------------------------------------------------------------TOTAL COUNTS---------------------------------------------------------------------------\n-----------------------------------------------------------------------------------------------------------------------------------------------------------\n";
	private static String ipv6Split = "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n---------------------------------------------------------------------------------TOTAL COUNTS------------------------------------------------------------------------------\n---------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n";
	private static long totalPackets, totalPacketSize;
	private static AppComponent app;
	static public void addPacketTotals(long ps) {
		totalPackets++;
		totalPacketSize += ps;
	}
	
	static public String createKey(AppComponent inspector, short etherType, byte protocol, MacAddress macSrc, byte[] ipSrc, short portSrc, MacAddress macDst, byte[] ipDst, short portDst) {
		app = inspector;
		String k1,k2,k3,k4="N/A",k5="N/A",k6,k7="N/A",k8="N/A";	
		//etherType
		switch (etherType) {
			case Ethernet.TYPE_ARP:
				k1 = "ARP"; break;
			case Ethernet.TYPE_RARP: 
				k1 = "RARP"; break;
			case Ethernet.TYPE_IPV4:
				k1 = "IPV4"; break;
			case Ethernet.TYPE_IPV6:
				k1 = "IPV6"; break;
			case Ethernet.TYPE_LLDP:
				k1 = "LLDP"; break;
			case Ethernet.TYPE_VLAN:
				k1 = "VLAN"; break;
			case Ethernet.VLAN_UNTAGGED:
				k1 = "VLAN_UT"; break;
			default:
				k1 = "UNKNOWN"; break;
		}	

		//protocol
		switch (protocol) {
			case IPv4.PROTOCOL_ICMP:
				k2 = "ICMP"; break;
			case IPv6.PROTOCOL_TCP:
				k2 = "TCP"; break;
			case IPv6.PROTOCOL_UDP:
				k2 = "UDP"; break;
			case IPv6.PROTOCOL_AH:
				k2 = "AH"; break;
			case IPv6.PROTOCOL_DSTOPT:
				k2 = "DSTOPT"; break;
			case IPv6.PROTOCOL_ESP:
				k2 = "ESP"; break;
			case IPv6.PROTOCOL_HOPOPT:
				k2 = "HOPOPT"; break;
			case IPv6.PROTOCOL_ICMP6:
				k2 = "ICMP6"; break;
			case IPv6.PROTOCOL_ROUTING:
				k2 = "ROUTING"; break;
			default:
				k2 = "N/A";
				break;
		}
		

		//MAC Source
		k3 = macSrc.toString();

		//IP Source
		if (ipSrc.length > 1) {
            if (ipSrc.length == 4) {
                k4 = Ip4Address.valueOf(ipSrc).toString();
            }
            else {
				ipv6enabled = true;
                k4 = Ip6Address.valueOf(ipSrc).toString();
            }

		}
		
		//Port Source
		if (portSrc != 0) {
			k5 = String.format("%d", Short.toUnsignedInt(portSrc)); 
		}

		//MAC Dest
        k6 = macDst.toString();
             
        //IP Dest
        if (ipDst.length > 1) {
			if (ipDst.length == 4) {
				k7 = Ip4Address.valueOf(ipDst).toString();
			}
			else {
				ipv6enabled = true;
				k7 = Ip6Address.valueOf(ipDst).toString();
			}
        }     

        //Port Source
        if (portDst != 0) {
			k8 = String.format("%d", Short.toUnsignedInt(portDst)); 
		}
		String keyrow = ipv6Key;

		app.getEthTypeList().add(k1);
        app.getProtocolList().add(k2);
        app.getSMACAddrList().add(k3);
        app.getDMACAddrList().add(k6);
        app.getSIPAddrList().add(k4);
        app.getDIPAddrList().add(k7);
        app.getSPortList().add(k5);
        app.getDPortList().add(k8);

		return String.format(keyrow, k1,k2,k3,k4,k5,k6,k7,k8);	
		//return String.format("%-10s|%-10s|%-18s|%s|%-8s|%-18s|%s|%-8s", k1,k2,k3,k4,k5,k6,k7,k8);
	} 

	@Override
	public boolean equals(Object obj) {
		SourceDestType that = (SourceDestType)obj;
		boolean equal = false;
		if (this.MACSrc.equals(that.MACSrc) &&
			this.MACDst.equals(that.MACDst) &&
			this.IPSrc.equals(that.IPSrc) &&
			this.IPDst.equals(that.IPDst) &&
			this.PortSrc.equals(that.PortSrc) &&
			this.PortDst.equals(that.PortDst))
			equal = true;
		return true;
	}


	static public String outputStats(Map<String, PacketStatsType> stats) {
		if (app != null) {
		StringBuilder output = new StringBuilder();
		String title = ipv6Title, row = ipv6Row, split = ipv6Split;
		output.append(title);
	
			
		for (Map.Entry<String, PacketStatsType> entry : stats.entrySet()) {
			long p1 = entry.getValue().packetCount;
			long p2 = entry.getValue().packetBandwidth;
			long p3 = p2/p1; 
			String value = String.format(ipValue, p1, p2, p3);
			output.append(entry.getKey());
			output.append(value); 
		}
		output.append(split);
		output.append(title);
		output.append(String.format(row,        
		app.getEthTypeList().size(),
        app.getProtocolList().size(),
        app.getSMACAddrList().size(),
        app.getSIPAddrList().size(),
        app.getSPortList().size(),
        app.getDMACAddrList().size(),
        app.getDIPAddrList().size(),
        app.getDPortList().size(), totalPackets, totalPacketSize, totalPacketSize/totalPackets));

		return output.toString();
		}
		else return "No detected traffic yet";
	}

	static public String outputStats(Map<String, PacketStatsType> stats, String [] args) {
		StringBuilder output = new StringBuilder();
        String title = ipv6Title, row = ipv6Row;
        output.append(title);
		//a mapping of key index (col number) and the desired matching field
		HashMap<Integer, String> matches = new HashMap<Integer, String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				matches.put(i, args[i]);
			}
		}
		//if no desired fields, i.e. no options were entered, then process it normally
		if (matches.size() == 0) {
			return SourceDestType.outputStats(stats);
		}	
		
		//current row of keys to match against	
		String [] keys = {};
		//used to count the unique member of each set of categories (keys)
        ArrayList<HashSet<String>> keyCounts = new ArrayList<HashSet<String>>();
		for (int i = 0; i < 8; i++)
		{
			keyCounts.add(new HashSet<String>());
		}
		long p1total = 0, p2total = 0;
		//split the HashMap key into the various keys
		for (Map.Entry<String, PacketStatsType> entry : stats.entrySet()) {
			keys = entry.getKey().split("\\s*\\|");
			
			boolean match = true;
			//if the desired field matches the corresponding key, keep going
			for (Map.Entry<Integer,String> k : matches.entrySet()) {
				//as soon as it doesn't match, drop it and move on
				if (keys[k.getKey()].equals(k.getValue()) == false) {
					match = false;
					break;
				}
			}		
			//if matched, we add it to our output, and count it
			if (match == true) {
				for (int i = 0; i < 8; i++)
				{
					keyCounts.get(i).add(keys[i]);
				}	
            	long p1 = entry.getValue().packetCount;
            	long p2 = entry.getValue().packetBandwidth;
            	long p3 = p2/p1;
				p1total += p1;
				p2total += p2;
            	String value = String.format(ipValue, p1, p2, p3);
            	output.append(entry.getKey());
            	output.append(value);
			}
        }
		if (p1total > 0) {
		output.append(ipv6Split);
		output.append(ipv6Title);
		output.append(String.format(ipv6Row,        
		keyCounts.get(0).size(),
        keyCounts.get(1).size(),
        keyCounts.get(2).size(),
        keyCounts.get(3).size(),
        keyCounts.get(4).size(),
        keyCounts.get(5).size(),
        keyCounts.get(6).size(),
        keyCounts.get(7).size(), p1total, p2total, p2total/p1total));

		return output.toString();
		}
		else return "No traffic matches this criteria";
		// + "\n" + matches.get(0) + "\n" + String.format("%s,%s,%s,%s,%s,%s,%s,%s", keys[0],keys[1], keys[2],keys[3],keys[4],keys[5],keys[6],keys[7]);


	}

	@Override
	public String toString() {
		return String.format("%-10s|%-10s|%-18s|%s|%-8s|%-18s|%s|%-8s", EthType, Protocol, MACSrc, IPSrc, PortSrc, MACDst, IPDst, PortDst);
	}

	static SourceDestType toSourceDestType(String s) {
		String[] ss = s.split(",");
		if (ss.length == 8)
		{
			SourceDestType sdt = new SourceDestType();
			sdt.EthType = 	ss[0];
			sdt.Protocol =  ss[1];
			sdt.MACSrc =	ss[2];
			sdt.IPSrc = 	ss[3];
			sdt.PortSrc = 	ss[4];
			sdt.MACDst = 	ss[5];
			sdt.IPDst =		ss[6];
			sdt.PortDst = 	ss[7];	
			return sdt;
		}
		else return null;
	}

}

