package model;

/**
 * Lớp đại diện cho một vật phẩm trong cửa hàng hoặc trong kho đồ của người chơi.
 * Chứa các thuộc tính cơ bản của vật phẩm như ID, tên, mô tả, giá, ảnh, số lượng, loại và giá trị tác động.
 */
public class ShopItem {
    private String itemId;        // ID duy nhất của vật phẩm (có thể là ID số từ SQLite dưới dạng String)
    private String name;          // Tên của vật phẩm
    private String description;   // Mô tả chi tiết về vật phẩm
    private int price;            // Giá của vật phẩm khi mua (chỉ dùng trong cửa hàng)
    private int imageResId;       // Resource ID của hình ảnh vật phẩm (ví dụ: R.drawable.hp_potion_small)
    private int quantity;         // <-- THÊM LẠI TRƯỜNG QUANTITY NÀY
    private String type;          // Loại của vật phẩm (ví dụ: "exp_item", "skill_book", "heal_potion")
    private int value;            // Giá trị tác động của vật phẩm (ví dụ: lượng EXP nhận được, ID của skill học được, lượng HP/Mana hồi phục)

    /**
     * Constructor đầy đủ cho ShopItem, bao gồm tất cả các thuộc tính.
     * Sử dụng khi khởi tạo ShopItem với đầy đủ thông tin, đặc biệt từ DB hoặc Firebase.
     *
     * @param itemId      ID của vật phẩm.
     * @param name        Tên vật phẩm.
     * @param description Mô tả vật phẩm.
     * @param price       Giá của vật phẩm.
     * @param imageResId  Resource ID của ảnh.
     * @param quantity    Số lượng vật phẩm mà người chơi sở hữu. <-- THÊM LẠI THAM SỐ NÀY
     * @param type        Loại vật phẩm.
     * @param value       Giá trị tác động của vật phẩm.
     */
    public ShopItem(String itemId, String name, String description, int price, int imageResId, int quantity, String type, int value) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = quantity; // <-- Gán giá trị quantity
        this.type = type;
        this.value = value;
    }

    /**
     * Constructor rút gọn cho ShopItem. Thường dùng khi hiển thị trong Shop
     * hoặc khi số lượng mặc định là 1 và loại/giá trị chưa rõ.
     *
     * @param itemId      ID của vật phẩm.
     * @param name        Tên vật phẩm.
     * @param description Mô tả vật phẩm.
     * @param price       Giá của vật phẩm.
     * @param imageResId  Resource ID của ảnh.
     */
    public ShopItem(String itemId, String name, String description, int price, int imageResId) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = 1;         // Mặc định số lượng là 1
        this.type = "unknown";     // Mặc định loại là "unknown"
        this.value = 0;            // Mặc định giá trị là 0
    }

    // --- Getters (Phương thức lấy giá trị) ---

    public String getItemId() {
        return itemId;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getPrice() {
        return price;
    }
    public int getImageResId() {
        return imageResId;
    }
    public int getQuantity() { // <-- THÊM LẠI PHƯƠNG THỨC NÀY
        return quantity;
    }
    public String getType() {
        return type;
    }
    public int getValue() {
        return value;
    }

    // --- Setters (Phương thức đặt giá trị) ---

    public void setQuantity(int quantity) { // <-- THÊM LẠI PHƯƠNG THỨC NÀY
        this.quantity = quantity;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setValue(int value) {
        this.value = value;
    }
}