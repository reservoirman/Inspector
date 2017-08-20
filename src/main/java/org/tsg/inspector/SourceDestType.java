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
	
	static public String createKey(short etherType, byte protocol, MacAddress macSrc, byte[] ipSrc, short portSrc, MacAddress macDst, byte[] ipDst, short portDst) {

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
				k1 = "VLAN UNTAGGED"; break;
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
                k7 = Ip4Address.valueOf(ipSrc).toString();
            }
            else {
                k7 = Ip6Address.valueOf(ipSrc).toString();
            }

		}
		
		//Port Source
		if (portSrc != 0) {
			k5 = String.valueOf(portSrc);
		}

		//MAC Dest
        k6 = macDst.toString();
             
        //IP Dest
        if (ipDst.length > 1) {
			if (ipDst.length == 4) {
				k7 = Ip4Address.valueOf(ipDst).toString();
			}
			else {
				k7 = Ip6Address.valueOf(ipDst).toString();
			}
        }     

        //Port Source
        if (portDst != 0) {
			k8 = String.valueOf(portDst);
		}
			
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s", k1,k2,k3,k4,k5,k6,k7,k8);
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
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s", EthType, Protocol, MACSrc, IPSrc, PortSrc, MACDst, IPDst, PortDst);
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

