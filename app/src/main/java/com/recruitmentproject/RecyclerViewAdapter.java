package com.recruitmentproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ListItem.List listItems;
    private Context context;

    public RecyclerViewAdapter(Context context) {
        this.listItems = new ListItem.List();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper locationsDb = new DatabaseHelper(context);
                locationsDb.updatePosition(listItem.getId());

                Intent intent = new Intent(view.getContext(), LocationDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", listItem.getId());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);

                refreshRecyclerView();

            }
        });

        holder.textViewName.setText(listItem.getName());
        holder.textViewLatitude.setText(String.valueOf(listItem.getLatitude()) + " E" + " N,");
        holder.textViewLongitude.setText(String.valueOf(listItem.getLongitude()) + " E");
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView textViewLatitude;
        public TextView textViewLongitude;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewLatitude = (TextView) itemView.findViewById(R.id.textViewLatitude);
            textViewLongitude = (TextView) itemView.findViewById(R.id.textViewLongitude);
        }
    }

    public void refreshRecyclerView() {

        ListItem.List databaseItems = new ListItem.List();
        DatabaseHelper locationsDb = new DatabaseHelper(context);
        Cursor res = locationsDb.getAllData();
        if(res.getCount() != 0) {
            while (res.moveToNext()) {
                ListItem listItem = new ListItem(res.getInt(0), res.getString(1), res.getDouble(3), res.getDouble(4));
                databaseItems.add(listItem);
            }
        }

        listItems.clear();
        listItems.addAll(databaseItems);
        notifyDataSetChanged();
    }
}
