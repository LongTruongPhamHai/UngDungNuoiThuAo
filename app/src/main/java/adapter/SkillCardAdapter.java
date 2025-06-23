package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungnuoithuao.R;

import java.util.ArrayList;
import java.util.List;

import model.SkillCard;

public class SkillCardAdapter extends RecyclerView.Adapter<SkillCardAdapter.SkillCardViewHolder> {

    private List<SkillCard> skillCardList;
    private OnSkillCardClickListener listener; // Interface để xử lý click

    // Interface để truyền sự kiện click ra ngoài Activity/Fragment
    public interface OnSkillCardClickListener {
        void onSkillCardClick(SkillCard skillCard);
    }

    public SkillCardAdapter(List<SkillCard> skillCardList, OnSkillCardClickListener listener) {
        this.skillCardList = new ArrayList<>(skillCardList); // Khởi tạo với bản sao của list
        this.listener = listener;
    }

    // Phương thức để cập nhật danh sách thẻ (được gọi từ Activity)
    public void updateCards(List<SkillCard> newCards) {
        this.skillCardList.clear();
        this.skillCardList.addAll(newCards);
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại
    }

    @NonNull
    @Override
    public SkillCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Phồng" (inflate) layout cho mỗi item thẻ bài
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill_card, parent, false);
        return new SkillCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillCardViewHolder holder, int position) {
        SkillCard skillCard = skillCardList.get(position);

        // Đặt hình ảnh cho ImageView
        // Đảm bảo skillCard.getImageResId() trả về ID tài nguyên hợp lệ (ví dụ: R.drawable.skill_fireball)
        if (skillCard.getImageResId() != 0) {
            holder.skillImage.setImageResource(skillCard.getImageResId());
        } else {
            holder.skillImage.setImageResource(R.drawable.null1); // Ảnh mặc định nếu không có
        }

        // Đặt tên và mana cost cho TextView
        holder.skillName.setText(skillCard.getName());
        holder.skillManaCost.setText("MP: " + skillCard.getManaCost());

        // Xử lý khi nhấn vào thẻ skill
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSkillCardClick(skillCard); // Gọi listener khi thẻ được click
            }
        });
    }

    @Override
    public int getItemCount() {
        return skillCardList.size(); // Trả về số lượng thẻ bài trong danh sách
    }

    // ViewHolder: Giữ các View cho mỗi item, giúp RecyclerView hoạt động hiệu quả
    public static class SkillCardViewHolder extends RecyclerView.ViewHolder {
        ImageView skillImage;
        TextView skillName;
        TextView skillManaCost;

        public SkillCardViewHolder(@NonNull View itemView) {
            super(itemView);
            skillImage = itemView.findViewById(R.id.skill_card_image);
            skillName = itemView.findViewById(R.id.skill_card_name);
            skillManaCost = itemView.findViewById(R.id.skill_card_mana_cost);
        }
    }
}
