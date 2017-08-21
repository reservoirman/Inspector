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
@Command(scope = "onos", name = "reset",
         description = "Resets the in-memory table of Inspector statistics.")
public class CommandClear extends AbstractShellCommand {


    @Override
    protected void execute() {
	InspectorPacketService ips = get(InspectorPacketService.class);
	if (ips != null) {
		ips.clearStats();
	}


	//print(">");
        //Scanner s = new Scanner(System.in);
	//String sentence = s.nextLine();
	
    }

}
