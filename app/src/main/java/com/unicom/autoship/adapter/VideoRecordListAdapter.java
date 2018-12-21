package com.unicom.autoship.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unicom.autoship.R;
import com.unicom.autoship.bean.PatrolHistory;
import com.unicom.autoship.bean.ShipBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoRecordListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private List<PatrolHistory> datas;
    private int screenWidth;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private int pos;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int section);
        //void onItemLongClick(View view ,int section,int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }


    public VideoRecordListAdapter(Context context, List<PatrolHistory> datas) {
        this.context = context;
        this.datas = datas;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_of_ship_video_record, null);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        //viewHolder.rl.getLayoutParams().height = (screenWidth - 90) / 2;
        //Glide.with(context).load(datas.get(position).getImg1()).into(viewHolder.iv);

        viewHolder.itemView.setTag(position);




        viewHolder.tvName.setText("" + datas.get(position).getBoatName()+"-"+datas.get(position).getPatrolNo());
        viewHolder.tvArea.setText("" + datas.get(position).getReach());
        viewHolder.tvDate.setText("" + datas.get(position).getPatrolStartTimeStr()+" - "+datas.get(position).getPatrolEndTimeStr());
        if (mOnItemClickListener != null)
            viewHolder.itemView.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_area)
        TextView tvArea;



        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //getTag获取的即是点击位置
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

}
