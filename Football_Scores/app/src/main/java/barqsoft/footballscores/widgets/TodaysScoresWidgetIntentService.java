package barqsoft.footballscores.widgets;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;

/**
 * Created by Admin on 11/01/2016.
 */
public class TodaysScoresWidgetIntentService extends IntentService {

    private static final String[] TODAY__SCORES_PROJECTION =new String[] {
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL

    };

    // these indices must match the projection
    private static final int INDEX_AWAY = 0;
    private static final int INDEX_AWAY_GOALS = 1;
    private static final int INDEX_HOME = 2;
    private static final int INDEX_HOME_GOALS = 3;

    private AppWidgetManager mAppWidgetManager;
    private int[] mAppWidgetIds;
    private String[] fragmentDateArray = new String[1];

    public TodaysScoresWidgetIntentService(){
        super("Wiggie");
    }

    public TodaysScoresWidgetIntentService(String args){
        super(args);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(this,TodaysScoresWidgetProvider.class));
        final int N = mAppWidgetIds.length;
        int weatherFart = R.drawable.manchester_city;
        String descr = "Clear";
        double maxTemp = 24;


        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        fragmentDateArray[0] = mformat.format(fragmentdate);

        Context context = getApplicationContext();

        Uri scoreWithDate = DatabaseContract.scores_table.buildScoreWithDate();

        // we'll query our contentProvider, as always
        Cursor cursor = context.getContentResolver().query(scoreWithDate, TODAY__SCORES_PROJECTION, null, fragmentDateArray, null);

        if (cursor.moveToFirst()) {
            String awayTeam = cursor.getString(INDEX_AWAY);
            String awayGoals = cursor.getString(INDEX_AWAY_GOALS);
            String homeTeam = cursor.getString(INDEX_HOME);
            String homeGoals = cursor.getString(INDEX_HOME_GOALS);


            // On Honeycomb and higher devices, we can retrieve the size of the large icon
            // Prior to that, we use a fixed size

            String title = context.getString(R.string.app_name);



            for (int i=0;i<N;i++){
                int appWidgetId = mAppWidgetIds[i];
                int layOutIdWidge;
                Bundle options =  mAppWidgetManager.getAppWidgetOptions(appWidgetId);
                //TODO base case for now expand later
                if(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)<= 110){
                    layOutIdWidge = R.layout.widget_today_scores;
                } else if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)>=220){
                    layOutIdWidge = R.layout.widget_today_scores_large;
                } else {
                    layOutIdWidge = R.layout.widget_today_scores_med;
                }



                RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),layOutIdWidge);
                remoteViews.setImageViewResource(R.id.widget_icon, R.drawable.ic_launcher);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
                    setRemoteContentDescription(remoteViews,homeTeam + " v " + awayTeam);
                }


                remoteViews.setTextViewText(R.id.home_name,homeTeam);
                remoteViews.setTextViewText(R.id.score_textview,homeGoals + ":" + awayGoals);
                remoteViews.setTextViewText(R.id.away_name,awayTeam);

                Intent fartTent = new Intent(context,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,0,fartTent,0);

                remoteViews.setOnClickPendingIntent(R.id.widget,pendingIntent);

                mAppWidgetManager.updateAppWidget(appWidgetId,remoteViews);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
