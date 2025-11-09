package com.example.wordy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWordActivity extends AppCompatActivity {
    Button addB, cancelB;
    EditText addET;
    private DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        addB = findViewById(R.id.addB);
        addB.setOnClickListener(addListener);
        cancelB = findViewById(R.id.cancelB);
        cancelB.setOnClickListener(cancelListener);
        addET = findViewById(R.id.addET);
        dRef = FirebaseDatabase.getInstance().getReference("words");
    }
    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String word = addET.getText().toString().toUpperCase();
            if (!(word.length() == 5 && word.matches("^[A-Z]*$"))) {
                addET.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
                Toast.makeText(getApplicationContext(), "Add unsuccessful: you must enter a 5-letter word", Toast.LENGTH_SHORT).show();
            } else {
                dRef.child(word).setValue(word);
                addET.setText("");
                addET.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                Toast.makeText(getApplicationContext(), "Word registered", Toast.LENGTH_SHORT).show();
            }
        }
    };
    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    };
}