package com.example.loinguyen.ibeacon.Location;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.example.loinguyen.ibeacon.Beacon.IBeacon;

import java.util.List;

public class Trilateration {

    private IBeacon beacon1, beacon2, beacon3;

    public Trilateration(IBeacon beacon1, IBeacon beacon2, IBeacon beacon3) {
        this.beacon1 = beacon1;
        this.beacon2 = beacon2;
        this.beacon3 = beacon3;

    }


    

}
