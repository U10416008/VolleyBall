package com.example.user.volleyball;

/**
 * Created by dingjie on 2017/12/13.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
public class DateAdapter extends RecyclerView.Adapter{
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
    private OnRecyclerViewListener onRecyclerViewListener;
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
               this.onRecyclerViewListener = onRecyclerViewListener;
    }
    @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
             Log.d("TAG", "onCreateViewHolder, i: " + i);
             View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_list_layout, null);
             LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
             view.setLayoutParams(lp);
        return new DateViewHolder(view);
    }
    private ArrayList<DateSchedule> list;
    public DateAdapter(ArrayList<DateSchedule> list) {
        this.list = list;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Log.d("TAG", "onBindViewHolder, i: " + position + ", viewHolder: " + viewHolder);
        DateViewHolder holder = (DateViewHolder) viewHolder;
        holder.position = position;
        String date = list.get(position).getDate();
        holder.nameTv.setText(date);
        String schedule = list.get(position).getSchedule();
        holder.ageTv.setText(schedule);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public TextView nameTv;
        public TextView ageTv;
        public int position;
        public DateViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.recycler_view_test_item_person_name_tv);
            ageTv = (TextView) itemView.findViewById(R.id.recycler_view_test_item_person_age_tv);
            rootView = itemView.findViewById(R.id.recycler_view_test_item_person_view);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }
                @Override
        public boolean onLongClick(View v) {
                        if(null != onRecyclerViewListener){
                                return onRecyclerViewListener.onItemLongClick(position);
                            }
                         return false;
                   }
    }


}
