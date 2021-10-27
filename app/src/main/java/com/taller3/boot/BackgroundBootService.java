package com.taller3.boot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taller3.Activities.LogInActivity;
import com.taller3.Module.User;
import com.taller3.R;
import com.taller3.Activities.UserListActivity;

public class BackgroundBootService extends Service
{
    public static String CHANNEL_ID = "Taller_3", PATH_USERS = "Users";
    int notficatonId = 3;
     FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener myListener;
    private FirebaseAuth mAuth;
    int noUsersAvailable = 0;
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(CHANNEL_ID, "Created");
        createNotificationChannel();
    }
    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onDestroy()
    {
        Log.i(CHANNEL_ID, "BOOT Service has been stopped");
        Toast.makeText(this, "BOOT service stopped", Toast.LENGTH_LONG).show();
        myRef.removeEventListener(myListener);
    }
    private void changeListUsers()
    {
        myRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren())
                {
                    User myUser = singleSnapshot.getValue(User.class);
                    if (myUser == null) throw new AssertionError();
                    if (myUser.getState() != null) {
                        if (myUser.getState().equals("available")) {
                            noUsersAvailable++;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w(CHANNEL_ID, "error en la consulta", databaseError.toException());
            }

        });

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //TODO Start sticky notification
        startForeground(2, buildComplexNotification("Service Started",
        "Connected to NubePUJ", R.drawable.ic_baseline_notif_icon, UserListActivity.class));
        database= FirebaseDatabase.getInstance();
        myRef= database.getReference(PATH_USERS);
        mAuth = FirebaseAuth.getInstance();
        changeListUsers();
        myListener = myRef.addValueEventListener( new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int availableUsersCurr = 0;

              for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
              {
                  User myUser = singleSnapshot.getValue(User.class);
                  if (myUser == null) throw new AssertionError();
                  if (myUser.getState().equals("available"))
                  {
                      availableUsersCurr++;
                  }
              }
              if (noUsersAvailable < availableUsersCurr)
              {
                  if (mAuth.getCurrentUser() != null) {
                      sendNotification(notficatonId, "Usuario Disponible!"
                              , "Un usuario se a vuelto disponible! Click aqui para abrir actividad",
                              R.drawable.ic_baseline_notif_icon, UserListActivity.class);
                  }
                  else
                  {
                      sendNotification(notficatonId, "Usuario nuevo Disponible!"
                              , "Un usuario se a vuelto disponible! Click aqui para entrar a sesión",
                              R.drawable.ic_baseline_notif_icon, LogInActivity.class);
                  }
              }
              noUsersAvailable = availableUsersCurr;
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w(CHANNEL_ID, "error en la consulta", databaseError.toException());
            }
        });
        return START_STICKY;
    }
    private void sendNotification(int id, String titulo, String contenido, int icono, Class target)
    {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(id, buildComplexNotification(titulo,contenido,icono,target ));
    }
    private Notification buildComplexNotification(String titulo, String contenido, int icono, Class target)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(icono);
        mBuilder.setContentTitle(titulo);
        mBuilder.setContentText(contenido);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent (this, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        return mBuilder.build();

    }



}
