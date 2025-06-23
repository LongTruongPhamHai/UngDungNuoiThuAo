package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
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

import java.util.List;

import adapter.SkillCardAdapter;
import model.BattleManager;
import model.Monster;
import model.SkillCard;

public class FightActivity extends AppCompatActivity implements BattleManager.BattleListener {

    private static final String TAG = "FightActivity";

    // --- KHAI BÁO CÁC BIẾN VIEW (ĐỐI TƯỢNG GIAO DIỆN) ---
    private FrameLayout flPlayerMonsterArea;
    private ImageView ivMyMonsterImage;
    private TextView tvMyMonsterName;

    private FrameLayout flEnemyMonsterArea;
    private ImageView ivMonsterImage;
    private TextView tvMonsterName;
    private ImageView ivEnemyMonsterStunned;

    private TextView tvDetailName;
    private TextView tvDetailHP;
    private TextView tvDetailMana;
    private TextView tvDetailAttack;
    private TextView tvDetailDefense;

    // --- KHAI BÁO CÁC BIẾN LIÊN QUAN ĐẾN NHẬT KÝ TRẬN ĐẤU VÀ THẺ BÀI ---
    private TextView tvBattleLog;
    private ScrollView svBattleLog;
    private Button btnEndTurn;
    private RecyclerView rvPlayerSkillCards;
    private SkillCardAdapter skillCardAdapter;

    // --- KHAI BÁO CÁC BIẾN DỮ LIỆU GAME ---
    private Monster playerMonster;
    private Monster enemyMonster;
    private BattleManager battleManager;

    // Biến cho Firebase Firestore
    private FirebaseFirestore db;
    private String playerUserId; // Biến để lưu userId của người chơi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fight);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- ÁNH XẠ CÁC VIEW TỪ LAYOUT XML VÀO BIẾN JAVA ---
        flPlayerMonsterArea = findViewById(R.id.vitriMyMonster);
        ivMyMonsterImage = findViewById(R.id.ivMyMonsterImage);
        tvMyMonsterName = findViewById(R.id.txtMyMonsterName);

        flEnemyMonsterArea = findViewById(R.id.vitriMonster);
        ivMonsterImage = findViewById(R.id.ivMonsterImage);
        tvMonsterName = findViewById(R.id.txtMonsterName);

        tvDetailName = findViewById(R.id.thongtinMonsterName);
        tvDetailHP = findViewById(R.id.txtMonsterHP);
        tvDetailMana = findViewById(R.id.txtMonsterMana);
        tvDetailAttack = findViewById(R.id.txtMonsterAttack);
        tvDetailDefense = findViewById(R.id.txtMonsterDef);

        tvBattleLog = findViewById(R.id.tv_battle_log);
        svBattleLog = findViewById(R.id.sv_battle_log);
        btnEndTurn = findViewById(R.id.btn_end_turn);
        rvPlayerSkillCards = findViewById(R.id.rv_player_skill_cards);

        // --- KHỞI TẠO CÁC ĐỐI TƯỢNG HỖ TRỢ ---
        DBHelper dbHelper = new DBHelper(this);
        db = FirebaseFirestore.getInstance();

        // Lấy userId từ Intent (bây giờ đến từ MainChienDau)
        playerUserId = getIntent().getStringExtra("userId");
        if (playerUserId == null || playerUserId.isEmpty()) {
            Log.e(TAG, "Không nhận được userId từ Intent! Kiểm tra MainChienDau.");
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng. Không thể tải dữ liệu quái vật.", Toast.LENGTH_LONG).show();
            playerUserId = "defaultUserId"; // Đặt ID mặc định để tránh lỗi null pointer, nhưng dữ liệu sẽ không đúng
        } else {
            Log.d(TAG, "FightActivity đã nhận userId: " + playerUserId); // LOG XÁC NHẬN
        }

        // --- BẮT ĐẦU TẢI DỮ LIỆU QUÁI VẬT VÀ HIỂN THỊ LÊN UI ---
        loadAndDisplayMonsters(dbHelper);

        btnEndTurn.setOnClickListener(v -> {
            if (battleManager != null) {
                battleManager.nextTurn();
            } else {
                Toast.makeText(this, "Trận đấu chưa bắt đầu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAndDisplayMonsters(DBHelper dbHelper) {
        db.collection("pet")
                .whereEqualTo("userid", playerUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                        String ten = document.getString("petname");
                        Integer hp = tryParseInt(document.getString("hp"));
                        Integer mana = tryParseInt(document.getString("mana"));
                        Integer attack = tryParseInt(document.getString("attack"));
                        Integer defense = tryParseInt(document.getString("def"));
                        Integer id_monster = tryParseInt(document.getString("id"));

                        String imagePath = dbHelper.getImagePathById(id_monster);

                        String atkSkillIdsStrFromFirebase = document.getString("atk_skill_ids");
                        String defSkillIdsStrFromFirebase = document.getString("def_skill_ids");
                        String buffSkillIdsStrFromFirebase = document.getString("buff_skill_ids");

                        List<Integer> atkSkillIds = dbHelper.parseIdString(atkSkillIdsStrFromFirebase);
                        List<Integer> defSkillIds = dbHelper.parseIdString(defSkillIdsStrFromFirebase);
                        List<Integer> buffSkillIds = dbHelper.parseIdString(buffSkillIdsStrFromFirebase);

                        playerMonster = new Monster(id_monster, ten, imagePath, hp, mana, attack, defense);

                        SQLiteDatabase readableDb = dbHelper.getReadableDatabase();
                        try {
                            playerMonster.getSkills().addAll(dbHelper.getAtkSkillsByIds(atkSkillIds, readableDb));
                            playerMonster.getSkills().addAll(dbHelper.getDefSkillsByIds(defSkillIds, readableDb));
                            playerMonster.getSkills().addAll(dbHelper.getBuffSkillsByIds(buffSkillIds, readableDb));
                        } finally {
                            if (readableDb != null && readableDb.isOpen()) {
                                readableDb.close();
                            }
                        }

                        updateMonsterDisplay(playerMonster, true);
                        updateDetailUI(playerMonster);

                        List<Monster> allMonstersFromDB = dbHelper.getFirstNMonsters(1);
                        if (!allMonstersFromDB.isEmpty()) {
                            enemyMonster = allMonstersFromDB.get(0);
                            enemyMonster.getSkills().addAll(dbHelper.getMonsterSkills(enemyMonster.getIdMonster()));
                            updateMonsterDisplay(enemyMonster, false);
                            flEnemyMonsterArea.setOnClickListener(v -> {
                                updateDetailUI(enemyMonster);
                                Toast.makeText(FightActivity.this, "Thông tin của địch: " + enemyMonster.getName(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Log.w(TAG, "Không tìm thấy quái vật địch trong DB. Thiết lập mặc định.");
                            enemyMonster = new Monster(999, "Kẻ địch", "default_enemy_placeholder", 80, 40, 12, 8);
                            updateMonsterDisplay(enemyMonster, false);
                            flEnemyMonsterArea.setOnClickListener(v -> {
                                updateDetailUI(enemyMonster);
                                Toast.makeText(FightActivity.this, "Thông tin của địch: " + enemyMonster.getName(), Toast.LENGTH_SHORT).show();
                            });
                        }

                        flPlayerMonsterArea.setOnClickListener(v -> {
                            if (playerMonster != null) {
                                updateDetailUI(playerMonster);
                                Toast.makeText(this, "Thông tin của bạn: " + playerMonster.getName(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Chưa có thông tin quái vật của bạn.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        rvPlayerSkillCards.setLayoutManager(new LinearLayoutManager(FightActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        skillCardAdapter = new SkillCardAdapter(playerMonster.getSkills(), skillCard -> {
                            if (battleManager != null && battleManager.isPlayerTurn() && !playerMonster.isStunned()) {
                                battleManager.playerUseSkill(skillCard);
                            } else if (playerMonster.isStunned()) {
                                Toast.makeText(FightActivity.this, "Quái vật của bạn đang bị choáng!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FightActivity.this, "Chưa đến lượt của bạn.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        rvPlayerSkillCards.setAdapter(skillCardAdapter);

                        tvBattleLog.setText("Sẵn sàng chiến đấu!\n");
                        svBattleLog.post(() -> svBattleLog.fullScroll(View.FOCUS_DOWN));

                        if (playerMonster != null && enemyMonster != null) {
                            battleManager = new BattleManager(playerMonster, enemyMonster, FightActivity.this);
                        }

                    } else {
                        Log.d(TAG, "Không tìm thấy tài liệu pet cho userId: " + playerUserId);
                        Toast.makeText(FightActivity.this, "Không tìm thấy dữ liệu pet của bạn trong Firebase!", Toast.LENGTH_LONG).show();
                        setupDefaultMonsters(dbHelper);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tải dữ liệu pet từ Firebase: " + e.getMessage(), e);
                    Toast.makeText(FightActivity.this, "Lỗi khi tải dữ liệu pet Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    setupDefaultMonsters(dbHelper);
                });
    }

    private void setupDefaultMonsters(DBHelper dbHelper) {
        Log.d(TAG, "Thiết lập quái vật mặc định.");
        playerMonster = new Monster(1, "Quái của Bạn", "default_player_placeholder", 100, 50, 15, 10);
        playerMonster.getSkills().addAll(dbHelper.getMonsterSkills(playerMonster.getIdMonster()));
        updateMonsterDisplay(playerMonster, true);
        updateDetailUI(playerMonster);

        List<Monster> allMonstersFromDB = dbHelper.getFirstNMonsters(1);
        if (!allMonstersFromDB.isEmpty()) {
            enemyMonster = allMonstersFromDB.get(0);
            enemyMonster.getSkills().addAll(dbHelper.getMonsterSkills(enemyMonster.getIdMonster()));
        } else {
            enemyMonster = new Monster(999, "Kẻ địch", "default_enemy_placeholder", 80, 40, 12, 8);
            Log.w(TAG, "Thêm quái vật địch mặc định trong setupDefaultMonsters.");
        }

        updateMonsterDisplay(enemyMonster, false);
        flEnemyMonsterArea.setOnClickListener(v -> {
            updateDetailUI(enemyMonster);
            Toast.makeText(FightActivity.this, "Thông tin của địch: " + enemyMonster.getName(), Toast.LENGTH_SHORT).show();
        });

        flPlayerMonsterArea.setOnClickListener(v -> {
            if (playerMonster != null) {
                updateDetailUI(playerMonster);
                Toast.makeText(this, "Thông tin của bạn: " + playerMonster.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        rvPlayerSkillCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        skillCardAdapter = new SkillCardAdapter(playerMonster.getSkills(), skillCard -> {
            if (battleManager != null && battleManager.isPlayerTurn() && !playerMonster.isStunned()) {
                battleManager.playerUseSkill(skillCard);
            } else if (playerMonster.isStunned()) {
                Toast.makeText(FightActivity.this, "Quái vật của bạn đang bị choáng!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FightActivity.this, "Chưa đến lượt của bạn.", Toast.LENGTH_SHORT).show();
            }
        });
        rvPlayerSkillCards.setAdapter(skillCardAdapter);
        btnEndTurn.setOnClickListener(v -> {
            Toast.makeText(this, "Nút kết thúc lượt được bấm (mặc định)!", Toast.LENGTH_SHORT).show();
        });
        tvBattleLog.setText("Sẵn sàng chiến đấu (mặc định)!\n");
        svBattleLog.post(() -> svBattleLog.fullScroll(View.FOCUS_DOWN));

        // Khởi tạo BattleManager sau khi đã tải xong quái vật
        if (playerMonster != null && enemyMonster != null) {
            battleManager = new BattleManager(playerMonster, enemyMonster, FightActivity.this);
        }
    }

    private void updateMonsterDisplay(Monster monster, boolean isPlayer) {
        ImageView imageView;
        TextView nameTextView;
        FrameLayout frameLayout;

        if (isPlayer) {
            frameLayout = flPlayerMonsterArea;
            imageView = ivMyMonsterImage;
            nameTextView = tvMyMonsterName;
            imageView.setScaleX(-1f);
        } else {
            frameLayout = flEnemyMonsterArea;
            imageView = ivMonsterImage;
            nameTextView = tvMonsterName;
            imageView.setScaleX(1f);
        }

        frameLayout.setVisibility(View.VISIBLE);
        nameTextView.setText(monster.getName());

        String imagePath = monster.imageResId;
        int resId = 0;
        if (imagePath != null && !imagePath.isEmpty()) {
            resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
        }
        if (resId != 0) {
            imageView.setImageResource(resId);
        } else {
            Log.w(TAG, "Không tìm thấy tài nguyên ảnh cho: " + imagePath + ". Sử dụng ảnh mặc định nếu có.");
        }
    }

    private void updateDetailUI(Monster monster) {
        if (monster != null) {
            tvDetailName.setText("Tên: " + monster.getName());
            tvDetailHP.setText("HP: " + monster.getHp());
            tvDetailMana.setText("Mana: " + monster.getMana());
            tvDetailAttack.setText("Tấn công: " + monster.getAttack());
            tvDetailDefense.setText("Phòng thủ: " + monster.getDefense());
        }
    }

    @Override
    public void onTurnStart(boolean isPlayerTurn) {
        String turnMessage = isPlayerTurn ? "Lượt của bạn!" : "Lượt của đối thủ!";
        appendToBattleLog(turnMessage);
        btnEndTurn.setEnabled(isPlayerTurn); // Chỉ cho phép bấm End Turn khi đến lượt người chơi
    }

    @Override
    public void onMonsterHpChange(Monster monster) {

    }

    @Override
    public void onMonsterManaChange(Monster monster) {
        updateDetailUI(monster);
    }

    @Override
    public void onBattleEnd(boolean playerWon) {


        String endMessage = playerWon ? "Bạn đã chiến thắng!" : "Bạn đã thua!";
        appendToBattleLog("--- Trận đấu kết thúc ---");
        appendToBattleLog(endMessage);
        btnEndTurn.setEnabled(false); // Vô hiệu hóa nút End Turn khi trận đấu kết thúc
        Toast.makeText(this, endMessage, Toast.LENGTH_LONG).show();
        // Có thể thêm logic để quay về màn hình trước hoặc hiển thị phần thưởng
        Intent resultIntent = new Intent(this, com.example.ungdungnuoithuao.BattleResultActivity.class);
        resultIntent.putExtra("isWin", playerWon ? 1 : 0);
        resultIntent.putExtra("userId", playerUserId); // Thêm dòng này để truyền userID

        startActivity(resultIntent);
        finish(); // Kết thúc Activity chiến đấu
    }

//    @Override
//    public void onPlayerCardsUpdated(List<SkillCard> cards) {
//
//    }

    @Override
    public void onPlayerCardsUpdated(List<SkillCard> cards) {
        if (skillCardAdapter != null) {
            skillCardAdapter.updateCards(cards);
            skillCardAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLogMessage(String message) {
        appendToBattleLog(message);
    }

    private void appendToBattleLog(String message) {
        tvBattleLog.append(message + "\n");
        svBattleLog.post(() -> svBattleLog.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onMonsterStunnedStatusChange(Monster monster, boolean isStunned) {
        // No stun icon in this layout
    }

    private int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Không thể parse giá trị thành số nguyên: " + value, e);
            return 0; // Hoặc một giá trị mặc định khác mà bạn muốn
        }
    }
}