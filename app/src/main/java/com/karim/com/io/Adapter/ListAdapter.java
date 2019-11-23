package com.karim.com.io.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karim.com.io.Model.TimerData;
import com.karim.com.io.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.viewholder> {
    Context _ctx;
    LayoutInflater mlLayoutInflater;
    List<TimerData>list;
    public ListAdapter(Context _ctx,List<TimerData>list) {
        this._ctx=_ctx;
        this.mlLayoutInflater=LayoutInflater.from(_ctx);
        this.list=list;
    }

    @NonNull
    @Override
   public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(_ctx).inflate(R.layout.list_item,parent,false) ;
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
              holder.dateTextView.setText(list.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewholder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            dateTextView=itemView.findViewById(R.id.date);
        }
    }
}
