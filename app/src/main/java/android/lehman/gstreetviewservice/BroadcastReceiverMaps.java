package android.lehman.gstreetviewservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Paulo-Lehman on 3/24/2015.
 */
public class BroadcastReceiverMaps extends BroadcastReceiver {

    private View view;

    public BroadcastReceiverMaps(View view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ImageView imageView = (ImageView) view.findViewById(R.id.locStreetView);
        Bitmap myBitmapView = intent.getParcelableExtra(Lab1Fragment.REFRESH_IMG);
        imageView.setImageBitmap(myBitmapView);

    }
}
