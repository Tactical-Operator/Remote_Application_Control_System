
package com.remotepulse.server.model;

/**
 * Represents an application discovered on the Windows system.
 *
 * Used to store an application's display name and unique application ID.
 * This information is used by Remote Pulse to identify installed apps
 * and configure them as tiles that can be launched from the client.
 */
public class InstalledApp {

    // Application name displayed to the user.
    private String name;

    // Unique identifier used to launch the application.
    private String appId;

    /**
     * No-argument constructor used when creating the object without
     * providing values immediately.
     */
    public InstalledApp() {
    }

    /**
     * Creates an InstalledApp object with its name and application ID.
     *
     * @param name  the application's display name
     * @param appId the application's unique identifier
     */
    public InstalledApp(String name, String appId) {
        this.name = name;
        this.appId = appId;
    }

    // Returns the application's display name.
    public String getName() {
        return name;
    }

    // Updates the application's display name.
    public void setName(String name) {
        this.name = name;
    }

    // Returns the application's unique identifier.
    public String getAppId() {
        return appId;
    }

    // Updates the application's unique identifier.
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
