package rs.fon.todoapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }


    /*
    * Ovo je metoda koja se izvrsava kada nas receiver uhvati intent preko intent-filtera.
    * Videti AndroidManifest.xml za detaljnije informacije.
    *
    * U ovom slucaju, vadimo tekst koji predstavlja tekst todo poruke.
    *
    * Zatim pravimo novu notifikaciju.
    * */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String text = extras.getString("text");

        NewTodoNotification.notify(context,text,0);
    }
}
