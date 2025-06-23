package com.example.ungdungnuoithuao;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.Word;

public class FlashcardActivity extends AppCompatActivity {
    private static final String TAG = "FlashcardActivity";
    private Spinner topicSpinner;
    private EditText searchEditText;
    private TextView flashcardText;
    private Button saveButton, nextButton, reviewButton;
    private FirebaseFirestore db;
    private final List<Word> words = new ArrayList<>();
    private final List<Word> savedWords = new ArrayList<>();
    private int currentIndex = -1;
    private boolean showingWord = true;
    private boolean reviewMode = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        Button backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topicSpinner = findViewById(R.id.topicSpinner);
        searchEditText = findViewById(R.id.searchEditText);
        flashcardText = findViewById(R.id.flashcardText);
        saveButton = findViewById(R.id.saveButton);
        nextButton = findViewById(R.id.nextButton);
        reviewButton = findViewById(R.id.reviewButton);
        db = FirebaseFirestore.getInstance();

        setupSpinner();
        loadSavedWords();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        flashcardText.setOnClickListener(v -> {
            if (currentIndex >= 0 && currentIndex < words.size()) {
                showingWord = !showingWord;
                flashcardText.setText(showingWord ? words.get(currentIndex).getWord() : words.get(currentIndex).getDef());
            }
        });

        nextButton.setOnClickListener(v -> showNextWord());
        saveButton.setOnClickListener(v -> saveWord());
        reviewButton.setOnClickListener(v -> {
            reviewMode = !reviewMode;
            words.clear();
            if (reviewMode) {
                words.addAll(savedWords);
                Collections.shuffle(words);
                currentIndex = -1;
                showNextWord();
                Toast.makeText(this, "Đã bật chế độ ôn tập", Toast.LENGTH_SHORT).show();
            } else {
                setupSpinner();
                flashcardText.setText("");
                Toast.makeText(this, "Đã quay lại chế độ bình thường", Toast.LENGTH_SHORT).show();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase(); // Chuẩn hóa chữ thường
                if (!query.isEmpty()) {
                    searchWords(query);
                } else {
                    String topic = topicSpinner.getSelectedItem().toString();
                    loadWords(topic);
                }
            }
        });
    }

    private void setupSpinner() {
        List<String> topics = new ArrayList<>();
        topics.add("Tất cả");
        topics.add("n.");
        topics.add("v.");
        topics.add("a.");
        topics.add("adv.");
        topics.add("prep.");
        topics.add("t.");
        topics.add("p.");
        topics.add("pl.");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, topics);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String topic = topics.get(position);
                if (searchEditText.getText().toString().trim().isEmpty()) {
                    loadWords(topic);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadWords(String topic) {
        words.clear();
        Query query = topic.equals("Tất cả") ?
                db.collection("dictionary") :
                db.collection("dictionary").whereEqualTo("pos", topic);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        if (word != null) {
                            words.add(word);
                            Log.d(TAG, "Loaded word: " + word.getWord());
                        }
                    }
                    Collections.shuffle(words);
                    currentIndex = -1;
                    showNextWord();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải từ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Load words failed: " + e.getMessage(), e);
                });
    }

    private void searchWords(String query) {
        words.clear();
        String topic = topicSpinner.getSelectedItem().toString();
        Query queryBase = topic.equals("Tất cả") ?
                db.collection("dictionary") :
                db.collection("dictionary").whereEqualTo("pos", topic);
        Log.d(TAG, "Searching for: " + query + " in topic: " + topic);
        queryBase.whereGreaterThanOrEqualTo("word", query)
                .whereLessThanOrEqualTo("word", query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Query returned " + queryDocumentSnapshots.size() + " documents");
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        if (word != null && word.getWord() != null) {
                            words.add(word);
                            Log.d(TAG, "Found word: " + word.getWord());
                        } else {
                            Log.w(TAG, "Failed to convert document or word is null: " + doc.getId());
                        }
                    }
                    if (words.isEmpty()) {
                        Toast.makeText(this, "Không tìm thấy từ!", Toast.LENGTH_SHORT).show();
                        flashcardText.setText("");
                    } else {
                        Collections.shuffle(words);
                        currentIndex = -1;
                        showNextWord();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Search failed: " + e.getMessage(), e);
                });
    }

    private void loadSavedWords() {
        db.collection("saved_flashcard")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedWords.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        if (word != null) {
                            savedWords.add(word);
                            Log.d(TAG, "Loaded saved word: " + word.getWord());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải từ đã lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Load saved words failed: " + e.getMessage(), e);
                });
    }

    private void saveWord() {
        if (currentIndex < 0 || currentIndex >= words.size()) {
            Toast.makeText(this, "Không có từ nào để lưu!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "currentIndex invalid: " + currentIndex);
            return;
        }
        Word word = words.get(currentIndex);
        if (word == null || word.getWord() == null || word.getWord().trim().isEmpty()) {
            Toast.makeText(this, "Từ không hợp lệ!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Word invalid: " + (word != null ? word.getWord() : "null"));
            return;
        }
        if (savedWords.contains(word)) {
            Toast.makeText(this, "Từ đã được lưu!", Toast.LENGTH_SHORT).show();
            return;
        }

        String docId = word.getWord().trim();
        db.collection("saved_flashcard").document(docId)
                .set(word)
                .addOnSuccessListener(aVoid -> {
                    savedWords.add(word);
                    Toast.makeText(this, "Đã lưu từ: " + docId, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Successfully saved word: " + docId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu từ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Save failed for word '" + docId + "': " + e.getMessage(), e);
                });
    }

    @SuppressLint("SetTextI18n")
    private void showNextWord() {
        if (!words.isEmpty()) {
            currentIndex = (currentIndex + 1) % words.size();
            showingWord = true;
            flashcardText.setText(words.get(currentIndex).getWord());
        } else {
            flashcardText.setText("Không có từ nào!");
        }
    }
}