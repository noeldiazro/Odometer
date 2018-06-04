package es.montanus.odometer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class OdometerService extends Service {

    private final IBinder binder;
    private final Random random;

    public OdometerService() {
        this.binder = new OdometerBinder();
        this.random = new Random();
    }

    private class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getDistance() {
        return random.nextDouble();
    }
}
