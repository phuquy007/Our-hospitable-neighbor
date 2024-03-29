package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ourhospitableneighbor.model.User;
import com.example.ourhospitableneighbor.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AboutMe extends AppCompatActivity {

    User user;
    DatabaseReference databaseReference;
    TextView profileNameTextView;
    TextView profilePhonenoTextView;
    TextView profileDOBTextView;
    TextView profileEmailTextView;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    TextView logOut;
    Button btneditName;
    Button btnEditEmail;
    Button btnEditPhone;
    Button btnEditDOB;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        profileNameTextView = findViewById(R.id.profileName);
        profileEmailTextView = findViewById(R.id.profileEmail);
        profilePhonenoTextView = findViewById(R.id.profilePhone);
        profileDOBTextView = findViewById(R.id.profileBirth);
        logOut = findViewById(R.id.txtLog_out);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        btneditName = findViewById(R.id.buttonEditName);

        btnEditDOB = findViewById(R.id.buttonEditBirthday);
        btnEditPhone = findViewById(R.id.buttonEditPhone);

        getUser();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateLogOut();
            }
        });

        btneditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickedEditName();
            }
        });

        btnEditDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickEditDOB();
            }
        });


        btnEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickEditPhone();
            }
        });
    }

    private void getUser() {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get db from firebase
                user = User.fromFirebaseSnapshot(snapshot);
                profileNameTextView.setText(user.getName());
                profileDOBTextView.setText(user.getDob());
                profileEmailTextView.setText(firebaseAuth.getCurrentUser().getEmail());
                profilePhonenoTextView.setText(user.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void buttonClickedEditName() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_name_aboutme, null);
//        final EditText etUsername = alertLayout.findViewById(R.id.edit_username);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Name Edit");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        Log.v("LOG", "inside dialog");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final EditText userFullName = alertLayout.findViewById(R.id.edit_username);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = userFullName.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("name").setValue(name);
                profileNameTextView.setText(name);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickEditDOB() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_editdob, null);
//        final EditText etUsername = alertLayout.findViewById(R.id.edit_dob);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Date Edit");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        Log.v("LOG", "inside dialog");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final EditText userDOB = alertLayout.findViewById(R.id.edit_dob);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dob = userDOB.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("dob").setValue(dob);
                profileDOBTextView.setText(dob);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickEditPhone() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edittelephone, null);
//        final EditText etUsername = alertLayout.findViewById(R.id.edit_phone);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Phone Edit");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        Log.v("LOG", "inside dialog");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final EditText userPhone = alertLayout.findViewById(R.id.edit_phone);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String phone = userPhone.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("phone").setValue(phone);
                profilePhonenoTextView.setText(phone);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }




    public void navigateLogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}