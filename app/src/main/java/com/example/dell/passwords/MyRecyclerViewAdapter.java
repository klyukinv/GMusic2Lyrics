package com.example.dell.passwords;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.view.LayoutInflater;
import android.widget.Filterable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<ExampleItem> exampleList;
    private List<ExampleItem> exampleListFull;
    private final int mRowLayout;
    private Context context;

    public MyRecyclerViewAdapter(List<ExampleItem> exampleList, int rowLayout, Context context) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        mRowLayout = rowLayout;
        this.context = context;
    }

    // Create ViewHolder which holds a View to be displayed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new MyRecyclerViewAdapter.ViewHolder(v, context);
    }

    // Binding: The process of preparing a child view to display data corresponding to a position within the adapter.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ExampleItem currentItem = exampleList.get(i);
        viewHolder.textView1.setText(currentItem.getText1());
        viewHolder.textView2.setText(currentItem.getText2());
        viewHolder.id = currentItem.getId();
    }

    @Override
    public int getItemCount() {
        return (null == exampleList) ? 0 : exampleList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ExampleItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ExampleItem item : exampleListFull) {
                    if (item.getText1().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getText2().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView1;
        TextView textView2;
        int id;
        Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text_view1);
            textView2 = itemView.findViewById(R.id.text_view2);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ScrollingActivity.class);
            intent.putExtra("ID", id);
            intent.putExtra("title", textView1.getText());
            intent.putExtra("artist", textView2.getText());
            context.startActivity(intent);
        }
    }

}
