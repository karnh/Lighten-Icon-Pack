package com.webh.lightentheme;

/**
 * Class to store all relevant details of a launcher
 */
public class LauncherDetails {
    public String id;
    public String name;
    public String drawable;
    public String package_name;

    public LauncherDetails(String id, String name, String packageName, String drawable){
        this.id = id;
        this.name = name;
        this.package_name = packageName;
        this.drawable = drawable;
    }
}
