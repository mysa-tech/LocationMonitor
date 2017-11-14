package tr.com.mysatech.locationmonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.content.Intent;
import android.renderscript.Double2;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    final String TAG = "GPS";
    final String ERROR_TAG = "My Service";
    public static boolean serviceRunning = false;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private Location mLastLocation = null;
    String mSpeedLimit = "";
    double speed = 0;
    Context mContext;
    GoogleApiClient gac;
    LocationRequest locationRequest;

    public MyService()
    {
    }

    @Override
    public void onCreate()
    {
        Log.d(ERROR_TAG, "MyService Started");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        isGooglePlayServicesAvailable();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b == null){
                Log.d(ERROR_TAG, "Bundle is null");
            }
            mSpeedLimit = MainActivity.mSpeedLimit;
            Log.d(ERROR_TAG, "speedLimit is: " + mSpeedLimit);
        }
        gac.connect();
        serviceRunning = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        gac.disconnect();
        serviceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Context context = getApplicationContext();
        if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        Log.d(TAG, "onConnected");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (mLastLocation == null ? "NO LastLocation" : mLastLocation.toString()));

        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    public double getSpeed()
    {
        return speed;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location pCurrentLocation)
    {
        if (pCurrentLocation != null) {
            mContext = getApplicationContext();
            if (this.mLastLocation != null && !mSpeedLimit.equals(""))
                setSpeed(3.6 * pCurrentLocation.distanceTo(mLastLocation) / ((pCurrentLocation.getTime() - this.mLastLocation.getTime())/1000));
            if (pCurrentLocation.hasSpeed())
                setSpeed(3.6 * pCurrentLocation.getSpeed());
            this.mLastLocation = pCurrentLocation;

            if (!mSpeedLimit.equals("")){
                if (getSpeed() > Double.parseDouble(mSpeedLimit) && Double.parseDouble(mSpeedLimit) > 0) {
                    if (getSpeed() > 0) {
                        Intent i = new Intent("my.speed.data");
                        i.putExtra("current_speed", Double.toString(getSpeed()));
                        sendBroadcast(i);
                    }
                }
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                //apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        //.show();
            } else {
                Log.d(TAG, "This device is not supported.");
                this.stopSelf();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }
}