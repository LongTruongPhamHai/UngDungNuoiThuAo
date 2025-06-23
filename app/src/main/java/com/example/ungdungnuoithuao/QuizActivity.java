package com.example.ungdungnuoithuao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.QuizQuestion;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private TextView questionText, scoreTv;
    private Button optionA, optionB, optionC, optionD, nextButton;
    private LinearLayout testLl;
    private FirebaseFirestore db;
    private List<QuizQuestion> questions = new ArrayList<>();
    private Set<String> shownQuestionIds = new HashSet<>();
    private QuizQuestion currentQuestion;
    private int currentIndex = -1, score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Button backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        questionText = findViewById(R.id.questionText);
        scoreTv = findViewById(R.id.score_tv);
        testLl = findViewById(R.id.test_ll);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        nextButton = findViewById(R.id.nextQuestionButton);

        db = FirebaseFirestore.getInstance();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loadQuestions();

        optionA.setOnClickListener(v ->
                checkAnswer(optionA.getText().toString())
        );
        optionB.setOnClickListener(v -> checkAnswer(optionB.getText().toString()));
        optionC.setOnClickListener(v -> checkAnswer(optionC.getText().toString()));
        optionD.setOnClickListener(v -> checkAnswer(optionD.getText().toString()));

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score += checkAnswer(optionA.getText().toString());
                loadNextQuestion();
            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score += checkAnswer(optionB.getText().toString());
                loadNextQuestion();
            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score += checkAnswer(optionC.getText().toString());
                loadNextQuestion();
            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score += checkAnswer(optionD.getText().toString());
                loadNextQuestion();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextQuestion();
                score += 0;
            }
        });


    }

    private void loadQuestions() {
        db.collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    questions.clear();
                    Log.d(TAG, "Loaded " + queryDocumentSnapshots.size() + " questions");
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        QuizQuestion question = doc.toObject(QuizQuestion.class);
                        if (question != null) {
                            question.setId(doc.getId()); // Sử dụng document ID
                            questions.add(question);
                            Log.d(TAG, "Question loaded: " + question.getTextQuestion());
                        } else {
                            Log.w(TAG, "Failed to convert document: " + doc.getId());
                        }
                    }
                    if (!questions.isEmpty()) {
                        loadNextQuestion();
                    } else {
                        questionText.setText("Không có câu hỏi!");
                        testLl.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading questions: " + e.getMessage(), e);
                    Toast.makeText(this, "Lỗi tải câu hỏi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void loadNextQuestion() {
        scoreTv.setText(String.valueOf(score) + "/10");
        if (!questions.isEmpty() && shownQuestionIds.size() < questions.size()) {
            do {
                currentIndex = (currentIndex + 1) % questions.size();
                currentQuestion = questions.get(currentIndex);
            } while (shownQuestionIds.contains(currentQuestion.getId()));
            shownQuestionIds.add(currentQuestion.getId());

            questionText.setText(currentQuestion.getTextQuestion());

            List<String> options = new ArrayList<>();
            options.add(currentQuestion.getOptionA());
            options.add(currentQuestion.getOptionB());
            options.add(currentQuestion.getOptionC());
            options.add(currentQuestion.getOptionD());
            Collections.shuffle(options);

            optionA.setText(options.get(0));
            optionB.setText(options.get(1));
            optionC.setText(options.get(2));
            optionD.setText(options.get(3));
        } else {
            questionText.setText("Hết câu hỏi!");
            testLl.setVisibility(View.GONE);
        }
    }

    private int checkAnswer(String selected) {
        int res = 0;
        if (currentQuestion != null && selected.equals(currentQuestion.getCorrectAnswer())) {
            res = 1;
            Toast.makeText(this, "Đúng!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai. Đáp án đúng là: " + (currentQuestion != null ? currentQuestion.getCorrectAnswer() : "N/A"), Toast.LENGTH_LONG).show();
        }
        return res;
    }
}