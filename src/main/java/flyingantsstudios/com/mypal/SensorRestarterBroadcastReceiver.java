package flyingantsstudios.com.mypal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by shadabbaig on 08/01/19.
 */

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, SensorService.class));;
    }
}
