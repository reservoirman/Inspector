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
//import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.onosproject.net.packet.PacketService;
import org.tsg.inspector.InspectorPacketService;
import java.io.*;
import java.lang.ClassNotFoundException;
import org.sqlite.JDBC;

/*
 * Sample Apache Karaf CLI command
 */
@Command(scope = "onos", name = "stats",
         description = "Displays the default table with the following columns: source IP, source MAC, source port, dest IP, dest MAC, dest port, protocol, packet count, total bytes, avg packet size.  Aggregate values for each will also be shown.")
public class AppCommand extends AbstractShellCommand {

    // Selectors
    @Option(name = "-sip", description = "Displays the packets from the specified source IP address only",
            required = false, multiValued = false)
    private String d = null;

    @Option(name = "-dip", description = "Displays the packets from the specified destination IP address only",
            required = false, multiValued = false)
    private String g = null;

    @Option(name = "-smac", description = "Displays the packets from the specified source MAC address only",
            required = false, multiValued = false)
    private String c = null;

    @Option(name = "-dmac", description = "Displays the packets from the specified destination MAC address only",
            required = false, multiValued = false)
    private String f = null;

    @Option(name = "-sport", description = "Displays the packets from the specified source TCP port only",
            required = false, multiValued = false)
    private String e = null;

    @Option(name = "-dport", description = "Displays the packets from the specified destination TCP port only",
            required = false, multiValued = false)
    private String h = null;

    @Option(name = "-protocol", description = "Displays the packets for the specified protocol only",
            required = false, multiValued = false)
    private String b = null;

    @Option(name = "-ethtype", description = "Displays the packets for the specified Ethernet type only",
            required = false, multiValued = false)
    private String a = null;

	public static void tryDB() //throws ClassNotFoundException
	{
    	try {
        	Class.forName("org.sqlite.JDBC");
        	//Class.forName("com.mysql.jdbc.Driver");
			String fileName = "test.db";

        	String url = "jdbc:sqlite:test.db";// + fileName;
			//String url = "jdbc:mysql://localhost/test?";
        	Connection conn = DriverManager.getConnection(url);
           	if (conn != null) {
           		//DatabaseMetaData meta = conn.getMetaData();
           	    //System.out.println("The driver name is " + meta.getDriverName());
           	    System.out.println("A new database has been created.");
        	}
    	}
    	catch (Exception e) {
   	     	System.err.println(e.getClass().getName() + ": " + e.getMessage());
        //System.exit(0);
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			for (int i =0 ; i < st.length; i++)
			{
				System.out.println(st[i].toString());
			}
		
			//e.printStackTree();
 	   }

	}

    @Override
    protected void execute() {
	//Connection conn;
	PacketService ps = get(PacketService.class);
	InspectorPacketService ips = get(InspectorPacketService.class);
	if (ips != null) {
		//print(ips.getStats());
		String [] abc = {a, b, c, d, e, f, g, h};
		try {
			print(ips.getStats(abc));
		//AppCommand.tryDB();	
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
            StackTraceElement[] st = e.getStackTrace();
            for (int i =0 ; i < st.length; i++)
            {
                System.out.println(st[i].toString());
            }


		}
		
	}
	
	//print(">");
        //Scanner s = new Scanner(System.in);
	//String sentence = s.nextLine();
	
    }

}
