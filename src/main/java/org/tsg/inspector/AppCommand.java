/*
 * Copyright 2014 Open Networking Laboratory
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

import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import java.util.Scanner;
import org.onosproject.net.HostId;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Option;
import java.util.List;
import java.sql.*;
import org.onosproject.net.packet.PacketService;
import org.tsg.inspector.InspectorPacketService;
/*
 * Sample Apache Karaf CLI command
 */
@Command(scope = "onos", name = "stats",
         description = "Displays the default table with the following columns: source IP, source MAC, source port, dest IP, dest MAC, dest port, protocol, packet count, packet avg size, packet bandwidth")
public class AppCommand extends AbstractShellCommand {

    // Selectors
    @Option(name = "-sip", description = "Displays the packets from the specified source IP address only.  A list of source IP addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String d = null;

    @Option(name = "-dip", description = "Displays the packets from the specified destination IP address only.  A list of destination IP addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String g = null;

    @Option(name = "-smac", description = "Displays the packets from the specified source MAC address only.  A list of source MAC addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String c = null;

    @Option(name = "-dmac", description = "Displays the packets from the specified destination MAC address only.  A list of destination MAC addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String f = null;

    @Option(name = "-sport", description = "Displays the packets from the specified source TCP port only.  A list of source ports may also be specified",
            required = false, multiValued = false)
    private String e = null;

    @Option(name = "-dport", description = "Displays the packets from the specified destination TCP port only.  A list of destination ports may also be specified",
            required = false, multiValued = false)
    private String h = null;

    @Option(name = "-protocol", description = "Displays the packets for the specified protocol only.  A list of protocols may also be specified",
            required = false, multiValued = false)
    private String b = null;

    @Option(name = "-ethtype", description = "Displays the packets for the specified ethernet type only.  A list of protocols may also be specified",
            required = false, multiValued = false)
    private String a = null;


    @Override
    protected void execute() {
	Connection conn;
	PacketService ps = get(PacketService.class);
	InspectorPacketService ips = get(InspectorPacketService.class);
	if (ips != null) {
		//print(ips.getStats());
		String [] abc = {a, b, c, d, e, f, g, h};
		print(ips.getStats(abc));
		
		
		
	}

	//print(">");
        //Scanner s = new Scanner(System.in);
	//String sentence = s.nextLine();
	
    }

}
