package com.example.himalayaproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalayaproject.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListRVAdapter extends RecyclerView.Adapter<DetailListRVAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mSimpleTimeFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里记得务必传入父控件，选择不包装就行，不传父控件可能影响绘制过程
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        Track cur = mDetailData.get(position);
        cur.setTrackTags(position + "");
        holder.setData(cur);
        //设置item点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mDetailData, position);
                }
            }
        });
    }

    public void setItemClickListener(ItemClickListener listener){

        mItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemClick(List<Track> detailData, int position);
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        mDetailData.clear();
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private TextView mId;
        private TextView mTitle;
        private TextView mHot;
        private TextView mLength;
        private TextView mTime;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mId = itemView.findViewById(R.id.detail_item_id);
            mTitle = itemView.findViewById(R.id.detail_item_title);
            mHot = itemView.findViewById(R.id.detail_item_hot);
            mLength = itemView.findViewById(R.id.detail_item_length);
            mTime = itemView.findViewById(R.id.detail_item_time);
        }
        public void setData(Track track){
            //把显示的ID转化为从1开始
            mId.setText(Integer.parseInt(track.getTrackTags()) + 1 + "");
            mTitle.setText(track.getTrackTitle());
            mHot.setText("热度" + track.getPlayCount() + "");
            String length = mSimpleTimeFormat.format((int)track.getDuration() * 1000);
            mLength.setText("时长" + length);
            String upTimeText = mSimpleDateFormat.format(track.getUpdatedAt());
            mTime.setText(upTimeText);
        }
    }
}
