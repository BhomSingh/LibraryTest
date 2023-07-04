package com.chat.befiler.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chat.befiler.Events.appEvents.MessageEventFileDownload;
import com.chat.befiler.commons.Common;
import com.chat.befiler.model.chat.ConversationByUID;
import com.example.signalrtestandroid.R;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ConversationsByUIListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mcontex;
    private final Common common;
    private int viewTypeSenderMessage = 0;
    private int viewTypeReceiverMessage = 1;
    private int viewTypeSystemMessage = 2;
    private int viewTypeImageSenderMessage = 3;
    private int viewTypeImageReceiverMessage = 4;

    private final ArrayList<ConversationByUID> item_list;

    public ConversationsByUIListAdapter(Context contex, ArrayList<ConversationByUID> item_list) {
        this.mcontex = contex;
        this.item_list = item_list;
        this.common = new Common();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType==viewTypeSenderMessage) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_list_loginuser,parent,false);
            viewHolder = new ViewHolder(view);
        }
        else if (viewType==viewTypeReceiverMessage) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_list_nonusermessage,parent,false);
            viewHolder= new ViewHolder(view);
        }
        else if (viewType==viewTypeSystemMessage) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_list_system,parent,false);
            viewHolder= new ViewHolder(view);
        }
        else if (viewType==viewTypeImageSenderMessage) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_conversation_list_loginuser,parent,false);
            viewHolder= new ViewHolderForChatImagesAndFile(view);
        }
        else if (viewType==viewTypeImageReceiverMessage) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_conversation_list_nonusermessage,parent,false);
            viewHolder= new ViewHolderForChatImagesAndFile(view);
        }

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConversationByUID conversation = item_list.get(position);
        if (holder.getItemViewType()==viewTypeImageSenderMessage || holder.getItemViewType() == viewTypeImageReceiverMessage) {
            if (conversation.type.equalsIgnoreCase("file")){
                ViewHolderForChatImagesAndFile viewHolderForChatImagesAndFile = (ViewHolderForChatImagesAndFile) holder;
                if (conversation.files != null && conversation.files.size()>0) {
                        if(!conversation.files.get(0).url.isEmpty()){
                            if(conversation.files.get(0).type != null) {
                                if (conversation.files.get(0).type.equalsIgnoreCase("zip")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.zip);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("rar")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.rar);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("7z")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.sevenz);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("txt")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.txt);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("pdf")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.pdf);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("docx") || conversation.files.get(0).type.equalsIgnoreCase("doc")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.doc);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("xls")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.xls);
                                } else if (conversation.files.get(0).type.equalsIgnoreCase("csv")) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.VISIBLE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.ivMultimedia.setImageResource(R.drawable.xls);
                                } else {
                                    if (!conversation.files.get(0).url.isEmpty()) {
                                        viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.GONE);
                                        viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.VISIBLE);
                                        Glide.with(mcontex).load(conversation.files.get(0).url).into(viewHolderForChatImagesAndFile.image);
                                    }
                                }
                            }else {
                                if (!conversation.files.get(0).url.isEmpty()) {
                                    viewHolderForChatImagesAndFile.layoutFileShow.setVisibility(View.GONE);
                                    viewHolderForChatImagesAndFile.layoutImage.setVisibility(View.VISIBLE);
                                    Glide.with(mcontex).load(conversation.files.get(0).url).into(viewHolderForChatImagesAndFile.image);
                                }
                            }
                            Glide.with(mcontex).load(R.drawable.cloudnew).into(viewHolderForChatImagesAndFile.icSource);
                            viewHolderForChatImagesAndFile.textMessage.setText(conversation.content);
                        }

                        if (conversation.isDownloading){
                            viewHolderForChatImagesAndFile.icSource.setVisibility(View.VISIBLE);
                        }else{
                            viewHolderForChatImagesAndFile.icSource.setVisibility(View.GONE);
                        }


                }
                if (!conversation.timestamp.isEmpty()) {
//                    viewHolderForChatImagesAndFile.txtTime.setText(common.covertTimeToText(mcontex,conversation.timestamp));
                      viewHolderForChatImagesAndFile.txtTime.setText(""+common.covertTimeToLong(conversation.timestamp));


//                      if (!viewHolderForChatImagesAndFile.txtTime.getText().toString().isEmpty()){
//                          if(viewHolderForChatImagesAndFile.txtTime.getText().toString().equalsIgnoreCase("In 0 min.")){
//                              viewHolderForChatImagesAndFile.txtTime.setText(R.string.lbl_jstnow);
//                          }
//                      }
                }
                viewHolderForChatImagesAndFile.itemView.setOnClickListener(v -> {
                    EventBus.getDefault().post(new MessageEventFileDownload("FileDownload",conversation.conversationUid,conversation.content,conversation.files,viewHolderForChatImagesAndFile.getAdapterPosition(),item_list.get(position)));
                });
            }

        }
        else{
            ViewHolder vh = (ViewHolder) holder;
            if (!conversation.type.equalsIgnoreCase("system") && !conversation.type.equalsIgnoreCase("file")){
                if (conversation.content != null && !conversation.content.isEmpty()) {
                    vh.textMessage.setText(conversation.content);
                }
                if (!conversation.timestamp.isEmpty()) {
                     vh.txtTime.setText(""+common.covertTimeToLong(conversation.timestamp));
//                    if (!vh.txtTime.getText().toString().isEmpty()){
//                        if(vh.txtTime.getText().toString().equalsIgnoreCase("In 0 min.")){
//                            vh.txtTime.setText(R.string.lbl_jstnow);
//                        }
//                    }
                }
                if (conversation.sender!=null && !conversation.sender.isEmpty()) {
                    vh.txtName.setText(conversation.sender);
                    if(vh.txtNameFirstLetter!=null){
                        vh.txtNameFirstLetter.setText(common.firstCharactorCapital(conversation.sender));
                    }
                }
            }
            else{
                if (conversation.content != null && !conversation.content.isEmpty()) {
                    vh.textMessage.setText(conversation.content);
                }
                if (!conversation.timestamp.isEmpty()) {
//                    vh.txtTime.setReferenceTime(common.covertTimeToLong(conversation.timestamp));
                    vh.txtTime.setText(""+common.covertTimeToLong(conversation.timestamp));

//                    if (!vh.txtTime.getText().toString().isEmpty()){
//                        if(vh.txtTime.getText().toString().equalsIgnoreCase("In 0 min.")){
//                            vh.txtTime.setText(R.string.lbl_jstnow);
//                        }
//                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {

        return item_list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (item_list.get(position).type.equalsIgnoreCase("system")) {
            return viewTypeSystemMessage;
        }
        else if (item_list.get(position).type.equalsIgnoreCase("file")) {
            if (item_list.get(position).toUserId == Integer.parseInt(common.getUserId(mcontex)) && !item_list.get(position).type.equalsIgnoreCase("system")) {

                return viewTypeImageSenderMessage;
            }else{
                return viewTypeImageReceiverMessage;
            }
        }
        else if (item_list.get(position).toUserId == Integer.parseInt(common.getUserId(mcontex)) && !item_list.get(position).type.equalsIgnoreCase("system")) {
            return viewTypeSenderMessage;
        }else{
            return viewTypeReceiverMessage;
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView  textMessage,txtName,txtNameFirstLetter;
        private ImageView image;
        private TextView txtTime;

        public ViewHolder(View view) {
            super(view);

            txtTime = view.findViewById(R.id.txtTime);
            textMessage = view.findViewById(R.id.textMessage);
            txtName = view.findViewById(R.id.txtName);
            txtNameFirstLetter = view.findViewById(R.id.txtNameFirstLetter);
        }
    }

    protected class ViewHolderForChatImagesAndFile extends RecyclerView.ViewHolder {

        private TextView textMessage;
        private TextView txtTime;
        private ImageView image,ivMultimedia,icSource;
        private LinearLayout layoutFileShow,layoutImage;

        public ViewHolderForChatImagesAndFile(View view) {
            super(view);

            txtTime = view.findViewById(R.id.txtTime);
            textMessage = view.findViewById(R.id.txtMessage);
            image = view.findViewById(R.id.image);
            ivMultimedia = view.findViewById(R.id.ivMultimedia);
            icSource = view.findViewById(R.id.icSource);
            layoutFileShow = view.findViewById(R.id.layoutFileShow);
            layoutImage = view.findViewById(R.id.layoutImage);

        }
    }
}



