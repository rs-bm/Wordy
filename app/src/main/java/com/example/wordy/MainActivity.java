package com.example.wordy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    GridLayout grid;
    EditText guessET;
    Button submitB;
    int guessCount;
    String answer;
    HashMap<Character, Integer> letterCounts; // The count of every letter in the answer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = findViewById(R.id.grid);
        guessET = findViewById(R.id.guessET);
        submitB = findViewById(R.id.submitB);
        submitB.setOnClickListener(submitListener);
        guessCount = 0;
        answer = "APPLE";
        letterCounts = new HashMap<>();
        for (char c : answer.toCharArray()) {
            if (letterCounts.containsKey(c)) {
                letterCounts.put(c, letterCounts.get(c) + 1);
            } else {
                letterCounts.put(c, 1);
            }
        }
    }

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String guess = guessET.getText().toString().toUpperCase();
            if (guess.length() == 5) {
                makeGuess(guess);
            } else {
                Toast.makeText(getApplicationContext(), "Your guess must be a 5-letter word.", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void makeGuess(String guess) {
        char c;
        TextView tv;
        boolean correct = true;
        // Copy letter counts to new hashmap for each guess as values are decremented when green or yellow is guessed
        HashMap<Character, Integer> hm = new HashMap<>();
        for (Character character : letterCounts.keySet()) {
            hm.put(character, letterCounts.get(character));
        }
        // Identify green and gray letters
        for (int i = 0; i < 5; i++) {
            c = guess.charAt(i);
            tv = (TextView) grid.getChildAt(guessCount*5+i);
            tv.setText(String.valueOf(c));
            if (answer.charAt(i) == c) {
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                hm.put(c, hm.get(c) - 1);
            } else {
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                correct = false;
            }
        }
        // Identify yellows second to ensure no letters are yellow that correctly match later in the same guess
        for (int i = 0; i < 5; i++) {
            c = guess.charAt(i);
            tv = (TextView) grid.getChildAt(guessCount*5+i);
            if (answer.charAt(i) != c && hm.containsKey(c) && hm.get(c) > 0) {
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));
                hm.put(c, hm.get(c) - 1);
            }
        }
        // If player guesses the answer
        if (correct == true) {
            Toast.makeText(this, "Correct! The word was: " + answer, Toast.LENGTH_SHORT).show();
            retry();
        }
        // If player runs out of guesses
        else if (guessCount == 5){
            Toast.makeText(this, "You're out of guesses! The word was: " + answer, Toast.LENGTH_SHORT).show();
            retry();
        }
        guessET.setText("");
        guessCount++;
    }
    private void retry() {
        guessET.setEnabled(false);
        submitB.setText("Retry");
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}