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
    private static final int DB_VERSION = 26; // Tăng version khi thay đổi dữ liệu bảng Items

    private Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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
                "atk_skill_ids TEXT," +
                "def_skill_ids TEXT," +
                "buff_skill_ids TEXT)");

        // Tạo bảng AtkSkills (Skill Tấn công)
        db.execSQL("CREATE TABLE AtkSkills (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "imageSkill TEXT," +
                "type TEXT," +
                "baseAtk INTEGER," +
                "mana INTEGER," +
                "effect TEXT)");

        // Tạo bảng DefSkills (Skill Phòng thủ)
        db.execSQL("CREATE TABLE DefSkills (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "imageSkill TEXT," +
                "type TEXT," +
                "baseDef INTEGER," +
                "mana INTEGER," +
                "effect TEXT)");

        // Tạo bảng BuffSkills (Skill Buff)
        db.execSQL("CREATE TABLE BuffSkills (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "imageSkill TEXT," +
                "type TEXT," +
                "increase INTEGER," +
                "mana INTEGER," +
                "effect TEXT)");

        // Bảng Items
        db.execSQL("CREATE TABLE Items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "imageResId TEXT," +
                "type TEXT," + // Ví dụ: "skill_book", "exp_item"
                "value INTEGER," + // Giá trị hồi phục/tăng thêm/kinh nghiệm/ID skill
                "price INTEGER," +
                "description TEXT)");

        // Dữ liệu ban đầu cho Monsters
        db.execSQL("INSERT INTO Monsters (id, name, imageResId, hp, mana, attack, defense, atk_skill_ids, def_skill_ids, buff_skill_ids) " +
                "VALUES (1, 'Slime', 'slime', 80, 30, 10, 8, '2', '', '1')");
        db.execSQL("INSERT INTO Monsters (id, name, imageResId, hp, mana, attack, defense, atk_skill_ids, def_skill_ids, buff_skill_ids) " +
                "VALUES (2, 'Goblin', 'goblin', 100, 40, 15, 5, '1,3', '', '')");
        db.execSQL("INSERT INTO Monsters (id, name, imageResId, hp, mana, attack, defense, atk_skill_ids, def_skill_ids, buff_skill_ids) " +
                "VALUES (3, 'Dragon', 'dragon', 120, 50, 20, 15, '', '1', '')");

        // Dữ liệu ban đầu cho AtkSkills
        db.execSQL("INSERT INTO AtkSkills (id, name, imageSkill, type, baseAtk, mana, effect) " +
                "VALUES (1, 'Cắn', 'can', 'damage', 100, 5, 'none')");
        db.execSQL("INSERT INTO AtkSkills (id, name, imageSkill, type, baseAtk, mana, effect) " +
                "VALUES (2, 'Cào', 'cao', 'damage', 150, 8, 'none')");
        db.execSQL("INSERT INTO AtkSkills (id, name, imageSkill, type, baseAtk, mana, effect) " +
                "VALUES (3, 'Chí Mạng', 'chem', 'damage', 75, 15, 'crit')");

        // Dữ liệu ban đầu cho DefSkills
        db.execSQL("INSERT INTO DefSkills (id, name, imageSkill, type, baseDef, mana, effect) " +
                "VALUES (1, 'Phòng Thủ', 'shield', 'defense', 10, 5, 'none')");

        // Dữ liệu ban đầu cho BuffSkills
        db.execSQL("INSERT INTO BuffSkills (id, name, imageSkill, type, 'increase', mana, effect) " +
                "VALUES (1, 'Hồi Máu', 'heal', 'buff', 20, 7, 'heal')");
        db.execSQL("INSERT INTO BuffSkills (id, name, imageSkill, type, 'increase', mana, effect) " +
                "VALUES (2, 'Tăng Sức Mạnh', 'power_up', 'buff', 15, 10, 'buff_attack')");

        // --- Dữ liệu ban đầu cho Items (chỉ sách và đồ ăn tăng EXP) ---
        db.execSQL("INSERT INTO Items (id, name, imageResId, type, value, price, description) " +
                "VALUES (1, 'Sách Kinh Nghiệm Nhỏ', 'exp_book_small', 'exp_book_small', 50, 100, 'Tăng 50 điểm kinh nghiệm.')");
        db.execSQL("INSERT INTO Items (id, name, imageResId, type, value, price, description) " +
                "VALUES (2, 'Sách Kinh Nghiệm Lớn', 'exp_book_large', 'exp_book_large', 200, 350, 'Tăng 200 điểm kinh nghiệm.')");
        db.execSQL("INSERT INTO Items (id ,name, imageResId, type, value, price, description) " +
                "VALUES ( 3,'Sách Kĩ Năng Tấn Công I', 'atk_skill_book_1', 'atk_skill_book_1', 1, 200, 'Dạy kĩ năng Cắn.')"); // Value là ID của AtkSkill
        db.execSQL("INSERT INTO Items (id, name, imageResId, type, value, price, description) " +
                "VALUES (4, 'Sách Kĩ Năng Phòng Thủ I', 'def_skill_book_1', 'def_skill_book_1', 1, 180, 'Dạy kĩ năng Phòng Thủ.')"); // Value là ID của DefSkill
        db.execSQL("INSERT INTO Items (id, name, imageResId, type, value, price, description) " +
                "VALUES (5, 'Bánh Ngọt EXP', 'exp_cake', 'exp_cake', 30, 60, 'Món bánh ngọt giúp tăng 30 EXP.')");
        db.execSQL("INSERT INTO Items (id, name, imageResId, type, value, price, description) " +
                "VALUES (6, 'Thịt Nướng EXP', 'exp_meat', 'exp_meat', 70, 150, 'Món thịt nướng thơm ngon giúp tăng 70 EXP.')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Monsters");
        db.execSQL("DROP TABLE IF EXISTS AtkSkills");
        db.execSQL("DROP TABLE IF EXISTS DefSkills");
        db.execSQL("DROP TABLE IF EXISTS BuffSkills");
        db.execSQL("DROP TABLE IF EXISTS Items");
        onCreate(db);
    }

    public String getImagePathById(int monsterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String imagePath = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT imageResId FROM Monsters WHERE id = ?", new String[]{String.valueOf(monsterId)});
            if (cursor != null && cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting imagePathById: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return imagePath;
    }

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
                    monster.getSkills().addAll(getMonsterSkillsInternal(id, db));
                    monsters.add(monster);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting FirstNMonsters: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return monsters;
    }

    public void addMonster(Monster monster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", monster.id);
        values.put("name", monster.name);
        values.put("imageResId", monster.imageResId);
        values.put("hp", monster.hp);
        values.put("mana", monster.mana);
        values.put("attack", monster.attack);
        values.put("defense", monster.defense);
        db.insert("Monsters", null, values);
        db.close();
    }

    public List<SkillCard> getMonsterSkills(int monsterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<SkillCard> skills = getMonsterSkillsInternal(monsterId, db);
        db.close();
        return skills;
    }

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
            Log.e("DBHelper", "Error getting Monster skills " + monsterId + ": " + e.getMessage(), e);
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
                    Log.e("DBHelper", "Error parsing skill ID: " + id, e);
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
            Log.e("DBHelper", "Error getting AtkSkills: " + e.getMessage(), e);
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
            Log.e("DBHelper", "Error getting DefSkills: " + e.getMessage(), e);
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
            Log.e("DBHelper", "Error getting BuffSkills: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return buffSkills;
    }

    private int getResourceIdByName(String resourceName, String resourceType) {
        Context context = this.context;
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }

    // --- Các hàm cho Items ---

    public static class Item {
        public int id;
        public String name;
        public String imageResId;
        public String type;
        public int value;
        public int price;
        public String description;

        public Item(int id, String name, String imageResId, String type, int value, int price, String description) {
            this.id = id;
            this.name = name;
            this.imageResId = imageResId;
            this.type = type;
            this.value = value;
            this.price = price;
            this.description = description;
        }
    }

    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.name);
        values.put("imageResId", item.imageResId);
        values.put("type", item.type);
        values.put("value", item.value);
        values.put("price", item.price);
        values.put("description", item.description);
        db.insert("Items", null, values);
        db.close();
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Items", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageResId = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    int value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));
                    int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                    items.add(new Item(id, name, imageResId, type, value, price, description));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting all items: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return items;
    }

    public Item getItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Item item = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Items WHERE id = ?", new String[]{String.valueOf(itemId)});
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String imageResId = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                int value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                item = new Item(id, name, imageResId, type, value, price, description);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting item by ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return item;
    }


}
