package android.lehman.gstreetviewservice;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lab1Fragment extends Fragment {

	private final String TAG = getClass().getSimpleName();
	private final boolean D = Log.isLoggable(TAG, Log.DEBUG);

    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText editTextLatLong;

    //constants used trough the Fragment, Service, and Broadcast Receiver
    private final String COORD_PATTERN = "([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)";
    protected static final String LAT_LONG = "LAT_LONG";
    protected static final String IMG_HEADING = "HEADING";
    protected static final String IMG_SIZE = "340x340";
        //REFRESH_IMG: used in onReceive(), and Service class
    protected static final String REFRESH_IMG = "refreshStreetViewImage";
        //Action name used by Fragment class
    protected static final String GMAPS_STREETVIEW = "lehman.android.intent.action.GMAPS_STREETVIEW";

    //retrieve heading position
    private int headingPostion = 0;

    private boolean serviceStarted = false;

	public Lab1Fragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (D) { Log.d(TAG, "Starting onCreateView");}
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        editTextLatLong = (EditText)rootView.findViewById(R.id.latLon);

        Button getLocation = (Button)rootView.findViewById(R.id.getLocation);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (((Button) v).getText().toString().equals(getResources().getString(R.string.get_location))) {
                    locationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {

                            CharSequence cSequenceLatLong = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
                            editTextLatLong.setText(cSequenceLatLong);
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d(TAG, "onStatusChanged");
                        }

                        public void onProviderEnabled(String provider) {
                            Log.d(TAG, "onProviderEnabled");
                        }

                        public void onProviderDisabled(String provider) {
                            Log.d(TAG, "onProviderDisabled");
                        }
                    };
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    ((Button) v).setText(R.string.stop_location);
                } else {
                    locationManager.removeUpdates(locationListener);
                    ((Button) v).setText(R.string.get_location);
                }
            }
        });

        Button start = (Button)rootView.findViewById(R.id.startService);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Toast.makeText(getActivity(),
                        "Service Starting",
                        Toast.LENGTH_SHORT).show();
                */

                if (validateCoordinates(editTextLatLong.getText().toString())) {
                	Intent intent = new Intent(getActivity(), StreetViewService.class);
                    intent.putExtra(LAT_LONG, editTextLatLong.getText().toString());
                    intent.putExtra(IMG_HEADING, String.valueOf(headingPostion));
                	getActivity().startService(intent);

                    serviceStarted = true;

                    /*
                    Toast.makeText(getActivity(),
                            "Service Started",
                            Toast.LENGTH_SHORT).show();
                    */
                    Toast.makeText(getActivity(),
                            String.format("Loading Image..."),
                            Toast.LENGTH_SHORT).show();
                } else {
                	Toast.makeText(getActivity(),
                            "Coordinates format not acceptable",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button stop = (Button)rootView.findViewById(R.id.stopService);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(),
                        "Service Stopped",
                        Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getActivity(), StreetViewService.class);
                getActivity().stopService(intent);
            }
        });

        ImageView mapsImageView = (ImageView) rootView.findViewById(R.id.locStreetView);
        mapsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceStarted) {
                    Intent intent = new Intent(getActivity(), StreetViewService.class);
                    intent.putExtra(LAT_LONG, editTextLatLong.getText().toString());

                    headingPostion += 20;
                    if (headingPostion == 360) {
                        headingPostion = 0;
                    }

                    intent.putExtra(IMG_HEADING, String.valueOf(headingPostion));
                    getActivity().startService(intent);

                    Toast.makeText(getActivity(),
                            String.format("Rotating Image..."),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),
                            String.format("Define coordinates on 'Lat/Long'..."),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Retrieving image from Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter(GMAPS_STREETVIEW);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        //Asking Broadcast Receiver to warn THIS Fragment when the image will be changed
        BroadcastReceiverMaps broadcastReceiverMaps = new BroadcastReceiverMaps(rootView);
        getActivity().registerReceiver(broadcastReceiverMaps,intentFilter);

        if (D) { Log.d(TAG, "onCreateView completed");}
		return rootView;
	}

    private boolean validateCoordinates(String regexInput) {
           Pattern pattern = Pattern.compile(COORD_PATTERN);
           Matcher matcher = pattern.matcher(regexInput);
           return matcher.matches();
    }

}
