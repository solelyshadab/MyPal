package flyingantsstudios.com.mypal;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by shadabbaig on 15/01/19.
 */

public class AppUsageStatistics {
    private static AppUsageStatistics appUsageStatistics;
    private Context context;
    private UsageStatsManager mUsageStatsManager;
    private static Set<String> monitoredAppsSet;
    private static Map<String, Long> lastOpenedAppMap = new HashMap<>();
    private long coolOffTime = 60000;

    private AppUsageStatistics( ){
        this.mUsageStatsManager = mUsageStatsManager;
        monitoredAppsSet = new HashSet<>(Arrays.asList(
                "whatsapp"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static AppUsageStatistics getInstance(){
        if(appUsageStatistics == null){
            appUsageStatistics = new AppUsageStatistics();
        }
         return appUsageStatistics;
    }
    public AppUsageStatistics setContext(Context context){
        this.context = context;
        return appUsageStatistics;
    }

    public void setMUsageStatsManager(){
        mUsageStatsManager = (UsageStatsManager) context.getApplicationContext()
               .getSystemService(context.USAGE_STATS_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String latestAppOpened(){
        String currentApp = "NULL";
        if (Build.VERSION.SDK_INT >= 21) {
            long time = System.currentTimeMillis();
            List<UsageStats> appList = mUsageStatsManager.queryUsageStats(0, time - 1000000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = ((UsageStats) mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                }
            }
        } else {
            currentApp = ((ActivityManager.RunningAppProcessInfo) ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses().get(0)).processName;
        }
        currentApp = currentApp.substring(currentApp.lastIndexOf('.') +1);
        long diff = 0;
        if(lastOpenedAppMap.containsKey(currentApp)){
            diff = System.currentTimeMillis() - lastOpenedAppMap.get(currentApp);
            Log.d("OPENED APP: ","Time diff is now: " + diff);
        }

        if(!monitoredAppsSet.contains(currentApp)){
            return "NULL";
        }

        if (!lastOpenedAppMap.containsKey(currentApp)){
            lastOpenedAppMap.put(currentApp,System.currentTimeMillis());
            Log.d("OPENED APP: ","Not in map, show pop- up!");
            return currentApp;
        }else{
            if(System.currentTimeMillis() - lastOpenedAppMap.get(currentApp) >= coolOffTime) {
                lastOpenedAppMap.remove(currentApp);
                Log.d("OPENED APP: ","In map but cooling period over, show pop- up!");
                return currentApp;
            }else {
                Log.d("OPENED APP: ","In map and cooling period NOT over, DO NOT show pop- up!");
                return  "NULL";
            }
        }

    }


    private String milliSecondsToDateTime(long milliseconds){
        Date currentDate = new Date(milliseconds);

        //printing value of Date
        //System.out.println("current Date: " + currentDate);

        DateFormat dataFormat = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
        dataFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        //formatted value of current Date
        //System.out.println("Milliseconds to Date: " + df.format(currentDate));
        return dataFormat.format(currentDate);
    }

    //2nd Solution got from stackoverflow
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getDaily(String appPackageName, long startTime, long endTime)
    {
        List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime,endTime);

        String x="";
        for(int i=0; i<usageStatsList.size(); i++) {

            UsageStats stat = usageStatsList.get(i);
            if(stat.getPackageName().contains(appPackageName)) {
                x = x + i + "-" + stat.getPackageName() + "-"
                        + converLongToTimeChar(stat.getTotalTimeInForeground()) + "\n";
                Log.d("NEW API: " ,"First Time stamp        : " +  converLongToTimeChar(stat.getFirstTimeStamp()));
                Log.d("NEW API: " ,"Last Time Used          : " +  converLongToTimeChar(stat.getLastTimeUsed()));
                Log.d("NEW API: " ,"Last Time stamp         : " +  converLongToTimeChar(stat.getLastTimeStamp()));
                Log.d("NEW API: " ,"Total time in foreground: " +  converLongToTimeChar(stat.getTotalTimeInForeground()));
            }
        }

        return x;
    }

    public String converLongToTimeChar(long usedTime) {
        String hour="", min="", sec="";

        int h=(int)(usedTime/1000/60/60);
        if (h!=0)
            hour = h+"h ";

        int m=(int)((usedTime/1000/60) % 60);
        if (m!=0)
            min = m+"m ";

        int s=(int)((usedTime/1000) % 60);
        if (s==0 && (h!=0 || m!=0))
            sec="";
        else
            sec = s+"s";

        return hour+min+sec;
    }



    private enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }
}
