package com.example.himalayaproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalayaproject.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendRVAdapter extends RecyclerView.Adapter<SearchRecommendRVAdapter.InnerHolder> {


    private List<QueryResult> mData = new ArrayList<>();
    private ItemClickListener mItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        final String keyword = mData.get(position).getKeyword();
        holder.setData(keyword);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(keyword);
                }
            }
        });
    }

    /**
     * 对外暴露的设置条目点击事件的方法
     * @param listener
     */
    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * 在外部设置点击事件的实现的接口
     */
    public interface ItemClickListener{
        void onItemClick(String keyword);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 外部设置数据
     * @param keywordList
     */
    public void setData(List<QueryResult> keywordList) {
        mData.clear();
        mData.addAll(keywordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private final TextView mSearchRecommendText;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mSearchRecommendText = itemView.findViewById(R.id.search_recommend_text);
        }

        public void setData(String keyword) {
            mSearchRecommendText.setText(keyword);
        }
    }
}
