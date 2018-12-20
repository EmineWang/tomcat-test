package com.yanming.test.util;

public class RequestClientInfo {
    private String referrer;
    private String ip;
    private String agent;

    public RequestClientInfo() {
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getReferrer() {
        return this.referrer;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public String getAgent() {
        return this.agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
