package com.chat.befiler.adapters;

import android.content.Context;
import android.graphics.Color;
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


public class ConversationsListingDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context contex;
    private final Common common;
    private final ArrayList<Conversation> item_list;
    public ConversationsListingDetailAdapter(Context contex, ArrayList<Conversation> item_list) {
        this.contex = contex;
        this.item_list = item_list;
        this.common = new Common();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_conversations_detail, parent, false);

        return new ViewHolder(v);
    }

    public ArrayList<Conversation> getItem_list() {
        return item_list;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        Conversation conversation = getItem_list().get(position);
        if (conversation.customerName!=null && !conversation.customerName.isEmpty()) {
            vh.textViewName.setText(common.firstCharactorCapital(conversation.customerName));
        }
        if (!conversation.isRecordUpdated) {
            int colorCodeBg = Integer.parseInt(String.valueOf(conversation.colorCode));
            vh.ivFirstName.setColorFilter(colorCodeBg);

        }
        if (conversation.isNewMessageReceive) {
            vh.ivNotifyMessage.setVisibility(View.VISIBLE);
        }else{
            vh.ivNotifyMessage.setVisibility(View.GONE);
        }
        if (!conversation.customerName.isEmpty()) {
            vh.txtFullName.setText(conversation.customerName);
        }
        vh.ivFirstName.setOnClickListener(v -> {
            Conversation conversation1 = getItem_list().get(position);
            common.saveConversation(contex,new Gson().toJson(getItem_list().get(position)));
            EventBus.getDefault().post(new MessageEventDetailFragment(
                    conversation1.customerName,common.firstCharactorCapital(conversation1.customerName),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid,true));
        });
        vh.txtFullName.setOnClickListener(v -> {
            Conversation conversation1 = getItem_list().get(position);
            common.saveConversation(contex,new Gson().toJson(getItem_list().get(position)));
            EventBus.getDefault().post(new MessageEventDetailFragment(
                    conversation1.customerName,common.firstCharactorCapital(conversation1.customerName),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid,true));
        });
        vh.textViewName.setOnClickListener(v -> {
            Conversation conversation1 = getItem_list().get(position);
            common.saveConversation(contex,new Gson().toJson(getItem_list().get(position)));
            EventBus.getDefault().post(new MessageEventDetailFragment(
                    conversation1.customerName,common.firstCharactorCapital(conversation1.customerName),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid,true));
        });
        vh.lnProfileUser.setOnClickListener(v -> {
            Conversation conversation1 = getItem_list().get(position);
            common.saveConversation(contex,new Gson().toJson(getItem_list().get(position)));
            EventBus.getDefault().post(new MessageEventDetailFragment(
                    conversation1.customerName,common.firstCharactorCapital(conversation1.customerName),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid,true));
        });


        vh.itemView.setOnClickListener(v -> {
            Conversation conversation1 = getItem_list().get(position);
            common.saveConversation(contex,new Gson().toJson(getItem_list().get(position)));
            EventBus.getDefault().post(new MessageEventDetailFragment(
                    conversation1.customerName,common.firstCharactorCapital(conversation1.customerName),conversation1.source,String.valueOf(conversation1.colorCode),conversation1.conversationUid,true));
        });

        vh.lnProfileUser.setOnClickListener(v -> {
            EventBus.getDefault().post(new MessageEvent("isComingFromUserProfileDetailSwitch"));
        });
    }

    @Override
    public int getItemCount() {

        return item_list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutFirstLetter;
        private LinearLayout lnProfileUser;
        private ImageView ivFirstName,ivNotifyMessage;
        private TextView textViewName, txtFullName;

        public ViewHolder(View view) {
            super(view);

            lnProfileUser = view.findViewById(R.id.lnProfileUser);
            layoutFirstLetter = view.findViewById(R.id.layoutFirstLetter);
            textViewName = view.findViewById(R.id.txtName);
            txtFullName = view.findViewById(R.id.txtFullName);
            ivFirstName = view.findViewById(R.id.ivFirstName);
            ivNotifyMessage = view.findViewById(R.id.ivNotifyMessage);

        }
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}



