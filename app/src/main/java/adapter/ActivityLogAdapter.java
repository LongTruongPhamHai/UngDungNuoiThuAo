package adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungnuoithuao.R;

import java.util.List;
import java.util.Map;

import model.ActivityLog;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityLogViewHolder> {
    private List<Map<String, Object>> actLogList;

    public ActivityLogAdapter(List<Map<String, Object>> actLogList) {
        this.actLogList = actLogList;
    }

    public static class ActivityLogViewHolder extends RecyclerView.ViewHolder {
        TextView typeTv, dateTv, timeTv, durationTv, distanceTv, stepTv, scoreTv, resultTv;
        TableRow durationTr, distanceTr, stepTr, scoreTr, resultTr;

        public ActivityLogViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTv = itemView.findViewById(R.id.type_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
            timeTv = itemView.findViewById(R.id.time_tv);
            durationTv = itemView.findViewById(R.id.duration_tv);
            distanceTv = itemView.findViewById(R.id.distance_tv);
            stepTv = itemView.findViewById(R.id.step_tv);
            scoreTv = itemView.findViewById(R.id.score_tv);
            resultTv = itemView.findViewById(R.id.result_tv);

            durationTr = itemView.findViewById(R.id.duration_tr);
            distanceTr = itemView.findViewById(R.id.distance_tr);
            stepTr = itemView.findViewById(R.id.step_tr);
            scoreTr = itemView.findViewById(R.id.score_tr);
            resultTr = itemView.findViewById(R.id.result_tr);
        }
    }

    @NonNull
    @Override
    public ActivityLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_log, parent, false);
        return new ActivityLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityLogViewHolder holder, int position) {
        Map<String, Object> actLogMap = actLogList.get(position);

        Log.d("ActLogAdapter", "-------------------------");
        Log.d("ActLogAdapter", "Type: " + actLogMap.get("type"));
        Log.d("ActLogAdapter", "Date: " + actLogMap.get("date"));
        Log.d("ActLogAdapter", "Time: " + actLogMap.get("time"));
        Log.d("ActLogAdapter", "Duration: " + actLogMap.get("duration"));
        Log.d("ActLogAdapter", "Distance: " + actLogMap.get("distance"));
        Log.d("ActLogAdapter", "Step: " + actLogMap.get("step"));
        Log.d("ActLogAdapter", "Score: " + actLogMap.get("score"));
        Log.d("ActLogAdapter", "Result: " + actLogMap.get("result"));

        holder.typeTv.setText(actLogMap.get("type").toString());
        holder.dateTv.setText(actLogMap.get("date").toString());
        holder.timeTv.setText(actLogMap.get("time").toString());
        holder.durationTv.setText(actLogMap.get("duration").toString());
        holder.distanceTv.setText(actLogMap.get("distance").toString());
        holder.stepTv.setText(actLogMap.get("step").toString());
        holder.scoreTv.setText(actLogMap.get("score").toString());
        holder.resultTv.setText(actLogMap.get("result").toString());

        holder.durationTr.setVisibility(View.VISIBLE);
        holder.distanceTr.setVisibility(View.GONE);
        holder.stepTr.setVisibility(View.GONE);
        holder.scoreTr.setVisibility(View.GONE);
        holder.resultTr.setVisibility(View.GONE);

        String type = actLogMap.get("type").toString();
        if (type.equals("Kiểm tra"))    holder.scoreTr.setVisibility(View.VISIBLE);
        else if (type.equals("Chạy bộ")) {
            holder.stepTr.setVisibility(View.VISIBLE);
            holder.distanceTr.setVisibility(View.VISIBLE);
        }
        else if (type.equals("Đạp xe")) {
            holder.distanceTr.setVisibility(View.VISIBLE);
        }
        else if (type.equals("Giải trí")) {
            holder.durationTr.setVisibility(View.GONE);
            holder.resultTr.setVisibility(View.VISIBLE);
        } else if (type.equals("Giảm chỉ số (Quá 24h không hoạt động)")) {
            holder.typeTv.setTextColor(Color.parseColor("#ff0000"));
            holder.durationTr.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return actLogList.size();
    }
}