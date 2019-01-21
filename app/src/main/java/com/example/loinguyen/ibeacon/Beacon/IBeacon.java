package com.example.loinguyen.ibeacon.Beacon;

import org.altbeacon.beacon.Beacon;

public class IBeacon {

    Beacon beacon;
    Point beaconLocation;

    public IBeacon(Beacon beacon, Point beaconLocation) {
        this.beacon = beacon;
        this.beaconLocation = beaconLocation;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public Point getBeaconLocation() {
        return beaconLocation;
    }

    public void setBeaconLocation(Point beaconLocation) {
        this.beaconLocation = beaconLocation;
    }
}
