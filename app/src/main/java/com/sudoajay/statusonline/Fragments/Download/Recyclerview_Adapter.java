package com.sudoajay.statusonline.Fragments.Download;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sudoajay.statusonline.HelperClass.CustomToast;
import com.sudoajay.statusonline.HelperClass.Delete;
import com.sudoajay.statusonline.MainActivity;
import com.sudoajay.statusonline.PhotoVideoViewer.PhotoVideoView;
import com.sudoajay.statusonline.R;
import com.sudoajay.statusonline.sharedPreferences.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Recyclerview_Adapter extends RecyclerView.Adapter<Recyclerview_Adapter.MyViewHolder> implements Filterable {

    private List<String> list, searchList, completeList;
    private MainActivity activity;
    private String tabName;
    private DownloadFragment downloadFragment;
    private List<String> filePathSelected = new ArrayList<>();
    private List<Boolean> fileSelected = new ArrayList<>();
    private PrefManager prefManager;

    public Recyclerview_Adapter(final MainActivity activity, final List<String> list, final DownloadFragment downloadFragment) {
        this.list = list;
        this.activity = activity;
        this.downloadFragment = downloadFragment;
        prefManager = new PrefManager(activity);
        completeList = new ArrayList<>(list);
        searchList = new ArrayList<>();
        SetupSelectedList();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverImageView, infoImageView;
        private TextView nameTextView, pathTextView;
        private CardView cardView;

        private MyViewHolder(View v) {
            super(v);
            nameTextView = v.findViewById(R.id.nameTextView);
            pathTextView = v.findViewById(R.id.pathTextView);
            coverImageView = v.findViewById(R.id.coverImageView);
            infoImageView = v.findViewById(R.id.info_ImageView);
            cardView = v.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public Recyclerview_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_download_recyclerview, parent, false);

        return new MyViewHolder(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        if (fileSelected.get(position)) {
            holder.cardView.setAlpha(0.5f);
        } else {
            holder.cardView.setAlpha(1f);
        }

        try {
            holder.nameTextView.setOnClickListener(new Onclick(position, holder));
            holder.coverImageView.setOnClickListener(new Onclick(position, holder));
            holder.pathTextView.setOnClickListener(new Onclick(position, holder));
            holder.infoImageView.setOnClickListener(new Onclick(position, holder));


            holder.nameTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OnLongClick(holder, position);
                    return true;
                }
            });
            holder.coverImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OnLongClick(holder, position);
                    return true;
                }
            });

            holder.pathTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OnLongClick(holder, position);
                    return true;
                }
            });

            holder.infoImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OnLongClick(holder, position);
                    return true;
                }
            });


            holder.nameTextView.setText(new File(list.get(position)).getName());
            String[] spilit = list.get(position).split(activity.getResources().getString(R.string.app_name));
            holder.pathTextView.setText("/" + activity.getResources().getString(R.string.app_name) + spilit[1]);

            Glide.with(activity)
                    .asBitmap()
                    .load(Uri.fromFile(new File(list.get(position))))
                    .into(holder.coverImageView);


        } catch (Exception e) {

        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    private void getTabName(final String path) {
        String[] spiltStorage = path.split(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        String[] spiltTabName = spiltStorage[1].split("/");
        if (spiltTabName[1].equalsIgnoreCase("photo")) tabName = "photo";
        else {
            tabName = "video";
        }
    }

    // action mode setup or you say configuration
    // anyomous class
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.downloads_option, menu);
            actionMode.setTitle(activity.getString(R.string.heading_Action_Mode));



            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

//            setting_Pressed =true;
            switch (menuItem.getItemId()) {
                case R.id.remove:
                    Call_Custom_Dailog(activity.getString(R.string.remove_CustomDialog_Text));
                    return false;
                case R.id.delete:
                    Call_Custom_Dailog(activity.getString(R.string.delete_CustomDialog_Text));
                    return false;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode Mode) {
            downloadFragment.GrabAndFill();
            activity.setActionMode(null);
            filePathSelected.clear();
            SetupSelectedList();
        }

    };

    public void Call_Custom_Dailog(final String Message) {

        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_save_dialog);
        TextView text_Message = dialog.findViewById(R.id.text_Message);
        text_Message.setText(Message);
        TextView button_No = dialog.findViewById(R.id.button_No);
        TextView button_Yes = dialog.findViewById(R.id.button_Yes);
        // if button is clicked, close the custom dialog

        button_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Message.equalsIgnoreCase(activity.getString(R.string.delete_CustomDialog_Text))) {
                    DeleteTheSelectedFile();
                    CustomToast.ToastIt(activity, "Successfully file was deleted", Toast.LENGTH_LONG);

                } else {
                    prefManager.setFilePath(new HashSet<>(filePathSelected));
                    CustomToast.ToastIt(activity, "Successfully file was removed", Toast.LENGTH_LONG);
                }

                DeletedSelectedFromList();
                notifyDataSetChanged();
                activity.getActionMode().finish();
                dialog.dismiss();
            }
        });
        button_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void DeletedSelectedFromList() {
        for (int i = 0; i < fileSelected.size(); i++) {
            if (fileSelected.get(i)) {
                list.remove(i);
            }
        }
        fileSelected.clear();
        filePathSelected.clear();
    }
    private void DeleteTheSelectedFile() {
        for (String filePath : filePathSelected) {
            new Delete().DeleteTheData(filePath);
        }
    }

    private void SetupSelectedList() {
        for (int i = 0; i < list.size(); i++) {
            fileSelected.add(false);
        }
    }

    private void OnLongClick(final MyViewHolder holder, final int index) {
        if (activity.getActionMode() != null) return;
        activity.setActionMode(activity.startSupportActionMode(mActionModeCallback));

        holder.cardView.setAlpha(0.5f);
        filePathSelected.add(list.get(index));
        fileSelected.set(index, true);
        notifyDataSetChanged();
    }

    public class Onclick implements View.OnClickListener {
        private int index;
        private MyViewHolder holder;

        private Onclick(final int index, MyViewHolder holder) {
            this.index = index;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            if (activity.getActionMode() == null) {
                getTabName(list.get(index));
                switch (v.getId()) {
                    case R.id.nameTextView:
                    case R.id.coverImageView:
                    case R.id.pathTextView:
//                        // get Tab Name
                        Intent intent = new Intent(activity, PhotoVideoView.class);
                        intent.putExtra("WhichTab", tabName);
                        intent.putExtra("WhichFragment", "Download");
                        intent.putStringArrayListExtra("PathArray", (ArrayList<String>) list);
                        intent.putExtra("PathArrayPosition", index + "");
                        activity.startActivity(intent);
                        break;
                    case R.id.info_ImageView:
                        activity.CallInfo_CustomDialog(tabName, (ArrayList<String>) list, index);
                        break;
                }
            } else {

                if (holder.cardView.getAlpha() == 1f) {
                    holder.cardView.setAlpha(0.5f);
                    filePathSelected.add(list.get(index));
                    fileSelected.set(index, true);
                } else {
                    filePathSelected.remove(list.get(index));
                    fileSelected.set(index, false);
                    holder.cardView.setAlpha(1f);
                }
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterPattern = constraint.toString().trim();

            if (constraint.length() != 0) {
                searchList.clear();
                for (String get : completeList) {
                    if (new File(get).getName().contains(filterPattern)) {
                        searchList.add(get);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = searchList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            fileSelected.clear();
            list.addAll(searchList);
            SetupSelectedList();
            notifyDataSetChanged();
        }
    };



}
