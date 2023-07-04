package com.chat.befiler.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chat.befiler.Events.appEvents.MessageEvent;
import com.chat.befiler.Events.appEvents.MessageEventDetailFragment;
import com.chat.befiler.commons.Common;
import com.chat.befiler.model.chat.Conversation;
import com.example.signalrtestandroid.R;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Random;


public class ConversationsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context contex;
    private final Common common;
    private final ArrayList<Conversation> item_list;
    public ConversationsListAdapter(Context contex, ArrayList<Conversation> item_list) {
        this.contex = contex;
        this.item_list = item_list;
        this.common = new Common();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_conversations, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        Conversation conversation = item_list.get(position);
        if (conversation.customerName!=null && !conversation.customerName.isEmpty()) {
            vh.textViewName.setText(common.firstCharactorCapital(conversation.customerName));
        }
        if (conversation.status.equalsIgnoreCase("0")){
            vh.layoutNewStatus.setVisibility(View.VISIBLE);
        }else{
            vh.layoutNewStatus.setVisibility(View.GONE);
        }
        if (!conversation.isRecordUpdated) {
            int colorCode = getRandomColor();
            conversation.colorCode = colorCode;
            item_list.set(position,conversation);
            vh.ivFirstName.setColorFilter(colorCode);
        }
        if(conversation.customerName!=null){
            if (!conversation.customerName.isEmpty()) {
                vh.txtFullName.setText(conversation.customerName);
            }
        }
        if (conversation.conversationType == null) {
            conversation.conversationType = "text";
        }
        if (conversation.conversationType != null) {
            if (!conversation.conversationType.isEmpty()) {
                if (conversation.content!=null){
                    if (!conversation.content.isEmpty()) {
                        vh.txtMessage.setText(conversation.content);
                    }
                }
                if (conversation.conversationType.equalsIgnoreCase("multimedia")) {
                        vh.txtMessage.setVisibility(View.GONE);
                        vh.ivMultimedia.setVisibility(View.VISIBLE);
                        vh.ivMultimedia.setImageResource(R.drawable.image);
                } else if (conversation.conversationType.equalsIgnoreCase("file")) {
                        vh.txtMessage.setVisibility(View.GONE);
                        vh.ivMultimedia.setVisibility(View.VISIBLE);
                        vh.ivMultimedia.setImageResource(R.drawable.multimedia);
                } else {
                        vh.ivMultimedia.setVisibility(View.GONE);
                        vh.txtMessage.setVisibility(View.VISIBLE);
                }

            }
        }
        if (conversation.source != null) {
            if (!conversation.source.isEmpty()) {
                if (conversation.source.equalsIgnoreCase("web")) {
                    vh.ivSource.setImageResource(R.drawable.ic_web);
                }
                if (conversation.source.equalsIgnoreCase("whatsapp")) {
                    vh.ivSource.setImageResource(R.drawable.ic_whatsapp);

                }
                if (conversation.source.equalsIgnoreCase("facebook")) {
                    vh.ivSource.setImageResource(R.drawable.ic_facebook_messenger);
                }
            }
        }
        if(conversation.groupName!=null){
            if (!conversation.groupName.isEmpty()) {
                vh.txttype.setText(conversation.groupName);
            }
        }
        if (!conversation.timestamp.isEmpty()) {
              vh.txtDate.setText(""+common.covertTimeToLong(conversation.timestamp));
        }

        vh.itemView.setOnClickListener(v -> {
            Conversation conversation1 = getItem_list().get(position);
            common.saveConversationList(contex,new Gson().toJson(getItem_list()));
            common.saveConversation(contex,new Gson().toJson(getItem_list().get(position)));
            if (!conversation1.customerName.isEmpty()){
                EventBus.getDefault().post(new MessageEventDetailFragment(
                        conversation1.customerName,common.firstCharactorCapital(conversation1.customerName),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid,false));
            }
        });

        vh.lnProfileUser.setOnClickListener(v -> {
            EventBus.getDefault().post(new MessageEvent("isComingFromUserProfileDetailSwitch"));
        });
    }

    @Override
    public int getItemCount() {

        return item_list.size();
    }

    public ArrayList<Conversation> getItem_list() {
        return item_list;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutFirstLetter;
        private LinearLayout lnProfileUser;
        private ImageView ivMultimedia, ivSource,ivFirstName;
        private TextView textViewName, txtFullName, txtMessage, txttype;
        private TextView txtDate;
        private LinearLayout layoutNewStatus;

        public ViewHolder(View view) {
            super(view);

            lnProfileUser = view.findViewById(R.id.lnProfileUser);
            layoutFirstLetter = view.findViewById(R.id.layoutFirstLetter);
            textViewName = view.findViewById(R.id.txtName);
            txtFullName = view.findViewById(R.id.txtFullName);
            txtMessage = view.findViewById(R.id.txtMessage);
            txtDate = view.findViewById(R.id.txtDate);
            txttype = view.findViewById(R.id.txttype);
            ivMultimedia = view.findViewById(R.id.ivMultimedia);
            ivSource = view.findViewById(R.id.ivSource);
            ivFirstName = view.findViewById(R.id.ivFirstName);
            layoutNewStatus = view.findViewById(R.id.layoutNewStatus);

        }
    }


    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}



