package com.kjmmake.android.mapviewer;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by LinePlus on 2017-07-26.
 */

public class Person implements Serializable {

    public String name;
    public String address;
    public double priority;
    public Location loc;

    public Person(String _A, String _B, double _C){
        this.name = _A;
        this.address = _B;
        this.priority = _C;
    }

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public Double getPriority(){
        return this.priority;
    }

    public void setLoc(Location _loc){
        this.loc = _loc;
    }

    public Location getLoc(){
        return this.loc;
    }

}
