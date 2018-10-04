package com.ibrickedlabs.BlogApp.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrickedlabs.BlogApp.Model.Blog;
import com.ibrickedlabs.BlogApp.R;

import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    private  Context context;
    private List<Blog> list;


    public BlogRecyclerAdapter(Context context, List<Blog> list) {
        this.context = context;
        this.list = list;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.row_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog=list.get(position);
        String imageUrl=null;
        holder.title.setText(blog.getTitle());
        holder.description.setText(blog.getDesc());
        //Need to format the long time in ms to April 16 2017 format
        java.text.DateFormat mformat=java.text.DateFormat.getDateInstance();
        String formattedDate=mformat.format(new Date(Long.valueOf(blog.getTimeStamp())));
        //setting up formatted date
        holder.timeStamp.setText(formattedDate);
        imageUrl=blog.getImage();
        //Need to use some library to load image
        Glide.with(context).load(imageUrl).into(holder.image);


    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class  ViewHolder extends  RecyclerView.ViewHolder{
        public TextView title;
        public  TextView description;
        public  TextView timeStamp;
        public ImageView  image;
        String userId;

        public ViewHolder(View itemView,Context ctx) {
            super(itemView);
            context=ctx;
            title=(TextView)itemView.findViewById(R.id.pageTitle);
            description=(TextView)itemView.findViewById(R.id.postDescription);
            image=(ImageView)itemView.findViewById(R.id.postImage);
            timeStamp=(TextView)itemView.findViewById(R.id.dateView);
            userId=null;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //we can move to next Activity from here
                }
            });

        }
    }
}
