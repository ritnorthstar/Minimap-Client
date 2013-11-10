package com.northstar.minimap;

import java.net.InetAddress;

/**
 * Created by Benjin on 11/9/13.
 */
public class Communicator {
    private InetAddress serverIpAddress = null;

    public void setServerIP(InetAddress address)
    {
        serverIpAddress = address;
    }

    public InetAddress getServerIP()
    {
        return serverIpAddress;
    }
}
