package com.clarkgarrett.instagramclient;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Karl on 2/12/2015.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    private String likesPrefix;
    private int minLines= 3;
    private int maxLines = 100;

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, R.layout.item_photo, objects);
        likesPrefix = context.getResources().getString(R.string.likes);
    }

    private static class ViewHolder{
        TextView tvCaption;
        TextView tvLikes;
        TextView tvUsername;
        TextView tvDate;
        ImageView ivPhoto;
        ImageView ivProfilePic;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        InstagramPhoto photo = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvCaption = (TextView)convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView)convertView.findViewById(R.id.ivPhoto);
            viewHolder.tvLikes =(TextView)convertView.findViewById(R.id.tvLikes);
            viewHolder.ivProfilePic =(ImageView)convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if(photo.created_Time == 0){
            viewHolder.tvDate.setText(" ");
        } else {
            String dateString = DateUtils.getRelativeTimeSpanString(photo.created_Time * 1000, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
            viewHolder.tvDate.setText(dateString);
        }

        viewHolder.tvCaption.setText(photo.caption);
        viewHolder.tvLikes.setText(likesPrefix + photo.likesCount);
        viewHolder.tvUsername.setText(photo.username);

        viewHolder.ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl)
          //      .placeholder(R.drawable.loading_image)
                .into(viewHolder.ivPhoto);

        viewHolder.ivProfilePic.setImageResource(0);
        Picasso.with(getContext()).load(photo.profilePicUrl).into(viewHolder.ivProfilePic);

        viewHolder.tvCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView v2 = (TextView)v;
                if (v2.getMaxLines() == maxLines){
                    v2.setMaxLines(minLines);
                }else {
                    v2.setMaxLines(maxLines);
                }
            }
        });

        return convertView;
    }
}
