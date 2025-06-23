package com.example.ungdungnuoithuao;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView txtMyLv;
    private TextView txtMyTen; // Khai báo TextView cho tên (sẽ là tên pet)
    //    private final int[] imageArrayTam = {R.drawable.tam1, R.drawable.tam2, R.drawable.tam3,
//            R.drawable.tam4};
    private ImageButton backBtn;
    private final int[] imageArrayAn = {R.drawable.an1, R.drawable.an2,
            R.drawable.an3, R.drawable.an4};

    private final int[] imageArrayButton = {R.drawable.button1, R.drawable.button2, R.drawable.button3, R.drawable.button2, R.drawable.button1, R.drawable.button};
    private final int[] imageArrayBG = {
            R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5,
            R.drawable.bg6, R.drawable.bg7, R.drawable.bg8,
            R.drawable.bg9, R.drawable.bg10, R.drawable.bg11, R.drawable.bg12, R.drawable.bg13,
            R.drawable.bg14, R.drawable.bg15, R.drawable.bg16
    };

    private String receivedUserId;

    private final Handler handler1 = new Handler();
    private final Handler handler2 = new Handler();
    private Runnable runnable1;
    private Runnable runnable2;
    private ProgressBar thanhEXP;
    private int currentEXP = 0;
    private final int maxEXP = 100;
    private int lv = 1;
    boolean isActive = false;

    boolean isDaAnNut = false;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        db = FirebaseFirestore.getInstance();
        receivedUserId = getIntent().getStringExtra("userId");


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//            )};

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (receivedUserId == null || receivedUserId.isEmpty()) {
            Log.e("GameActivity", "Error: userId not received from HomeActivity!");
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng. Quay lại màn hình chính.", Toast.LENGTH_LONG).show();
            // finish();
        } else {
            Log.d("GameActivity", "Received userId from HomeActivity: " + receivedUserId);
            loadUserStats(receivedUserId); // Tải EXP và Level của người dùng
            loadPetName(receivedUserId); // Tải tên pet
        }

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtMyLv = findViewById(R.id.txtMyLv);
        txtMyTen = findViewById(R.id.txtMyTen); // Ánh xạ TextView cho tên pet
        thanhEXP = findViewById(R.id.thanhEXP);
        thanhEXP.setMax(maxEXP);

        ImageView imageViewBtnAn = findViewById(R.id.imageViewBtnAn);
        ImageView imageViewBtnShop = findViewById(R.id.imageViewBtnShop);
        ImageView imageViewBtnChienDau = findViewById(R.id.imageViewBtnChienDau);
        ImageView imageViewBtnChiSo = findViewById(R.id.imageViewBtnChiSo);

        imageView = findViewById(R.id.imageView);
        ImageView imageView2 = findViewById(R.id.imageView2);

        FrameLayout btnShop = findViewById(R.id.btnShop);
        FrameLayout btnAn = findViewById(R.id.btnAn);
        FrameLayout btnChienDau = findViewById(R.id.btnChienDau);
        FrameLayout btnChiSo = findViewById(R.id.btnChiSo);

        TextView btnAnText = findViewById((R.id.btnAnText));
        TextView btnShopText = findViewById((R.id.btnShopText));
        TextView btnChienDauText = findViewById((R.id.btnChienDauText));
        TextView btnChiSoText = findViewById((R.id.btnChiSoText));


        imageView.setImageResource(R.drawable.basic);
        startAnimation(imageView2, imageArrayBG, 200, handler2);

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActive) {
                    isDaAnNut = false;
                    GameActivity.this.startAnimationButton(imageViewBtnShop, imageArrayButton, 50, handler2, btnShopText);
                    isActive = true;

//                Intent intent = new Intent(v.getContext(), ShopActivity.class);
//                String userId = getIntent().getStringExtra("userId");
//                intent.putExtra("userId", userId);
//                startActivity(intent);
                }
            }
        });

        btnAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActive) {
                    isDaAnNut = false;
                    GameActivity.this.startAnimationButton(imageViewBtnAn, imageArrayButton, 50, handler2, btnAnText);
                    isActive = true;
                    GameActivity.this.startAnimation(imageView, imageArrayAn, 500, handler1);

                    handler1.postDelayed(() -> {
                        GameActivity.this.stopAnimation(imageView, handler1, runnable1);
                        currentEXP += 30;
                        GameActivity.this.checkEXP();
                    }, 2000);

                } else {
                    new AlertDialog.Builder(GameActivity.this)
                            .setTitle("Thông báo")
                            .setMessage("Đang bận làm việc khác, chờ chút điii?")
                            .setPositiveButton("OK", (dialog, which) -> Toast.makeText(GameActivity.this.getApplicationContext(), "Đồng ý nhé", Toast.LENGTH_SHORT).show())
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }
        });

        btnChienDau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.this.startAnimationButton(imageViewBtnChienDau, imageArrayButton, 100, handler2, btnChienDauText);

                new Handler().postDelayed(() -> {
                    if (receivedUserId == null || receivedUserId.isEmpty()) {
                        Toast.makeText(GameActivity.this, "Lỗi: Không có ID người dùng để chiến đấu!", Toast.LENGTH_SHORT).show();
                        Log.e("GameActivity", "UserId is null or empty when trying to start MapActivity.");
                    } else {
                        Intent myintent = new Intent(GameActivity.this, MapActivity.class);
                        myintent.putExtra("userId", receivedUserId);
                        Log.d("GameActivity", "Passing userId to MapActivity: " + receivedUserId);
                        startActivity(myintent);
                    }
                }, 100);
                isDaAnNut = false;
            }
        });


        btnChiSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.this.startAnimationButton(imageViewBtnChiSo, imageArrayButton, 100, handler2, btnChiSoText);
                new Handler().postDelayed(() -> {
                    Intent myintent = new Intent(GameActivity.this, StatActivity.class);
                    myintent.putExtra("userId", receivedUserId); // << Truyền userId sang StatActivity
                    Log.d("GameActivity", "Passing userId to StatActivity: " + receivedUserId); // Log
                    GameActivity.this.startActivity(myintent);
                }, 100);
                isDaAnNut = false;
            }
        });
    }

    private void startAnimation(final ImageView targetView, final int[] imageArray,
                                final long delayMs, final Handler handler) {
        runnable1 = new Runnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                targetView.setImageResource(imageArray[currentIndex]);
                currentIndex = (currentIndex + 1) % imageArray.length;
                handler.postDelayed(this, delayMs);
            }
        };
        handler.post(runnable1);
    }

    private void startAnimationButton(final ImageView targetView, final int[] imageArray,
                                      final long delayMs, final Handler handler, final TextView textView) {
        runnable2 = new Runnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                targetView.setImageResource(imageArray[currentIndex]);
                float translateY = 0f;
                switch (currentIndex) {
                    case 0:
                        translateY = 10f;
                        break;
                    case 1:
                        translateY = 20f;
                        break;
                    case 2:
                        translateY = 25f;
                        break;
                    case 3:
                        translateY = 20f;
                        break;
                    case 4:
                        translateY = 10f;
                        break;
                    case 5:
                        translateY = 0f;
                        break;
                    default:
                        translateY = 0f;
                        break;
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationY", textView.getTranslationY(), translateY);
                animator.setDuration(delayMs);
                animator.start();

                currentIndex = (currentIndex + 1) % imageArray.length;
                if (currentIndex == 0) {
                    handler.removeCallbacks(this);
                    isDaAnNut = true;
                } else {
                    handler.postDelayed(this, delayMs);
                }
            }
        };
        handler.post(runnable2);
    }


    private void stopAnimation(final ImageView targetView, Handler handler, Runnable runnable) {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            targetView.setImageResource(R.drawable.basic);
            isActive = false;
        }
    }

    private void checkEXP() {
        if (currentEXP >= maxEXP) {
            currentEXP = currentEXP % maxEXP;
            lv++;
            Toast.makeText(this, "Level Up! New Level: " + lv, Toast.LENGTH_SHORT).show();
        }
        txtMyLv.setText(String.valueOf(lv));
        thanhEXP.setProgress(currentEXP);

        if (receivedUserId != null && !receivedUserId.isEmpty()) {
            updateUserExpAndLevel(receivedUserId, currentEXP, lv);
        } else {
            Log.e("GameActivity", "Cannot save EXP/Level: userId is null or empty.");
        }
    }

    private void updateUserExpAndLevel(String userId, int exp, int level) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("exp", exp);
        updates.put("level", level);

        db.collection("user").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("GameActivity", "User EXP and Level updated successfully for userId: " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.e("GameActivity", "Error updating user EXP and Level for userId: " + userId, e);
                    Toast.makeText(GameActivity.this, "Lỗi cập nhật EXP và Level: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserStats(String userId) {
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long expLong = documentSnapshot.getLong("exp");
                        Long levelLong = documentSnapshot.getLong("level");

                        if (expLong != null) {
                            currentEXP = expLong.intValue();
                        } else {
                            currentEXP = 0;
                            Log.w("GameActivity", "EXP field is null in Firebase for user: " + userId);
                        }
                        if (levelLong != null) {
                            lv = levelLong.intValue();
                        } else {
                            lv = 1;
                            Log.w("GameActivity", "Level field is null in Firebase for user: " + userId);
                        }

                        txtMyLv.setText(String.valueOf(lv));
                        thanhEXP.setProgress(currentEXP);
                        Log.d("GameActivity", "Loaded User EXP: " + currentEXP + ", Level: " + lv);
                    } else {
                        Log.d("GameActivity", "User document not found for loading stats: " + userId);
                        Toast.makeText(GameActivity.this, "Không tìm thấy dữ liệu người dùng trên Firebase!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GameActivity", "Error loading user stats for userId: " + userId, e);
                    Toast.makeText(GameActivity.this, "Lỗi tải dữ liệu người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPetName(String userId) {
        db.collection("pet")
                .whereEqualTo("userid", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        String petname = document.getString("petname");

                        if (petname != null && !petname.isEmpty()) {
                            txtMyTen.setText(petname);
                            Log.d("GameActivity", "Loaded Petname: " + petname);
                        } else {
                            txtMyTen.setText("Pet của bạn");
                            Log.w("GameActivity", "Petname field is null or empty in Firebase for userId: " + userId);
                        }
                    } else {
                        txtMyTen.setText("Chưa có Pet");
                        Log.d("GameActivity", "No pet document found for userId: " + userId);
                        Toast.makeText(GameActivity.this, "Không tìm thấy dữ liệu Pet của bạn!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    txtMyTen.setText("Lỗi tải Pet");
                    Log.e("GameActivity", "Error loading petname for userId: " + userId, e);
                    Toast.makeText(GameActivity.this, "Lỗi tải tên Pet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
