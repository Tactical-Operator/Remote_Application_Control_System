package com.remotepulse.server.model;

public class InstalledApp {

    private String name;
    private String appId;

    public InstalledApp(){

    }

    public InstalledApp(String name, String appId)
    {
        this.name = name;
        this.appId = appId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getAppId(){
        return appId;
    }

    public void setAppId(String appId){
        this.appId = appId;
    }
    
}

// What this class represents

// It represents one application returned by Windows.

// For example, after Windows gives us:

// WhatsApp
// 5319275A.WhatsAppDesktop_cv1g1gvanyjgm!App

// Java will represent it as:

// InstalledApp
//     name  → WhatsApp
//     appId → 5319275A.WhatsAppDesktop_cv1g1gvanyjgm!App

// For VS Code:

// InstalledApp
//     name  → Visual Studio Code
//     appId → Microsoft.VisualStudioCode

// We're deliberately keeping this class simple for now.
