/*
 * Copyright 2015 Open Networking Laboratory
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

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import java.util.ArrayList;

import org.onosproject.app.ApplicationService;
import org.onosproject.cli.AbstractChoicesCompleter;
import org.onosproject.core.Application;

import static java.util.stream.Collectors.toList;
import static org.onosproject.app.ApplicationState.INSTALLED;
import static org.onosproject.cli.AbstractShellCommand.get;

/**
 * All installed application name completer.
 */
public class Holla extends AbstractChoicesCompleter {

	public static int PacketSize = 0;

    @Override
    public List<String> choices() {

        // Fetch the service and return the list of app names
        ApplicationService service = get(ApplicationService.class);
        Iterator<Application> it = service.getApplications().iterator();
	
	ArrayList<String> testList = new ArrayList<String>();
	InspectorPacketService ips =  get(InspectorPacketService.class);
	testList.add(ips.holla);		
	testList.add("10.0.0.1");
	testList.add("10.0.0.2");
	testList.add("10.0.0.3");
	testList.add("10.0.0.4");

        // Filter the list of apps, selecting only the installed ones.
        // Add each app name to the list of choices.
        return testList;
/*
            StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false)
                    .filter(app -> service.getState(app.id()) == INSTALLED)
                    .map(app -> app.id().name())
                    .collect(toList());
*/
    }

}
