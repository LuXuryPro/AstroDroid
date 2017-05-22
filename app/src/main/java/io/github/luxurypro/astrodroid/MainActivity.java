package io.github.luxurypro.astrodroid;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.DateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String EXTRA_MESSAGE = "io.github.luxurypro.astrodroid.MESSAGE";
    private Handler someHandler;
    private TextView jDateField;
    private TextView latitude;
    private TextView longitude;

    private LocationProviderService locationProviderService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            locationProviderService = ((LocationProviderService.LocalBinder) service).getService();
            if (!locationProviderService.isEnabled()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            locationProviderService = null;
        }
    };
    private boolean mIsBoundToService;

    public MainActivity() {
        this.someHandler = new Handler();
    }

    private TextView dateField;

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateField = (TextView) this.findViewById(R.id.data);
        jDateField = (TextView) this.findViewById(R.id.dataJ);
        latitude = (TextView) this.findViewById(R.id.gpsLat);
        longitude = (TextView) this.findViewById(R.id.gpsLon);
        someHandler.post(runnable);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        someHandler.removeCallbacks(this.runnable);
        this.doUnbindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        someHandler.post(this.runnable);
        this.doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        someHandler.removeCallbacks(this.runnable);
        someHandler = null;
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this, LocationProviderService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBoundToService = true;
    }


    void doUnbindService() {
        if (mIsBoundToService) {
            unbindService(mConnection);
            mIsBoundToService = false;
        }
    }

    public void runSkyView(View view) {
        startActivity(new Intent(this, SkyActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                goToSettings(null);
                return true;
            case R.id.about:
                showAboutDialog(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void showAboutDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");

        final String aboutDialogText = "\n\nAstroDroid v1.0\nAD 2017";
        TextView textView = new TextView(this);
        textView.setText(aboutDialogText);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        builder.setView(textView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void goToSettings(View v) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
