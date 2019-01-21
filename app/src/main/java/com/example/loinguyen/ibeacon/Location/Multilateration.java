package com.example.loinguyen.ibeacon.Location;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public class Multilateration {

    private List<Beacon> beacons;

    public Multilateration(List<Beacon> beacons) {
        this.beacons = beacons;
    }
}
