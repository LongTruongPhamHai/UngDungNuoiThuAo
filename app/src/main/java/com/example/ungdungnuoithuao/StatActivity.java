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

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import model.Monster;

public class StatActivity extends AppCompatActivity {

    TextView txtHP, txtTen, txtMana, txtTanCong, txtPhongNgu, txtDiemTiemNang, txtLv; // Thêm txtLv
    ImageView imgMonster;
    Button btnCongHP, btnCongMana, btnCongAttack, btnCongDef; // Khai báo các nút cộng

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
            Log.e("MainChiSo", "Error: userId not received from previous Activity!");
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            finish(); // Dừng nếu không có userId
            return;
        } else {
            Log.d("MainChiSo", "Received userId: " + receivedUserId);
        }

        // Ánh xạ View
        txtTen = findViewById(R.id.thongtinMyMonsterName);
        txtLv = findViewById(R.id.txtMyMonsterLv); // Ánh xạ txtLv
        txtHP = findViewById(R.id.txtMyMonsterHP);
        txtMana = findViewById(R.id.txtMyMonsterMana);
        txtTanCong = findViewById(R.id.txtMyMonsterAttack);
        txtPhongNgu = findViewById(R.id.txtMyMonsterDef);
        txtDiemTiemNang = findViewById(R.id.txtDiemTiemNang);
        imgMonster = findViewById(R.id.imageView2);

        btnCongHP = findViewById(R.id.btnCongHP);
        btnCongMana = findViewById(R.id.btnMana); // Ánh xạ nút Mana
        btnCongAttack = findViewById(R.id.btnAttack); // Ánh xạ nút Attack
        btnCongDef = findViewById(R.id.btnDef); // Ánh xạ nút Def


        // --- Tải dữ liệu pet từ Firestore ---
        loadPetData(); // Gọi hàm tải dữ liệu pet của người chơi

        // --- Xử lý sự kiện click cho các nút cộng chỉ số ---

        // Nút Cộng HP
        btnCongHP.setOnClickListener(v -> {
            if (!isPetDataLoaded()) return; // Kiểm tra dữ liệu pet đã tải chưa

            if (potentialPoints > 0) {
                updatePetStat("hp", 5); // Tăng HP 5 điểm
            } else {
                Toast.makeText(this, "Không đủ điểm tiềm năng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Cộng Mana
        btnCongMana.setOnClickListener(v -> {
            if (!isPetDataLoaded()) return;

            if (potentialPoints > 0) {
                updatePetStat("mana", 5); // Tăng Mana 5 điểm
            } else {
                Toast.makeText(this, "Không đủ điểm tiềm năng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Cộng Tấn Công
        btnCongAttack.setOnClickListener(v -> {
            if (!isPetDataLoaded()) return;

            if (potentialPoints > 0) {
                updatePetStat("attack", 1); // Tăng Tấn Công 1 điểm
            } else {
                Toast.makeText(this, "Không đủ điểm tiềm năng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Cộng Phòng Thủ
        btnCongDef.setOnClickListener(v -> {
            if (!isPetDataLoaded()) return;

            if (potentialPoints > 0) {
                updatePetStat("def", 1); // Tăng Phòng Thủ 1 điểm
            } else {
                Toast.makeText(this, "Không đủ điểm tiềm năng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Master Button và Hoàn trả đã được gỡ bỏ theo yêu cầu.
    }

    // Hàm kiểm tra xem dữ liệu pet đã được tải và sẵn sàng chưa
    private boolean isPetDataLoaded() {
        if (myMonster == null || currentPetDocId == null || currentPetDocId.isEmpty()) {
            Toast.makeText(this, "Đang tải dữ liệu quái vật, vui lòng đợi!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Hàm dùng chung để cập nhật chỉ số của pet và điểm tiềm năng trên Firestore.
     *
     * @param statToUpdate Tên trường chỉ số cần cập nhật (ví dụ: "hp", "mana", "attack", "def").
     * @param valueToAdd   Giá trị muốn tăng thêm cho chỉ số đó.
     */
    private void updatePetStat(String statToUpdate, int valueToAdd) {
        potentialPoints--; // Luôn giảm điểm tiềm năng khi tăng bất kỳ chỉ số nào

        // Cập nhật chỉ số trong đối tượng Monster cục bộ
        switch (statToUpdate) {
            case "hp":
                myMonster.setHp(myMonster.getHp() + valueToAdd);
                txtHP.setText("HP: " + myMonster.getHp());
                break;
            case "mana":
                myMonster.setMana(myMonster.getMana() + valueToAdd);
                txtMana.setText("Mana: " + myMonster.getMana());
                break;
            case "attack":
                myMonster.setAttack(myMonster.getAttack() + valueToAdd);
                txtTanCong.setText("Tấn công: " + myMonster.getAttack());
                break;
            case "def":
                myMonster.setDefense(myMonster.getDefense() + valueToAdd);
                txtPhongNgu.setText("Phòng thủ: " + myMonster.getDefense());
                break;
        }
        txtDiemTiemNang.setText("Điểm tiềm năng: " + potentialPoints); // Cập nhật UI điểm tiềm năng

        // Chuẩn bị dữ liệu để cập nhật Firestore
        Map<String, Object> updatedStats = new HashMap<>();
        updatedStats.put(statToUpdate, FieldValue.increment(valueToAdd)); // Tăng chỉ số
        updatedStats.put("potential_point", FieldValue.increment(-1L)); // Giảm điểm tiềm năng

        db.collection("pet")
                .document(currentPetDocId)
                .update(updatedStats)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Cập nhật " + statToUpdate + " và tiềm năng thành công!"))
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi cập nhật " + statToUpdate + " và tiềm năng", e);
                    Toast.makeText(this, "Lỗi: Không thể cập nhật chỉ số!", Toast.LENGTH_SHORT).show();
                    // Nếu lỗi, có thể cần khôi phục lại trạng thái UI hoặc tải lại dữ liệu
                    loadPetData(); // Tải lại để đồng bộ lại trạng thái
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
                        String idMonsterStr = document.getString("id"); // Lấy id monster dưới dạng String
                        Integer hp = parseIntOrDefault(document.getString("hp"), 0);
                        Integer mana = parseIntOrDefault(document.getString("mana"), 0);
                        Integer attack = parseIntOrDefault(document.getString("attack"), 0);
                        Integer defense = parseIntOrDefault(document.getString("def"), 0);
                        Integer potential_point = parseIntOrDefault(document.getString("potential_point"), 0);
                        Integer level = parseIntOrDefault(document.getString("lv"), 2); // Lấy level pet, mặc định 1

                        // Khởi tạo myMonster với dữ liệu pet
                        // Đảm bảo id_monster là int khi tạo Monster
                        int id_monster = parseIntOrDefault(idMonsterStr, 0);
                        myMonster = new Monster(id_monster, petname, null, hp, mana, attack, defense);

                        potentialPoints = potential_point; // Cập nhật biến potentialPoints toàn cục

                        // Cập nhật UI
                        txtTen.setText("Tên: " + petname);
                        txtLv.setText("Lv: " + level); // Cập nhật level
                        txtHP.setText("HP: " + myMonster.getHp());
                        txtMana.setText("Mana: " + myMonster.getMana());
                        txtTanCong.setText("Tấn công: " + myMonster.getAttack());
                        txtPhongNgu.setText("Phòng thủ: " + myMonster.getDefense());
                        txtDiemTiemNang.setText("Điểm tiềm năng: " + potentialPoints);

                        // Load ảnh quái vật
                        DBHelper dbHelper = new DBHelper(this); // Khởi tạo lại nếu cần, hoặc dùng instance đã có
                        String imagePath = dbHelper.getImagePathById(id_monster); // Đảm bảo getImagePathById không đóng DB
                        if (imagePath != null && !imagePath.isEmpty()) {
                            int resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
                            if (resId != 0) {
                                imgMonster.setImageResource(resId);
                            } else {
                                Log.e("MainChiSo", "Không tìm thấy tài nguyên ảnh: " + imagePath);
                            }
                        } else {
                            Log.e("MainChiSo", "Đường dẫn ảnh trống hoặc null cho ID: " + id_monster);
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainChiSo", "Lỗi khi tải dữ liệu pet từ Firestore", e);
                    Toast.makeText(StatActivity.this, "Lỗi khi tải dữ liệu Pet: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Chỉ lưu khi có dữ liệu pet hợp lệ và đã có ID document của pet
        if (myMonster != null && currentPetDocId != null && !currentPetDocId.isEmpty()) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("petname", myMonster.getName());
            updatedData.put("hp", String.valueOf(myMonster.getHp()));
            updatedData.put("mana", String.valueOf(myMonster.getMana()));
            updatedData.put("attack", String.valueOf(myMonster.getAttack()));
            updatedData.put("def", String.valueOf(myMonster.getDefense()));
            updatedData.put("potential_point", String.valueOf(potentialPoints));
            // Các trường khác như exp, lv, skill_ids, v.v., sẽ được merge nếu không có trong updatedData.
            // Nếu bạn muốn đảm bảo chúng được lưu, bạn phải thêm chúng vào updatedData.

            db.collection("pet")
                    .document(currentPetDocId)
                    .set(updatedData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lưu dữ liệu Pet OnStop thành công!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lưu dữ liệu Pet OnStop", e));
        } else {
            Log.w("MainChiSo", "myMonster hoặc currentPetDocId là null khi onStop, không thể lưu dữ liệu Pet.");
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
            Log.e("MainChiSo", "Error parsing number: " + value, e);
            return defaultValue;
        }
    }

}