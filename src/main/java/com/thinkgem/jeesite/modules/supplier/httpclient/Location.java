package com.thinkgem.jeesite.modules.supplier.httpclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgz on 04/05/2017.
 */
public class Location {
    Long id;
    String name;
    String longitude; // 经度
    String latitude; // 纬度

    Location parent;
    List<Location> children = new ArrayList<Location>();
    List<ParkInfo> parklist = new ArrayList<ParkInfo>();

    public Location(){

    }

    public boolean add(Location ch){
        return children.add(ch);
    }
    public boolean addChildren(List<Location> chs){
        return children.addAll(chs);
    }
    public boolean add(ParkInfo pi){
        return parklist.add(pi);
    }

    public Location(String name){
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }

    public List<Location> getChildren() {
        return children;
    }

    public void setChildren(List<Location> children) {
        this.children = children;
    }

    public List<ParkInfo> getParklist() {
        return parklist;
    }

    public void setParklist(List<ParkInfo> parklist) {
        this.parklist = parklist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
