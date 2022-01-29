package siren.ocean.recognize.apapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import siren.ocean.recognize.AppContext;
import siren.ocean.recognize.R;
import siren.ocean.recognize.util.CommonUtil;

/**
 * 手机摄像头设置参数
 * Created by Siren on 2022/1/28.
 */
public class PhoneCamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ItemSelectedListener itemClickListener;
    private List<String> data;

    public interface ItemSelectedListener {
        void onSelected(int position);
    }

    public PhoneCamAdapter(List<String> data, ItemSelectedListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).itemView.setTag(position);
            ((ItemViewHolder) holder).textView.setText(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            textView.setOnClickListener(v -> itemClickListener.onSelected(getLayoutPosition()));
            int size = CommonUtil.dpToPx(AppContext.get(), 10);
            textView.setPadding(size, size, size, size);
        }
    }
}
