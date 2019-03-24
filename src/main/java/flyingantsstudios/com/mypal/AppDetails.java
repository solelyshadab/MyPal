package flyingantsstudios.com.mypal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shadabbaig on 06/01/19.
 */

public class AppDetails {
    private Set<String> monitoredAppsSet = new HashSet<>(Arrays.asList(
            "YouTube","Camera","Gallery","Clock","Contacts Storage","Facebook",
            "Cricbuzz","WhatsApp","Permission manager","ESPNcricinfo"));

    public ArrayList<App> getPackages(Context context) {
        ArrayList<App> apps = getInstalledApps(false, context); /* false = no system packages */
        final int max = apps.size();
        Log.d("INFO: ", "List Of Installed Apps are...");
        for (int i=0; i<max; i++) {
            apps.get(i).prettyPrint();
        }
        Log.d("INFO: ", "Finished Printing Lit of Apps...");
        return apps;
    }

    private ArrayList<App> getInstalledApps(boolean getSysPackages, Context context) {
        ArrayList<App> res = new ArrayList<>();

        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if (getSysPackages && p.versionName == null) {
                continue ;
            }
            if(!isMonitoredApp(p,context))
                continue;

            App newInfo = new App();
            newInfo.appname = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(newInfo);
        }
        return res;
    }

    private boolean isMonitoredApp(PackageInfo packageInfo, Context context){
        String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        return monitoredAppsSet.contains(appName);
    }
}
