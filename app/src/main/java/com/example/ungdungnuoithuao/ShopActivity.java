package com.example.ungdungnuoithuao;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ShopAdapter;
import model.ShopItem;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    private RecyclerView rvShopItems;
    private ShopAdapter shopAdapter;
    private List<ShopItem> displayShopItems;
    private DBHelper dbHelper;

    private TextView tvUserCoins;
    private FirebaseFirestore db;
    private String playerUserId;
    private long currentUserCoins = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvShopItems = findViewById(R.id.rv_shop_items);
        tvUserCoins = findViewById(R.id.tv_user_coins);

        dbHelper = new DBHelper(this);
        db = FirebaseFirestore.getInstance();

        playerUserId = getIntent().getStringExtra("userId");
        if (playerUserId == null || playerUserId.isEmpty()) {
            Log.e(TAG, "Không nhận được userId từ Intent. ShopActivity cần userId.");
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayShopItems = new ArrayList<>();
        rvShopItems.setLayoutManager(new GridLayoutManager(this, 1));

        shopAdapter = new ShopAdapter(displayShopItems, item -> {
            attemptPurchase(item);
        });
        rvShopItems.setAdapter(shopAdapter);

        loadUserDataAndShopItems();
    }


    private void loadUserDataAndShopItems() {
        DocumentReference userDocRef = db.collection("user").document(playerUserId);

        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Tài liệu người dùng tồn tại: " + documentSnapshot.getId());
                        Long coinsLong = documentSnapshot.getLong("coin");
                        if (coinsLong != null) {
                            currentUserCoins = coinsLong.longValue();
                            updateCoinDisplay();
                            Log.d(TAG, "Tải thành công Coins của người dùng: " + currentUserCoins);
                        } else {
                            Log.w(TAG, "Trường 'coin' không tồn tại hoặc không phải là Number trong Firestore cho userId: " + playerUserId + ". Đặt mặc định 0.");
                            currentUserCoins = 0;
                            updateCoinDisplay();
                        }
                    } else {
                        Log.w(TAG, "Không tìm thấy tài liệu người dùng với ID: " + playerUserId + " trong collection 'user'. Đang tạo tài khoản mới...");
                        Map<String, Object> newUserData = new HashMap<>();
                        newUserData.put("coin", 0L);
                        newUserData.put("experience", 0L);
                        newUserData.put("level", 1L);

                        userDocRef.set(newUserData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Đã tạo tài liệu người dùng mới với ID: " + playerUserId);
                                    currentUserCoins = 0;
                                    updateCoinDisplay();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Lỗi khi tạo tài liệu người dùng mới: " + e.getMessage(), e);
                                    Toast.makeText(ShopActivity.this, "Lỗi tạo tài khoản mới: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                    loadShopItems();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tải tiền người dùng từ Firebase: " + e.getMessage(), e);
                    if (e instanceof FirebaseFirestoreException) {
                        FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
                        if (firestoreException.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Toast.makeText(ShopActivity.this, "Lỗi quyền truy cập Firebase: Vui lòng kiểm tra Rules hoặc đăng nhập!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Lỗi PERMISSION_DENIED: Kiểm tra Firebase Security Rules cho collection 'user'. Đảm bảo người dùng đã đăng nhập.");
                        } else if (firestoreException.getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
                            Toast.makeText(ShopActivity.this, "Không có kết nối mạng hoặc dịch vụ Firebase không khả dụng.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ShopActivity.this, "Lỗi Firebase: " + firestoreException.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ShopActivity.this, "Lỗi không xác định khi tải tiền: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    currentUserCoins = 0;
                    updateCoinDisplay();
                    loadShopItems();
                });
    }

    private void updateCoinDisplay() {
        tvUserCoins.setText("Coins: " + currentUserCoins);
    }

    private void loadShopItems() {
        List<DBHelper.Item> rawItems = dbHelper.getAllItems();
        displayShopItems.clear();

        for (DBHelper.Item rawItem : rawItems) {
            int imageResId = getResources().getIdentifier(rawItem.imageResId, "drawable", getPackageName());
            if (imageResId == 0) {
                Log.w(TAG, "Không tìm thấy tài nguyên ảnh cho: " + rawItem.imageResId + ". Đảm bảo file ảnh tồn tại trong res/drawable.");
            }
            displayShopItems.add(new ShopItem(
                    String.valueOf(rawItem.id),
                    rawItem.name,
                    rawItem.description,
                    rawItem.price,
                    imageResId,
                    1,
                    rawItem.type,
                    rawItem.value
            ));
        }
        shopAdapter.notifyDataSetChanged();
        Log.d(TAG, "Đã tải " + displayShopItems.size() + " item vào cửa hàng.");
    }


    private void attemptPurchase(ShopItem itemToBuy) {
        if (currentUserCoins < itemToBuy.getPrice()) {
            Toast.makeText(this, "Không đủ Coins để mua " + itemToBuy.getName() + "!", Toast.LENGTH_SHORT).show();
            return;
        }

        // BƯỚC 1: Đọc tài liệu người dùng để kiểm tra tiền và lấy thông tin cần thiết.
        DocumentReference userRef = db.collection("user").document(playerUserId);
        userRef.get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    long currentCoinsInDb = 0;
                    if (userDocumentSnapshot.exists()) {
                        Long coins = userDocumentSnapshot.getLong("coin");
                        if (coins != null) {
                            currentCoinsInDb = coins.longValue();
                        }
                    }

                    if (currentCoinsInDb < itemToBuy.getPrice()) {
                        Toast.makeText(ShopActivity.this, "Không đủ tiền để mua " + itemToBuy.getName() + ". Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        return; // Dừng nếu tiền không đủ (kiểm tra lần 2)
                    }

                    // BƯỚC 2: Trừ tiền của người dùng và cập nhật lên Firebase.
                    long newCoins = currentCoinsInDb - itemToBuy.getPrice();
                    userRef.update("coin", newCoins)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Đã trừ " + itemToBuy.getPrice() + " coins. Coins mới: " + newCoins);
                                currentUserCoins = newCoins; // Cập nhật biến cục bộ UI
                                updateCoinDisplay(); // Cập nhật hiển thị UI

                                // BƯỚC 3: Tìm tài liệu Inventory của người chơi (dùng query vì Document ID ngẫu nhiên)
                                checkItemBuyed(playerUserId, itemToBuy);
                            });
                });
    }

    // Phương thức checkItemBuyed không còn được sử dụng trong attemptPurchase() nữa
    // Bạn có thể xóa nó hoặc giữ lại nếu nó được sử dụng ở nơi khác, nhưng lưu ý các comment về ánh xạ.


    private void checkItemBuyed(String playerUserId, ShopItem itemToBuy) {
        db.collection("inventory")
                .whereEqualTo("userid", playerUserId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Số tài liệu trả về: " + querySnapshot.size());

                    if (querySnapshot.isEmpty()) { // QUAN TRỌNG: Xử lý trường hợp tài liệu không tồn tại
                        Log.d(TAG, "Không tìm thấy tài liệu inventory cho người dùng: " + playerUserId + ". Đang tạo mới...");
                        Map<String, Object> newInventoryData = new HashMap<>();
                        newInventoryData.put("userid", playerUserId);
                        newInventoryData.put("atk_skill_book_1", 0L);
                        newInventoryData.put("def_skill_book_1", 0L);
                        newInventoryData.put("exp_book_large", 0L);
                        newInventoryData.put("exp_book_small", 0L);
                        newInventoryData.put("exp_meat", 0L);
                        newInventoryData.put("exp_cake", 0L);
                        newInventoryData.put("potential_point", 0L); // Nếu có


                        // Thêm số lượng vật phẩm vừa mua vào tài liệu mới (chỉ 1 đơn vị)
                        String initialFieldToUpdate = itemToBuy.getType(); // Dùng itemToBuy.getType()

                        switch (initialFieldToUpdate) { // Đảm bảo các case này khớp
                            case "atk_skill_book": initialFieldToUpdate = "atk_skill_book_1"; break;
                            case "def_skill_book": initialFieldToUpdate = "def_skill_book_1"; break;
                            case "exp_book_large": initialFieldToUpdate = "exp_book_large"; break;
                            case "exp_book_small": initialFieldToUpdate = "exp_book_small"; break;
                            case "exp_meat": initialFieldToUpdate = "exp_meat"; break;
                            case "exp_cake": initialFieldToUpdate = "exp_cake"; break;
                            case "potential_point": initialFieldToUpdate = "potential_point"; break;
                            default:
                                Log.w(TAG, "Loại vật phẩm không có ánh xạ để khởi tạo kho đồ (Type): " + itemToBuy.getType());
                                Toast.makeText(ShopActivity.this, "Lỗi: Không thể khởi tạo kho đồ cho vật phẩm này.", Toast.LENGTH_SHORT).show();
                                // HOÀN TIỀN
                                db.collection("user").document(playerUserId).update("coin", FieldValue.increment(itemToBuy.getPrice()))
                                        .addOnSuccessListener(unused -> Log.d(TAG, "Đã hoàn tiền " + itemToBuy.getPrice() + " coins do lỗi khởi tạo kho đồ."))
                                        .addOnFailureListener(eRefund -> Log.e(TAG, "Lỗi khi hoàn tiền: " + eRefund.getMessage(), eRefund));
                                return;
                        }
                        newInventoryData.put(initialFieldToUpdate, 1L); // Đặt số lượng ban đầu là 1

                        db.collection("inventory").add(newInventoryData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "Đã tạo tài liệu inventory mới với ID: " + documentReference.getId() + " và thêm vật phẩm đã mua.");
                                    Toast.makeText(ShopActivity.this, "Mua " + itemToBuy.getName() + " thành công!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Lỗi khi tạo tài liệu inventory mới: " + e.getMessage(), e);
                                    Toast.makeText(ShopActivity.this, "Lỗi tạo kho đồ mới: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    // HOÀN TIỀN
                                    db.collection("user").document(playerUserId).update("coin", FieldValue.increment(itemToBuy.getPrice()))
                                            .addOnSuccessListener(unused -> Log.d(TAG, "Đã hoàn tiền " + itemToBuy.getPrice() + " coins do lỗi tạo kho đồ mới."))
                                            .addOnFailureListener(eRefund -> Log.e(TAG, "Lỗi khi hoàn tiền: " + eRefund.getMessage(), eRefund));
                                });

                    } else { // Tài liệu inventory đã tồn tại
                        DocumentSnapshot inventoryDocument = querySnapshot.getDocuments().get(0);
                        DocumentReference inventoryDocRef = inventoryDocument.getReference();

                        // Lấy giá trị hiện tại từ tài liệu inventory (sử dụng Long và mặc định 0L)
                        long qtatk_skill_book_1 = inventoryDocument.getLong("atk_skill_book_1");
                        long qtdef_skill_book_1 = inventoryDocument.getLong("def_skill_book_1");
                        long qtexp_book_large = inventoryDocument.getLong("exp_book_large");
                        long qtexp_book_small = inventoryDocument.getLong("exp_book_small");
                        long qtexp_meat = inventoryDocument.getLong("exp_meat");
                        long qtexp_cake = inventoryDocument.getLong("exp_cake");



                        // Tăng số lượng vật phẩm đã mua (tăng thêm 1 đơn vị)
                        String itemIdentifier = itemToBuy.getType(); // Lấy loại vật phẩm

                        Log.d(TAG, "Item Type/Identifier for existing inventory update: " + itemIdentifier); // THÊM LOG NÀY

                        switch (itemIdentifier) {
                            case "atk_skill_book":
                                qtatk_skill_book_1++;
                                break;
                            case "def_skill_book":
                                qtdef_skill_book_1++;
                                break;
                            case "exp_book_large":
                                qtexp_book_large++;
                                break;
                            case "exp_book_small":
                                qtexp_book_small++;
                                break;
                            case "exp_meat":
                                qtexp_meat++;
                                break;
                            case "exp_cake":
                                qtexp_cake++;
                                break;

                            default:
                                Log.w(TAG, "Loại vật phẩm không xác định hoặc không có ánh xạ (trong update): " + itemIdentifier + ". Không thể cập nhật kho đồ.");
                                Toast.makeText(ShopActivity.this, "Không thể cập nhật kho đồ cho vật phẩm này.", Toast.LENGTH_SHORT).show();
                                // HOÀN TIỀN
                                db.collection("user").document(playerUserId).update("coin", FieldValue.increment(itemToBuy.getPrice()))
                                        .addOnSuccessListener(unused -> Log.d(TAG, "Đã hoàn tiền " + itemToBuy.getPrice() + " coins do lỗi cập nhật kho đồ."))
                                        .addOnFailureListener(eRefund -> Log.e(TAG, "Lỗi khi hoàn tiền: " + eRefund.getMessage(), eRefund));
                                return;
                        }

                        // Tạo Map chứa dữ liệu đã cập nhật
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("atk_skill_book_1", qtatk_skill_book_1);
                        updatedData.put("def_skill_book_1", qtdef_skill_book_1);
                        updatedData.put("exp_book_large", qtexp_book_large);
                        updatedData.put("exp_book_small", qtexp_book_small);
                        updatedData.put("exp_meat", qtexp_meat);
                        updatedData.put("exp_cake", qtexp_cake);



                        // Cập nhật tài liệu kho đồ của người dùng
                        inventoryDocRef.update(updatedData)
                                .addOnSuccessListener(bVoid -> {
                                    Log.d(TAG, "Đã cập nhật kho đồ thành công cho vật phẩm: " + itemToBuy.getName());
                                    Toast.makeText(ShopActivity.this, "Mua " + itemToBuy.getName() + " thành công!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Lỗi khi cập nhật kho đồ: " + e.getMessage(), e);
                                    Toast.makeText(ShopActivity.this, "Lỗi cập nhật kho đồ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    // QUAN TRỌNG: HOÀN TIỀN NẾU CẬP NHẬT KHO ĐỒ THẤT BẠI!
                                    db.collection("user").document(playerUserId).update("coin", FieldValue.increment(itemToBuy.getPrice()))
                                            .addOnSuccessListener(unused -> Log.d(TAG, "Đã hoàn tiền " + itemToBuy.getPrice() + " coins do lỗi cập nhật kho đồ."))
                                            .addOnFailureListener(eRefund -> Log.e(TAG, "Lỗi khi hoàn tiền: " + eRefund.getMessage(), eRefund));
                                });
                    }
                }) // end addOnSuccessListener của truy vấn inventory
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi truy vấn tài liệu inventory: " + e.getMessage(), e);
                    // Hoàn tiền nếu truy vấn ban đầu thất bại (do Permissions hoặc mạng)
                    db.collection("user").document(playerUserId).update("coin", FieldValue.increment(itemToBuy.getPrice()))
                            .addOnSuccessListener(unused -> Log.d(TAG, "Đã hoàn tiền " + itemToBuy.getPrice() + " coins do lỗi truy vấn kho đồ."))
                            .addOnFailureListener(eRefund -> Log.e(TAG, "Lỗi khi hoàn tiền: " + eRefund.getMessage(), eRefund));
                });
    }



}