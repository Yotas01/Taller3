package com.taller3.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taller3.Module.User;
import com.taller3.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    ListView listView;
    FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    public static String PATH_USERS = "Users";
    List<Bitmap> rImgs;
    List<String> rName;
    List<String> rIDs;
    int otherCounter = 0, firstCounter = 0;
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        database= FirebaseDatabase.getInstance();
        myRef= database.getReference().child(PATH_USERS);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        listView = findViewById(R.id.listViewUsers);
        rImgs = new ArrayList<>();
        rName = new ArrayList<>();
        rIDs = new ArrayList<>();
        getUsers();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                openMapForUser(position);
            }
        });



    }

    private void openMapForUser(int position)
    {
            Intent i = new Intent(this, MapUsuarioActivity.class);
            i.putExtra("key",rIDs.get(position));
            startActivity(i);
    }
    private void getUsers()
    {
        myRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int counter = 0;
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren())
                {
                    User myUser = singleSnapshot.getValue(User.class);
                    String myName = myUser.getName();
                    Log.w("TAG", "encontro usuario " + myName);
                    addToLists(myName, singleSnapshot.getKey());

                    try {
                        downloadFile(singleSnapshot.getKey(), myUser.getPhoto());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }


                    counter++;

                }

                //setAdapters();
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("TAG", "error en la consulta", databaseError.toException());
            }

        });
    }
    private void setAdapters()
    {
        adapter = new MyAdapter(this, rName, rIDs, rImgs);
        listView.setAdapter(adapter);

    }
    private void addToLists(String name, String id)
    {
        rName.add(name);
        rIDs.add(id);
    }
    private void downloadFile(String id, String imagePath) throws IOException
    {
        File localFile= File.createTempFile("images_"+id, "jpg");

        String myPath = imagePath;
        Log.i("TAG",myPath);
        StorageReference imageRef= mStorageRef.child(myPath);
        firstCounter++;
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                addToBitmapList(myBitmap);
                Log.i("TAG", "succesfully downloaded");
                otherCounterAdd();
                if (firstCounter == otherCounter)
                {
                    setAdapters();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure (@NonNull Exception exception)
                {
                    Log.w("TAG", "error in dowload");
                }
            });
    }
    private void addToBitmapList(Bitmap myBitmap)
    {
        //adapter.setImage(myBitmap);
        rImgs.add(myBitmap);
    }
    private void otherCounterAdd ()
    {
        otherCounter ++;
    }




    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        List<String> rTitle;
        List<String> rIDs;
        List<Bitmap> rImgs;

        MyAdapter (Context c, List<String> title, List<String> id, List<Bitmap> image) {
            super(c, R.layout.row_user_list, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rIDs = id;
            rImgs = image;
            //rImgs = new ArrayList<>();

        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_user_list, parent, false);
            ImageView images = row.findViewById(R.id.imageListUsers);
            TextView myTitle = row.findViewById(R.id.textView1);

            images.setImageBitmap(rImgs.get(position));

            myTitle.setText(rTitle.get(position));
            return row;

        }
        public String getID(int position)
        {
            return rIDs.get(position);
        }


    }
}
