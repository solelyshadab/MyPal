package flyingantsstudios.com.mypal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shadabbaig on 08/01/19.
 */

public class SensorService extends Service {
    public int counter=0;
    private Map<String, Long> lastOpenedAppMap = new HashMap<>();
    private long coolOffTime = 60000;
    AppUsageStatistics appUsageStatistics;
    private Set<String> monitoredAppSet = new HashSet<>(Arrays.asList("whatsapp","paytm","linkedin", "cricbuzz"));

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public SensorService() {
        super();
        Log.i("HERE", "here I am!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                String lastApp = getLatestAppOpened();
                if(!Objects.equals(lastApp, "NULL")){
                    showPopUp(lastApp);
                }
            }
        };
    }

//    public void initializeTimerTask() {
//        this.timerTask = new TimerTask() {
//            public void run() {
//                String currentApp = "NULL";
//                if (VERSION.SDK_INT >= 21) {
//                    UsageStatsManager usm = (UsageStatsManager) SensorService.this.getSystemService(Context.USAGE_STATS_SERVICE);
//                    long time = System.currentTimeMillis();
//                    List<UsageStats> appList = usm.queryUsageStats(0, time - 1000000, time);
//                    if (appList != null && appList.size() > 0) {
//                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap();
//                        for (UsageStats usageStats : appList) {
//                            mySortedMap.put(Long.valueOf(usageStats.getLastTimeUsed()), usageStats);
//                        }
//                        if (!mySortedMap.isEmpty()) {
//                            currentApp = ((UsageStats) mySortedMap.get(mySortedMap.lastKey())).getPackageName();
//                        }
//                    }
//                } else {
//                    currentApp = ((ActivityManager.RunningAppProcessInfo) ((ActivityManager) SensorService.this.getSystemService("activity")).getRunningAppProcesses().get(0)).processName;
//                }
//                currentApp = currentApp.substring(currentApp.lastIndexOf('.') +1);
//                if(!monitoredAppSet.contains(currentApp))
//                    return;
//
//                if (!lastOpenedAppMap.containsKey(currentApp)){
//                    showPopUp(currentApp);
//                    lastOpenedAppMap.put(currentApp,System.currentTimeMillis());
//                }else if(System.currentTimeMillis() - lastOpenedAppMap.get(currentApp) >= coolOffTime) {
//                    lastOpenedAppMap.remove(currentApp);
//                }
//                //SensorService.this.showToastMessage(currentApp);
//            }
//        };
//    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void showToastMessage(){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                Toast.makeText(getApplicationContext(),"Counter is : " + counter ,
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"App opened is : " + getLatestAppOpened() ,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void showToastMessage(final String app_name) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @RequiresApi(api = 24)
            public void run() {
                Context applicationContext = SensorService.this.getApplicationContext();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("App opened is : ");
                stringBuilder.append(app_name);
                Toast.makeText(applicationContext, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopUp(String app_name){
        if(app_name.contains("mypal") || app_name.contains("launcher")){
            return;
        }
        String s = "The app opened is: ";
        StringBuilder sb = new StringBuilder( s + app_name);
        Context applicationContext = SensorService.this.getApplicationContext();
        Intent intent = new Intent(applicationContext, PopUpService.class);
        intent.putExtra("popupMessage", sb.toString());
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getLatestAppOpened(){
        appUsageStatistics = AppUsageStatistics.getInstance();
        appUsageStatistics.setContext(this).setMUsageStatsManager();
        return appUsageStatistics.latestAppOpened();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
