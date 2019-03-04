package com.konatsup.musicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class PlaylistAdapter extends RealmBaseAdapter<Tune> implements ListAdapter {

    public PlaylistAdapter(OrderedRealmCollection<Tune> realmResults){
        super(realmResults);
    }

    public static class ViewHolder{
        ImageView imageView;
        TextView titleTextView;
        TextView artistTextView;
        TextView likeTextView;

        public ViewHolder(View view){
            imageView = (ImageView)view.findViewById(R.id.imageView);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            artistTextView = (TextView) view.findViewById(R.id.artistTextView);
            likeTextView = (TextView) view.findViewById(R.id.likeTextView);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        // Viewを再利用している場合は新たにViewを作らない
        if (convertView == null) {
            convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist, parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(adapterData != null){
            final Tune item = adapterData.get(position);
            viewHolder.imageView.setImageResource(R.drawable.jacket1);
            viewHolder.titleTextView.setText(item.getTitle());
            viewHolder.artistTextView.setText(item.getArtist());
            viewHolder.likeTextView.setText(String.valueOf(item.getLike()));

            /* ClickListener系はとりあえず外しておく*/
//            viewHolder.titleTextView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v){
//                    if(viewHolder.titleTextView.getText().toString()==""){
//                        viewHolder.titleTextView.setText(item.getTitle());
//                    }else {
//                        viewHolder.titleTextView.setText("");
//                    }
//                }
//            });
//            viewHolder.contentTextView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v){
//                    if(viewHolder.contentTextView.getText().toString()==""){
//                        viewHolder.contentTextView.setText(item.getContent());
//                    }else {
//                        viewHolder.contentTextView.setText("");
//                    }
//                }
//            });
        }
        return convertView;
    }

}