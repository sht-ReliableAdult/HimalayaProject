package com.example.himalayaproject.Adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.himalayaproject.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class RecommendRVAdapter extends RecyclerView.Adapter<RecommendRVAdapter.InnerHolder> {
    private List<Album> mData = new ArrayList<>();
    private String TAG = "RecommendRVAdapter";
    private IItemClick mIItemClick;

    //创建适配器的setData方法
    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //载View，需要创建新ViewHolder的时候调用
        View itemVew = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemVew);
    }

    @Override
    public void onBindViewHolder(@NonNull final InnerHolder holder, int position) {
        //封装数据，涉及复用
        //打上位置标签
        holder.itemView.setTag(position);
        //这里对外暴露一个接口，交给fragment来设置具体的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIItemClick != null) {
                    int clickPos = (Integer) holder.itemView.getTag();
                    mIItemClick.onItemClick(clickPos, mData.get(clickPos));
                }
            }
        });
        holder.setData(mData.get(position));
    }

    /**
     * 对fragment暴露的点击事件设置接口
     */
    public interface IItemClick{
        void onItemClick(int position, Album album);
    }

    /**
     * RecycleView所在的fragment使用本方法set并自己继承接口重写一个具体的点击事件逻辑，
     * 把自己传给Adaptor作为成员变量mIItemClick
     * @param itemClick
     */
    public void setIItemClick(IItemClick itemClick){
        this.mIItemClick = itemClick;
    }

    @Override
    public int getItemCount() {
        //返回获取到数据的个数
        if (mData != null) return mData.size();
        return 0;
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImage;
        private TextView mItemTitle;
        private TextView mItemDescription;
        private TextView mItemHot;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = itemView.findViewById(R.id.item_image);
            mItemTitle = itemView.findViewById(R.id.item_title);
            mItemDescription = itemView.findViewById(R.id.item_description);
            mItemHot = itemView.findViewById(R.id.item_hot);
        }

        public void setData(Album album) {
            //找到控件设置数据
            //设置条目图片+标题+描述+热度
            mItemTitle.setText(album.getAlbumTitle());
            mItemDescription.setText(album.getAlbumIntro());
            mItemHot.setText("热度：" + album.getPlayCount() + "");
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Glide.with(itemView.getContext()).load(coverUrlLarge).into(mItemImage);
            }else{
                mItemImage.setImageResource(R.mipmap.logo);
            }
        }
    }
}
