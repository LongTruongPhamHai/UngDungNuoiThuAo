package model;

import java.util.ArrayList;
import java.util.List;

public class Monster {
    public int id;
    public String name;
    public String imageResId;
    public int hp;
    public int attack;
    public int defense;
    public int mana;
    public com.example.ungdungnuoithuao.DBHelper DBHelper;
    private boolean isStunned;     // <-- Thuộc tính trạng thái bị choáng
    private int bleedDuration;     // <-- Thuộc tính thời gian chảy máu
    private List<SkillCard> skills; // <-- Thuộc tính mới: Danh sách kỹ năng mà quái vật này sở hữu

    // Constructor đã được cập nhật để khởi tạo các thuộc tính mới
    public Monster(int id, String name, String imageResId, int hp, int mana, int attack, int defense) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.hp = hp;
        this.mana = mana;
        this.attack = attack;
        this.defense = defense;
        this.isStunned = false;       // Mặc định quái vật không bị choáng
        this.bleedDuration = 0;     // Mặc định không bị chảy máu
        this.skills = new ArrayList<>(); // Khởi tạo danh sách kỹ năng rỗng khi tạo quái vật
    }

    // --- Getters (Để các lớp khác có thể ĐỌC dữ liệu) ---
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMana() { return mana; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getIdMonster() { return id; }

    // Getter cho trạng thái choáng
    public boolean isStunned() { return isStunned; } // <-- Quan trọng cho BattleManager để kiểm tra
    // Getter cho thời gian chảy máu
    public int getBleedDuration() { return bleedDuration; }
    // Getter cho danh sách kỹ năng của quái vật
    public List<SkillCard> getSkills() { return skills; } // <-- Quan trọng cho BattleManager để lấy skill AI và rút thẻ

    // --- Setters (Để các lớp khác có thể THAY ĐỔI dữ liệu) ---
    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp < 0) this.hp = 0; // Đảm bảo HP không bao giờ âm
    }
    public void setMana(int mana) {
        this.mana = mana;
        if (this.mana < 0) this.mana = 0; // Đảm bảo Mana không bao giờ âm
        // Nếu có maxMana, có thể thêm: if (this.mana > this.maxMana) this.mana = this.maxMana;
    }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }

    // Setter cho trạng thái choáng (để BattleManager có thể gỡ choáng)
    public void setStunned(boolean stunned) { this.isStunned = stunned; } // <-- Quan trọng cho BattleManager để đặt lại trạng thái
    // Setter cho thời gian chảy máu (nếu cần thay đổi từ bên ngoài, nhưng thường dùng applyBleedEffect)
    public void setBleedDuration(int bleedDuration) { this.bleedDuration = bleedDuration; }

    // --- Hàm tính sát thương (chỉ nhận sát thương cuối cùng đã tính toán) ---
    public void takeDamage(int finalDamage) {
        this.hp -= finalDamage;
        if (this.hp < 0) this.hp = 0; // Đảm bảo HP không âm
        System.out.println(name + " nhận " + finalDamage + " sát thương. HP còn lại: " + hp);
    }

    // --- Các hàm tăng chỉ số ---
    public void increaseAttack(int amount) {
        this.attack += amount;
        System.out.println(name + " Tấn Công tăng thêm " + amount + " điểm. Hiện tại: " + attack);
    }

    public void increaseDefense(int amount) {
        this.defense += amount;
        System.out.println(name + " Phòng Thủ tăng thêm " + amount + " điểm. Hiện tại: " + defense);
    }

    // --- Hàm áp dụng hiệu ứng trạng thái ---
    public void applyStunEffect() {
        this.isStunned = true; // Đặt trạng thái choáng thành true
        System.out.println(name + " bị CHOÁNG!");
    }

    public void applyBleedEffect(int duration) {
        this.bleedDuration = duration; // Đặt thời gian chảy máu
        System.out.println(name + " bị CHẢY MÁU trong " + duration + " lượt!");
    }

    // --- Hàm để thêm kỹ năng vào danh sách của quái vật ---
    public void addSkill(SkillCard skill) {
        if (!skills.contains(skill)) { // Đảm bảo không thêm trùng kỹ năng
            skills.add(skill);
            System.out.println(name + " đã học kỹ năng: " + skill.getName());
        }
    }

    // --- Hàm xử lý các hiệu ứng cuối lượt ---
    public void endTurnEffects() {
        // Xử lý sát thương từ chảy máu
        if (bleedDuration > 0) {
            int bleedDamage = 5; // Ví dụ: Mỗi lượt mất 5 HP do chảy máu
            this.hp -= bleedDamage;
            if (this.hp < 0) this.hp = 0;
            System.out.println(name + " mất " + bleedDamage + " HP do chảy máu. HP còn lại: " + hp);
            bleedDuration--; // Giảm thời gian chảy máu
        }
        // Lưu ý: Hiệu ứng choáng (isStunned) sẽ được kiểm tra và gỡ bỏ ở đầu lượt trong BattleManager,
        // không cần gỡ ở đây để tránh xung đột logic.
    }
}
