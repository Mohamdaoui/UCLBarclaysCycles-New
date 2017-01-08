package com.mohamdaoui.uclbarclayscycles.models;

/**
 * Created by mohamdao on 07/01/2017.
 */

public class Station {
    private String id;
    private String name;
    private String terminalName;
    private String lat;
    private String lng;
    private String installed;
    private String locked;
    private String installDate;
    private String temporary;
    private String nbBikes;
    private String nbEmptyDocks;
    private String nbDocks;
    private double distance;

    public Station(String id, String name, String terminalName, String lat, String lng, String installed, String locked, String installDate, String temporary, String nbBikes, String nbEmptyDocks, String nbDocks, double distance) {
        this.id = id;
        this.name = name;
        this.terminalName = terminalName;
        this.lat = lat;
        this.lng = lng;
        this.installed = installed;
        this.locked = locked;
        this.installDate = installDate;
        this.temporary = temporary;
        this.nbBikes = nbBikes;
        this.nbEmptyDocks = nbEmptyDocks;
        this.nbDocks = nbDocks;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getInstalled() {
        return installed;
    }

    public void setInstalled(String installed) {
        this.installed = installed;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public String getNbBikes() {
        return nbBikes;
    }

    public void setNbBikes(String nbBikes) {
        this.nbBikes = nbBikes;
    }

    public String getNbEmptyDocks() {
        return nbEmptyDocks;
    }

    public void setNbEmptyDocks(String nbEmptyDocks) {
        this.nbEmptyDocks = nbEmptyDocks;
    }

    public String getNbDocks() {
        return nbDocks;
    }

    public void setNbDocks(String nbDocks) {
        this.nbDocks = nbDocks;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
