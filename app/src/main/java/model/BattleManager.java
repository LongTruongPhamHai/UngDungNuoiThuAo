package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BattleManager {
    private Monster playerMonster; // Quái vật của người chơi
    private Monster enemyMonster;  // Quái vật của đối thủ (AI)
    private boolean playerTurn;    // true: lượt người chơi, false: lượt AI
    private List<SkillCard> playerCards; // Các thẻ kỹ năng người chơi có trong tay (hand)
    private BattleListener listener;

    public interface BattleListener {
        void onTurnStart(boolean isPlayerTurn);
        void onMonsterHpChange(Monster monster);
        void onMonsterManaChange(Monster monster);
        void onBattleEnd(boolean playerWon); // Sẽ được gọi từ hàm endBattle
        void onPlayerCardsUpdated(List<SkillCard> cards);
        void onLogMessage(String message);
        void onMonsterStunnedStatusChange(Monster monster, boolean isStunned);
    }

    public BattleManager(Monster playerMonster, Monster enemyMonster, BattleListener listener) {
        this.playerMonster = playerMonster;
        this.enemyMonster = enemyMonster;
        this.listener = listener;
        this.playerTurn = true;
        this.playerCards = new ArrayList<>();

        initializeBattle();
    }

    private void initializeBattle() {
        if (listener != null) listener.onLogMessage("Trận đấu bắt đầu!");
        drawPlayerCards();
        if (listener != null) listener.onTurnStart(playerTurn);
    }

    public void nextTurn() {
        playerMonster.endTurnEffects();
        enemyMonster.endTurnEffects();

        if (listener != null) {
            listener.onMonsterHpChange(playerMonster);
            listener.onMonsterManaChange(playerMonster);
            listener.onMonsterHpChange(enemyMonster);
            listener.onMonsterManaChange(enemyMonster);
        }

        // Kiểm tra điều kiện kết thúc trận đấu
        if (playerMonster.getHp() <= 0 || enemyMonster.getHp() <= 0) {
            endBattle();
            return;
        }

        playerTurn = !playerTurn;

        String currentTurnPlayerName = playerTurn ? playerMonster.getName() : enemyMonster.getName();
        if (listener != null) listener.onLogMessage("--- Lượt của " + currentTurnPlayerName + " ---");

        Monster currentActingMonster = playerTurn ? playerMonster : enemyMonster;

        if (currentActingMonster.isStunned()) {
            if (listener != null) listener.onLogMessage(currentActingMonster.getName() + " bị CHOÁNG và không thể hành động lượt này!");
            currentActingMonster.setStunned(false);
            if (listener != null) listener.onMonsterStunnedStatusChange(currentActingMonster, false);
            nextTurn();
            return;
        } else {
            if (listener != null) listener.onMonsterStunnedStatusChange(currentActingMonster, false);
        }

        if (!playerTurn) {
            performEnemyMove();
        } else {
            drawPlayerCards();
            if (listener != null) listener.onTurnStart(playerTurn);
        }
    }

    private void drawPlayerCards() {
        playerCards.clear();
        List<SkillCard> playerMonsterSkills = playerMonster.getSkills();

        if (playerMonsterSkills == null || playerMonsterSkills.isEmpty()) {
            if (listener != null) listener.onLogMessage(playerMonster.getName() + " không có kỹ năng để rút!");
            return;
        }

        List<SkillCard> tempPool = new ArrayList<>(playerMonsterSkills);
        Collections.shuffle(tempPool);

        int cardsToDraw = Math.min(5, tempPool.size());

        for (int i = 0; i < cardsToDraw; i++) {
            playerCards.add(tempPool.get(i));
        }
        if (listener != null) listener.onPlayerCardsUpdated(playerCards);
        if (listener != null) listener.onLogMessage(playerMonster.getName() + " đã rút được các thẻ kỹ năng mới.");
    }

    public void playerUseSkill(SkillCard skillUsed) {
        if (!playerTurn || playerMonster.isStunned() || !playerCards.contains(skillUsed) || playerMonster.getMana() < skillUsed.getManaCost()) {
            if (!playerTurn) listener.onLogMessage("Chưa đến lượt của bạn!");
            else if (playerMonster.isStunned()) listener.onLogMessage(playerMonster.getName() + " bị choáng và không thể hành động!");
            else if (!playerCards.contains(skillUsed)) listener.onLogMessage("Kỹ năng không có trong tay!");
            else listener.onLogMessage(playerMonster.getName() + " không đủ năng lượng để dùng " + skillUsed.getName() + "!");
            return;
        }

        int damage = skillUsed.useSkill(playerMonster, enemyMonster);
        if (listener != null) {
            listener.onMonsterHpChange(playerMonster);
            listener.onMonsterManaChange(playerMonster);
            listener.onMonsterHpChange(enemyMonster);
            listener.onMonsterManaChange(enemyMonster);
            listener.onLogMessage(playerMonster.getName() + " đã dùng " + skillUsed.getName() + " và gây " + damage + " sát thương!");
            if (skillUsed.getEffect().equals("stun")) {
                listener.onMonsterStunnedStatusChange(enemyMonster, true);
            }
        }

        playerCards.remove(skillUsed);
        nextTurn();
    }

    private void performEnemyMove() {
        List<SkillCard> usableSkills = new ArrayList<>();
        List<SkillCard> enemyMonsterSkills = enemyMonster.getSkills();

        if (enemyMonsterSkills == null || enemyMonsterSkills.isEmpty()) {
            if (listener != null) listener.onLogMessage(enemyMonster.getName() + " không có kỹ năng để sử dụng!");
            nextTurn();
            return;
        }

        for (SkillCard skill : enemyMonsterSkills) {
            if (enemyMonster.getMana() >= skill.getManaCost()) {
                usableSkills.add(skill);
            }
        }

        if (usableSkills.isEmpty()) {
            if (listener != null) listener.onLogMessage(enemyMonster.getName() + " không đủ năng lượng cho các kỹ năng và bỏ qua lượt.");
            nextTurn();
            return;
        }

        Random random = new Random();
        SkillCard chosenSkill = usableSkills.get(random.nextInt(usableSkills.size()));

        int damage = chosenSkill.useSkill(enemyMonster, playerMonster);
        if (listener != null) {
            listener.onMonsterHpChange(enemyMonster);
            listener.onMonsterManaChange(enemyMonster);
            listener.onMonsterHpChange(playerMonster);
            listener.onMonsterManaChange(playerMonster);
            listener.onLogMessage(enemyMonster.getName() + " đã dùng " + chosenSkill.getName() + " và gây " + damage + " sát thương!");
            if (chosenSkill.getEffect().equals("stun")) {
                listener.onMonsterStunnedStatusChange(playerMonster, true);
            }
        }

        nextTurn();
    }

    // Hàm xử lý khi trận đấu kết thúc
    private void endBattle() {
        boolean playerWon = playerMonster.getHp() > 0;
        if (listener != null) {
            String result = playerWon ? "Chiến thắng thuộc về bạn!" : "Bạn đã thất bại!";
            listener.onLogMessage("--- Trận đấu kết thúc ---");
            listener.onLogMessage(result);
            listener.onBattleEnd(playerWon); // Gọi callback onBattleEnd với kết quả
        }
        // TODO: Xử lý phần thưởng/hình phạt ở đây
    }

    public Monster getPlayerMonster() { return playerMonster; }
    public Monster getEnemyMonster() { return enemyMonster; }
    public List<SkillCard> getPlayerCards() { return playerCards; }
    public boolean isPlayerTurn() { return playerTurn; }
}
