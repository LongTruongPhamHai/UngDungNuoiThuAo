package model;

public class SkillCard {
    private String name;
    private String type;
    private String effect; // Hiệu ứng đặc biệt
    private int baseDamage;
    private int baseArmor;
    private int imageResId;
    private int manaCost;
    private int level;

    public SkillCard(String name, String type, String effect, int baseDamage, int baseArmor, int imageResId, int manaCost, int level) {
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.baseDamage = baseDamage;
        this.baseArmor = baseArmor;
        this.imageResId = imageResId;
        this.manaCost = manaCost;
        this.level = level;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getEffect() {
        return effect;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getBaseArmor() {
        return baseArmor;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getLevel() {
        return level;
    }

    // Phương thức useSkill bây giờ nhận cả người dùng skill (caster) và mục tiêu (target) và trả về sát thương gây ra
    public int useSkill(Monster caster, Monster target) {
        int damageDealt = 0; // Biến để theo dõi lượng sát thương gây ra
        // Kiểm tra đủ mana trước khi dùng skill
        if (caster.getMana() < manaCost) {
            System.out.println(caster.getName() + " không có đủ năng lượng (" + manaCost + " MP) để dùng " + name + "!");
            return damageDealt; // Không đủ mana thì không dùng skill, trả về 0 sát thương
        }
        caster.setMana(caster.getMana() - manaCost); // Trừ mana sau khi dùng skill

        // --- Logic sát thương/hiệu ứng ---
        if (this.type.equals("damage")) {
            int calculatedDamage = this.baseDamage; // Sát thương cơ bản từ skill

            // Áp dụng hiệu ứng đặc biệt trước khi tính sát thương cuối cùng
            switch (effect) {
                case "crit":
                    calculatedDamage *= 2; // Chí mạng x2 sát thương
                    System.out.println("SÁT THƯƠNG CHÍ MẠNG! " + caster.getName() + " gây " + calculatedDamage + " sát thương gốc.");
                    break;
                case "bleed":
                    target.applyBleedEffect(3); // Gây chảy máu trong 3 lượt
                    System.out.println(caster.getName() + " gây CHẢY MÁU lên " + target.getName() + "!");
                    break;
                case "stun":
                    target.applyStunEffect(); // Gây choáng một lượt
                    System.out.println(caster.getName() + " LÀM CHOÁNG " + target.getName() + "!");
                    break;
            }

            // Tính toán sát thương cuối cùng: Sát thương của người dùng + sát thương từ Skill - phòng thủ của mục tiêu
            int finalDamageToTarget = (caster.getAttack() + calculatedDamage) - target.getDefense();
            if (finalDamageToTarget < 0) { // Sát thương không thể âm
                finalDamageToTarget = 0;
            }
            target.takeDamage(finalDamageToTarget); // Gọi hàm takeDamage của mục tiêu
            damageDealt = finalDamageToTarget; // Gán lượng sát thương đã gây ra

            System.out.println(caster.getName() + " dùng " + name + " và gây " + finalDamageToTarget + " sát thương lên " + target.getName() + ".");

        } else if (this.type.equals("buff")) {
            switch (effect) {
                case "buff_attack":
                    caster.increaseAttack(this.baseDamage); // Dùng baseDamage để chỉ lượng tăng attack
                    System.out.println(caster.getName() + " tăng Sức Tấn Công thêm " + this.baseDamage + " điểm!");
                    break;
                case "buff_defense":
                    caster.increaseDefense(this.baseArmor); // Dùng baseArmor để chỉ lượng tăng defense
                    System.out.println(caster.getName() + " tăng Phòng Thủ thêm " + this.baseArmor + " điểm!");
                    break;
                case "heal": // <-- Bổ sung logic hồi máu
                    // Giả sử baseDamage ở đây là lượng HP hồi phục
                    caster.setHp(caster.getHp() + this.baseDamage); // Hồi máu cho người dùng skill
                    System.out.println(caster.getName() + " hồi phục " + this.baseDamage + " HP!");
                    break;
                // Có thể thêm restore mana cho caster ở đây nếu có effect tương ứng
            }
        }
        // TODO: Thêm các loại skill khác (Debuff,...) ở đây nếu cần
        return damageDealt; // Trả về lượng sát thương (hoặc 0 nếu không phải skill gây sát thương)
    }
}
