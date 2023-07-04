package com.chat.befiler.fragments;

import static com.chat.befiler.commons.Constants.Assign_Agent;
import static com.chat.befiler.commons.Constants.Assign_Group;
import static com.chat.befiler.commons.Constants.COLOR_CODE_KEY;
import static com.chat.befiler.commons.Constants.HIDE_TOOLBAR;
import static com.chat.befiler.commons.Constants.NAME_KEY;
import static com.chat.befiler.commons.Constants.NAME_LETTER_KEY;
import static com.chat.befiler.commons.Constants.Reopen_Conversation;
import static com.chat.befiler.commons.Constants.Resolve_Conversation;
import static com.chat.befiler.commons.Constants.SOURCE_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chat.befiler.Events.appEvents.FileDeleteEvent;
import com.chat.befiler.Events.appEvents.MessageEventFileDownload;
import com.chat.befiler.Events.chatEvents.AllConversationCountEvent;
import com.chat.befiler.Events.chatEvents.AssignedChatEvent;
import com.chat.befiler.Events.chatEvents.ConversationEvent;
import com.chat.befiler.Events.appEvents.MessageEvent;
import com.chat.befiler.Events.chatEvents.ConversationStatusEvent;
import com.chat.befiler.Events.chatEvents.ConversationStatusListenerEvent;
import com.chat.befiler.Events.chatEvents.ReceiveMessageEvent;
import com.chat.befiler.Events.chatEvents.SendChatEvent;
import com.chat.befiler.activities.MainActivity;
import com.chat.befiler.adapters.ConversationsByUIListAdapter;
import com.chat.befiler.adapters.ConversationsListAdapter;
import com.chat.befiler.adapters.ConversationsListingDetailAdapter;
import com.chat.befiler.adapters.FileDataClass;
import com.chat.befiler.adapters.SelectedFilesListAdapter;
import com.chat.befiler.commons.Common;
import com.chat.befiler.commons.Constants;
import com.chat.befiler.commons.FileUtil;
import com.chat.befiler.commons.PaginationScrollListener;
import com.chat.befiler.commons.PermissionHelper;
import com.chat.befiler.commons.Utils;
import com.chat.befiler.model.chat.AgentsByGroupIdModel;
import com.chat.befiler.model.chat.AssignChatListener;
import com.chat.befiler.model.chat.AssignChatListenerRequest;
import com.chat.befiler.model.chat.Conversation;
import com.chat.befiler.model.chat.ConversationByUID;
import com.chat.befiler.model.chat.ConversationStatusListenerDataModel;
import com.chat.befiler.model.chat.ConversationStatusModel;
import com.chat.befiler.model.chat.GroupsDataModel;
import com.chat.befiler.model.chat.OnErrorData;
import com.chat.befiler.model.chat.RecieveMessage;
import com.chat.befiler.model.chat.SendMessageModel;
import com.chat.befiler.model.chat.UploadFilesData;
import com.chat.befiler.retrofit.ApiClient;
import com.chat.befiler.retrofit.GroupsByUserDataModel;
import com.chat.befiler.retrofit.WebResponse;
import com.chat.befiler.retrofit.WebResponse2;
import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.example.signalrtestandroid.R;
import com.example.signalrtestandroid.databinding.FragmentConversationsDetailBinding;
import com.google.gson.Gson;
import com.vanniktech.emoji.EmojiPopup;
import com.wang.avi.AVLoadingIndicatorView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */

public class ConversationsDetailFragment extends Fragment {

    ArrayList<UploadFilesData> uploadFilesData;
    ArrayList<FileDataClass> filesNames = null;
    ArrayList<MultipartBody.Part> filePart = null;
    File fileTemp = null;
    Handler handler = null;
    Runnable myRunnable = null;
    public String mCurrentPhotoPath = "";
    public int selectGroupId = -1;
    public int selectAgentId = -1;
    public String selectGroupName = "";
    public String selectAgentName = "";
    ArrayList<String> arrayListGroupName;
    ArrayList<Integer> arrayListGroupId;
    ArrayList<String> arrayListAgentName;
    ArrayList<Integer> arrayListAgentId;
    int pageNumber = 1;
    int pageSize = 15;
    int totalPages = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    //init layout manager for list
    LinearLayoutManager mLayoutManager;
    FragmentConversationsDetailBinding fragmentConversationsBinding;
    private int CAPTURE_PICTURE_FROM_CAMERA = 2;
    private int PICK_IMAGE_FOR_SELECT = 3;
    Common common;
    String conversationByUID = "";
    String conversationByUIDFromAssign = "";
    int addedConversationPos = -1;
    Context mContext;
    ConversationsByUIListAdapter conversationsListAdapter = null;
    SelectedFilesListAdapter selectedFilesListAdapter = null;
    ArrayList<ConversationByUID> conversationArrayList ;
    ConversationsListingDetailAdapter conversationsListingDetailAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().post(new MessageEvent(HIDE_TOOLBAR));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().post(new MessageEvent(HIDE_TOOLBAR));
        mContext = context;
    }

    Conversation conversation = null;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentConversationsBinding = FragmentConversationsDetailBinding.inflate(getLayoutInflater());
        View view = fragmentConversationsBinding.getRoot();

        common = new Common();
        filesNames = new ArrayList<>();
        filePart = new ArrayList<>();
        conversationArrayList = new ArrayList<>();
        conversation = common.getConversationData(mContext);
        final EmojiPopup popup = EmojiPopup.Builder
                .fromRootView(view).build(fragmentConversationsBinding.edtMessage);
        if (getArguments()!=null){
            Bundle bundle = getArguments();
            if(bundle.containsKey(NAME_LETTER_KEY) && bundle.containsKey(NAME_KEY) && bundle.containsKey(SOURCE_KEY) && bundle.containsKey(COLOR_CODE_KEY) && bundle.containsKey(Constants.CONVERSATION_BY_UID_KEY)){
                if (bundle.getString(NAME_LETTER_KEY)!=null){
                    fragmentConversationsBinding.txtNameFirstLetter.setText(bundle.getString(NAME_LETTER_KEY));
                }
                if (bundle.getString(Constants.CONVERSATION_BY_UID_KEY)!=null){
                    conversationByUID = bundle.getString(Constants.CONVERSATION_BY_UID_KEY);
                }
                if (bundle.getString(SOURCE_KEY)!=null){
                    fragmentConversationsBinding.tvSource.setText(getString(R.string.fromlabel)+" "+bundle.getString(SOURCE_KEY));
                }
                if (bundle.getString(NAME_KEY)!=null){
                    fragmentConversationsBinding.tvName.setText(bundle.getString(NAME_KEY));
                }
                if (bundle.getString(COLOR_CODE_KEY)!=null){
                    if(!bundle.getString(COLOR_CODE_KEY).isEmpty()){
                        int colorCodeBg = Integer.parseInt(bundle.getString(COLOR_CODE_KEY));
                        fragmentConversationsBinding.ivFirstName.setColorFilter(colorCodeBg);
                    }
                }
            }
        }
        if (conversation!=null){
            if (conversation.isResolved){
                fragmentConversationsBinding.layoutReopenClick.setVisibility(View.VISIBLE);
                fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.GONE);
                fragmentConversationsBinding.layoutTyping.setVisibility(View.GONE);
            }else{
                fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.VISIBLE);
                fragmentConversationsBinding.layoutReopenClick.setVisibility(View.GONE);
                fragmentConversationsBinding.layoutTyping.setVisibility(View.VISIBLE);
            }
        }
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true);
        //mLayoutManager.setStackFromEnd(true);
        fragmentConversationsBinding.rvConversations.setLayoutManager(mLayoutManager);
        LinearLayoutManager layoutManagerSelectFiles = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
        fragmentConversationsBinding.rvSelections.setLayoutManager(layoutManagerSelectFiles);


        fragmentConversationsBinding.menuClick.setOnClickListener(v -> {
            EventBus.getDefault().post(new MessageEvent("SwitchToConversationList"));

        });
        fragmentConversationsBinding.layoutReopenClick.setOnClickListener(v -> {
            reOpenDialog(mContext);
        });
        fragmentConversationsBinding.layoutResolvedClick.setOnClickListener(v -> {

            ConversationStatusModel conversationStatusModel = new ConversationStatusModel();
            conversationStatusModel.conversationUid = conversationByUID;
            conversationStatusModel.Status = 2;
            conversationStatusModel.agentName = common.getUserLoginDate(mContext).userName;
            conversationStatusModel.agentId = Integer.parseInt(common.getUserId(mContext));
            conversationStatusModel.groupName = common.getConversationData(mContext).groupName;;
            EventBus.getDefault().post(new ConversationStatusEvent(conversationStatusModel,"ResolvedConversation"));
        });

        fragmentConversationsBinding.sendMessage.setOnClickListener(v -> {
            if (!filesNames.isEmpty()){
                fragmentConversationsBinding.ivSendMessage.setVisibility(View.GONE);
                fragmentConversationsBinding.progressSend.setVisibility(View.VISIBLE);
                uploadFiles(conversationByUID,filePart);
            }else{
                if (!fragmentConversationsBinding.edtMessage.getText().toString().isEmpty()){

                    sendMessage("text",conversation,fragmentConversationsBinding.edtMessage.getText().toString(),uploadFilesData);
                    fragmentConversationsBinding.edtMessage.setText("");
                }else{
                    Toast.makeText(getActivity(), "Please type a message", Toast.LENGTH_SHORT).show();
                }

            }
        });
        fragmentConversationsBinding.layoutImageUpload.setOnClickListener(v -> {
            uploadImageDialog(getContext());
        });
        fragmentConversationsBinding.layoutFileAttach.setOnClickListener(v -> {
            storagePermission(false,true,null);

        });
        fragmentConversationsBinding.ivSmile.setOnClickListener(v -> {
            fragmentConversationsBinding.ivSmile.setVisibility(View.GONE);
            fragmentConversationsBinding.ivKeyboard.setVisibility(View.VISIBLE);
            popup.toggle();
        });
        fragmentConversationsBinding.ivKeyboard.setOnClickListener(v -> {
            fragmentConversationsBinding.ivKeyboard.setVisibility(View.GONE);
            fragmentConversationsBinding.ivSmile.setVisibility(View.VISIBLE);
            popup.dismiss();
        });

        //init layout manager for main list
        LinearLayoutManager layoutManagerList = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        fragmentConversationsBinding.rvConversationList.setLayoutManager(layoutManagerList);

        String isSuperAdmin = "";
        if (!common.getIsSuperAdmin(mContext).isEmpty()) {
            isSuperAdmin = common.getIsSuperAdmin(mContext);
        }
        if(isSuperAdmin.equalsIgnoreCase("true")){
            //get all groups
            getAllGroups(true);
        }else{
            //get group by userId
            getAllGroupsByUserId(Integer.parseInt(!common.getUserId(mContext).isEmpty() ? common.getUserId(mContext) : "0"));
        }

        //api for all conversations
        getConversationByUID(pageNumber,pageSize,conversationByUID,"0",false);

        KeyboardVisibilityEvent.setEventListener(getActivity(), isOpen -> {
            if (isOpen){
                fragmentConversationsBinding.ivSmile.setVisibility(View.GONE);
                fragmentConversationsBinding.ivKeyboard.setVisibility(View.VISIBLE);
            } else{
                fragmentConversationsBinding.ivSmile.setVisibility(View.VISIBLE);
                fragmentConversationsBinding.ivKeyboard.setVisibility(View.GONE);
            }

        });
        if(!isSuperAdmin.equalsIgnoreCase("true")) {
            if (checkAgentPermissionContainForAssign(Resolve_Conversation) || checkAgentPermissionContainForAssign(Reopen_Conversation)) {
                if (conversation.isResolved){
                    fragmentConversationsBinding.layoutReopenClick.setVisibility(View.VISIBLE);
                    fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.GONE);
                }else{
                    fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.VISIBLE);
                    fragmentConversationsBinding.layoutReopenClick.setVisibility(View.GONE);
                }
            } else {
                fragmentConversationsBinding.layoutReopenClick.setVisibility(View.GONE);
                fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.GONE);
            }

            if (checkAgentPermissionContainForAssign(Assign_Agent) || checkAgentPermissionContainForAssign(Assign_Group)) {
                fragmentConversationsBinding.layoutGroupAndUsers.setVisibility(View.VISIBLE);
            } else {
                fragmentConversationsBinding.layoutGroupAndUsers.setVisibility(View.GONE);
            }
        }

        if (common.getSelectedMenu(mContext).equalsIgnoreCase(Constants.RESOLVED)){
            fragmentConversationsBinding.layoutGroupAndUsers.setVisibility(View.GONE);
        }

        fragmentConversationsBinding.spinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    selectGroupId = -1;
                    selectGroupName = "";
                }else{
                    fragmentConversationsBinding.layoutAgentSpinner.setVisibility(View.VISIBLE);
                    selectGroupId = arrayListGroupId.get(i);
                    selectGroupName = arrayListGroupName.get(i);
                    getAgentsByGroupId(selectGroupId);
                }
                if(i==0){
                    if (adapterView.getChildAt(0) != null) {
                        ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.LTGRAY);
                        ((TextView) adapterView.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                    }
                } else {
                    if (adapterView.getChildAt(0) != null) {
                        ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) adapterView.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                        ((TextView) adapterView.getChildAt(0)).setTextSize(12f);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        fragmentConversationsBinding.spinnerAgents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i==0){
                    selectAgentId = -1;
                    selectAgentName = "";
                }else{
                    selectAgentId = arrayListAgentId.get(i);
                    selectAgentName = arrayListAgentName.get(i);
                    ArrayList<Long> arrayListGroupId = new ArrayList<Long>();
                    arrayListGroupId.add(Long.parseLong(String.valueOf(selectGroupId)));
                    EventBus.getDefault().post(new AssignChatListenerRequest(arrayListGroupId,conversation.conversationId,selectGroupName,Long.parseLong(String.valueOf(selectAgentId)),"AssignChat"));
                }
                if(i==0){
                    if (adapterView.getChildAt(0) != null) {
                        ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.LTGRAY);
                        ((TextView) adapterView.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                    }
                }
                else {
                    if (adapterView.getChildAt(0) != null) {
                        ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) adapterView.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                        ((TextView) adapterView.getChildAt(0)).setTextSize(12f);

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        fragmentConversationsBinding.rvConversations.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                // check weather is last page or not
                if (pageNumber<=totalPages){
                    pageNumber++;
                    isLoading = true;
                    fragmentConversationsBinding.loadMoreProgress.setVisibility(View.VISIBLE);
//                    getAllConversation(pageNumber,pageSize,common.getUserId(mContext),common.getIsSuperAdmin(mContext),se,false);
                    //api for all conversations
//                    getConversationByUID(pageNumber,pageSize,conversationByUID,common.getUserId(mContext),false);
                    getConversationByUID(pageNumber,pageSize,conversationByUID,"0",false);
                } else {
                    isLoading = false;
                    isLastPage = true;
                }
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        fragmentConversationsBinding.jumptoBottom.setOnClickListener(view1 -> {
            scrollToBottom();
        });

        fragmentConversationsBinding.layoutFirstLetter.setOnClickListener(view1 -> {
            EventBus.getDefault().post(new MessageEvent("isComingFromUserProfileDetailSwitch"));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MessageEvent(HIDE_TOOLBAR));

    }

    public void getConversationByUID(int pageNumber,int pageSize,String conversationByUID,String customerId,boolean isCalledFromAssigned) {
        if(pageNumber==1){
            if (isAdded()){
                if(getActivity()!=null){
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        }

        new ApiClient(getContext()).getWebService().getConversationByUID(pageNumber,pageSize,conversationByUID,customerId).enqueue(new Callback<WebResponse2<ArrayList<ConversationByUID>>>() {
            @Override
            public void onResponse(Call<WebResponse2<ArrayList<ConversationByUID>>> call, Response<WebResponse2<ArrayList<ConversationByUID>>> response) {
                if(response.code()==200){
                    if(response.body().getResult().size()>0){
                        totalPages = response.body().getTotalPages();
                        if(pageNumber==1){
                            if(response.body().getResult().size()>0) {
                                conversationArrayList = response.body().getResult();
                                conversationsListAdapter = new ConversationsByUIListAdapter(mContext,conversationArrayList);
                                fragmentConversationsBinding.rvConversations.setAdapter(conversationsListAdapter);
                                scrollToBottom();
                            }
                        }else {
                            // check weather is last page or not
                            if (pageNumber <totalPages) {
                                //show loader here
                                    fragmentConversationsBinding.loadMoreProgress.setVisibility(View.VISIBLE);
                            } else {
                                isLastPage = true;
                            }
                            fragmentConversationsBinding .jumptoBottom.setVisibility(View.VISIBLE);
                            isLoading = false;
                            if(response.body().getResult().size()>0){
                                conversationArrayList.addAll(response.body().getResult());
                                conversationsListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else{
                    if(response.code()==401){
//                        switch to login
                    }
                }
                if (isAdded()){
                    if(getActivity()!=null){
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() ->
                                fragmentConversationsBinding.loadMoreProgress.setVisibility(View.GONE)
                        , 500);
            }

            @Override
            public void onFailure(Call<WebResponse2<ArrayList<ConversationByUID>>> call, Throwable t) {
                if (isAdded()){
                    if(getActivity()!=null){
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
                if (t.getMessage().contains("Failed to connect")){
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
            }
        });
    }

    public void uploadFiles(String conversationByUID,ArrayList<MultipartBody.Part> partArrayList){
        if (getActivity().getWindow() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        new ApiClient(mContext).getWebService().uploadFiles(conversationByUID,partArrayList).enqueue(new Callback<WebResponse<ArrayList<UploadFilesData>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<UploadFilesData>>> call, Response<WebResponse<ArrayList<UploadFilesData>>> response) {
                if (response!=null){
                    if(response.code()==200){
                        if(response.isSuccessful() && response.body()!=null){
                            uploadFilesData = new ArrayList<>();
                            if(response.body().getResult()!=null){
                                uploadFilesData = response.body().getResult();
                                //send message here
                                if(uploadFilesData.size()>0){
                                    if (common!=null && common.getConversationData(mContext)!=null){
                                        Conversation conversation = common.getConversationData(mContext);
                                        sendMessage("file",conversation,fragmentConversationsBinding.edtMessage.getText().toString(),uploadFilesData);
                                        fragmentConversationsBinding.edtMessage.setText("");
                                    }
                                }
                                hideSelectedFilesLayout();
                            }
                        }
                    }else{
                        if (response.code()==401){

                        }else{

                        }
                    }

                }
                if(getActivity().getWindow()!=null){
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
            @Override
            public void onFailure(Call<WebResponse<ArrayList<UploadFilesData>>> call, Throwable t) {
                if(getActivity().getWindow()!=null){
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                if (t.getMessage().contains("Failed to connect")){
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void hideSelectedFilesLayout(){
        if(!filesNames.isEmpty()){
            filesNames.clear();
        }
        if(!filePart.isEmpty()){
            filePart.clear();
        }
        if(selectedFilesListAdapter!=null){
            selectedFilesListAdapter.notifyDataSetChanged();
        }
        fragmentConversationsBinding.layoutSelectedFiles.setVisibility(View.GONE);
    }
    public void scrollToBottom(){
        fragmentConversationsBinding .jumptoBottom.setVisibility(View.GONE);
        fragmentConversationsBinding.rvConversations.postDelayed(() -> fragmentConversationsBinding.rvConversations.scrollToPosition(0), 200);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AssignedChatEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("AssignChatToAnother")){
                if (event.assignChatListener.conversationUId.equalsIgnoreCase(conversationByUID)){
                    fragmentConversationsBinding.layoutTyping.setVisibility(View.GONE);
                    //Remove item becuase it assigned to another agent by admin side
                    if(conversationsListAdapter!=null && !conversationArrayList.isEmpty() && fragmentConversationsBinding!=null) {
                        if (isContainAssignChat(conversationArrayList, event.assignChatListener)) {
                            conversationArrayList.add(fragmentConversationsBinding.rvConversations.getAdapter().getItemCount(), getConversationFromAssignListener(event.assignChatListener));
                            conversationsListAdapter.notifyItemInserted(fragmentConversationsBinding.rvConversations.getAdapter().getItemCount());
                            scrollToBottom();
                        } else {
                            conversationArrayList.add(getConversationFromAssignListener(event.assignChatListener));
                            conversationsListAdapter = new ConversationsByUIListAdapter(mContext, conversationArrayList);
                            fragmentConversationsBinding.rvConversations.setAdapter(conversationsListAdapter);
                        }
                    }
                }else{
                    fragmentConversationsBinding.layoutTyping.setVisibility(View.VISIBLE);
                }
                handler = new Handler();
                myRunnable = () -> {

                    EventBus.getDefault().post(new MessageEvent("SwitchToConversationList"));

                };
                handler.postDelayed(myRunnable, 1500);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FileDeleteEvent event) {
        if (!event.eventType.isEmpty()) {
            if (event.eventType.equalsIgnoreCase("DeleteFile")) {
                if(event.position!=-1){
                    if(!filesNames.isEmpty() && !filePart.isEmpty()){
                        filesNames.remove(event.position);
                        filePart.remove(event.position);
                        if(selectedFilesListAdapter!=null){
                            selectedFilesListAdapter.notifyItemRemoved(event.position);
                        }
                        if(filesNames.isEmpty() && filePart.isEmpty()){
                            hideSelectedFilesLayout();
                        }
                    }

                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventFileDownload event) {
        if (!event.eventType.isEmpty()) {
            if (event.eventType.equalsIgnoreCase("FileDownload")) {
                storagePermission(event);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceiveMessageEvent event) {
        if (!event.eventType.isEmpty()) {
            if (event.eventType.equalsIgnoreCase("ReceiveMessage")) {
                if(conversationByUID.equalsIgnoreCase(event.recieveMessage.conversationUid)){
                    if(conversationsListAdapter!=null && !conversationArrayList.isEmpty() && fragmentConversationsBinding!=null){
                        if (isContainConversationByUId(conversationArrayList,event.recieveMessage)){
                            conversationArrayList.add(0, getConversationFromReceiveMsg(event.recieveMessage));
                            conversationsListAdapter.notifyItemInserted(0);
                            scrollToBottom();
                        } else{
                            conversationArrayList.add(getConversationFromReceiveMsg(event.recieveMessage));
                            conversationsListAdapter = new ConversationsByUIListAdapter(mContext,conversationArrayList);
                            fragmentConversationsBinding.rvConversations.setAdapter(conversationsListAdapter);
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnErrorData event) {
        if (!event.eventType.isEmpty()) {
            if (event.eventType.equalsIgnoreCase("OnErrorMessage")) {
                    if (event.recieveMessage!=null){
                        if(event.recieveMessage.content!=null && !event.recieveMessage.content.isEmpty()){
                            Toast.makeText(mContext, event.recieveMessage.content, Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    }

    public ConversationByUID getConversationFromReAssignListener(Conversation assignChatListener){
        ConversationByUID conversationByUID = new ConversationByUID();
        conversationByUID.conversationType = "text";
        conversationByUID.type = "system";
        conversationByUID.conversationUid = assignChatListener.conversationUid;
        conversationByUID.toUserId = 0;
        conversationByUID.customerName = "";
        conversationByUID.sender = "";
        conversationByUID.agentId = 0;
        conversationByUID.customerId = assignChatListener.customerId;
        if (!assignChatListener.sender.isEmpty()){
            conversationByUID.content = "This chat has been assigned to agent "+assignChatListener.sender.substring(0, 1).toUpperCase()+assignChatListener.sender.substring(1);;
        }else{
            conversationByUID.content = "This chat has been assigned to agent "+assignChatListener.sender;
        }
        conversationByUID.files = new ArrayList<>();
        conversationByUID.fromUserId = 0;
        conversationByUID.isFromWidget = false;
        conversationByUID.isPrivate = false;
        conversationByUID.groupId = assignChatListener.groupId;
        conversationByUID.groupName = assignChatListener.groupName;
        conversationByUID.timestamp = assignChatListener.timestamp;
        conversationByUID.receiver = "";
        conversationByUID.pageId = "";
        conversationByUID.pageName = "";
        conversationByUID.tiggerevent = 0;

        return conversationByUID;
    }

    public ConversationByUID getConversationFromResolveListener(ConversationStatusListenerDataModel conversationStatusListenerDataModel){
        Conversation conversation = null;
        ConversationByUID conversationByUID = null;
        if (common.getConversationData(mContext)!=null){
             conversation = common.getConversationData(mContext);
        }
        if (conversation!=null && conversationStatusListenerDataModel!=null){
            conversationByUID = new ConversationByUID();
            conversationByUID.conversationType = "text";
            conversationByUID.type = "system";
            conversationByUID.conversationUid = conversation.conversationUid;
            conversationByUID.toUserId = 0;
            conversationByUID.customerName = "";
            conversationByUID.sender = "";
            conversationByUID.agentId = 0;
            conversationByUID.customerId = conversation.customerId;
            conversationByUID.content = conversationStatusListenerDataModel.notifyMessage;
            conversationByUID.files = new ArrayList<>();
            conversationByUID.fromUserId = 0;
            conversationByUID.isFromWidget = false;
            conversationByUID.isPrivate = false;
            conversationByUID.groupId = conversation.groupId;
            conversationByUID.groupName = conversation.groupName;
            conversationByUID.receiver = "";
            conversationByUID.pageId = "";
            conversationByUID.pageName = "";
            conversationByUID.tiggerevent = 0;
            conversationByUID.timestamp = conversationStatusListenerDataModel.timestamp;
        }

        return conversationByUID;
    }

    public ConversationByUID getConversationFromAssignListener(AssignChatListener assignChatListener){
        ConversationByUID conversationByUID = new ConversationByUID();
        conversationByUID.conversationType = "text";
        conversationByUID.type = "system";
        conversationByUID.conversationUid = assignChatListener.conversationUId;
        conversationByUID.toUserId = 0;
        conversationByUID.customerName = "";
        conversationByUID.sender = "";
        conversationByUID.agentId = 0;
        conversationByUID.customerId = assignChatListener.customerId;
        conversationByUID.content = assignChatListener.notifyMessage;
        conversationByUID.files = new ArrayList<>();
        conversationByUID.fromUserId = 0;
        conversationByUID.isFromWidget = false;
        conversationByUID.isPrivate = false;
        conversationByUID.groupId = assignChatListener.groupId;
        conversationByUID.groupName = assignChatListener.groupName;
        conversationByUID.timestamp = assignChatListener.timestamp;
        conversationByUID.receiver = "";
        conversationByUID.pageId = "";
        conversationByUID.pageName = "";
        conversationByUID.tiggerevent = 0;

        return conversationByUID;
    }

    public ConversationByUID getConversationFromReceiveMsg(RecieveMessage recieveMessage){
        ConversationByUID conversationByUID = new ConversationByUID();
        conversationByUID.conversationType = recieveMessage.type.equalsIgnoreCase("file") ? "multimedia" : "text";
        conversationByUID.type = recieveMessage.type;
        conversationByUID.conversationUid = recieveMessage.conversationUid;
        conversationByUID.toUserId = recieveMessage.toUserId;
        conversationByUID.customerName = recieveMessage.customerName;
        conversationByUID.sender = recieveMessage.sender;
        conversationByUID.agentId = recieveMessage.agentId;
        conversationByUID.customerId = recieveMessage.customerId;
        conversationByUID.content = recieveMessage.content;
        conversationByUID.files = recieveMessage.files;
        conversationByUID.fromUserId = recieveMessage.fromUserId;
        conversationByUID.isFromWidget = recieveMessage.isFromWidget;
        conversationByUID.isPrivate = recieveMessage.isPrivate;
        conversationByUID.groupId = recieveMessage.groupId;
        conversationByUID.groupName = recieveMessage.groupName;
        conversationByUID.timestamp = recieveMessage.timestamp;
        conversationByUID.receiver = recieveMessage.receiver;
        conversationByUID.pageId = recieveMessage.pageId;
        conversationByUID.pageName = recieveMessage.pageName;
        conversationByUID.tiggerevent = recieveMessage.tiggerevent;
        return conversationByUID;
    }

    public boolean isContainReAssignChat(ArrayList<ConversationByUID> conversationArrayList, Conversation conversation){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).customerId == conversation.customerId){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }
    public boolean isContainAssignChat(ArrayList<ConversationByUID> conversationArrayList, AssignChatListener assignChatListener){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).customerId == assignChatListener.customerId){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }
    public boolean isContainConversationByUId(ArrayList<ConversationByUID> conversationArrayList, RecieveMessage conversation){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).customerId == conversation.customerId){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        common.setIsUnAssign(mContext,false);
        EventBus.getDefault().post(new MessageEvent("ShowToolbar"));
    }

    public void sendMessage(String type,Conversation conversation,String txtMessage,ArrayList<UploadFilesData> uploadFilesData){
        long cusId;
        if (conversationArrayList.size()>0){
            cusId = conversationArrayList.get(0).customerId;
        }else{
            cusId = conversation.customerId;
        }

        if (type.equalsIgnoreCase("file")){
            if(uploadFilesData.size()>0){
                for (int i=0;i<uploadFilesData.size();i++){
                    SendMessageModel sendMessageModel = new SendMessageModel();
                    sendMessageModel.agentId = Long.parseLong(common.getUserId(mContext));
                    sendMessageModel.conversationUid = conversation.conversationUid ;
                    sendMessageModel.conversationId = conversation.conversationUid ;
                    sendMessageModel.customerId = cusId ;
                    sendMessageModel.message = uploadFilesData.get(i).documentOrignalName;
                    sendMessageModel.receiverConnectionId =  String.valueOf(conversation.customerConnectionId);
                    sendMessageModel.receiverName = conversation.customerName ;
                    sendMessageModel.isFromWidget = false ;
                    sendMessageModel.type = type;
                    sendMessageModel.groupId = conversation.groupId;
                    sendMessageModel.conversationType = type.equalsIgnoreCase("file") ? "multimedia" : "text";
                    sendMessageModel.documentOrignalname = uploadFilesData.get(i).documentOrignalName;
                    sendMessageModel.documentName = uploadFilesData.get(i).documentName;
                    sendMessageModel.documentType = uploadFilesData.get(i).documentType ;
                    sendMessageModel.icon = "" ;
                    sendMessageModel.pageId = "" ;
                    sendMessageModel.pageName = "" ;
                    EventBus.getDefault().post(new SendChatEvent(sendMessageModel,"SendNewMessage"));
                    if (uploadFilesData.size()-1 == i){
                        fragmentConversationsBinding.ivSendMessage.setVisibility(View.VISIBLE);
                        fragmentConversationsBinding.progressSend.setVisibility(View.GONE);
                    }
                }
                if(!txtMessage.isEmpty()){
                    sendMessage("text",conversation,fragmentConversationsBinding.edtMessage.getText().toString(),uploadFilesData);
                }
            }

        }else{
            SendMessageModel sendMessageModel = new SendMessageModel();
            sendMessageModel.customerId = cusId ;
            sendMessageModel.agentId = Long.parseLong(common.getUserId(mContext));
            sendMessageModel.conversationUid = conversation.conversationUid ;
            sendMessageModel.conversationId = conversation.conversationUid ;
            sendMessageModel.message = txtMessage;
            sendMessageModel.receiverConnectionId =  String.valueOf(conversation.customerConnectionId);
            sendMessageModel.receiverName = conversation.customerName ;
            sendMessageModel.isFromWidget = false ;
            sendMessageModel.type = type;
            sendMessageModel.groupId = conversation.groupId;
            sendMessageModel.conversationType = type.equalsIgnoreCase("file") ? "multimedia" : "text";
            sendMessageModel.documentOrignalname = "" ;
            sendMessageModel.documentName = "";
            sendMessageModel.documentType = "" ;
            sendMessageModel.icon = "" ;
            sendMessageModel.pageId = "" ;
            sendMessageModel.pageName = "" ;
            EventBus.getDefault().post(new SendChatEvent(sendMessageModel,"SendNewMessage"));
        }
    }

    // Image Picker Gallery And Camira Open
    private void uploadImageDialog(final Context context) {
        Dialog dialog = new Dialog(context);
        View newUserView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        newUserView = inflater.inflate(R.layout.upload_img_dialoge, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newUserView.setBackground(ContextCompat.getDrawable(context, R.drawable.round_border_rectangle));
        dialog.setContentView(newUserView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        //show dialog
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        ImageView btnSelectImg = newUserView.findViewById(R.id.btnSelectImg);
        ImageView btnCaptureImg = newUserView.findViewById(R.id.btnCaptureImg);
        ImageView btnCancel = newUserView.findViewById(R.id.btnCancel);

        btnSelectImg.setOnClickListener(view -> {
            // select image from gallery
            storagePermission(true,false,dialog);
        });

        btnCaptureImg.setOnClickListener(view -> {
            // open camera
            storagePermission(false,false,dialog);
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();

    }

    private void storagePermission(MessageEventFileDownload messageEventFileDownload){
        ArrayList<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        PermissionHelper.grantMultiplePermissions(getActivity(), permissionList, new PermissionHelper.PermissionInterface() {
            @Override
            public void onSuccess() {
                if (!messageEventFileDownload.files.isEmpty()){
                    if (!messageEventFileDownload.files.get(0).url.isEmpty()) {
                        String rootPath = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/BefilerChat/";
                        File root = new File(rootPath);
                        if (!root.exists()) {
                            root.mkdirs();
                        }
                        File f = new File(rootPath + messageEventFileDownload.documentName);
                        if (f.exists()) {
                            //open file here
                            try {
                                common.openFile(mContext,f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{

                            ConversationByUID conversationByUID = messageEventFileDownload.conversationByUID;
                            DownloadImpl.getInstance(mContext)
                                    .url(messageEventFileDownload.files.get(0).url).target(f)
                                    .enqueue(new DownloadListenerAdapter() {
                                        @Override
                                        public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                                            super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
                                            if (messageEventFileDownload.conversationByUID!=null){
                                                conversationByUID.isDownloading = true;
                                                if(conversationsListAdapter!=null && messageEventFileDownload.position!=-1 && conversationsListAdapter.getItemCount()>0){
                                                    conversationsListAdapter.notifyItemChanged(messageEventFileDownload.position,conversationByUID);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onProgress(String url, long downloaded, long length, long usedTime) {
                                            super.onProgress(url, downloaded, length, usedTime);
                                            //Log.i("TAG", " progress:" + downloaded + " url:" + url);
                                        }

                                        @Override
                                        public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {

                                            handler = new Handler();
                                            myRunnable = () -> {
                                                // Things to be done
                                                try {
                                                    if (messageEventFileDownload.conversationByUID!=null){
                                                        conversationByUID.isDownloading = false;
                                                        if(conversationsListAdapter!=null && messageEventFileDownload.position!=-1 && conversationsListAdapter.getItemCount()>0){
                                                            conversationsListAdapter.notifyItemChanged(messageEventFileDownload.position,conversationByUID);
                                                        }
                                                    }
                                                    if(common!=null){
                                                        common.openFile(mContext,new File(path.getPath()));
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            };
                                            handler.postDelayed(myRunnable, 1200);
                                            //Toast.makeText(mContext, "File Saved", Toast.LENGTH_SHORT).show();
                                            return super.onResult(throwable, path, url, extra);
                                        }
                                    });

                        }
                    }
                }

            }

            @Override
            public void onError() {
                storagePermission(messageEventFileDownload);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post(new MessageEvent("ShowToolbar"));
        if(handler!=null){
            handler.removeCallbacks(myRunnable);
        }
    }

    private void storagePermission(boolean openGalleryStatus, boolean isFileAttach, Dialog dialog) {
        ArrayList<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.CAMERA);
        PermissionHelper.grantMultiplePermissions(getActivity(), permissionList, new PermissionHelper.PermissionInterface() {
            @Override
            public void onSuccess() {
                if (isFileAttach){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    String [] mimeTypes = {"image/*","application/msword", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_IMAGE_FOR_SELECT);
                }
                else if (openGalleryStatus) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    String[] mimeTypes = {"image/*"};
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_IMAGE_FOR_SELECT);
                    if(dialog!=null&& dialog.isShowing()){
                        dialog.dismiss();
                    }

                } else {
                    if(dialog!=null&& dialog.isShowing()){
                        dialog.dismiss();
                    }
                    dispatchTakePictureIntent(CAPTURE_PICTURE_FROM_CAMERA);
                }

            }

            @Override
            public void onError() {

            }
        });
    }

    private void dispatchTakePictureIntent(int requearCode) {
        if (mContext!=null){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI;
                    if (Build.VERSION.SDK_INT >= 24) {
                        photoURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", photoFile);
                    }else{
                        photoURI = Uri.fromFile(photoFile);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, requearCode);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3:
                boolean isFileSizeExceed = false;
                if (resultCode == -1) {
                    // Checking whether data is null or not
                    if (data != null) {
                        // Checking for selection multiple files or single.
                        if (data.getClipData() != null) {
                            // Getting the length of data and logging up the logs using index
                            for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                                // Getting the URIs of the selected files and logging them into logcat at debug level
                                Uri uri = data.getClipData().getItemAt(index).getUri();
                                if (mContext.getContentResolver().getType(uri).equalsIgnoreCase("image/jpeg") ||
                                        mContext.getContentResolver().getType(uri).equalsIgnoreCase("image/jpg") ||
                                        mContext.getContentResolver().getType(uri).equalsIgnoreCase("image/png")) {

                                    File compressedImageFile = null;
                                    try {
                                        try {
                                            fileTemp = FileUtil.from(mContext, uri);
                                            Log.d("file", "File...:::: uti - " + fileTemp.getPath() + " file -" + fileTemp + " : " + fileTemp.exists());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        compressedImageFile = new Compressor(mContext).compressToFile(fileTemp);
                                        filePart.add(MultipartBody.Part.createFormData("files", compressedImageFile.getName(),
                                                RequestBody.create(MediaType.parse("*/*"), compressedImageFile)));
                                        filesNames.add(new FileDataClass(compressedImageFile.getName(),common.getFolderSizeLabel(compressedImageFile),common.getMimeType(Uri.fromFile(compressedImageFile),getContext())));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    if (Utils.isCheckFileSize(uri, mContext)) {
                                        File compressedImageFile = null;
                                        try {
                                            fileTemp = FileUtil.from(mContext, uri);
                                            Log.d("file", "File...:::: uti - " + fileTemp.getPath() + " file -" + fileTemp + " : " + fileTemp.exists());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        filePart.add(MultipartBody.Part.createFormData("files", fileTemp.getName(),
                                                RequestBody.create(MediaType.parse("*/*"), fileTemp)));
                                        filesNames.add(new FileDataClass(fileTemp.getName(),common.getFolderSizeLabel(fileTemp),common.getMimeType(Uri.fromFile(fileTemp),getContext())));

                                    } else {
                                        Toast.makeText(mContext, "File size exceeded from 2.5 mb", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        } else {
                            // Getting the URI of the selected file and logging into logcat at debug level
                            Uri uri = data.getData();
                            if (mContext.getContentResolver().getType(uri).equalsIgnoreCase("image/jpeg") ||
                                    mContext.getContentResolver().getType(uri).equalsIgnoreCase("image/jpg") ||
                                    mContext.getContentResolver().getType(uri).equalsIgnoreCase("image/png")) {
                                //uris.add(uri);
                                File compressedImageFile = null;
                                try {
                                    try {
                                        fileTemp = FileUtil.from(mContext, uri);
                                        Log.d("file", "File...:::: uti - " + fileTemp.getPath() + " file -" + fileTemp + " : " + fileTemp.exists());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    compressedImageFile = new Compressor(getContext()).compressToFile(fileTemp);
                                    filePart.add(MultipartBody.Part.createFormData("files", compressedImageFile.getName(),
                                            RequestBody.create(MediaType.parse("*/*"), compressedImageFile)));
                                    filesNames.add(new FileDataClass(compressedImageFile.getName(),common.getFolderSizeLabel(compressedImageFile),common.getMimeType(Uri.fromFile(compressedImageFile),getContext())));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                if (Utils.isCheckFileSize(uri, mContext)) {
                                    try {
                                        fileTemp = FileUtil.from(mContext, uri);
                                        Log.d("file", "File...:::: uti - " + fileTemp.getPath() + " file -" + fileTemp + " : " + fileTemp.exists());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    filePart.add(MultipartBody.Part.createFormData("files", fileTemp.getName(),
                                            RequestBody.create(MediaType.parse("*/*"), fileTemp)));
                                    filesNames.add(new FileDataClass(fileTemp.getName(),common.getFolderSizeLabel(fileTemp),common.getMimeType(Uri.fromFile(fileTemp),getContext())));
                                } else {
                                    Toast.makeText(mContext, "File size exceeded from 2.5 mb", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        if (!filePart.isEmpty()) {
                            fragmentConversationsBinding.layoutSelectedFiles.setVisibility(View.VISIBLE);
                            //uploadFiles(conversationByUID,filePart);
                            selectedFilesListAdapter = new SelectedFilesListAdapter(mContext,filesNames);
                            fragmentConversationsBinding.rvSelections.setAdapter(selectedFilesListAdapter);
                        }else{
                            Toast.makeText(mContext, "No file found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case 2:
                // Opening Camera
                if (requestCode == CAPTURE_PICTURE_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
                    if(!mCurrentPhotoPath.isEmpty()) {
                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri;
                        if (Utils.isFileLessThan2MB(new File(mCurrentPhotoPath))){
                            contentUri = Uri.fromFile(new File(mCurrentPhotoPath));
                        }else{
                            contentUri = Uri.fromFile(common.compressImage(mCurrentPhotoPath,mContext));
                        }
                        mediaScanIntent.setData(contentUri);
                        if (mContext != null && isAdded()) {
                            mContext.sendBroadcast(mediaScanIntent);
                        }
                        File compressedImageFile = null;
                        try {
                            fileTemp = FileUtil.from(mContext, contentUri);
                            Log.d("file", "File...:::: uti - " + fileTemp.getPath() + " file -" + fileTemp + " : " + fileTemp.exists());
                            compressedImageFile = new Compressor(mContext).compressToFile(fileTemp);
                            filePart.add(MultipartBody.Part.createFormData("files", compressedImageFile.getName(),
                                    RequestBody.create(MediaType.parse("*/*"), compressedImageFile)));
                            filesNames.add(new FileDataClass(fileTemp.getName(),common.getFolderSizeLabel(compressedImageFile),common.getMimeType(Uri.fromFile(compressedImageFile),getContext())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!filePart.isEmpty()) {
                            fragmentConversationsBinding.layoutSelectedFiles.setVisibility(View.VISIBLE);
                            selectedFilesListAdapter = new SelectedFilesListAdapter(mContext,filesNames);
                            fragmentConversationsBinding.rvSelections.setAdapter(selectedFilesListAdapter);
                        }else{
                            Toast.makeText(mContext, "No file found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
                if(mContext!=null) {
                    //CreateDialoge.dialoge(mContext, "You haven't picked Image");
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReLoadConversationEvent event) {
        if(event!=null) {
            if (event.eventType.equalsIgnoreCase("ReloadConversationWhenConnect")) {
                //api for all conversations
                getConversationByUID(pageNumber,pageSize,conversationByUID,common.getUserId(mContext),false);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConversationEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("AddToList")){
                ArrayList<Conversation> conversationArrayList = new ArrayList<>();
                if (common.getConversationList(mContext)!=null && !common.getConversationList(mContext).isEmpty()){
                    for (int i=0;i<common.getConversationList(mContext).size();i++){
                        if (!common.getConversationList(mContext).get(i).conversationUid.equalsIgnoreCase(conversationByUID)){
                            Conversation conversation = common.getConversationList(mContext).get(i);
                            conversation.isNewMessageReceive = true;
                            conversationArrayList.add(conversation);
                        }else{
                            conversationArrayList.add(common.getConversationList(mContext).get(i));
                        }
                    }
                    if(!conversationArrayList.isEmpty() && conversationsListingDetailAdapter!=null){
                        conversationsListingDetailAdapter = new ConversationsListingDetailAdapter(mContext,conversationArrayList);
                        fragmentConversationsBinding.rvConversationList.setAdapter(conversationsListingDetailAdapter);
                    }
                }
//
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConversationStatusListenerEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("ConversationStatusListener")){
                    if (event.message.isResolved){
                        fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.GONE);
                        fragmentConversationsBinding.layoutTyping.setVisibility(View.GONE);
                        fragmentConversationsBinding.layoutReopenClick.setVisibility(View.VISIBLE);
                        fragmentConversationsBinding.layoutGroupAndUsers.setVisibility(View.GONE);

                    }else{
                        fragmentConversationsBinding.layoutResolvedClick.setVisibility(View.VISIBLE);
                        fragmentConversationsBinding.layoutTyping.setVisibility(View.VISIBLE);
                        fragmentConversationsBinding.layoutReopenClick.setVisibility(View.GONE);
                        fragmentConversationsBinding.layoutGroupAndUsers.setVisibility(View.VISIBLE);
                    }
                    if(conversationsListAdapter!=null && !conversationArrayList.isEmpty() && fragmentConversationsBinding!=null) {
                            conversationArrayList.add(fragmentConversationsBinding.rvConversations.getAdapter().getItemCount(), getConversationFromResolveListener(event.message));
                            conversationsListAdapter.notifyItemInserted(fragmentConversationsBinding.rvConversations.getAdapter().getItemCount());
                            scrollToBottom();
                    }else{
                        conversationArrayList.add(getConversationFromResolveListener(event.message));
                        conversationsListAdapter = new ConversationsByUIListAdapter(mContext, conversationArrayList);
                        fragmentConversationsBinding.rvConversations.setAdapter(conversationsListAdapter);
                    }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void reOpenDialog(final Context mContext) {
        Dialog newUserDialog = new Dialog(mContext);
        View newUserView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        newUserView = inflater.inflate(R.layout.layour_reopen_dailog, null);
        newUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newUserView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_border_rectangle));
        newUserDialog.setContentView(newUserView);
        newUserDialog.show();
        Button btn_yes = newUserView.findViewById(R.id.btn_yes);
        Button btn_no = newUserView.findViewById(R.id.btn_no);
        TextView txtMessage = newUserView.findViewById(R.id.txtMessage);
        txtMessage.setText("Upon 'Reopening' this conversation, it will be automatically added to the 'New' conversation view Are you sure you want to reopen?");
        RelativeLayout imgClose = newUserView.findViewById(R.id.imgClose);
        final AVLoadingIndicatorView progressBarPromoCode = newUserView.findViewById(R.id.progressBarPromoCode);
        progressBarPromoCode.setVisibility(View.GONE);

        imgClose.setOnClickListener(view -> newUserDialog.dismiss());
        btn_no.setOnClickListener(view -> {
            newUserDialog.dismiss();
        });
        btn_yes.setOnClickListener(view -> {
            newUserDialog.dismiss();
            ConversationStatusModel conversationStatusModel = new ConversationStatusModel();
            conversationStatusModel.conversationUid = conversationByUID;
            conversationStatusModel.Status = 3;
            conversationStatusModel.agentName = common.getUserLoginDate(mContext).userName;
            conversationStatusModel.agentId = Integer.parseInt(common.getUserId(mContext));
            conversationStatusModel.groupName = common.getConversationData(mContext).groupName;;
            EventBus.getDefault().post(new ConversationStatusEvent(conversationStatusModel,"ResolvedConversation"));
        });
    }



    public void getAllGroups(boolean isActive){
        new ApiClient(mContext).getWebService().getAllGroups(isActive).enqueue(new Callback<WebResponse<ArrayList<GroupsDataModel>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<GroupsDataModel>>> call, Response<WebResponse<ArrayList<GroupsDataModel>>> response) {
                if (response!=null) {
                    if (response.code() == 200) {
                        if (response.isSuccessful() && response.body() != null) {
                             arrayListGroupName = new ArrayList<>();
                             arrayListGroupId = new ArrayList<>();
                            if(!response.body().getResult().isEmpty()){
                                arrayListGroupId.add(0);
                                arrayListGroupName.add("Groups");
                                for(int i=0;i<response.body().getResult().size();i++){
                                    arrayListGroupName.add(response.body().getResult().get(i).getGroupName());
                                    arrayListGroupId.add(response.body().getResult().get(i).getGroupId());
                                }
                            }
                            fragmentConversationsBinding.layoutGroupSpinner.setVisibility(View.VISIBLE);
                            if (!arrayListGroupName.isEmpty() && !arrayListGroupId.isEmpty()){
                                loadGroupsSpinnerData(arrayListGroupName,arrayListGroupId);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<GroupsDataModel>>> call, Throwable t) {
                if (t.getMessage().contains("Failed to connect")){
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
            }
        });
    }

    public void getAllGroupsByUserId(int userId){
        new ApiClient(mContext).getWebService().getGroupsByUserId(userId).enqueue(new Callback<WebResponse<GroupsByUserDataModel>>() {
            @Override
            public void onResponse(Call<WebResponse<GroupsByUserDataModel>> call, Response<WebResponse<GroupsByUserDataModel>> response) {
                if (response!=null) {
                    if (response.code() == 200) {
                        if (response.isSuccessful() && response.body() != null) {
                            arrayListGroupName = new ArrayList<>();
                            arrayListGroupId = new ArrayList<>();
                            arrayListAgentName = new ArrayList<>();
                            arrayListAgentId = new ArrayList<>();
                            if(response.body().getResult() !=null){
                                arrayListGroupId.add(0);
                                arrayListGroupName.add("Groups");
                                if(!response.body().getResult().getGroupsList().isEmpty()){
                                    for(int i=0;i<response.body().getResult().getGroupsList().size();i++){
                                        arrayListGroupName.add(response.body().getResult().getGroupsList().get(i).getGroupName());
                                        arrayListGroupId.add(response.body().getResult().getGroupsList().get(i).getGroupId());
                                    }
                                }
                                arrayListAgentId.add(0);
                                arrayListAgentName.add("Agents");
                                if(!response.body().getResult().getUsersList().isEmpty()){
                                    for(int i=0;i<response.body().getResult().getUsersList().size();i++){
                                        arrayListAgentName.add(response.body().getResult().getUsersList().get(i).getFirstName()+" "+response.body().getResult().getUsersList().get(i).getLastName());
                                        arrayListAgentId.add(response.body().getResult().getUsersList().get(i).getUserId());
                                    }
                                }

                            }
                            if (!arrayListGroupName.isEmpty() && !arrayListGroupId.isEmpty()){
                                fragmentConversationsBinding.layoutGroupSpinner.setVisibility(View.VISIBLE);
                                loadGroupsSpinnerData(arrayListGroupName,arrayListGroupId);
                            }else{
                                fragmentConversationsBinding.layoutGroupSpinner.setVisibility(View.GONE);
                            }
                            if (!arrayListAgentId.isEmpty() && !arrayListAgentName.isEmpty()){
                                loadAgentSpinnerSpinnerData(arrayListAgentName,arrayListAgentId);
                            }else{
                                fragmentConversationsBinding.layoutGroupSpinner.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WebResponse<GroupsByUserDataModel>> call, Throwable t) {
                if (t.getMessage().contains("Failed to connect")){
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
            }
        });
    }

    public void getAgentsByGroupId(int groupId){
        new ApiClient(mContext).getWebService().getAgentsByGroupId(groupId).enqueue(new Callback<WebResponse<ArrayList<AgentsByGroupIdModel>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<AgentsByGroupIdModel>>> call, Response<WebResponse<ArrayList<AgentsByGroupIdModel>>> response) {
                if (response!=null) {
                    if (response.code() == 200) {
                        if (response.isSuccessful() && response.body() != null) {
                            arrayListAgentName = new ArrayList<>();
                            arrayListAgentId = new ArrayList<>();
                            if(response.body().getResult() !=null){
                                arrayListAgentId.add(0);
                                arrayListAgentName.add("Agents");
                                if(!response.body().getResult().isEmpty()){
                                    for(int i=0;i<response.body().getResult().size();i++){
                                        arrayListAgentName.add(response.body().getResult().get(i).getFirstName()+" "+response.body().getResult().get(i).getLastName());
                                        arrayListAgentId.add(response.body().getResult().get(i).getAgentId());
                                    }
                                }

                                if (!arrayListAgentId.isEmpty() && !arrayListAgentName.isEmpty()){
                                    fragmentConversationsBinding.layoutAgentSpinner.setVisibility(View.VISIBLE);
                                    loadAgentSpinnerSpinnerData(arrayListAgentName,arrayListAgentId);
                                }else{
                                    fragmentConversationsBinding.layoutAgentSpinner.setVisibility(View.GONE);
                                }

                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<AgentsByGroupIdModel>>> call, Throwable t) {
                if (t.getMessage().contains("Failed to connect")){
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
            }
        });
    }

    private void loadGroupsSpinnerData(ArrayList<String> arrayListName ,ArrayList<Integer> arrayListGroupId){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayListName) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.LTGRAY);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }
                return view;
            }
        };
        fragmentConversationsBinding.spinnerGroups.setAdapter(spinnerArrayAdapter);
        fragmentConversationsBinding.spinnerGroups.setSelection(0, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((TextView) fragmentConversationsBinding.spinnerGroups.getSelectedView()).setTextColor(getResources().getColor(R.color.light_grey, getActivity().getTheme()));
            ((TextView) fragmentConversationsBinding.spinnerGroups.getSelectedView()).setTypeface(null, Typeface.BOLD);
        }else {
            ((TextView) fragmentConversationsBinding.spinnerGroups.getSelectedView()).setTextColor(getResources().getColor(R.color.light_grey));
            ((TextView) fragmentConversationsBinding.spinnerGroups.getSelectedView()).setTypeface(null, Typeface.BOLD);

        }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);


    }

    private void loadAgentSpinnerSpinnerData(ArrayList<String> arrayListName ,ArrayList<Integer> arrayListAgentId){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayListName) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.LTGRAY);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }
                return view;
            }
        };
        fragmentConversationsBinding.spinnerAgents.setAdapter(spinnerArrayAdapter);
        fragmentConversationsBinding.spinnerAgents.setSelection(0, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((TextView) fragmentConversationsBinding.spinnerAgents.getSelectedView()).setTextColor(getResources().getColor(R.color.light_grey, getActivity().getTheme()));
            ((TextView) fragmentConversationsBinding.spinnerAgents.getSelectedView()).setTypeface(null, Typeface.BOLD);
        }else {
            ((TextView) fragmentConversationsBinding.spinnerAgents.getSelectedView()).setTextColor(getResources().getColor(R.color.light_grey));
            ((TextView) fragmentConversationsBinding.spinnerAgents.getSelectedView()).setTypeface(null, Typeface.BOLD);

        }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);


    }

    public boolean checkAgentPermissionContainForAssign(int actionId){
        if(common!=null){
            if(!common.getPermission(mContext).getListPermissions().isEmpty()){
                for(int i=0;i<common.getPermission(mContext).getListPermissions().size();i++){
                    if (actionId == common.getPermission(mContext).getListPermissions().get(i).getActionId()){
                        return true;
                    }
                }

            }
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}