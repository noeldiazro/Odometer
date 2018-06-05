package es.montanus.odometer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OdometerService extends Service {

    private static final double METERS_PER_MILE = 1609.344;
    private final IBinder binder;
    private LocationListener listener;
    private Location lastLocation;
    private double distanceInMeters;
    private LocationManager locationManager;

    public OdometerService() {
        this.binder = new OdometerBinder();
    }

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        listener = new BaseLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation != null)
                    distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (isGranted(ACCESS_FINE_LOCATION)) {
            String provider = locationManager.getBestProvider(new Criteria(), true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null && listener != null) {
            if (isGranted(ACCESS_FINE_LOCATION))
                locationManager.removeUpdates(listener);
        }
        locationManager = null;
        listener = null;
    }

    public double getDistance() {
        return distanceInMeters / METERS_PER_MILE;
    }
}
