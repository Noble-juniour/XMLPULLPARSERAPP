package com.example.xmlpullparserapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    public Context context;

    private ArrayList<NewsItem> news = new ArrayList<>();


    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public RecyclerViewAdapter() {
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDate,txtDescription,txtTitle;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtPubdate);
            txtDescription = (TextView) itemView.findViewById(R.id.txtdesc);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);

            parent = (CardView) itemView.findViewById(R.id.parent);

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_listitem, viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder: started");
        holder.txtTitle.setText(news.get(position).getTitle());
        holder.txtDescription.setText(news.get(position).getDesc());
        holder.txtDate.setText(news.get(position).getDate());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate the use to web view activity
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url",news.get(position).getLink());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return news.size();
    }




    public void setNews(ArrayList<NewsItem> news) {
        this.news = news;
        notifyDataSetChanged();
    }
}
