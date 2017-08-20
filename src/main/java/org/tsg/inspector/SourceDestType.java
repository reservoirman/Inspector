package org.tsg.inspector;

import java.util.*;
import java.io.*;

import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.IPv6;
import org.onlab.packet.IpAddress;
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
	
	static public String createKey(short etherType, byte protocol, MacAddress macSrc, int ipSrc, short portSrc, MacAddress macDst, int ipDst, short portDst) {
		String key = "";
	
		//etherType
		switch (etherType) {
			case Ethernet.TYPE_ARP:
				key += "ARP"; break;
			case Ethernet.TYPE_RARP: 
				key += "RARP"; break;
			case Ethernet.TYPE_IPV4:
				key += "IPV4"; break;
			case Ethernet.TYPE_IPV6:
				key += "IPV6"; break;
			case Ethernet.TYPE_LLDP:
				key += "LLDP"; break;
			case Ethernet.TYPE_VLAN:
				key += "VLAN"; break;
			case Ethernet.VLAN_UNTAGGED:
				key += "VLAN UNTAGGED"; break;
			default:
				key += "UNKNOWN ETHERNET TYPE"; break;
		}
		
		key += ",";	

		//protocol
		switch (protocol) {
			case IPv4.PROTOCOL_ICMP:
				key += "ICMP"; break;
			case IPv6.PROTOCOL_TCP:
				key += "TCP"; break;
			case IPv6.PROTOCOL_UDP:
				key += "UDP"; break;
			case IPv6.PROTOCOL_AH:
				key += "AH"; break;
			case IPv6.PROTOCOL_DSTOPT:
				key += "DSTOPT"; break;
			case IPv6.PROTOCOL_ESP:
				key += "ESP"; break;
			case IPv6.PROTOCOL_HOPOPT:
				key += "HOPOPT"; break;
			case IPv6.PROTOCOL_ICMP6:
				key += "ICMP6"; break;
			case IPv6.PROTOCOL_ROUTING:
				key += "ROUTING"; break;
			default:
				key += "PROTOCOL NOT APPLICABLE";
				break;
		}
		
		key += ",";

		//MAC Source
		key += macSrc.toString();
		key += ",";

		//IP Source
		key += IpAddress.valueOf(ipSrc).toString();
		key += ",";
		
		//Port Source
		key += String.valueOf(portSrc);
		key += ",";

		//MAC Dest
        key += macDst.toString();
        key += ",";     
        //IP Dest
        key += IpAddress.valueOf(ipDst).toString();
        key += ",";     

        //Port Source
        key += String.valueOf(portDst);

			
		return key;
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

	@Override
	public String toString() {
		return String.format("%s,%s,%s,%s,%s,%s", MACSrc, IPSrc, PortSrc, MACDst, IPDst, PortDst);
	}

	static SourceDestType toSourceDestType(String s) {
		String[] ss = s.split(",");
		if (ss.length == 6)
		{
			SourceDestType sdt = new SourceDestType();
			sdt.MACSrc =	ss[0];
			sdt.IPSrc = 	ss[1];
			sdt.PortSrc = 	ss[2];
			sdt.MACDst = 	ss[3];
			sdt.IPDst =		ss[4];
			sdt.PortDst = 	ss[5];	
			return sdt;
		}
		else return null;
	}

}

