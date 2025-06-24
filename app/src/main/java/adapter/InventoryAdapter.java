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

/**
 * Adapter cho RecyclerView hiển thị danh sách các vật phẩm trong kho đồ của người chơi.
 * Lớp này chịu trách nhiệm liên kết dữ liệu (ShopItem) với các View trong danh sách.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    // Danh sách các vật phẩm (ShopItem) sẽ được hiển thị trong RecyclerView
    private List<ShopItem> inventoryItemList;
    // Listener để xử lý sự kiện khi nút "SỬ DỤNG" trên một vật phẩm được nhấn
    private OnUseClickListener useClickListener;

    /**
     * Interface định nghĩa callback khi nút "SỬ DỤNG" trên một vật phẩm được click.
     * Activity chứa RecyclerView sẽ cần implement interface này để xử lý sự kiện.
     */
    public interface OnUseClickListener {
        void onUseClick(ShopItem item); // <-- Chỉ cần ShopItem, vì nó đã có quantity
    }

    /**
     * Constructor của InventoryAdapter.
     *
     * @param inventoryItemList Danh sách các vật phẩm ShopItem để hiển thị.
     * @param listener          Đối tượng implement OnUseClickListener để xử lý sự kiện sử dụng vật phẩm.
     */
    public InventoryAdapter(List<ShopItem> inventoryItemList, OnUseClickListener listener) {
        this.inventoryItemList = inventoryItemList;
        this.useClickListener = listener;
        // KHÔNG CẦN FirebaseFirestore db và String playerUserId ở đây
        // vì adapter không tự truy vấn Firestore nữa
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(itemView);
    }

    /**
     * Được gọi bởi RecyclerView để hiển thị dữ liệu tại một vị trí cụ thể.
     * Phương thức này cập nhật nội dung của ViewHolder để phản ánh item tại vị trí đã cho
     * và thiết lập các listener cho các View tương tác (như nút).
     */
    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        ShopItem currentItem = inventoryItemList.get(position);

        // Đặt văn bản cho tên và mô tả của vật phẩm
        holder.tvItemName.setText(currentItem.getName());
        holder.tvItemDescription.setText(currentItem.getDescription());
        // Lấy số lượng trực tiếp từ ShopItem (vì nó đã được fetch và gán trong Activity)
        holder.tvItemQuantity.setText("Số lượng: " + currentItem.getQuantity());

        // Đặt hình ảnh cho vật phẩm
        if (currentItem.getImageResId() != 0) {
            holder.ivItemImage.setImageResource(currentItem.getImageResId());
        }

        // Thiết lập OnClickListener cho nút "SỬ DỤNG"
        holder.btnUseItem.setOnClickListener(v -> {
            if (useClickListener != null) {
                // Gọi callback onUseClick của listener, truyền item hiện tại
                useClickListener.onUseClick(currentItem);
            }
        });

        // Vô hiệu hóa nút "SỬ DỤNG" nếu số lượng vật phẩm là 0
        holder.btnUseItem.setEnabled(currentItem.getQuantity() > 0);
    }

    /**
     * Trả về tổng số lượng item trong tập dữ liệu của adapter.
     */
    @Override
    public int getItemCount() {
        return inventoryItemList.size();
    }

    /**
     * Phương thức tiện ích để cập nhật danh sách các vật phẩm trong adapter.
     */
    public void updateItems(List<ShopItem> newItems) {
        this.inventoryItemList = newItems;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder đại diện cho một hàng (item) trong RecyclerView.
     */
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivItemImage;
        public TextView tvItemName;
        public TextView tvItemDescription;
        public TextView tvItemQuantity;
        public Button btnUseItem;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemDescription = itemView.findViewById(R.id.tv_item_description);
            tvItemQuantity = itemView.findViewById(R.id.tv_item_quantity);
            btnUseItem = itemView.findViewById(R.id.btn_use_item);
        }
    }
}