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

/*
 * Sample Apache Karaf CLI command
 */
@Command(scope = "onos", name = "sample",
         description = "Sample Apache Karaf CLI command")
public class AppCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "hostId", description = "Host ID of source", required = false, multiValued = false)
    private String hostId = null;

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
