package com.example.loinguyen.ibeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.Serializable;
import java.util.Collection;

public class MainActivity extends Activity implements BeaconConsumer {
    private static final String TAG = "Beacon Detect";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String UNIQUE_ID = "com.example.loinguyen.ibeacon";
    private boolean found = false;
    public static final String BEACON_EXTRA = "beacon_extra";
    private static final String PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private BeaconManager beaconManager;
    private Region region;

    private BeaconAdapter beaconAdapter;


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconAdapter = new BeaconAdapter(this);
        listView = findViewById(R.id.beaconLV);
        listView.setAdapter(beaconAdapter);
        region = new Region(UNIQUE_ID, Identifier.parse(PROXIMITY_UUID), null, null);
        beaconManager.bind(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {

            //Scan lasts for SCAN_PERIOD time
            beaconManager.setForegroundScanPeriod(1000l);
            //mBeaconManager.setBackgroundScanPeriod(0l);

            //Wait every SCAN_PERIOD_INBETWEEN time
            beaconManager.setForegroundBetweenScanPeriod(0l);

            //Update default time with the new one
            beaconManager.updateScanPeriods();
        }
        catch (RemoteException e){
            e.printStackTrace();
        }


        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "Beacon Just Detected" + region.getUniqueId());
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "Beacon Not Detected" + region.getUniqueId());
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.d(TAG, "Beacon Detected/Not Detected");
            }
        });
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {listView = findViewById(R.id.beaconLV);
                if (beacons.size() > 0) {
                    for (final Beacon beacon : beacons) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                beaconAdapter.initAll(beacons);

                            }
                        });
                    }
                }
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, "No Start Monitoring iBeacon");
        }
    }

    public void showIBeacon(Beacon beacon)
    {
        Intent viewIntent = new Intent(this, ShowBeacon.class);
        viewIntent.putExtra(BEACON_EXTRA, (Serializable) beacon);
        this.startActivity(viewIntent);
    }
  /*  public void isBluetoothEnabled() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if(beaconManager.isBound(this)){
            beaconManager.setBackgroundMode(true);
        }
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        //isBluetoothEnabled();

    }
}
