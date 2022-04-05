package com.example.radiokanal.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.radiokanal.R;
import com.example.radiokanal.model.RadioModel;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PREVIOUS = "actionprevious";

    public static Notification notification;

    public static void createNotification(Context context, RadioModel radioModel, int playbutton, int pos, int size){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.baydak);

            PendingIntent pendingIntentPrev;
            int drw_previous;
            if(pos==0){
                pendingIntentPrev = null;
                drw_previous = 0;
            }else{
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_PREVIOUS);
                pendingIntentPrev = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.ic_baseline_skip_previous_24;
            }

            int drw_play = R.drawable.exo_icon_play;
            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            int drw_next;
            if(pos==size){
                pendingIntentNext = null;
                drw_next = 0;
            }else{
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_baseline_skip_next_24;
            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baydak)
                    .setContentTitle(radioModel.getName())
                    .setContentText(radioModel.getDescription())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .addAction(drw_previous, "Previous", pendingIntentPrev)
                    .addAction(drw_play, "Play", pendingIntent)
                    .addAction(drw_next, "Previous", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}
