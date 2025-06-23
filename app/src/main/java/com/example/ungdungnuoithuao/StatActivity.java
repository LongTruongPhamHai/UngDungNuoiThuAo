package com.example.ungdungnuoithuao;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import model.Monster;

public class StatActivity extends AppCompatActivity {
    TextView txtHP, txtTen, txtMana, txtTanCong, txtPhongNgu, txtDiemTiemNang;
    ImageView imgMonster;
    Button btnCongHP, btnMasterButton;

    // Biến lưu trữ userId được truyền từ Activity trước
    private String receivedUserId;
    // Biến lưu trữ ID document thực sự của pet trên Firestore (ví dụ: 7ppUekX7egLVYrz09Ex1X)
    private String currentPetDocId;

    Monster myMonster; // Biến Monster để lưu trữ dữ liệu của quái vật hiện tại
    private int potentialPoints; // Điểm tiềm năng của pet

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Khởi tạo Firestore instance
        db = FirebaseFirestore.getInstance();

        // Lấy userId từ Intent
        receivedUserId = getIntent().getStringExtra("userId");
        if (receivedUserId == null || receivedUserId.isEmpty()) {
            Log.e("StatActivity", "Error: userId not received from previous Activity!");
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            // Có thể thêm finish() hoặc điều hướng về Activity trước đó
            return; // Dừng nếu không có userId
        } else {
            Log.d("StatActivity", "Received userId: " + receivedUserId);
        }


        // Ánh xạ View
        btnCongHP = findViewById(R.id.btnCongHP);
        btnMasterButton = findViewById(R.id.btnMasterButton);
        txtHP = findViewById(R.id.txtMyMonsterHP);
        txtTen = findViewById(R.id.thongtinMyMonsterName);
        txtMana = findViewById(R.id.txtMyMonsterMana);
        txtTanCong = findViewById(R.id.txtMyMonsterAttack);
        txtPhongNgu = findViewById(R.id.txtMyMonsterDef);
        txtDiemTiemNang = findViewById((R.id.txtDiemTiemNang));
        imgMonster = findViewById(R.id.imageView2);

        // --- Tải dữ liệu pet từ Firestore ---
        loadPetData(); // Gọi hàm tải dữ liệu pet của người chơi

        // --- Xử lý sự kiện click cho các nút ---
        btnCongHP.setOnClickListener(v -> {
            if (myMonster == null || currentPetDocId == null) {
                Toast.makeText(this, "Đang tải dữ liệu quái vật, vui lòng đợi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (potentialPoints > 0) {
                potentialPoints--; // Giảm điểm tiềm năng
                int newHP = myMonster.getHp() + 5; // Tăng HP
                myMonster.setHp(newHP); // Cập nhật HP trong đối tượng Monster

                // Cập nhật UI
                txtHP.setText("HP: " + myMonster.getHp());
                txtDiemTiemNang.setText("Điểm tiềm năng: " + potentialPoints);

                // Lưu vào Firestore
                Map<String, Object> updatedStats = new HashMap<>();
                updatedStats.put("hp", String.valueOf(myMonster.getHp())); // Lưu dưới dạng String nếu dữ liệu gốc là String
                updatedStats.put("potential_point", String.valueOf(potentialPoints)); // Lưu dưới dạng String

                db.collection("pet") // Cập nhật trên collection "pet"
                        .document(currentPetDocId) // Dùng ID document của pet
                        .update(updatedStats)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Cập nhật HP và tiềm năng thành công!"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi cập nhật HP và tiềm năng", e));

            } else {
                Toast.makeText(this, "Không đủ điểm tiềm năng!", Toast.LENGTH_SHORT).show();
            }
        });

        btnMasterButton.setOnClickListener(v -> {
            if (myMonster == null || currentPetDocId == null) {
                Toast.makeText(this, "Đang tải dữ liệu quái vật, vui lòng đợi!", Toast.LENGTH_SHORT).show();
                return;
            }

            potentialPoints++; // Tăng điểm tiềm năng
            txtDiemTiemNang.setText("Điểm tiềm năng: " + potentialPoints);

            // Lưu trữ tại Firebase
            Map<String, Object> updatedStats = new HashMap<>();
            updatedStats.put("potential_point", String.valueOf(potentialPoints)); // Lưu dưới dạng String

            db.collection("pet") // Cập nhật trên collection "pet"
                    .document(currentPetDocId) // Dùng ID document của pet
                    .update(updatedStats)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Cập nhật tiềm năng thành công!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi cập nhật tiềm năng", e));
        });
    }

    // Hàm mới để tải dữ liệu pet của người chơi từ Firestore
    private void loadPetData() {
        db.collection("pet")
                .whereEqualTo("userid", receivedUserId) // Tìm pet dựa vào userId
                .limit(1) // Lấy 1 pet duy nhất (giả định 1 user 1 pet chính)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        currentPetDocId = document.getId(); // LƯU ID DOCUMENT CỦA PET

                        String petname = document.getString("petname");

                        // Đọc các giá trị số từ Firestore. Chúng là String trong ảnh, nên phải parse.
                        Integer hp = parseIntOrDefault(document.getString("hp"), 0);
                        Integer mana = parseIntOrDefault(document.getString("mana"), 0);
                        Integer attack = parseIntOrDefault(document.getString("attack"), 0);
                        Integer defense = parseIntOrDefault(document.getString("def"), 0);
                        Integer id_monster = parseIntOrDefault(document.getString("id"), 0); // ID của monster cho ảnh
                        Integer potential_point = parseIntOrDefault(document.getString("potential_point"), 0);

                        // Khởi tạo myMonster với dữ liệu pet
                        myMonster = new Monster(id_monster, petname, null, hp, mana, attack, defense);

                        // Cập nhật biến potentialPoints toàn cục SAU KHI load từ Firestore
                        potentialPoints = potential_point;

                        // Cập nhật UI
                        txtTen.setText("Tên: " + petname);
                        txtHP.setText("HP: " + myMonster.getHp());
                        txtMana.setText("Mana: " + myMonster.getMana());
                        txtTanCong.setText("Tấn công: " + myMonster.getAttack());
                        txtPhongNgu.setText("Phòng thủ: " + myMonster.getDefense());
                        txtDiemTiemNang.setText("Điểm tiềm năng: " + potentialPoints);

                        // Load ảnh quái vật
                        DBHelper dbHelper = new DBHelper(this);
                        String imagePath = dbHelper.getImagePathById(id_monster);
                        if (imagePath != null && !imagePath.isEmpty()) {
                            int resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
                            if (resId != 0) {
                                imgMonster.setImageResource(resId);
                            } else {
                                Log.e("StatActivity", "Không tìm thấy tài nguyên ảnh: " + imagePath);
                            }
                        } else {
                            Log.e("StatActivity", "Đường dẫn ảnh trống hoặc null cho ID: " + id_monster);
                        }

                    } else {
                        Log.d("StatActivity", "Không tìm thấy tài liệu pet cho userId: " + receivedUserId);
                        Toast.makeText(StatActivity.this, "Không tìm thấy dữ liệu Pet của bạn!", Toast.LENGTH_LONG).show();
                        // Có thể set dữ liệu mặc định cho pet ở đây nếu không tìm thấy
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("StatActivity", "Lỗi khi tải dữ liệu pet từ Firestore", e);
                    Toast.makeText(StatActivity.this, "Lỗi khi tải dữ liệu Pet: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Chỉ lưu khi có dữ liệu pet hợp lệ và đã có ID document của pet
        if (myMonster != null && currentPetDocId != null && !currentPetDocId.isEmpty()) {
            Map<String, Object> updatedData = new HashMap<>();
            // Lưu ý: Đảm bảo các trường này khớp với tên trường trong Firestore và kiểu dữ liệu (String)
            updatedData.put("petname", myMonster.getName()); // Lưu tên pet
            updatedData.put("hp", String.valueOf(myMonster.getHp()));
            updatedData.put("mana", String.valueOf(myMonster.getMana()));
            updatedData.put("attack", String.valueOf(myMonster.getAttack()));
            updatedData.put("def", String.valueOf(myMonster.getDefense()));
            updatedData.put("potential_point", String.valueOf(potentialPoints));
            // Cần lưu thêm các trường khác nếu có (exp, lv, iqscore, physicalscore, spiritscore)
            // Nếu bạn không thay đổi chúng, setOptions.merge() sẽ giữ lại giá trị cũ.
            // updatedData.put("exp", String.valueOf(myMonster.getExp())); // Nếu Monster có getExp
            // updatedData.put("lv", String.valueOf(myMonster.getLevel())); // Nếu Monster có getLevel
            // ... và các trường khác tương tự

            db.collection("pet") // Cập nhật trên collection "pet"
                    .document(currentPetDocId) // Dùng ID document của pet
                    .set(updatedData, SetOptions.merge()) // set với merge để không ghi đè các trường khác
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lưu dữ liệu Pet OnStop thành công!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lưu dữ liệu Pet OnStop", e));
        } else {
            Log.w("StatActivity", "myMonster hoặc currentPetDocId là null khi onStop, không thể lưu dữ liệu Pet.");
        }
    }

    // Hàm hỗ trợ parse String sang Integer, trả về giá trị mặc định nếu lỗi
    private Integer parseIntOrDefault(String value, Integer defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e("StatActivity", "Error parsing number: " + value, e);
            return defaultValue;
        }
    }
}