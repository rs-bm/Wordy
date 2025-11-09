package com.example.wordy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    GridLayout grid;

    Button clearB, submitB, addB, restartB;
    int guessCount;
    String answer;
    HashMap<Character, Integer> letterCounts;
    List<String> words;
    private DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = findViewById(R.id.grid);
        clearB = findViewById(R.id.clearB);
        clearB.setOnClickListener(clearListener);
        submitB = findViewById(R.id.submitB);
        submitB.setOnClickListener(submitListener);
        addB = findViewById(R.id.addB);
        addB.setOnClickListener(addListener);
        restartB = findViewById(R.id.restartB);
        restartB.setOnClickListener(restartListener);

        guessCount = 0;
        letterCounts = new HashMap<>();
        words = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            grid.getChildAt(i).setEnabled(true);
        }
        dRef = FirebaseDatabase.getInstance().getReference("words");
        String defAns = "APPLE";
        dRef.child(defAns).setValue(defAns);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    words.add(ds.getValue().toString());
                }
                getNextAnswer();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et;
            // clear first row; make it editable
            for (int i = 0; i < 5; i++) {
                et = (EditText) grid.getChildAt(i);
                et.setText("");
                et.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                et.setEnabled(true);
            }
            // clear remaining rows; make them uneditable
            for (int i = 5; i < (guessCount + 1) * 5; i++) {
                et = (EditText) grid.getChildAt(i);
                et.setText("");
                et.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                et.setEnabled(false);
            }
            guessCount = 0;
        }
    };
    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et;
            // validate characters on input row
            for (int i = 0; i < 5; i++) {
                et = (EditText) grid.getChildAt(guessCount * 5 + i);
                if (!et.getText().toString().matches("^[a-zA-Z]$")) {
                    Toast.makeText(getApplicationContext(), "Your guess must be a 5-letter word.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            makeGuess();
        }
    };
    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), AddWordActivity.class));
        }
    };
    View.OnClickListener restartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    };
    private void getNextAnswer() {
        // get random selection from array of possible answers
        Random random = new Random();
        int index = random.nextInt(words.size());
        answer = words.get(index);
        // get counts of each letter in answer (for guess coloring)
        for (char c : answer.toCharArray()) {
            if (letterCounts.containsKey(c)) {
                letterCounts.put(c, letterCounts.get(c) + 1);
            } else {
                letterCounts.put(c, 1);
            }
        }
    }
    private void makeGuess() {
        char c;
        EditText et;
        boolean correct = true;
        // Copy letter counts to new hashmap for each guess as values are decremented when green or yellow is guessed
        HashMap<Character, Integer> hm = new HashMap<>();
        for (Character character : letterCounts.keySet()) {
            hm.put(character, letterCounts.get(character));
        }
        // Identify green and gray letters
        for (int i = 0; i < 5; i++) {
            et = (EditText) grid.getChildAt(guessCount * 5 + i);
            c = et.getText().toString().toUpperCase().charAt(0);
            if (answer.charAt(i) == c) {
                et.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                hm.put(c, hm.get(c) - 1);
            } else {
                et.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                correct = false;
            }
        }
        // Identify yellows second
        // This ensures no letters are yellow that correctly match later in the same guess
        for (int i = 0; i < 5; i++) {
            et = (EditText) grid.getChildAt(guessCount * 5 + i);
            c = et.getText().toString().toUpperCase().charAt(0);
            if (answer.charAt(i) != c && hm.containsKey(c) && hm.get(c) > 0) {
                et.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));
                hm.put(c, hm.get(c) - 1);
            }
        }
        // If player guesses the answer
        if (correct) {
            Toast.makeText(this, "Correct! The word was: " + answer, Toast.LENGTH_SHORT).show();
            submitB.setOnClickListener(null);
        }
        // If player runs out of guesses
        else if (guessCount == 5){
            Toast.makeText(this, "You're out of guesses! The word was: " + answer, Toast.LENGTH_SHORT).show();
            submitB.setOnClickListener(null);
        } else {
            goToNextRow();
        }
    }
    private void goToNextRow() {
        EditText et;
        for (int i = 0; i < 5; i++) {
            // lock guess row
            et = (EditText) grid.getChildAt(guessCount * 5 + i);
            et.setEnabled(false);
            // make next row editable
            et = (EditText) grid.getChildAt((guessCount + 1) * 5 + i);
            et.setEnabled(true);
        }
        guessCount++;
    }
}