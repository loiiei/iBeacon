package com.example.loinguyen.ibeacon;
import com.estimote.coresdk.*;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

public class ShowBeacon extends Activity {

    private Beacon beacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beacon = getIntent().getParcelableExtra(MainActivity.BEACON_EXTRA);
        if(beacon == null){
            finish();
        }
        setContentView(R.layout.activity_show_beacon);
        ((TextView) findViewById(R.id.tv1)).setText("iBeacon Found");
        ((TextView) findViewById(R.id.tv2)).setText(beacon.getId1().toString());
        ((TextView) findViewById(R.id.tv3)).setText("RSSI: " + beacon.getRssi()+" Major: " + beacon.getId2().toString() + " - Minor: "+ beacon.getId3().toString());
        ((TextView) findViewById(R.id.tv4)).setText("Distance: " + String.valueOf(beacon.getDistance() + " m"));


    }
}
