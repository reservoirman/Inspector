/*
 * Copyright 2014-2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tsg.inspector;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.Ethernet;
import org.onlab.packet.ICMP;
import org.onlab.packet.ICMP6;
import org.onlab.packet.IPv4;
import org.onlab.packet.IPv6;
import org.onlab.packet.Ip4Prefix;
import org.onlab.packet.Ip6Prefix;
import org.onlab.packet.TCP;
import org.onlab.packet.UDP;
import org.onlab.packet.VlanId;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.host.HostService;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketPriority;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;

import java.util.Dictionary;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.slf4j.LoggerFactory.getLogger;
//newly added by TSG:
import org.apache.felix.scr.annotations.Service;
import java.util.*;
import org.onlab.packet.ARP;
import org.onlab.packet.MacAddress;
//import org.apache.karaf.shell.commands.Command;
//import org.onosproject.cli.AbstractShellCommand;
//import org.apache.karaf.shell.commands.Argument;
//import org.apache.karaf.shell.commands.Option;




/**
 * Sample reactive forwarding application.
 */
@Component(immediate = true)
@Service
public class AppComponent implements InspectorPacketService {

    private static final int DEFAULT_TIMEOUT = 10;
    private static final int DEFAULT_PRIORITY = 10;

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowObjectiveService flowObjectiveService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ComponentConfigService cfgService;

    private ReactivePacketProcessor processor = new ReactivePacketProcessor();

    private ApplicationId appId;

    @Property(name = "packetOutOnly", boolValue = false,
            label = "Enable packet-out only forwarding; default is false")
    private boolean packetOutOnly = false;

    @Property(name = "packetOutOfppTable", boolValue = false,
            label = "Enable first packet forwarding using OFPP_TABLE port " +
                "instead of PacketOut with actual port; default is false")
    private boolean packetOutOfppTable = false;

    @Property(name = "flowTimeout", intValue = DEFAULT_TIMEOUT,
            label = "Configure Flow Timeout for installed flow rules; " +
                "default is 10 sec")
    private int flowTimeout = DEFAULT_TIMEOUT;

    @Property(name = "flowPriority", intValue = DEFAULT_PRIORITY,
            label = "Configure Flow Priority for installed flow rules; " +
                "default is 10")
    private int flowPriority = DEFAULT_PRIORITY;

    @Property(name = "ipv6Forwarding", boolValue = false,
            label = "Enable IPv6 forwarding; default is false")
    private boolean ipv6Forwarding = false;

    @Property(name = "matchDstMacOnly", boolValue = false,
            label = "Enable matching Dst Mac Only; default is false")
    private boolean matchDstMacOnly = false;

    @Property(name = "matchVlanId", boolValue = false,
            label = "Enable matching Vlan ID; default is false")
    private boolean matchVlanId = false;

    @Property(name = "matchIpv4Address", boolValue = false,
            label = "Enable matching IPv4 Addresses; default is false")
    private boolean matchIpv4Address = false;

    @Property(name = "matchIpv4Dscp", boolValue = false,
            label = "Enable matching IPv4 DSCP and ECN; default is false")
    private boolean matchIpv4Dscp = false;

    @Property(name = "matchIpv6Address", boolValue = false,
            label = "Enable matching IPv6 Addresses; default is false")
    private boolean matchIpv6Address = false;

    @Property(name = "matchIpv6FlowLabel", boolValue = false,
            label = "Enable matching IPv6 FlowLabel; default is false")
    private boolean matchIpv6FlowLabel = false;

    @Property(name = "matchTcpUdpPorts", boolValue = false,
            label = "Enable matching TCP/UDP ports; default is false")
    private boolean matchTcpUdpPorts = false;

    @Property(name = "matchIcmpFields", boolValue = false,
            label = "Enable matching ICMPv4 and ICMPv6 fields; " +
                "default is false")
    private boolean matchIcmpFields = false;

	



    @Activate
    public void activate(ComponentContext context) {
        cfgService.registerProperties(getClass());
        appId = coreService.registerApplication("org.tsg.inspector");
	
	//Socket socket = new Socket("localhost", 1004);

        packetService.addProcessor(processor, PacketProcessor.ADVISOR_MAX + 2);
        readComponentConfiguration(context);
        requestPackests();

        log.info("Started with Application ID {}", appId.id());
    }

    @Deactivate
    public void deactivate() {
        // TODO revoke all packet requests when deactivate
        cfgService.unregisterProperties(getClass(), false);
        flowRuleService.removeFlowRulesById(appId);
        packetService.removeProcessor(processor);
        processor = null;
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        // TODO revoke unnecessary packet requests when config being modified
        readComponentConfiguration(context);
        requestPackests();
    }

    /**
     * Request packet in via PacketService.
     */
    private void requestPackests() {
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        selector.matchEthType(Ethernet.TYPE_IPV4);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE,
                                     appId);
        selector.matchEthType(Ethernet.TYPE_ARP);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE,
                                     appId);

        if (ipv6Forwarding) {
            selector.matchEthType(Ethernet.TYPE_IPV6);
            packetService.requestPackets(selector.build(),
                                         PacketPriority.REACTIVE, appId);
        }
    }

    /**
     * Extracts properties from the component configuration context.
     *
     * @param context the component context
     */
    private void readComponentConfiguration(ComponentContext context) {
        Dictionary<?, ?> properties = context.getProperties();
        boolean packetOutOnlyEnabled =
            isPropertyEnabled(properties, "packetOutOnly");
        if (packetOutOnly != packetOutOnlyEnabled) {
            packetOutOnly = packetOutOnlyEnabled;
            log.info("Configured. Packet-out only forwarding is {}",
                packetOutOnly ? "enabled" : "disabled");
        }
        boolean packetOutOfppTableEnabled =
            isPropertyEnabled(properties, "packetOutOfppTable");
        if (packetOutOfppTable != packetOutOfppTableEnabled) {
            packetOutOfppTable = packetOutOfppTableEnabled;
            log.info("Configured. Forwarding using OFPP_TABLE port is {}",
                packetOutOfppTable ? "enabled" : "disabled");
        }
        boolean ipv6ForwardingEnabled =
            isPropertyEnabled(properties, "ipv6Forwarding");
        if (ipv6Forwarding != ipv6ForwardingEnabled) {
            ipv6Forwarding = ipv6ForwardingEnabled;
            log.info("Configured. IPv6 forwarding is {}",
                ipv6Forwarding ? "enabled" : "disabled");
        }
        boolean matchDstMacOnlyEnabled =
            isPropertyEnabled(properties, "matchDstMacOnly");
        if (matchDstMacOnly != matchDstMacOnlyEnabled) {
            matchDstMacOnly = matchDstMacOnlyEnabled;
            log.info("Configured. Match Dst MAC Only is {}",
                matchDstMacOnly ? "enabled" : "disabled");
        }
        boolean matchVlanIdEnabled =
            isPropertyEnabled(properties, "matchVlanId");
        if (matchVlanId != matchVlanIdEnabled) {
            matchVlanId = matchVlanIdEnabled;
            log.info("Configured. Matching Vlan ID is {}",
                matchVlanId ? "enabled" : "disabled");
        }
        boolean matchIpv4AddressEnabled =
            isPropertyEnabled(properties, "matchIpv4Address");
        if (matchIpv4Address != matchIpv4AddressEnabled) {
            matchIpv4Address = matchIpv4AddressEnabled;
            log.info("Configured. Matching IPv4 Addresses is {}",
                matchIpv4Address ? "enabled" : "disabled");
        }
        boolean matchIpv4DscpEnabled =
            isPropertyEnabled(properties, "matchIpv4Dscp");
        if (matchIpv4Dscp != matchIpv4DscpEnabled) {
            matchIpv4Dscp = matchIpv4DscpEnabled;
            log.info("Configured. Matching IPv4 DSCP and ECN is {}",
                matchIpv4Dscp ? "enabled" : "disabled");
        }
        boolean matchIpv6AddressEnabled =
            isPropertyEnabled(properties, "matchIpv6Address");
        if (matchIpv6Address != matchIpv6AddressEnabled) {
            matchIpv6Address = matchIpv6AddressEnabled;
            log.info("Configured. Matching IPv6 Addresses is {}",
                matchIpv6Address ? "enabled" : "disabled");
        }
        boolean matchIpv6FlowLabelEnabled =
            isPropertyEnabled(properties, "matchIpv6FlowLabel");
        if (matchIpv6FlowLabel != matchIpv6FlowLabelEnabled) {
            matchIpv6FlowLabel = matchIpv6FlowLabelEnabled;
            log.info("Configured. Matching IPv6 FlowLabel is {}",
                matchIpv6FlowLabel ? "enabled" : "disabled");
        }
        boolean matchTcpUdpPortsEnabled =
            isPropertyEnabled(properties, "matchTcpUdpPorts");
        if (matchTcpUdpPorts != matchTcpUdpPortsEnabled) {
            matchTcpUdpPorts = matchTcpUdpPortsEnabled;
            log.info("Configured. Matching TCP/UDP fields is {}",
                matchTcpUdpPorts ? "enabled" : "disabled");
        }
        boolean matchIcmpFieldsEnabled =
            isPropertyEnabled(properties, "matchIcmpFields");
        if (matchIcmpFields != matchIcmpFieldsEnabled) {
            matchIcmpFields = matchIcmpFieldsEnabled;
            log.info("Configured. Matching ICMP (v4 and v6) fields is {}",
                matchIcmpFields ? "enabled" : "disabled");
        }
        Integer flowTimeoutConfigured =
            getIntegerProperty(properties, "flowTimeout");
        if (flowTimeoutConfigured == null) {
            log.info("Flow Timeout is not configured, default value is {}",
                     flowTimeout);
        } else {
            flowTimeout = flowTimeoutConfigured;
            log.info("Configured. Flow Timeout is configured to {}",
                     flowTimeout, " seconds");
        }
        Integer flowPriorityConfigured =
            getIntegerProperty(properties, "flowPriority");
        if (flowPriorityConfigured == null) {
            log.info("Flow Priority is not configured, default value is {}",
                     flowPriority);
        } else {
            flowPriority = flowPriorityConfigured;
            log.info("Configured. Flow Priority is configured to {}",
                     flowPriority);
        }
    }

    /**
     * Get Integer property from the propertyName
     * Return null if propertyName is not found.
     *
     * @param properties properties to be looked up
     * @param propertyName the name of the property to look up
     * @return value when the propertyName is defined or return null
     */
    private static Integer getIntegerProperty(Dictionary<?, ?> properties,
                                              String propertyName) {
        Integer value = null;
        try {
            String s = (String) properties.get(propertyName);
            value = isNullOrEmpty(s) ? value : Integer.parseInt(s.trim());
        } catch (NumberFormatException | ClassCastException e) {
            value = null;
        }
        return value;
    }

    /**
     * Check property name is defined and set to true.
     *
     * @param properties properties to be looked up
     * @param propertyName the name of the property to look up
     * @return true when the propertyName is defined and set to true
     */
    private static boolean isPropertyEnabled(Dictionary<?, ?> properties,
                                             String propertyName) {
        boolean enabled = false;
        try {
            String flag = (String) properties.get(propertyName);
            if (flag != null) {
                enabled = flag.trim().equals("true");
            }
        } catch (ClassCastException e) {
            // No propertyName defined.
            enabled = false;
        }
        return enabled;
    }

		private HashSet<String> ipAddrList = new HashSet<String>();
		private HashSet<String> macAddrList = new HashSet<String>();
		private HashSet<String> portList = new HashSet<String>();
		private HashSet<String> protocolList = new HashSet<String>();
		private HashSet<String> ethTypeList = new HashSet<String>();
	    public Set<String> getIPAddrList() {
			return ipAddrList;
		}
    	public Set<String> getMACAddrList() {
			return macAddrList;
		}
		public Set<String> getPortList() {
			return portList;
		}
		public Set<String> getEthTypeList() {
			return ethTypeList;
		}
		public Set<String> getProtocolList() {
			return protocolList;
		}

		HashMap<String, PacketStatsType> stats = new HashMap<String, PacketStatsType>();
		
		public void gatherStatistics(PacketContext context) {
			InboundPacket pkt = context.inPacket();
            Ethernet ethPkt = pkt.parsed();
			
			//get eth type:
			short ethType = ethPkt.getEtherType();
			

			//if LLDP (amd BSN, RARP, VLAN), get source MAC and dest MAC only (no IP, ports, or protocol)
			MacAddress srcMac = ethPkt.getSourceMAC();
			MacAddress dstMac = ethPkt.getDestinationMAC();

			byte[] srcIp = new byte[1];
			byte[] dstIp = new byte[1];
			byte protocol = 0;
			short srcPort = 0;
			short dstPort = 0;
			//if ARP, get source MAC source IP, dest MAC dest IP only (no ports or protoool)
			if (ethType == Ethernet.TYPE_ARP)
			{
				ARP arpPkt = (ARP)ethPkt.getPayload();
				srcIp = arpPkt.getSenderProtocolAddress();
				dstIp = arpPkt.getTargetProtocolAddress();
			}			
			else if (ethType == Ethernet.TYPE_IPV4) {
				IPv4 ipv4Pkt = (IPv4)ethPkt.getPayload();
				srcIp = IPv4.toIPv4AddressBytes(ipv4Pkt.getSourceAddress());
				dstIp = IPv4.toIPv4AddressBytes(ipv4Pkt.getDestinationAddress());
				protocol = ipv4Pkt.getProtocol();
				switch(protocol) {
					case IPv4.PROTOCOL_TCP:
						TCP tcp4Pkt = (TCP)ipv4Pkt.getPayload();
						srcPort = tcp4Pkt.getSourcePort();
						dstPort = tcp4Pkt.getDestinationPort();
						break;
					case IPv4.PROTOCOL_UDP:
						UDP udp4Pkt = (UDP)ipv4Pkt.getPayload();
						srcPort = udp4Pkt.getSourcePort();
						dstPort = udp4Pkt.getDestinationPort();
						break;
					default: break;
				}
			}
			else if (ethType == Ethernet.TYPE_IPV6) {
                IPv6 ipv6Pkt = (IPv6)ethPkt.getPayload();
                srcIp = ipv6Pkt.getSourceAddress();
                dstIp = ipv6Pkt.getDestinationAddress();
                protocol = ipv6Pkt.getNextHeader();
                switch(protocol) {
                    case IPv6.PROTOCOL_TCP:
                        TCP tcp6Pkt = (TCP)ipv6Pkt.getPayload();
                        srcPort = tcp6Pkt.getSourcePort();
                        dstPort = tcp6Pkt.getDestinationPort();
                        break;
                    case IPv6.PROTOCOL_UDP:
                        UDP udp6Pkt = (UDP)ipv6Pkt.getPayload();
                        srcPort = udp6Pkt.getSourcePort();
                        dstPort = udp6Pkt.getDestinationPort();
                        break;
                    default: break;
                }
			}

			String key = SourceDestType.createKey(this, ethType, protocol, srcMac, srcIp, srcPort, dstMac, dstIp, dstPort);
			//System.out.println("key = " + key);
			//log.info("key = {}", key);
		
			//split key and add the various fields to their respective HashSets: (now being done in SourceDestType)
			/* String k[] = s.split("[\\p{Punct}\\s]+");
			ethTypeList.add(k[0]);
			protocolList.add(k[1]);
			macAddressList.add(k[2]);
			macAddressList.add(k[5]);
			ipAddressList.add(k[3]);
			ipAddressList.add(k[6]);
			portList.add(k[4]);
			portList.add(k[7]);			
			*/
			//get packet size
			long packetSize = pkt.unparsed().capacity();
			
			PacketStatsType p = stats.get(key);
			
			if (p == null) {
				p = new PacketStatsType();
				p.packetCount = 1;
				p.packetBandwidth = packetSize;
			}	
			else
			{
				p.packetCount++;
				p.packetBandwidth += packetSize;
			}
			stats.put(key, p);

			//System.out.println("%packet size = " +  pkt.unparsed().capacity());
			//log.info("PacketService = {}", packetService.toString());
		}

	public String getStats()
	{
		String output = SourceDestType.outputStats(stats);
		return output;
	}
	
	public String getStats(String[] args) {
		String output = SourceDestType.outputStats(stats, args);
		return output;	
	}

	public void clearStats()
	{
		stats.clear();
	}

    /**
     * Packet processor responsible for forwarding packets along their paths.
     */
    	private class ReactivePacketProcessor implements PacketProcessor {

        @Override
        public void process(PacketContext context) {
            // Stop processing if the packet has been handled, since we
            // can't do any more to it.
            log.info("Process packet!");

            if (context.isHandled()) {
                return;
            }
			
			gatherStatistics(context);
			InboundPacket pkt = context.inPacket();
            Ethernet ethPkt = pkt.parsed();
	
           if (ethPkt == null) {
                return;
            }


            // Bail if this is deemed to be a control packet.
            if (isControlPacket(ethPkt)) {
                return;
            }

            // Skip IPv6 multicast packet when IPv6 forward is disabled.
            if (!ipv6Forwarding && isIpv6Multicast(ethPkt)) {
                return;
            }

            HostId id = HostId.hostId(ethPkt.getDestinationMAC());

            // Do not process link-local addresses in any way.
            if (id.mac().isLinkLocal()) {
                return;
            }

            // Do we know who this is for? If not, flood and bail.
            Host dst = hostService.getHost(id);
            if (dst == null) {
                flood(context);
                return;
            }

            // Are we on an edge switch that our destination is on? If so,
            // simply forward out to the destination and bail.
            if (pkt.receivedFrom().deviceId().equals(dst.location().deviceId())) {
                if (!context.inPacket().receivedFrom().port().equals(dst.location().port())) {
                    installRule(context, dst.location().port());
                }
                return;
            }

            // Otherwise, get a set of paths that lead from here to the
            // destination edge switch.
            Set<Path> paths =
                topologyService.getPaths(topologyService.currentTopology(),
                                         pkt.receivedFrom().deviceId(),
                                         dst.location().deviceId());
            if (paths.isEmpty()) {
                // If there are no paths, flood and bail.
                flood(context);
                return;
            }

            // Otherwise, pick a path that does not lead back to where we
            // came from; if no such path, flood and bail.
            Path path = pickForwardPath(paths, pkt.receivedFrom().port());
            if (path == null) {
                log.warn("Doh... don't know where to go... {} -> {} received on {}",
                         ethPkt.getSourceMAC(), ethPkt.getDestinationMAC(),
                         pkt.receivedFrom());
                flood(context);
                return;
            }

            // Otherwise forward and be done with it.
            installRule(context, path.src().port());
        }

    }

    // Indicates whether this is a control packet, e.g. LLDP, BDDP
    private boolean isControlPacket(Ethernet eth) {
        short type = eth.getEtherType();
        return type == Ethernet.TYPE_LLDP || type == Ethernet.TYPE_BSN;
    }

    // Indicated whether this is an IPv6 multicast packet.
    private boolean isIpv6Multicast(Ethernet eth) {
        return eth.getEtherType() == Ethernet.TYPE_IPV6 && eth.isMulticast();
    }

    // Selects a path from the given set that does not lead back to the
    // specified port.
    private Path pickForwardPath(Set<Path> paths, PortNumber notToPort) {
        for (Path path : paths) {
            if (!path.src().port().equals(notToPort)) {
                return path;
            }
        }
        return null;
    }

    // Floods the specified packet if permissible.
    private void flood(PacketContext context) {
        if (topologyService.isBroadcastPoint(topologyService.currentTopology(),
                                             context.inPacket().receivedFrom())) {
            packetOut(context, PortNumber.FLOOD);
        } else {
            context.block();
        }
    }

    // Sends a packet out the specified port.
    private void packetOut(PacketContext context, PortNumber portNumber) {
        context.treatmentBuilder().setOutput(portNumber);
        context.send();
    }

    // Install a rule forwarding the packet to the specified port.
    private void installRule(PacketContext context, PortNumber portNumber) {
        //
        // We don't support (yet) buffer IDs in the Flow Service so
        // packet out first.
        //
        Ethernet inPkt = context.inPacket().parsed();
        TrafficSelector.Builder selectorBuilder = DefaultTrafficSelector.builder();

        // If PacketOutOnly or ARP packet than forward directly to output port
        if (packetOutOnly || inPkt.getEtherType() == Ethernet.TYPE_ARP) {
            packetOut(context, portNumber);
            return;
        }

        //
        // If matchDstMacOnly
        //    Create flows matching dstMac only
        // Else
        //    Create flows with default matching and include configured fields
        //
        if (matchDstMacOnly) {
            selectorBuilder.matchEthDst(inPkt.getDestinationMAC());
        } else {
            selectorBuilder.matchInPort(context.inPacket().receivedFrom().port())
                    .matchEthSrc(inPkt.getSourceMAC())
                    .matchEthDst(inPkt.getDestinationMAC());

            // If configured Match Vlan ID
            if (matchVlanId && inPkt.getVlanID() != Ethernet.VLAN_UNTAGGED) {
                selectorBuilder.matchVlanId(VlanId.vlanId(inPkt.getVlanID()));
            }

            //
            // If configured and EtherType is IPv4 - Match IPv4 and
            // TCP/UDP/ICMP fields
            //
            if (matchIpv4Address && inPkt.getEtherType() == Ethernet.TYPE_IPV4) {
                IPv4 ipv4Packet = (IPv4) inPkt.getPayload();
                byte ipv4Protocol = ipv4Packet.getProtocol();
                Ip4Prefix matchIp4SrcPrefix =
                    Ip4Prefix.valueOf(ipv4Packet.getSourceAddress(),
                                      Ip4Prefix.MAX_MASK_LENGTH);
                Ip4Prefix matchIp4DstPrefix =
                    Ip4Prefix.valueOf(ipv4Packet.getDestinationAddress(),
                                      Ip4Prefix.MAX_MASK_LENGTH);
                selectorBuilder.matchEthType(Ethernet.TYPE_IPV4)
                        .matchIPSrc(matchIp4SrcPrefix)
                        .matchIPDst(matchIp4DstPrefix);

                if (matchIpv4Dscp) {
                    byte dscp = ipv4Packet.getDscp();
                    byte ecn = ipv4Packet.getEcn();
                    selectorBuilder.matchIPDscp(dscp).matchIPEcn(ecn);
                }

                if (matchTcpUdpPorts && ipv4Protocol == IPv4.PROTOCOL_TCP) {
                    TCP tcpPacket = (TCP) ipv4Packet.getPayload();
                    selectorBuilder.matchIPProtocol(ipv4Protocol)
                            .matchTcpSrc(tcpPacket.getSourcePort())
                            .matchTcpDst(tcpPacket.getDestinationPort());
                }
                if (matchTcpUdpPorts && ipv4Protocol == IPv4.PROTOCOL_UDP) {
                    UDP udpPacket = (UDP) ipv4Packet.getPayload();
                    selectorBuilder.matchIPProtocol(ipv4Protocol)
                            .matchUdpSrc(udpPacket.getSourcePort())
                            .matchUdpDst(udpPacket.getDestinationPort());
                }
                if (matchIcmpFields && ipv4Protocol == IPv4.PROTOCOL_ICMP) {
                    ICMP icmpPacket = (ICMP) ipv4Packet.getPayload();
                    selectorBuilder.matchIPProtocol(ipv4Protocol)
                            .matchIcmpType(icmpPacket.getIcmpType())
                            .matchIcmpCode(icmpPacket.getIcmpCode());
                }
            }

            //
            // If configured and EtherType is IPv6 - Match IPv6 and
            // TCP/UDP/ICMP fields
            //
            if (matchIpv6Address && inPkt.getEtherType() == Ethernet.TYPE_IPV6) {
                IPv6 ipv6Packet = (IPv6) inPkt.getPayload();
                byte ipv6NextHeader = ipv6Packet.getNextHeader();
                Ip6Prefix matchIp6SrcPrefix =
                    Ip6Prefix.valueOf(ipv6Packet.getSourceAddress(),
                                      Ip6Prefix.MAX_MASK_LENGTH);
                Ip6Prefix matchIp6DstPrefix =
                    Ip6Prefix.valueOf(ipv6Packet.getDestinationAddress(),
                                      Ip6Prefix.MAX_MASK_LENGTH);
                selectorBuilder.matchEthType(Ethernet.TYPE_IPV6)
                        .matchIPv6Src(matchIp6SrcPrefix)
                        .matchIPv6Dst(matchIp6DstPrefix);

                if (matchIpv6FlowLabel) {
                    selectorBuilder.matchIPv6FlowLabel(ipv6Packet.getFlowLabel());
                }

                if (matchTcpUdpPorts && ipv6NextHeader == IPv6.PROTOCOL_TCP) {
                    TCP tcpPacket = (TCP) ipv6Packet.getPayload();
                    selectorBuilder.matchIPProtocol(ipv6NextHeader)
                            .matchTcpSrc(tcpPacket.getSourcePort())
                            .matchTcpDst(tcpPacket.getDestinationPort());
                }
                if (matchTcpUdpPorts && ipv6NextHeader == IPv6.PROTOCOL_UDP) {
                    UDP udpPacket = (UDP) ipv6Packet.getPayload();
                    selectorBuilder.matchIPProtocol(ipv6NextHeader)
                            .matchUdpSrc(udpPacket.getSourcePort())
                            .matchUdpDst(udpPacket.getDestinationPort());
                }
                if (matchIcmpFields && ipv6NextHeader == IPv6.PROTOCOL_ICMP6) {
                    ICMP6 icmp6Packet = (ICMP6) ipv6Packet.getPayload();
                    selectorBuilder.matchIPProtocol(ipv6NextHeader)
                            .matchIcmpv6Type(icmp6Packet.getIcmpType())
                            .matchIcmpv6Code(icmp6Packet.getIcmpCode());
                }
            }
        }
        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setOutput(portNumber)
                .build();

        ForwardingObjective forwardingObjective = DefaultForwardingObjective.builder()
                .withSelector(selectorBuilder.build())
                .withTreatment(treatment)
                .withPriority(flowPriority)
                .withFlag(ForwardingObjective.Flag.VERSATILE)
                .fromApp(appId)
                .makeTemporary(flowTimeout)
                .add();

        flowObjectiveService.forward(context.inPacket().receivedFrom().deviceId(),
                                     forwardingObjective);

        //
        // If packetOutOfppTable
        //  Send packet back to the OpenFlow pipeline to match installed flow
        // Else
        //  Send packet direction on the appropriate port
        //
        if (packetOutOfppTable) {
            packetOut(context, PortNumber.TABLE);
        } else {
            packetOut(context, portNumber);
        }
    }
}
