package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungnuoithuao.R;

import java.util.List;

import model.ShopItem;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<ShopItem> shopItemList;
    private OnBuyClickListener buyClickListener;

    public interface OnBuyClickListener {
        void onBuyClick(ShopItem item);
    }

    public ShopAdapter(List<ShopItem> shopItemList, OnBuyClickListener listener) {
        this.shopItemList = shopItemList;
        this.buyClickListener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopItem currentItem = shopItemList.get(position);
        holder.tvItemName.setText(currentItem.getName());
        holder.tvItemDescription.setText(currentItem.getDescription());
        holder.tvItemPrice.setText("Giá: " + currentItem.getPrice() + " Coins");

        // Đặt ảnh cho item (tương tự như SkillCardAdapter)
        if (currentItem.getImageResId() != 0) {
            holder.ivItemImage.setImageResource(currentItem.getImageResId());
        }
//        } else {
//            holder.ivItemImage.setImageResource(R.drawable.ic_item_placeholder); // Ảnh mặc định
//        }

        holder.btnBuyItem.setOnClickListener(v -> {
            if (buyClickListener != null) {
                buyClickListener.onBuyClick(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopItemList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivItemImage;
        public TextView tvItemName;
        public TextView tvItemDescription;
        public TextView tvItemPrice;
        public Button btnBuyItem;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemDescription = itemView.findViewById(R.id.tv_item_description);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            btnBuyItem = itemView.findViewById(R.id.btn_buy_item);
        }
    }
}