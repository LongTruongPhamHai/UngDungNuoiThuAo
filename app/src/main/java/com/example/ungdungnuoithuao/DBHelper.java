package com.example.ungdungnuoithuao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.Monster;
import model.SkillCard;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "monster.db";
    private static final int DB_VERSION = 19; // Đảm bảo version khớp với phiên bản anh dùng

    private Context context; // Thêm biến context để lưu Context

    // Constructor: Nhận Context và lưu lại
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context; // Lưu Context được truyền vào
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Monsters
        db.execSQL("CREATE TABLE Monsters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "imageResId TEXT," +
                "hp INTEGER," +
                "mana INTEGER," +
                "attack INTEGER," +
                "defense INTEGER," +
                "atk_skill_ids TEXT," + // Thêm cột để lưu ID các skill tấn công của quái vật (dạng "1,2,3")
                "def_skill_ids TEXT," + // Thêm cột để lưu ID các skill phòng thủ của quái vật
                "buff_skill_ids TEXT)");// Thêm cột để lưu ID các skill buff của quái vật

        // Tạo bảng AtkSkills (Skill Tấn công)
        db.execSQL("CREATE TABLE AtkSkills (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + // Không AUTOINCREMENT để có thể đặt ID cố định
                "name TEXT," +
                "imageSkill TEXT," +
                "type TEXT," +
                "baseAtk INTEGER," +
                "mana INTEGER," +
                "effect TEXT)");

        // Tạo bảng DefSkills (Skill Phòng thủ)
        db.execSQL("CREATE TABLE DefSkills (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + // Không AUTOINCREMENT
                "name TEXT," +
                "imageSkill TEXT," +
                "type TEXT," +
                "baseDef INTEGER," +
                "mana INTEGER," +
                "effect TEXT)");

        // Tạo bảng BuffSkills (Skill Buff)
        db.execSQL("CREATE TABLE BuffSkills (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + // Không AUTOINCREMENT
                "name TEXT," +
                "imageSkill TEXT," +
                "type TEXT," +
                "increase INTEGER," +
                "mana INTEGER," +
                "effect TEXT)");



        db.execSQL("INSERT INTO Monsters (id, name, imageResId, hp, mana, attack, defense, atk_skill_ids, def_skill_ids, buff_skill_ids) " +
                "VALUES (1, 'Slime', 'slime', 80, 30, 10, 8, '2', '', '1')"); // Slime
        db.execSQL("INSERT INTO Monsters (id, name, imageResId, hp, mana, attack, defense, atk_skill_ids, def_skill_ids, buff_skill_ids) " +
                "VALUES (2, 'Goblin', 'goblin', 100, 40, 15, 5, '1,3', '', '')"); // Goblin
        db.execSQL("INSERT INTO Monsters (id, name, imageResId, hp, mana, attack, defense, atk_skill_ids, def_skill_ids, buff_skill_ids) " +
                "VALUES (3, 'Dragon', 'dragon', 120, 50, 20, 15, '', '1', '')"); // Dragon




        // Dữ liệu thẻ AtkSkills
        // Quan trọng: ID cần được định rõ nếu không AUTOINCREMENT. imageSkill là tên file ảnh không đuôi.
        db.execSQL("INSERT INTO AtkSkills (id, name, imageSkill, type, baseAtk, mana, effect) " +
                "VALUES (1, 'Cắn', 'can', 'damage', 100, 5, 'none')");
        db.execSQL("INSERT INTO AtkSkills (id, name, imageSkill, type, baseAtk, mana, effect) " +
                "VALUES (2, 'Cào', 'cao', 'damage', 150, 8, 'none')");
        db.execSQL("INSERT INTO AtkSkills (id, name, imageSkill, type, baseAtk, mana, effect) " +
                "VALUES (3, 'Chí Mạng', 'chem', 'damage', 75, 15, 'crit')");


        // Dữ liệu thẻ DefSkills
        db.execSQL("INSERT INTO DefSkills (id, name, imageSkill, type, baseDef, mana, effect) " +
                "VALUES (1, 'Phòng Thủ', 'shield', 'defense', 10, 5, 'none')");
//        db.execSQL("INSERT INTO DefSkills (id, name, imageSkill, type, baseDef, mana, effect) " +
//                "VALUES (2, 'Tăng Giáp', 'shieldboost', 'buff', 0, 10, 'buff_defense')");


        // Dữ liệu thẻ BuffSkills
        db.execSQL("INSERT INTO BuffSkills (id, name, imageSkill, type, 'increase', mana, effect) " +
                "VALUES (1, 'Hồi Máu', 'heal', 'buff', 20, 7, 'heal')");
        db.execSQL("INSERT INTO BuffSkills (id, name, imageSkill, type, 'increase', mana, effect) " +
                "VALUES (2, 'Tăng Sức Mạnh', 'power_up', 'buff', 15, 10, 'buff_attack')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Monsters");
        db.execSQL("DROP TABLE IF EXISTS AtkSkills");
        db.execSQL("DROP TABLE IF EXISTS DefSkills");
        db.execSQL("DROP TABLE IF EXISTS BuffSkills");
        onCreate(db);
    }

    // Hàm để lấy đường dẫn ảnh của quái vật theo ID
    public String getImagePathById(int monsterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String imagePath = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT imageResId FROM Monsters WHERE id = ?", new String[]{String.valueOf(monsterId)});
            if (cursor != null && cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
            } else {
                // Log.e("DBHelper", "Không tìm thấy imageResId cho monster_id: " + monsterId); // Bỏ log
            }
        } catch (Exception e) {
            // Log.e("DBHelper", "Lỗi khi lấy imagePathById: " + e.getMessage(), e); // Bỏ log
        } finally {
            if (cursor != null) cursor.close();
            db.close(); // Đóng DB ở đây vì đây là hàm lấy thông tin đơn lẻ
        }
        return imagePath;
    }

    // Hàm để lấy một danh sách N quái vật đầu tiên
    public List<Monster> getFirstNMonsters(int limit) {
        List<Monster> monsters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Monsters LIMIT ?", new String[]{String.valueOf(limit)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageResId = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
                    int hp = cursor.getInt(cursor.getColumnIndexOrThrow("hp"));
                    int mana = cursor.getInt(cursor.getColumnIndexOrThrow("mana"));
                    int attack = cursor.getInt(cursor.getColumnIndexOrThrow("attack"));
                    int defense = cursor.getInt(cursor.getColumnIndexOrThrow("defense"));

                    Monster monster = new Monster(id, name, imageResId, hp, mana, attack, defense);
                    // Lấy và thêm skill cho Monster này
                    // Quan trọng: KHÔNG ĐÓNG DB KẾT NỐI TRONG getMonsterSkills, NÓ SẼ ĐƯỢC ĐÓNG BỞI HÀM NÀY.
                    monster.getSkills().addAll(getMonsterSkillsInternal(id, db)); // Gọi hàm nội bộ, truyền db đã mở
                    monsters.add(monster);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            // Log.e("DBHelper", "Lỗi khi lấy FirstNMonsters: " + e.getMessage(), e); // Bỏ log
        } finally {
            if (cursor != null) cursor.close();
            db.close(); // Đóng DB ở đây sau khi hoàn tất các thao tác với cursor
        }
        return monsters;
    }

    // Hàm để thêm Monster
    public void addMonster(Monster monster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", monster.id); // Thêm ID vào nếu không AUTOINCREMENT
        values.put("name", monster.name);
        values.put("imageResId", monster.imageResId);
        values.put("hp", monster.hp);
        values.put("mana", monster.mana);
        values.put("attack", monster.attack);
        values.put("defense", monster.defense);
        // values.put("atk_skill_ids", "1,2,3"); // Anh có thể lưu ID skill ở đây khi thêm monster
        db.insert("Monsters", null, values);
        db.close();
    }


    // --- HÀM LẤY DANH SÁCH SKILL CỦA MỘT QUÁI VẬT TỪ DB ---
    // Hàm này sẽ được gọi từ bên ngoài (ví dụ MainChienDau1) hoặc từ các hàm nội bộ của DBHelper.
    // Đảm bảo nó sẽ mở và đóng DB.
    public List<SkillCard> getMonsterSkills(int monsterId) {
        SQLiteDatabase db = this.getReadableDatabase(); // Mở kết nối DB
        List<SkillCard> skills = getMonsterSkillsInternal(monsterId, db); // Gọi hàm nội bộ
        db.close(); // Đóng kết nối DB sau khi hoàn tất
        return skills;
    }

    // Hàm nội bộ để lấy danh sách skill của một quái vật, nhận một kết nối DB đã mở.
    // Dùng cho các hàm khác trong DBHelper cần truy vấn nhiều lần mà không muốn mở/đóng DB liên tục.
    private List<SkillCard> getMonsterSkillsInternal(int monsterId, SQLiteDatabase db) {
        List<SkillCard> skills = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("Monsters", new String[]{"atk_skill_ids", "def_skill_ids", "buff_skill_ids"},
                    "id = ?", new String[]{String.valueOf(monsterId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String atkSkillIdsStr = cursor.getString(cursor.getColumnIndexOrThrow("atk_skill_ids"));
                String defSkillIdsStr = cursor.getString(cursor.getColumnIndexOrThrow("def_skill_ids"));
                String buffSkillIdsStr = cursor.getString(cursor.getColumnIndexOrThrow("buff_skill_ids"));

                List<Integer> atkSkillIds = parseIdString(atkSkillIdsStr);
                List<Integer> defSkillIds = parseIdString(defSkillIdsStr);
                List<Integer> buffSkillIds = parseIdString(buffSkillIdsStr);

                skills.addAll(getAtkSkillsByIds(atkSkillIds, db));
                skills.addAll(getDefSkillsByIds(defSkillIds, db));
                skills.addAll(getBuffSkillsByIds(buffSkillIds, db));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi khi lấy skill của Monster " + monsterId + ": " + e.getMessage(), e); // Giữ log này để debug skill
        } finally {
            if (cursor != null) cursor.close();
        }
        return skills;
    }

    public List<Integer> parseIdString(String idString) {
        List<Integer> ids = new ArrayList<>();
        if (idString != null && !idString.isEmpty()) {
            String[] idArray = idString.split(",");
            for (String id : idArray) {
                try {
                    ids.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException e) {
                    // Log.e("DBHelper", "Lỗi parse ID skill: " + id, e); // Bỏ log
                }
            }
        }
        return ids;
    }

    public List<SkillCard> getAtkSkillsByIds(List<Integer> skillIds, SQLiteDatabase db) {
        List<SkillCard> atkSkills = new ArrayList<>();
        if (skillIds.isEmpty()) return atkSkills;

        String inClause = TextUtils.join(",", skillIds);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM AtkSkills WHERE id IN (" + inClause + ")", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageSkill = cursor.getString(cursor.getColumnIndexOrThrow("imageSkill"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    int baseAtk = cursor.getInt(cursor.getColumnIndexOrThrow("baseAtk"));
                    int mana = cursor.getInt(cursor.getColumnIndexOrThrow("mana"));
                    String effect = cursor.getString(cursor.getColumnIndexOrThrow("effect"));

                    int imageResId = getResourceIdByName(imageSkill, "drawable");
                    atkSkills.add(new SkillCard(name, type, effect, baseAtk, 0, imageResId, mana, 1));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi khi lấy AtkSkills: " + e.getMessage(), e); // Giữ log này để debug skill
        } finally {
            if (cursor != null) cursor.close();
        }
        return atkSkills;
    }

    public List<SkillCard> getDefSkillsByIds(List<Integer> skillIds, SQLiteDatabase db) {
        List<SkillCard> defSkills = new ArrayList<>();
        if (skillIds.isEmpty()) return defSkills;

        String inClause = TextUtils.join(",", skillIds);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM DefSkills WHERE id IN (" + inClause + ")", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageSkill = cursor.getString(cursor.getColumnIndexOrThrow("imageSkill"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    int baseDef = cursor.getInt(cursor.getColumnIndexOrThrow("baseDef"));
                    int mana = cursor.getInt(cursor.getColumnIndexOrThrow("mana"));
                    String effect = cursor.getString(cursor.getColumnIndexOrThrow("effect"));

                    int imageResId = getResourceIdByName(imageSkill, "drawable");
                    defSkills.add(new SkillCard(name, type, effect, 0, baseDef, imageResId, mana, 1));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi khi lấy DefSkills: " + e.getMessage(), e); // Giữ log này để debug skill
        } finally {
            if (cursor != null) cursor.close();
        }
        return defSkills;
    }

    public List<SkillCard> getBuffSkillsByIds(List<Integer> skillIds, SQLiteDatabase db) {
        List<SkillCard> buffSkills = new ArrayList<>();
        if (skillIds.isEmpty()) return buffSkills;

        String inClause = TextUtils.join(",", skillIds);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM BuffSkills WHERE id IN (" + inClause + ")", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageSkill = cursor.getString(cursor.getColumnIndexOrThrow("imageSkill"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    int increase = cursor.getInt(cursor.getColumnIndexOrThrow("increase"));
                    int mana = cursor.getInt(cursor.getColumnIndexOrThrow("mana"));
                    String effect = cursor.getString(cursor.getColumnIndexOrThrow("effect"));

                    int imageResId = getResourceIdByName(imageSkill, "drawable");
                    buffSkills.add(new SkillCard(name, type, effect, increase, 0, imageResId, mana, 1));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi khi lấy BuffSkills: " + e.getMessage(), e); // Giữ log này để debug skill
        } finally {
            if (cursor != null) cursor.close();
        }
        return buffSkills;
    }

    private int getResourceIdByName(String resourceName, String resourceType) {
        Context context = this.context;
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }
}
