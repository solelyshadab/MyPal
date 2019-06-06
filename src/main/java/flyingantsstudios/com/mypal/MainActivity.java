package flyingantsstudios.com.mypal;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new SensorService();
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

        findListofApps();
        getUsageStats();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true +"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }


    private void findListofApps(){
        List<App> appList = new AppDetails().getPackages(this);
        TextView textView = (TextView) findViewById(R.id.app_details_text);
        StringBuilder apps = new StringBuilder();
        for(App app: appList){
            apps.append(app.appname + "\n");
        }
        textView.setText(apps.toString());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUsageStats(){
        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DATE, 1);
        beginCal.set(Calendar.MONTH, 0);
        beginCal.set(Calendar.YEAR, 2016);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DATE, 6);
        endCal.set(Calendar.MONTH, 0);
        endCal.set(Calendar.YEAR, 2019);
        final UsageStatsManager usageStatsManager=(UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

        List<UsageStats> finalList = new ArrayList<>();
        for(UsageStats usageStat : queryUsageStats){
            //Log.d("Package", usageStat.getPackageName());
            if(usageStat.getPackageName().contains("whatsapp")){
                finalList.add(usageStat);
                Log.d("WhatsApp", "used for: " +usageStat.getTotalTimeInForeground());
            }
        }
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
