package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import adapter.InventoryAdapter;
import model.ShopItem;

public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnUseClickListener {

    private static final String TAG = "InventoryActivity";

    private RecyclerView rvInventoryItems;
    private InventoryAdapter inventoryAdapter;
    private List<ShopItem> displayInventoryItems;
    private DBHelper dbHelper; // Đối tượng DBHelper

    private FirebaseFirestore db;
    private String playerUserId;

    public static final int REQUEST_CODE_USE_ITEM = 1001;
    public static final String EXTRA_USED_ITEM_TYPE = "used_item_type";
    public static final String EXTRA_USED_ITEM_VALUE = "used_item_value";
    public static final String EXTRA_USED_ITEM_NAME = "used_item_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvInventoryItems = findViewById(R.id.rv_iventory_items);

        dbHelper = new DBHelper(this); // Khởi tạo DBHelper
        db = FirebaseFirestore.getInstance();

        playerUserId = getIntent().getStringExtra("userId");
        if (playerUserId == null || playerUserId.isEmpty()) {
            Log.e(TAG, "Không nhận được userId từ Intent. InventoryActivity cần userId.");
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayInventoryItems = new ArrayList<>();
        rvInventoryItems.setLayoutManager(new GridLayoutManager(this, 1));

        inventoryAdapter = new InventoryAdapter(displayInventoryItems, this);
        rvInventoryItems.setAdapter(inventoryAdapter);

        loadInventoryItems();
    }

    /**
     * Tải các vật phẩm từ kho đồ của người chơi từ Firebase Firestore.
     * Tự động mở và đóng kết nối SQLite cho mỗi truy vấn.
     */
    private void loadInventoryItems() {
        db.collection("inventory").document(playerUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    displayInventoryItems.clear(); // Xóa danh sách cũ

                    if (documentSnapshot.exists()) {
                        Map<String, Object> itemsData = documentSnapshot.getData();

                        if (itemsData != null && !itemsData.isEmpty()) {
                            for (Map.Entry<String, Object> entry : itemsData.entrySet()) {
                                String firebaseFieldName = entry.getKey(); // Ví dụ: "exp_book_small"
                                Object quantityValue = entry.getValue();

                                if (firebaseFieldName.equals("userId")) {
                                    continue;
                                }

                                int quantity = 0;
                                if (quantityValue instanceof Number) {
                                    quantity = ((Number) quantityValue).intValue();
                                } else {
                                    Log.w(TAG, "Giá trị số lượng không phải là Number cho item: " + firebaseFieldName + ". Giá trị: " + quantityValue);
                                }

                                if (quantity > 0) {
                                    // BẮT ĐẦU: TỰ MỞ KẾT NỐI DB VÀ TRUY VẤN TRỰC TIẾP TẠI ĐÂY
                                    DBHelper.Item rawItem = null;
                                    SQLiteDatabase localDb = null;
                                    Cursor cursor = null;
                                    try {
                                        localDb = dbHelper.getReadableDatabase(); // Mở DB
                                        cursor = localDb.query(
                                                "Items", // Tên bảng
                                                new String[]{"id", "name", "description", "price", "imageResId", "type", "value"},
                                                "imageResId = ?", // Điều kiện WHERE
                                                new String[]{firebaseFieldName}, // Giá trị cho WHERE
                                                null, null, null);

                                        if (cursor != null && cursor.moveToFirst()) {
                                            // Lấy dữ liệu từ Cursor
                                            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                                            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                                            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                                            int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                                            String imageResIdStr = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
                                            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                                            int value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));

                                            rawItem = new DBHelper.Item(id, name, imageResIdStr, type, value, price, description);
                                        } else {
                                            Log.d(TAG, "Không tìm thấy item trong SQLite với imageResId: " + firebaseFieldName);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Lỗi khi truy vấn SQLite cho item " + firebaseFieldName + ": " + e.getMessage(), e);
                                    } finally {
                                        if (cursor != null) cursor.close();
                                        if (localDb != null && localDb.isOpen()) {
                                            localDb.close(); // Đóng DB sau mỗi truy vấn
                                        }
                                    }
                                    // KẾT THÚC: TỰ MỞ/ĐÓNG DB

                                    if (rawItem != null) {
                                        int imageResId = getResources().getIdentifier(rawItem.imageResId, "drawable", getPackageName());
                                        if (imageResId == 0) {
                                            Log.w(TAG, "Không tìm thấy tài nguyên ảnh cho: " + rawItem.imageResId + ". Đảm bảo file ảnh tồn tại trong res/drawable.");
                                        }

                                        ShopItem shopItem = new ShopItem(
                                                String.valueOf(rawItem.id),
                                                rawItem.name,
                                                rawItem.description,
                                                rawItem.price,
                                                imageResId,
                                                quantity,
                                                rawItem.type,
                                                rawItem.value
                                        );
                                        displayInventoryItems.add(shopItem);
                                        Log.d(TAG, "Đã thêm item vào kho đồ: " + shopItem.getName() + ", Số lượng: " + shopItem.getQuantity() + ", Firebase Field: " + firebaseFieldName);
                                    } else {
                                        Log.w(TAG, "Không thể tạo ShopItem vì không tìm thấy dữ liệu trong SQLite cho tên trường Firebase: " + firebaseFieldName);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Tài liệu kho đồ cho userId: " + playerUserId + " rỗng (không có item nào).");
                        }
                    } else {
                        Log.d(TAG, "Tài liệu kho đồ cho userId: " + playerUserId + " KHÔNG tồn tại. Đang tạo mới...");
                        db.collection("inventory").document(playerUserId)
                                .set(new HashMap<String, Object>() {{
                                    put("userId", playerUserId);
                                }})
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Đã tạo tài liệu inventory mới cho " + playerUserId))
                                .addOnFailureListener(e -> Log.e(TAG, "Lỗi tạo tài liệu inventory mới: " + e.getMessage()));
                    }

                    inventoryAdapter.updateItems(displayInventoryItems);
                    Log.d(TAG, "Đã tải tổng cộng " + displayInventoryItems.size() + " item vào RecyclerView.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tải kho đồ từ Firebase: " + e.getMessage(), e);
                    Toast.makeText(InventoryActivity.this, "Lỗi tải kho đồ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onUseClick(ShopItem itemToUse) {
        attemptUseItem(itemToUse);
    }

    /**
     * Hàm xử lý giao dịch sử dụng vật phẩm với Firebase Firestore.
     * Đảm bảo tính nhất quán dữ liệu.
     * @param itemToUse Vật phẩm ShopItem được sử dụng.
     */
    private void attemptUseItem(ShopItem itemToUse) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference inventoryRef = db.collection("inventory").document(playerUserId);
            DocumentSnapshot inventoryDocSnapshot = (DocumentSnapshot) transaction.get(inventoryRef).getData();

            if (!inventoryDocSnapshot.exists()) {
                throw new FirebaseFirestoreException("Không tìm thấy kho đồ của người chơi. Giao dịch bị hủy.", FirebaseFirestoreException.Code.NOT_FOUND);
            }

            DocumentReference userRef = db.collection("users").document(playerUserId);
            DocumentSnapshot userDocSnapshot = (DocumentSnapshot) transaction.get(userRef).getData();

            DocumentReference petRef = db.collection("pet").document(playerUserId);
            DocumentSnapshot petDocSnapshot = (DocumentSnapshot) transaction.get(petRef).getData();

            String firebaseFieldNameForInventory = getFirebaseFieldNameForShopItem(itemToUse); // Gọi hàm mapping

            if (firebaseFieldNameForInventory == null) {
                throw new FirebaseFirestoreException("Không thể xác định tên trường Firebase cho vật phẩm: " + itemToUse.getName(), FirebaseFirestoreException.Code.INVALID_ARGUMENT);
            }

            Number currentQuantityNum = (Number) inventoryDocSnapshot.get(firebaseFieldNameForInventory);
            int currentQuantityInTransaction = (currentQuantityNum != null) ? currentQuantityNum.intValue() : 0;

            if (currentQuantityInTransaction <= 0) {
                throw new FirebaseFirestoreException("Bạn không còn " + itemToUse.getName() + " trong kho đồ. Giao dịch bị hủy.", FirebaseFirestoreException.Code.ABORTED);
            }

            transaction.update(inventoryRef, firebaseFieldNameForInventory, FieldValue.increment(-1L));
            Log.d(TAG, "Đã giảm 1 " + itemToUse.getName() + " trong kho đồ. Số lượng mới (chưa sync): " + (currentQuantityInTransaction - 1));

            if (itemToUse.getType() != null) {
                if (itemToUse.getType().equals("exp_item")) {
                    transaction.update(userRef, "experience", FieldValue.increment(itemToUse.getValue()));
                    Log.d(TAG, "Đã tăng EXP của người chơi: " + itemToUse.getValue());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_USED_ITEM_TYPE, "exp_item");
                    resultIntent.putExtra(EXTRA_USED_ITEM_VALUE, itemToUse.getValue());
                    resultIntent.putExtra(EXTRA_USED_ITEM_NAME, itemToUse.getName());
                    setResult(RESULT_OK, resultIntent);
                } else if (itemToUse.getType().equals("skill_book")) {
                    if (!petDocSnapshot.exists()) {
                        throw new FirebaseFirestoreException("Không tìm thấy pet của người chơi để học kỹ năng. (Tài liệu pet không tồn tại)", FirebaseFirestoreException.Code.NOT_FOUND);
                    }

                    // BẮT ĐẦU: TỰ MỞ KẾT NỐI DB VÀ TRUY VẤN TRỰC TIẾP TẠI ĐÂY (CHO getItemById)
                    DBHelper.Item rawSkillDetails = null;
                    SQLiteDatabase tempDbForSkillDetails = null;
                    Cursor skillCursor = null;
                    try {
                        tempDbForSkillDetails = dbHelper.getReadableDatabase(); // Mở DB
                        skillCursor = tempDbForSkillDetails.rawQuery("SELECT * FROM Items WHERE id = ?", new String[]{String.valueOf(itemToUse.getValue())});

                        if (skillCursor != null && skillCursor.moveToFirst()) {
                            int id = skillCursor.getInt(skillCursor.getColumnIndexOrThrow("id"));
                            String name = skillCursor.getString(skillCursor.getColumnIndexOrThrow("name"));
                            String imageResIdStr = skillCursor.getString(skillCursor.getColumnIndexOrThrow("imageResId"));
                            String type = skillCursor.getString(skillCursor.getColumnIndexOrThrow("type"));
                            int value = skillCursor.getInt(skillCursor.getColumnIndexOrThrow("value"));
                            int price = skillCursor.getInt(skillCursor.getColumnIndexOrThrow("price"));
                            String description = skillCursor.getString(skillCursor.getColumnIndexOrThrow("description"));
                            rawSkillDetails = new DBHelper.Item(id, name, imageResIdStr, type, value, price, description);
                        } else {
                            Log.d(TAG, "Không tìm thấy skill details trong SQLite cho ID: " + itemToUse.getValue());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi truy vấn SQLite cho skill details: " + e.getMessage(), e);
                    } finally {
                        if (skillCursor != null) skillCursor.close();
                        if (tempDbForSkillDetails != null && tempDbForSkillDetails.isOpen()) {
                            tempDbForSkillDetails.close(); // Đóng DB
                        }
                    }
                    // KẾT THÚC: TỰ MỞ/ĐÓNG DB


                    if (rawSkillDetails != null) {
                        String skillFieldToUpdate = "";
                        if (rawSkillDetails.type.equals("atk_skill")) { // LƯU Ý: type ở đây là từ SQLite
                            skillFieldToUpdate = "atk_skill_ids";
                        } else if (rawSkillDetails.type.equals("def_skill")) {
                            skillFieldToUpdate = "def_skill_ids";
                        } else if (rawSkillDetails.type.equals("buff_skill")) {
                            skillFieldToUpdate = "buff_skill_ids";
                        }

                        if (!skillFieldToUpdate.isEmpty()) {
                            String currentSkillIdsStr = petDocSnapshot.getString(skillFieldToUpdate);
                            List<Integer> currentSkillIds = dbHelper.parseIdString(currentSkillIdsStr); // parseIdString vẫn dùng được

                            if (!currentSkillIds.contains(itemToUse.getValue())) {
                                String newSkillIdsStr;
                                if (currentSkillIdsStr == null || currentSkillIdsStr.isEmpty()) {
                                    newSkillIdsStr = String.valueOf(itemToUse.getValue());
                                } else {
                                    newSkillIdsStr = currentSkillIdsStr + "," + String.valueOf(itemToUse.getValue());
                                }
                                transaction.update(petRef, skillFieldToUpdate, newSkillIdsStr);
                                Log.d(TAG, "Pet đã học kỹ năng mới: " + rawSkillDetails.name);
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(EXTRA_USED_ITEM_TYPE, "skill_book");
                                resultIntent.putExtra(EXTRA_USED_ITEM_VALUE, itemToUse.getValue());
                                resultIntent.putExtra(EXTRA_USED_ITEM_NAME, rawSkillDetails.name);
                                setResult(RESULT_OK, resultIntent);
                            } else {
                                throw new FirebaseFirestoreException("Quái vật của bạn đã học kỹ năng này rồi!", FirebaseFirestoreException.Code.ALREADY_EXISTS);
                            }
                        } else {
                            throw new FirebaseFirestoreException("Loại kỹ năng không xác định trong DBHelper cho item: " + rawSkillDetails.name + " (type: " + rawSkillDetails.type + ")", FirebaseFirestoreException.Code.INVALID_ARGUMENT);
                        }
                    } else {
                        throw new FirebaseFirestoreException("Không tìm thấy thông tin chi tiết kỹ năng trong SQLite cho ID: " + itemToUse.getValue(), FirebaseFirestoreException.Code.NOT_FOUND);
                    }
                }
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(InventoryActivity.this, "Đã sử dụng " + itemToUse.getName() + " thành công!", Toast.LENGTH_SHORT).show();
            loadInventoryItems();
            if (Objects.equals(itemToUse.getType(), "exp_item") || Objects.equals(itemToUse.getType(), "skill_book")) {
                finish();
            }
        }).addOnFailureListener(e -> {
            if (e.getMessage() != null) {
                if (e.getMessage().contains("không còn") || e.getMessage().contains("đã học") || e.getMessage().contains("Không tìm thấy pet")) {
                    Toast.makeText(InventoryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InventoryActivity.this, "Lỗi khi sử dụng " + itemToUse.getName() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            Log.e(TAG, "Lỗi transaction sử dụng item: " + e.getMessage(), e);
        });
    }

    /**
     * Phương thức ánh xạ từ ShopItem sang tên trường trong Firebase Inventory.
     * Tự động mở và đóng kết nối SQLite cho mỗi truy vấn.
     * @param item ShopItem cần ánh xạ.
     * @return Tên trường tương ứng trong Firebase (ví dụ: "atk_skill_book_1"), hoặc null nếu không tìm thấy.
     */
    private String getFirebaseFieldNameForShopItem(ShopItem item) {
        DBHelper.Item rawItemDetails = null;
        SQLiteDatabase tempDb = null;
        Cursor cursor = null;
        try {
            tempDb = dbHelper.getReadableDatabase(); // Mở DB
            cursor = tempDb.rawQuery("SELECT * FROM Items WHERE id = ?", new String[]{String.valueOf(tryParseInt(item.getItemId()))});

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String imageResIdStr = cursor.getString(cursor.getColumnIndexOrThrow("imageResId"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                int value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                rawItemDetails = new DBHelper.Item(id, name, imageResIdStr, type, value, price, description);
            } else {
                Log.d(TAG, "Không tìm thấy rawItemDetails trong SQLite cho ItemId: " + item.getItemId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy rawItemDetails trong getFirebaseFieldNameForShopItem: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (tempDb != null && tempDb.isOpen()) {
                tempDb.close(); // Đóng DB
            }
        }

        if (rawItemDetails != null && rawItemDetails.imageResId != null && !rawItemDetails.imageResId.isEmpty()) {
            return rawItemDetails.imageResId; // Ưu tiên trả về imageResId từ SQLite
        }

        // Fallback nếu không tìm thấy rawItemDetails hoặc imageResId rỗng
        switch (item.getType()) {
            case "atk_skill_book": return "atk_skill_book_1"; // Giả định type này map đến tên field này
            case "def_skill_book": return "def_skill_book_1";
            case "exp_book_large": return "exp_book_large";
            case "exp_book_small": return "exp_book_small";
            case "exp_meat": return "exp_meat";
            case "exp_cake": return "exp_cake";
            case "potential_point": return "potential_point";
            default:
                Log.w(TAG, "Không tìm thấy ánh xạ Firebase field cho ShopItem: " + item.getName() + " (Type: " + item.getType() + ", ItemId: " + item.getItemId() + ")");
                return null;
        }
    }


    private int tryParseInt(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Không thể parse giá trị thành số nguyên: " + value, e);
            return 0;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}