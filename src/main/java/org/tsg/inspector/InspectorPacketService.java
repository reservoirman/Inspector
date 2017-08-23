package org.tsg.inspector;

import org.onosproject.net.packet.PacketService;
import java.util.*;
import org.onosproject.net.packet.PacketContext;

//@Component(immediate = true)
//@Service
public interface InspectorPacketService {

	public String holla = "Service connection good!  Holla!";
	public Set<String> getSIPAddrList();
	public Set<String> getSMACAddrList();
	public Set<String> getSPortList();
	public Set<String> getDIPAddrList();
	public Set<String> getDMACAddrList();
	public Set<String> getDPortList();
	public Set<String> getProtocolList();
	public Set<String> getEthTypeList();
	public void gatherStatistics(PacketContext context);
	public String getStats();
	public void clearStats();
	public String getStats(String [] args);
	
}
