package flyingantsstudios.com.mypal;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by shadabbaig on 06/01/19.
 */

public class App {
    public String appname = "";
    public String pname = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;

    public void prettyPrint() {
        Log.d("APP INFO: ",appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
    }
}
