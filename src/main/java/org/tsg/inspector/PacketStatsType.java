package org.tsg.inspector;

import java.util.*;
import java.io.*;


//used as the value type
public class PacketStatsType {
    long packetCount= 0;
    long packetBandwidth = 0;
    long droppedPacketCount = 0;
    long droppedPacketBandwidth = 0;

    @Override
    public String toString() {
        return String.format("%d,%d,%d,%d", packetCount, packetBandwidth, droppedPacketBandwidth, droppedPacketBandwidth);
    }

    static PacketStatsType toPacketStatsType(String s) {
        String[] ss = s.split(",");
        if (ss.length == 4)
        {
            PacketStatsType pst = new PacketStatsType();
            pst.packetCount =               Long.parseLong(ss[0]);
            pst.packetBandwidth =           Long.parseLong(ss[1]);
            pst.droppedPacketCount =        Long.parseLong(ss[2]);
            pst.droppedPacketBandwidth =    Long.parseLong(ss[3]);

            return pst;
        }
        else return null;

    }

}


