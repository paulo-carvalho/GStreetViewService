package android.lehman.gstreetviewservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Paulo-Lehman on 3/13/2015.
 */
public class StreetViewService extends Service {

    private final String TAG = getClass().getSimpleName();
    private final boolean D = Log.isLoggable(TAG, Log.DEBUG);

    @Override
    public void onCreate() {
        super.onCreate();
        if(D) {
            Log.d(TAG, "onCreated");}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String latlong = intent.getStringExtra(Lab1Fragment.LAT_LONG);
        String headingPosition = intent.getStringExtra(Lab1Fragment.IMG_HEADING);

        ServiceTask serviceTask = new ServiceTask();
        serviceTask.execute(latlong, headingPosition);

        /*
        Toast.makeText(getApplicationContext(),
                "Achieved onStartCommand method",
                Toast.LENGTH_SHORT).show();
        */

        if(D) {
            Log.d(TAG, "onStarted");}
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(D) {
            Log.d(TAG, "onDestroyed");}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ServiceTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            String stringUri = "https://maps.googleapis.com/maps/api/streetview?"
                    + "size=" + Lab1Fragment.IMG_SIZE + "&"
                    + "location=" + params[0] + "&"
                    + "heading=" + params[1] + "&"
                    + "pitch=10";

            Log.d(TAG, stringUri);

            try {
                Uri uri = Uri.parse(stringUri);
                URL mapsURL = new URL(uri.toString());

                return BitmapFactory.decodeStream(mapsURL.openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap bitmapImage) {
            Intent intent = new Intent();
            intent.setAction(Lab1Fragment.GMAPS_STREETVIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Lab1Fragment.REFRESH_IMG, bitmapImage);
            sendBroadcast(intent);

            /*
            Toast toast = Toast.makeText(getApplicationContext(),
                    String.format("Loading Image..."),
                    Toast.LENGTH_SHORT);
            toast.show();
            */

            if (D) { Log.d(TAG, "onPostExecute");}
        }

    }
}