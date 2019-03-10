package com.example.loinguyen.ibeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.example.loinguyen.ibeacon.Adapter.BeaconAdapter;
import com.opencsv.CSVWriter;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity implements BeaconConsumer {
    private static final String TAG = "Beacon Detect";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String UNIQUE_ID = "com.example.loinguyen.ibeacon";
    private boolean found = false;
    public static final String BEACON_EXTRA = "beacon_extra";
    private static final String PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private BeaconManager beaconManager;
    private Region region, region1, region2, region3, region4;
    public static ArrayList<Beacon> beaconsList = new ArrayList<Beacon>();;
    private BeaconAdapter beaconAdapter;


    ListView listView;
    TextView textView;

    String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    String fileName = "RssiData.csv";
    String filePath = csv + File.separator + fileName;


    final List<String> rssi = new ArrayList<String>();
    String data, data1, data2, data3 = "";

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
        region1 = new Region(UNIQUE_ID, Identifier.parse(PROXIMITY_UUID), Identifier.parse("36616"), null);
        region2 = new Region(UNIQUE_ID, Identifier.parse(PROXIMITY_UUID), Identifier.parse("64882"), null);
        region3 = new Region(UNIQUE_ID, Identifier.parse(PROXIMITY_UUID), Identifier.parse("16751"), null);
        region4 = new Region(UNIQUE_ID, Identifier.parse(PROXIMITY_UUID), Identifier.parse("7503"), null);


        beaconManager.bind(this);
        textView = findViewById(R.id.btnExport);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportData(data, data1, data2, data3);
                    data = "";
                    data1 = "";
                    data2 = "";data3 = "";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    beaconsList.clear();
                    for (final Beacon beacon : beacons) {
                        if(beacon.getId2().equals(Identifier.parse("36616"))
                                ) {
                            beaconsList.add(beacon); }
                        else if(beacon.getId2().equals(Identifier.parse("64882"))) {
                            beaconsList.add(beacon);
                        }
                        if(beacon.getId2().equals(Identifier.parse("16751"))) {
                            beaconsList.add(beacon);
                        }
                        else
                        if(beacon.getId2().equals(Identifier.parse("7503"))) {
                            beaconsList.add(beacon);
                        }
                    }
                    if(beaconsList.size() > 3) {
                        Collections.sort(beaconsList, new Comparator<Beacon>() {
                            @Override
                            public int compare(Beacon beacon1, Beacon beacon2) {
                                double rssi1 = beacon1.getRssi();
                                double rssi2 = beacon2.getRssi();
                                return rssi1 > rssi2 ? -1 : 1;
                            }

                        });


                            data =  data + " , " + beaconsList.get(0).getId2() + " : " + String.valueOf(beaconsList.get(0).getRssi());
                            data1 = data1 + " , "  + beaconsList.get(1).getId2() + " : "+ String.valueOf(beaconsList.get(1).getRssi());
                            data2 = data2 + " , " + beaconsList.get(2).getId2() + " : "+ String.valueOf(beaconsList.get(2).getRssi());
                            data3 = data3 + " , " + beaconsList.get(3).getId2() + " : "+ String.valueOf(beaconsList.get(3).getRssi());


                    }
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

    public void showIBeacon(Beacon beacon) {
        Intent viewIntent = new Intent(this, ShowBeacon.class);
        viewIntent.putExtra(BEACON_EXTRA, (Serializable) beacon);
        this.startActivity(viewIntent);
    }

    public void exportData(String data, String data1, String data2, String data3) throws IOException {

        File f = new File(filePath);
        FileWriter mFileWriter = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(mFileWriter);
        bw.write(data);
        bw.write("\n");
        bw.write(data1);
        bw.write("\n");
        bw.write(data2);
        bw.write("\n");
        bw.write(data3);
        bw.close();
        Toast.makeText(MainActivity.this, filePath , Toast.LENGTH_LONG).show();
    }

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
