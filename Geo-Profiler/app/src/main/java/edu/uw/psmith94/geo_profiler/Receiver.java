package edu.uw.psmith94.geo_profiler;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;


/**
 * Created by Min on 5/28/2016.
 */

public class Receiver extends BroadcastReceiver {

    LocationManager mLocationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            SmsMessage[] sms = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String author = sms[0].getOriginatingAddress();

            Calendar c = Calendar.getInstance();
            int today = c.get(Calendar.DAY_OF_WEEK);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            String dayContent = "";

            switch (today) {
                case 1:
                    dayContent = Profile.SUN;
                case 2:
                    dayContent = Profile.MON;
                case 3:
                    dayContent = Profile.TUE;
                case 4:
                    dayContent = Profile.WED;
                case 5:
                    dayContent = Profile.THU;
                case 6:
                    dayContent = Profile.FRI;
                case 7:
                    dayContent = Profile.SAT;
                default:
                    dayContent = Profile.MON;
            }

            String[] projection = new String[]{Profile.ID, Profile.LAT, Profile.LNG, Profile.SHAPE, Profile.MESSAGE,
                    Profile.RADIUS, Profile.TIME_END, Profile.TIME_START, dayContent, Profile.ACTIVE};

            String whereClause = Profile.ACTIVE + "= 1" + " AND " + dayContent + " = 1";

            Cursor cur = context.getContentResolver().query(ProfileProvider.CONTENT_URI, projection,
                    whereClause, null, null);



            Location lo = getLastKnownLocation(context);
            // checks if location is null or not. If it is not null, then proceed, if yes, then don't do anything

            while(cur.moveToNext()){
                //checks time first, then the coordinates
                Log.v("TAG", (cur.getString(cur.getColumnIndex(Profile.TIME_END))));
                Log.v("TAG", (cur.getString(cur.getColumnIndex(Profile.TIME_START))));

            }
            cur.close();

        }
    }


    private Location getLastKnownLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


}


