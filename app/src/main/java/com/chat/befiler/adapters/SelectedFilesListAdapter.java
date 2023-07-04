package com.chat.befiler.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chat.befiler.Events.appEvents.FileDeleteEvent;
import com.chat.befiler.Events.appEvents.MessageEvent;
import com.chat.befiler.Events.appEvents.MessageEventDetailFragment;
import com.chat.befiler.commons.Common;
import com.chat.befiler.model.chat.Conversation;
import com.example.signalrtestandroid.R;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;


public class SelectedFilesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context contex;
    private final Common common;
    private final ArrayList<FileDataClass> item_list;
    public SelectedFilesListAdapter(Context contex, ArrayList<FileDataClass> item_list) {
        this.contex = contex;
        this.item_list = item_list;
        this.common = new Common();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_selectedfiles, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        FileDataClass fileDataClass = item_list.get(position);
        if(!fileDataClass.fileName.isEmpty()){
            vh.txtName.setText(fileDataClass.fileName);
        }
        if(!fileDataClass.fileSizes.isEmpty()){
            vh.txtSize.setText(fileDataClass.fileSizes);
        }
        if (fileDataClass.mimeType.equalsIgnoreCase("image/jpeg") || fileDataClass.mimeType.equalsIgnoreCase("image/jpg") || fileDataClass.mimeType.equalsIgnoreCase("image/png")) {
            vh.ivMultimedia.setImageResource(R.drawable.image);
        } else if (fileDataClass.mimeType.equalsIgnoreCase("application/pdf")) {
            vh.ivMultimedia.setImageResource(R.drawable.pdf);
        }
        else if (fileDataClass.mimeType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        || fileDataClass.mimeType.equalsIgnoreCase("application/msword")) {
            vh.ivMultimedia.setImageResource(R.drawable.doc);
        }

        vh.layoutDelete.setOnClickListener(v -> {
            EventBus.getDefault().post(new FileDeleteEvent(vh.getAdapterPosition(), "DeleteFile"));
        });

        vh.itemView.setOnClickListener(v -> {
            //Conversation conversation1 = item_list.get(position);
            //common.saveConversation(contex,new Gson().toJson(item_list.get(position)));
            //EventBus.getDefault().post(new MessageEventDetailFragment(
                   // conversation1.customerName,common.firstCharactorCapital(conversation1.sender),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid));
        });
    }

    @Override
    public int getItemCount() {

        return item_list.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMultimedia;
        private TextView txtSize, txtName;
        private RelativeLayout layoutDelete;

        public ViewHolder(View view) {
            super(view);

            txtName = view.findViewById(R.id.txtFileName);
            txtSize = view.findViewById(R.id.txtSize);
            ivMultimedia = view.findViewById(R.id.ivFile);
            layoutDelete = view.findViewById(R.id.layoutDelete);

        }
    }


}



