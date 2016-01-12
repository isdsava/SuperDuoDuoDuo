package barqsoft.footballscores.widgets;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by Admin on 11/01/2016.
 */
public class TodaysScoresWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds){

        final int N = appWidgetIds.length;

            for (int i=0;i<N;i++) {
                int appWidgetid = appWidgetIds[i];
                int layOutIdWidge;
                Bundle options =  appWidgetManager.getAppWidgetOptions(appWidgetid);
                if(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)<= 110){
                    layOutIdWidge = R.layout.widget_today_scores;
                } else if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)>=220){
                    layOutIdWidge = R.layout.widget_today_scores_large;
                } else {
                    layOutIdWidge = R.layout.widget_today_scores_med;
                }

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layOutIdWidge);

                context.startService(new Intent(context, TodaysScoresWidgetIntentService.class));

                Intent clickIntent = new Intent(context, MainActivity.class);
                //PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                //        .addNextIntentWithParentStack(clickIntentTemplate)
                //        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                PendingIntent clickPendingIntent = PendingIntent.getActivity(context,0,clickIntent,0);

                remoteViews.setOnClickPendingIntent(R.id.widget, clickPendingIntent);


                appWidgetManager.updateAppWidget(appWidgetid, remoteViews);

            }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, TodaysScoresWidgetIntentService.class));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    @Override
    public  void onReceive( Context context,Intent intent){
        super.onReceive(context, intent);



        if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())){
            context.startService(new Intent(context,TodaysScoresWidgetIntentService.class));
        }

    }
}
