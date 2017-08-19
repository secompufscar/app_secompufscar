package br.com.secompufscar.secomp_ufscar;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by olivato on 23/07/17.
 */
// FCM = Firebase Cloud Messaging
public class SECOMPFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        final String msg = remoteMessage.getNotification().getBody();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(),"Acontecendo agora: "+msg,Toast.LENGTH_LONG).show();
                //findViewById(android.R.id.content)
            }
        });

    }
}
