package org.tsg.inspector;

import org.onosproject.net.packet.PacketService;
import java.util.*;
import org.onosproject.net.packet.PacketContext;

//@Component(immediate = true)
//@Service
public interface InspectorPacketService {

	public String holla = "Service connection good!  Holla!";
	public Set<String> getIPAddrList();
	public Set<String> getMACAddrList();
	public Set<String> getPortList();
	public Set<String> getProtocolList();
	public Set<String> getEthTypeList();
	public void gatherStatistics(PacketContext context);
	public String getStats();
	public void clearStats();
	
}
