package com.remotepulse.server.model;

public class ServerInfo {

    private String ip;
    private int port;
    private String address;

    public ServerInfo() {
    }

    public ServerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.address = ip + ":" + port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}