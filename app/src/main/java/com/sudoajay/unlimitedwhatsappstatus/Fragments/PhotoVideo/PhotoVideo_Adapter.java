package com.sudoajay.unlimitedwhatsappstatus.Fragments.PhotoVideo;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sudoajay.unlimitedwhatsappstatus.DataBase.PhotoDataBase;
import com.sudoajay.unlimitedwhatsappstatus.DataBase.VideoDataBase;
import com.sudoajay.unlimitedwhatsappstatus.R;

import java.util.List;


public class PhotoVideo_Adapter extends RecyclerView.Adapter<PhotoVideo_Adapter.ViewHolder> implements Filterable
{
    private String tabName;
    private PhotoVideoFragment photoVideoFragment;
    private List<String> links, name, imgLink;

    public PhotoVideo_Adapter(final PhotoVideoFragment photoVideoFragment, final String tabName, final List<String> links, final List<String> name, final List<String> imgLink) {
        this.photoVideoFragment = photoVideoFragment;
        this.tabName = tabName;
        this.links =links;
        this.name = name;
        this.imgLink = imgLink;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView localCardViewImageView;
        public ViewHolder(View v) {
            super(v);
            localCardViewImageView =v.findViewById(R.id.localCardViewImageView);
        }
    }

    @NonNull
    @Override
    public PhotoVideo_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(photoVideoFragment.getContext()).inflate(R.layout.custom_local_online_card_view, parent, false);
        return new PhotoVideo_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoVideo_Adapter.ViewHolder holder, final int position) {
        String url;
        if (tabName.equals("photo"))
            url = links.get(position);
        else {
            url = imgLink.get(position);
        }
        Glide.with(photoVideoFragment)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_icon)
                .into(holder.localCardViewImageView);

        holder.localCardViewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(photoVideoFragment.getContext(), PhotoVideoView.class);
//                intent.putExtra("WhichTab",tabName);
//                intent.putExtra("WhichFragment","online");
//                intent.putStringArrayListExtra("PathArray", (ArrayList<String>) links);
//                intent.putStringArrayListExtra("PathName", (ArrayList<String>) name);
//                intent.putStringArrayListExtra("ImageLink", (ArrayList<String>) imgLink);
//                intent.putExtra("PathArrayPosition",position+"");
//                photoVideoFragment.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return links.size();
    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterPattern = constraint.toString().toLowerCase().trim();
            if (constraint.length() == 0) {
                filterPattern = "";
            }

            FilterResults results = new FilterResults();
            results.values = filterPattern;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            String text = (String) results.values;
            if (!text.equals("")) {
                links.clear();
                name.clear();
                imgLink.clear();
                if (tabName.equals("photo")) {
                    GrabPhoto(text);
                } else {
                    GrabVideo(text);
                }
                notifyDataSetChanged();
            }

        }
    };

    public void GrabPhoto(final String text) {
        PhotoDataBase photoDataBase = new PhotoDataBase(photoVideoFragment.getContext());
        Cursor cursor = photoDataBase.FilterName(text,links.size());
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (links.size() >= 500) links = RemoveArrayFromList(links);

                links.add(cursor.getString(1));

                if (name.size() >= 500) name = RemoveArrayFromList(name);
                name.add(cursor.getString(2));

            } while (cursor.moveToNext());
        }
        photoVideoFragment.CheckForEmpty();
    }

    public void GrabVideo(final String text) {
        VideoDataBase videoDataBase = new VideoDataBase(photoVideoFragment.getContext());
        Cursor cursor = videoDataBase.FilterName(text,links.size());
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (links.size() >= 500) links = RemoveArrayFromList(links);
                links.add(cursor.getString(1));
                if (name.size() >= 500) name = RemoveArrayFromList(name);
                name.add(cursor.getString(2));
                if (imgLink.size() >= 500) imgLink = RemoveArrayFromList(imgLink);
                imgLink.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        photoVideoFragment.CheckForEmpty();
    }

    private List<String> RemoveArrayFromList(final List<String> grab) {
        int offset = 300;
        for (int i = offset - 1; i >= 0; i--) {
            grab.remove(i);
        }
        return grab;
    }
}
