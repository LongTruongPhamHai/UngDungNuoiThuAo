package model;

/**
 * Lớp đại diện cho một vật phẩm trong cửa hàng hoặc trong kho đồ của người chơi.
 * Chứa các thuộc tính cơ bản của vật phẩm như ID, tên, mô tả, giá, ảnh, số lượng, loại và giá trị tác động.
 */
public class InventoryItem {
    private String itemId;        // ID duy nhất của vật phẩm (có thể là ID số từ SQLite dưới dạng String)
    private String name;          // Tên của vật phẩm
    private String description;   // Mô tả chi tiết về vật phẩm
    private int price;            // Giá của vật phẩm khi mua (chỉ dùng trong cửa hàng)
    private int imageResId;       // Resource ID của hình ảnh vật phẩm (ví dụ: R.drawable.hp_potion_small)
    private int quantity;         // Số lượng vật phẩm mà người chơi sở hữu hoặc số lượng mua
    private String type;          // Loại của vật phẩm (ví dụ: "exp_item", "skill_book", "heal_potion")
    private int value;            // Giá trị tác động của vật phẩm (ví dụ: lượng EXP nhận được, ID của skill học được, lượng HP/Mana hồi phục)

    /**
     * Constructor đầy đủ cho InventoryItem, bao gồm tất cả các thuộc tính.
     * Sử dụng khi khởi tạo InventoryItem với đầy đủ thông tin, đặc biệt từ DB hoặc Firebase.
     *
     * @param itemId      ID của vật phẩm.
     * @param name        Tên vật phẩm.
     * @param description Mô tả vật phẩm.
     * @param price       Giá của vật phẩm.
     * @param imageResId  Resource ID của ảnh.
     * @param quantity    Số lượng vật phẩm.
     * @param type        Loại vật phẩm.
     * @param value       Giá trị tác động của vật phẩm.
     */
    public InventoryItem(String itemId, String name, String description, int price, int imageResId, int quantity, String type, int value) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = quantity; // Đảm bảo gán giá trị cho trường quantity
        this.type = type;
        this.value = value; // Gán giá trị cho trường 'value'
    }

    /**
     * Constructor rút gọn cho InventoryItem, không bao gồm quantity, type, và value.
     * Thường dùng khi hiển thị trong Shop và số lượng mặc định là 1, loại và giá trị chưa rõ.
     * Các trường quantity, type, value sẽ được gán giá trị mặc định.
     *
     * @param itemId      ID của vật phẩm.
     * @param name        Tên vật phẩm.
     * @param description Mô tả vật phẩm.
     * @param price       Giá của vật phẩm.
     * @param imageResId  Resource ID của ảnh.
     */
    public InventoryItem(String itemId, String name, String description, int price, int imageResId) {
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

    /**
     * Lấy ID của vật phẩm.
     * @return ID vật phẩm dưới dạng String.
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Lấy tên của vật phẩm.
     * @return Tên vật phẩm.
     */
    public String getName() {
        return name;
    }

    /**
     * Lấy mô tả của vật phẩm.
     * @return Mô tả vật phẩm.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Lấy giá của vật phẩm.
     * @return Giá vật phẩm.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Lấy Resource ID của hình ảnh vật phẩm.
     * @return Resource ID của ảnh.
     */
    public int getImageResId() {
        return imageResId;
    }

    /**
     * Lấy số lượng vật phẩm.
     * @return Số lượng vật phẩm.
     */
    public int getQuantity() { // <--- THÊM PHƯƠNG THỨC NÀY
        return quantity;
    }

    /**
     * Lấy loại của vật phẩm.
     * @return Loại vật phẩm dưới dạng String.
     */
    public String getType() {
        return type;
    }

    /**
     * Lấy giá trị tác động của vật phẩm.
     * @return Giá trị tác động của vật phẩm.
     */
    public int getValue() {
        return value;
    }

    // --- Setters (Phương thức đặt giá trị) ---
    // (Bạn có thể thêm setters nếu cần thay đổi giá trị của các thuộc tính sau khi khởi tạo)

    /**
     * Đặt lại số lượng vật phẩm.
     * @param quantity Số lượng mới.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Đặt lại loại vật phẩm.
     * @param type Loại mới.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Đặt lại giá trị tác động của vật phẩm.
     * @param value Giá trị mới.
     */
    public void setValue(int value) {
        this.value = value;
    }
}