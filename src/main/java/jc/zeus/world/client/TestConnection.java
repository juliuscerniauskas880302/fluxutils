package jc.zeus.world.client;

import java.net.InetAddress;

public class TestConnection {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName("telnet.c12.stratosmobile.net");
            System.out.println("Host is reachable: " + address.isReachable(1000));
        } catch (java.net.UnknownHostException e) {
            System.out.println("Unknown host exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
