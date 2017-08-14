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
/*
 * Sample Apache Karaf CLI command
 */
@Command(scope = "onos", name = "stats",
         description = "Displays the default table with the following columns: source IP, source MAC, source port, dest IP, dest MAC, dest port, protocol, packet count, packet avg size, packet bandwidth")
public class AppCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "holla", description = "Host ID of source", required = false, multiValued = false)
    private String hostId = null;

    // Selectors
    @Option(name = "-sip", description = "Displays the packets from the specified source IP address only.  A list of source IP addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String srcIpString = null;

    @Option(name = "-dip", description = "Displays the packets from the specified destination IP address only.  A list of destination IP addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String dstIpString = null;

    @Option(name = "-smac", description = "Displays the packets from the specified source MAC address only.  A list of source MAC addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = true)
    private String srcMacString = null;

    @Option(name = "-dmac", description = "Displays the packets from the specified destination MAC address only.  A list of destination MAC addresses may also be specified.  Aggregate values for each will also be shown.",
            required = false, multiValued = false)
    private String dstMacString = null;

    @Option(name = "-sport", description = "Displays the packets from the specified source TCP port only.  A list of source ports may also be specified",
            required = false, multiValued = true)
    private List<String> srcTcpString = null;

    @Option(name = "-dport", description = "Displays the packets from the specified destination TCP port only.  A list of destination ports may also be specified",
            required = false, multiValued = true)
    private List<String> dstTcpString = null;


    @Override
    protected void execute() {
        print("Hello %s", "World");
        
	if (hostId != null) {
	    print("The host is %s", hostId);
	}
	//print(">");
        //Scanner s = new Scanner(System.in);
	//String sentence = s.nextLine();
	
    }

}
