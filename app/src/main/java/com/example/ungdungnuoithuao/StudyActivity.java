package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ungdungnuoithuao.data.CSVImporter;
import com.example.ungdungnuoithuao.helpers.NotificationHelper;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class StudyActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button toggleDNDButton, englishBtn, dictionaryBtn, testBtn, freeBtn, backBtn;
        toggleDNDButton = findViewById(R.id.toggleDNDButton);
        englishBtn = findViewById(R.id.english_btn);
        dictionaryBtn = findViewById(R.id.dictionary_btn);
        testBtn = findViewById(R.id.test_btn);
        freeBtn = findViewById(R.id.free_btn);
        backBtn = findViewById(R.id.back_btn);

        CSVImporter.importCSVToFirestore(this);

        toggleDNDButton.setOnClickListener(v -> NotificationHelper.toggleDoNotDisturb(this));

        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent tới Acitivity học tiếng Anh
                Intent toFlashcard = new Intent(StudyActivity.this, FlashcardActivity.class);
                toFlashcard.putExtra("userId", userId);
                startActivity(toFlashcard);
            }
        });

        dictionaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDictionary = new Intent(StudyActivity.this, DictionaryActivity.class);
                toDictionary.putExtra("userId", userId);
                startActivity(toDictionary);
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent tới Acitivity kiểm tra
                Intent toQuiz = new Intent(StudyActivity.this, QuizActivity.class);
                toQuiz.putExtra("userId", userId);
                startActivity(toQuiz);
            }
        });

        freeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelfStudy = new Intent(StudyActivity.this, SelfStudyActivity.class);
                toSelfStudy.putExtra("userId", userId);
                startActivity(toSelfStudy);
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}