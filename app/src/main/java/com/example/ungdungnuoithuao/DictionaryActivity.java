package com.example.ungdungnuoithuao;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.WordAdapter;
import model.Word;

public class DictionaryActivity extends AppCompatActivity {
    private WordAdapter adapter;
    private final List<Word> words = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Button backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText searchBar = findViewById(R.id.searchBar);
        RecyclerView wordList = findViewById(R.id.wordList);
        wordList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(words);
        wordList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadWords("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Ẩn ActionBar
        }

//        searchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            @Override
//            public void afterTextChanged(Editable s) {
//                loadWords(s.toString());
//            }
//        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                loadWords(s.toString().trim().toLowerCase()); // chuyển thường nếu cần
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadWords(String query) {
        words.clear();
        db.collection("dictionary")
                .orderBy("word")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        words.add(word);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải từ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}