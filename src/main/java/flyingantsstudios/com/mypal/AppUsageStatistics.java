package flyingantsstudios.com.mypal;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * Created by shadabbaig on 15/01/19.
 */

public class AppUsageStatistics {
    private UsageStatsManager mUsageStatsManager;
    private Context context;
    private Set<String> monitoredAppsSet;

    public AppUsageStatistics(Context context){
        this.context = context;
        mUsageStatsManager = (UsageStatsManager) context.getApplicationContext()
                .getSystemService(context.USAGE_STATS_SERVICE);
        monitoredAppsSet = new HashSet<>(Arrays.asList(
                "youtube","camera","gallery","clock","contacts storage","facebook",
                "cricbuzz","whatsapp","espncricinfo","mypal","paytm"));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, System.currentTimeMillis()-10000,
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
                    context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
        return queryUsageStats;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String latestAppOpened(){
        List<UsageStats> usageStatsList = getUsageStatistics(StatsUsageInterval.DAILY.mInterval);
        long currentTime = System.currentTimeMillis();
        int diff = 10000;
        //getDaily("whatsapp",System.currentTimeMillis() - 10000,System.currentTimeMillis());
        for(UsageStats usageStats: usageStatsList){
            String pkgName = usageStats.getPackageName();
            if(pkgName.contains("whatsapp")|| pkgName.contains("facebook") || pkgName.contains("instagram")
                    || pkgName.contains("chrome") || pkgName.contains("camera")) {
                Log.d("STATS DATA: ", "Package name: " + usageStats.getPackageName());
                Log.d("STATS DATA: ", "Current time: " + milliSecondsToDateTime(currentTime));
                Log.d("STATS DATA: ", "LastUsd time: " + milliSecondsToDateTime(usageStats.getLastTimeUsed()));
                Log.d("STATS DATA: ", "Forgrnd time: " + milliSecondsToDateTime(usageStats.getTotalTimeInForeground()));
                Log.d("STATS DATA: ", "First tmstmp: " + milliSecondsToDateTime(usageStats.getFirstTimeStamp()));
                Log.d("STATS DATA: ", "Last tmestmp: " + milliSecondsToDateTime(usageStats.getLastTimeStamp()));
                long tempDiff = currentTime - usageStats.getLastTimeUsed();
                Log.d("STATS DATA: " ,""+tempDiff);
            }
            long tempDiff = currentTime - usageStats.getLastTimeUsed();
            if( ( Math.abs(tempDiff) <=diff) && monitoredAppsSet.contains(usageStats.getPackageName())){
                return usageStats.getPackageName();
            }

        }

        return "NONE";
    }

    private boolean isMonitoredApp(String packageName){

        for(String app: monitoredAppsSet){
            return packageName.toLowerCase().contains(app);
        }
        return false;
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
