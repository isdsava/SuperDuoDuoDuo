package barqsoft.footballscores.widgets;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by Admin on 11/01/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresListRemoteViewsService  extends RemoteViewsService {


    private Context mContext;
    private static final String[] TODAY__SCORES_PROJECTION =new String[] {
            DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.MATCH_DAY,
            DatabaseContract.scores_table.DATE_COL


    };

    // these indices must match the projection
    private static final int INDEX_GAME_ID = 1;
    private static final int INDEX_AWAY = 1;
    private static final int INDEX_AWAY_GOALS = 2;
    private static final int INDEX_HOME = 3;
    private static final int INDEX_HOME_GOALS = 4;
    private static final int INDEX_MATCH_DAY = 5;
    private static final int INDEX_DATE = 6;




    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor mCursor = null;



            @Override
            public void onCreate() {
            }



            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }

            }

            @Override
            public RemoteViews getViewAt(int i){


                if (i == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(i)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                String awayTeam = mCursor.getString(INDEX_AWAY);
                String awayGoals = mCursor.getString(INDEX_AWAY_GOALS);
                String homeTeam = mCursor.getString(INDEX_HOME);
                String homeGoals = mCursor.getString(INDEX_HOME_GOALS);
                int matchDay = mCursor.getInt(INDEX_MATCH_DAY);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(remoteViews, homeTeam);
                }

                remoteViews.setTextViewText(R.id.home_name, homeTeam);
                remoteViews.setTextViewText(R.id.score_textview,homeGoals + " : " + awayGoals);
                remoteViews.setTextViewText(R.id.data_textview,Integer.toString(matchDay));
                remoteViews.setTextViewText(R.id.away_name, awayTeam);

                //TODO not sure this need as no zoom into football scores anyway

                final Intent fillInIntent = new Intent();
                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView () {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount () {
                return 1;
            }

            @Override
            public long getItemId ( int position){
                if (mCursor.moveToPosition(position))
                    return mCursor.getLong(INDEX_GAME_ID);
                return position;
            }

            @Override
            public boolean hasStableIds () {
                return true;
            }

            @Override
            public void onDataSetChanged () {

                if (mCursor != null) {
                    mCursor.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                String fragmentDateArray[] = new String[1];
                Date fragmentdate = new Date(System.currentTimeMillis());
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                fragmentDateArray[0] = mformat.format(fragmentdate);

                Uri scoreWithDate = DatabaseContract.scores_table.buildScoreWithDate();

                mCursor = getContentResolver().query(scoreWithDate,
                        TODAY__SCORES_PROJECTION,
                        null,
                        fragmentDateArray,
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public int getCount () {
                return mCursor == null ? 0 : mCursor.getCount();
            }


            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription (RemoteViews views, String description){
                views.setContentDescription(R.id.widget_icon, description);
            }

        };
    }
}
