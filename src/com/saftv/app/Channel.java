package com.saftv.app;

public class Channel {
    private int id;
    private final String name;
    private final String category;
    private final String url;
    private final String userAgent;
    private final String referer;
    private boolean active = true;
    private String epgId;

    public Channel(int id, String name, String category, String url, String userAgent, String referer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.url = url;
        this.userAgent = userAgent;
        this.referer = referer;
        this.active = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getUrl() { return url; }
    public String getUserAgent() { return userAgent; }
    public String getReferer() { return referer; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getEpgId() { return epgId; }
    public void setEpgId(String epgId) { this.epgId = epgId; }

    @Override
    public String toString() {
        return name;
    }
}
