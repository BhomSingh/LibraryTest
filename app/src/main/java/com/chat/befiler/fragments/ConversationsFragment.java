package com.chat.befiler.fragments;

import static com.chat.befiler.commons.Constants.ALL_ASSIGNED;
import static com.chat.befiler.commons.Constants.ASSIGNED;
import static com.chat.befiler.commons.Constants.NEW;
import static com.chat.befiler.commons.Constants.RESOLVED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.chat.befiler.Events.chatEvents.AllConversationCountEvent;
import com.chat.befiler.Events.chatEvents.AssignedChatEvent;
import com.chat.befiler.Events.chatEvents.ConversationEvent;
import com.chat.befiler.Events.chatEvents.ConversationStatusListenerEvent;
import com.chat.befiler.Events.chatEvents.EngagedChatEvent;
import com.chat.befiler.Events.appEvents.MessageEvent;
import com.chat.befiler.adapters.ConversationsByUIListAdapter;
import com.chat.befiler.adapters.ConversationsListAdapter;
import com.chat.befiler.commons.Common;
import com.chat.befiler.commons.Constants;
import com.chat.befiler.commons.PaginationScrollListener;
import com.chat.befiler.model.chat.AssignChatListener;
import com.chat.befiler.model.chat.Conversation;
import com.chat.befiler.model.chat.ConversationByUID;
import com.chat.befiler.model.chat.ConversationStatusListenerDataModel;
import com.chat.befiler.model.chat.ConversationsCountModel;
import com.chat.befiler.model.chat.EngageListener;
import com.chat.befiler.retrofit.ApiClient;
import com.chat.befiler.retrofit.WebResponse;
import com.chat.befiler.retrofit.WebResponse2;
import com.example.signalrtestandroid.R;
import com.example.signalrtestandroid.databinding.FragmentConversationsBinding;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ConversationsFragment extends Fragment {
    int pageSize = 15;
    int pageNumber = 1;
    int totalPages = 0;
    Handler handler ;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    LinearLayoutManager mLayoutManager;
    FragmentConversationsBinding fragmentConversationsBinding;
    Common common;
    int addedConversationPos = -1;
    String selectMenu = "0";
    Context mcontext;
    ConversationsListAdapter conversationsListAdapter = null;
    ArrayList<Conversation> conversationArrayList_All ;
    ArrayList<Conversation> conversationArrayList ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        common.saveCurrentScreen(mcontext,"ConversationFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentConversationsBinding = FragmentConversationsBinding.inflate(getLayoutInflater());
        View view = fragmentConversationsBinding.getRoot();

        if(getArguments()!=null){
            Bundle bundle = getArguments();
            if (bundle.containsKey("selectMenu") && bundle!=null){
                if(bundle.getString("selectMenu")!=null){
                    selectMenu = bundle.getString("selectMenu");
                }
            }
        }
        common = new Common();
        //init layout manager for list
        conversationArrayList = new ArrayList<>();
        conversationArrayList_All = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(mcontext);
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(fragmentConversationsBinding.rvConversations.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(mcontext, R.drawable.vertical_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        fragmentConversationsBinding.rvConversations.addItemDecoration(horizontalDecoration);
        fragmentConversationsBinding.rvConversations.setLayoutManager(mLayoutManager);

        //api for all conversations
        getAllConversation(pageNumber,pageSize,common.getUserId(mcontext),common.getIsSuperAdmin(mcontext),selectMenu,false);

        fragmentConversationsBinding.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllConversation(pageNumber,pageSize,common.getUserId(mcontext),common.getIsSuperAdmin(mcontext),selectMenu,false);
                fragmentConversationsBinding.pullToRefresh.setRefreshing(false);
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
                    getAllConversation(pageNumber,pageSize,common.getUserId(mcontext),common.getIsSuperAdmin(mcontext),selectMenu,false);
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

        return view;
    }

    public void getAllConversation(int pageNumber,int pageSize,String agentId ,String isAdmin,String selectMenu,boolean isCallForUpdateCount) {
            if(pageNumber==1){
                if (isAdded()){
                    if(getActivity()!=null){
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        new ApiClient(getContext()).getWebService().getConversationList(pageNumber,pageSize,agentId, isAdmin,!selectMenu.equalsIgnoreCase("") ? Integer.parseInt(selectMenu) : 0).enqueue(new Callback<WebResponse2<ArrayList<Conversation>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WebResponse2<ArrayList<Conversation>>> call, Response<WebResponse2<ArrayList<Conversation>>> response) {
                if(response.body() != null) {
                    EventBus.getDefault().post(new AllConversationCountEvent(null,"updateMenuCount"));
                    totalPages = response.body().getTotalPages();
                        if(pageNumber==1){
                            if(response.body().getResult().size()>0) {
                                //conversationArrayList_All = response.body().getResult();
//                                conversationArrayList = getConversationsArrayByMenu(selectMenu,conversationArrayList_All);
                                conversationArrayList = response.body().getResult();
                                conversationsListAdapter = new ConversationsListAdapter(mcontext,conversationArrayList);
                                fragmentConversationsBinding.rvConversations.setAdapter(conversationsListAdapter);
                                //zero position conversation is storing
                                if(conversationArrayList.size()>0){
                                    common.saveConversation(mcontext, new Gson().toJson(conversationArrayList.get(0)));
                                }
                                fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                                fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
                            }else{
                                ArrayList<Conversation> arrayList =new ArrayList<>();
                                EventBus.getDefault().post(new AllConversationCountEvent(new ConversationsCountModel(arrayList,arrayList,arrayList,arrayList), "updateMenuCount"));
                                fragmentConversationsBinding.pullToRefresh.setVisibility(View.GONE);
                                fragmentConversationsBinding.tvNoChat.setVisibility(View.VISIBLE);
                            }
                            EventBus.getDefault().post(new MessageEvent("startSignalR"));
                        }else {
                            // check weather is last page or not
                            if (pageNumber <totalPages) {
                                //show loader here
                                fragmentConversationsBinding.loadMoreProgress.setVisibility(View.VISIBLE);
                            } else {
                                isLastPage = true;
                            }
                            isLoading = false;
                            if(response.body().getResult().size()>0){

//                                conversationArrayList_All.addAll(response.body().getResult());
//                                conversationArrayList.clear();
//                                conversationArrayList.addAll(getConversationsArrayByMenu(selectMenu,conversationArrayList_All));
                                conversationArrayList.addAll(response.body().getResult());
                                if (conversationsListAdapter!=null){
                                    conversationsListAdapter.notifyDataSetChanged();
                                }
                                fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                                fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
                            }
                        }
                        if (conversationArrayList.size()==0){
                            fragmentConversationsBinding.pullToRefresh.setVisibility(View.GONE);
                            fragmentConversationsBinding.tvNoChat.setVisibility(View.VISIBLE);
                        }else{
                            fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                            fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
                            if (selectMenu.equalsIgnoreCase(NEW)){
                                fragmentConversationsBinding.tvNoChat.setText(R.string.lbl_no_new_conversation);
                            }else if (selectMenu.equalsIgnoreCase(ALL_ASSIGNED)){
                                fragmentConversationsBinding.tvNoChat.setText(R.string.lbl_no_conversation);
                            }else if (selectMenu.equalsIgnoreCase(RESOLVED)){
                                fragmentConversationsBinding.tvNoChat.setText(R.string.lbl_no_conversation);
                            }else if (selectMenu.equalsIgnoreCase(ASSIGNED)){
                                fragmentConversationsBinding.tvNoChat.setText(R.string.lbl_no_assigned_conversation);
                            }
                        }
                        if (isAdded()){
                            if(getActivity()!=null){
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
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
            public void onFailure(Call<WebResponse2<ArrayList<Conversation>>> call, Throwable t) {
                if (isAdded()){
                    if(getActivity()!=null){
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
                if (t.getMessage().contains("Failed to connect")){
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
                fragmentConversationsBinding.loadMoreProgress.setVisibility(View.GONE);
            }
        });

    }

    public ArrayList<Conversation> getConversationsArrayByMenu(String selectMenu,ArrayList<Conversation> conversationArrayList){
        ArrayList<Conversation> arrayList = new ArrayList<>();
        if(conversationArrayList.size()>0){
            for (int i=0;i<conversationArrayList.size();i++){
                if (selectMenu.equalsIgnoreCase(Constants.ALL_ASSIGNED) && conversationArrayList.get(i).toUserId != Integer.parseInt(common.getUserId(mcontext))){
                    if(conversationArrayList.get(i).status.equalsIgnoreCase(Constants.ASSIGNED)){
                        arrayList.add(conversationArrayList.get(i));
                    }
                }else{
                    if (selectMenu.equalsIgnoreCase(Constants.ASSIGNED)) {
                        if (conversationArrayList.get(i).toUserId == Integer.parseInt(common.getUserId(mcontext))
                                && conversationArrayList.get(i).status.equalsIgnoreCase(selectMenu)){
                            arrayList.add(conversationArrayList.get(i));
                        }
                    } else {
                        if (conversationArrayList.get(i).status.equalsIgnoreCase(selectMenu)){
                            arrayList.add(conversationArrayList.get(i));
                        }
                    }
                }
            }
        }
        return arrayList;
    }


    public ConversationsCountModel getConversationsCountByStatus(String selectMenu,ArrayList<Conversation> conversationArrayList){
        ArrayList<Conversation> arrayListAllAssign = new ArrayList<>();
        ArrayList<Conversation> arrayListASSIGNED = new ArrayList<>();
        ArrayList<Conversation> arrayListNew = new ArrayList<>();
        ArrayList<Conversation> arrayListResolved = new ArrayList<>();
        if(conversationArrayList.size()>0){
            for (int i=0;i<conversationArrayList.size();i++){

               if(conversationArrayList.get(i).status.equalsIgnoreCase(Constants.ASSIGNED) && conversationArrayList.get(i).toUserId != Integer.parseInt(common.getUserId(mcontext))){
                        arrayListAllAssign.add(conversationArrayList.get(i));
               }

               if (conversationArrayList.get(i).toUserId == Integer.parseInt(common.getUserId(mcontext))
                            && conversationArrayList.get(i).status.equalsIgnoreCase(Constants.ASSIGNED)){
                        arrayListASSIGNED.add(conversationArrayList.get(i));
               }
               if (conversationArrayList.get(i).status.equalsIgnoreCase(RESOLVED)) {
                        arrayListResolved.add(conversationArrayList.get(i));
                }

               if (conversationArrayList.get(i).status.equalsIgnoreCase(NEW)) {
                        arrayListNew.add(conversationArrayList.get(i));
               }
            }
        }
        return  new ConversationsCountModel(arrayListAllAssign,arrayListASSIGNED,arrayListNew,arrayListResolved);
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
    public void onMessageEvent(ConversationEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("AddToList")){
                if(conversationsListAdapter!=null && !conversationArrayList.isEmpty()){
                    if (isContainConversation(conversationArrayList,event.conversation)){
                        if(addedConversationPos!=-1){
                            event.conversation.isRecordUpdated = true;
                            conversationArrayList.set(addedConversationPos,event.conversation);
                            conversationsListAdapter.notifyItemChanged(addedConversationPos);
                        }
                    }else{
                            event.conversation.isRecordUpdated = false;
                            conversationArrayList.add(0, event.conversation);
                            conversationsListAdapter.notifyItemInserted(0);
                    }
                }else{
                        event.conversation.isRecordUpdated = false;
                        conversationArrayList.add(event.conversation);
                        conversationsListAdapter = new ConversationsListAdapter(mcontext,conversationArrayList);
                        fragmentConversationsBinding.rvConversations.setAdapter(conversationsListAdapter);
                }
                if(conversationArrayList.size()>0){
                    fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                    fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
                }else{
                    fragmentConversationsBinding.pullToRefresh.setVisibility(View.GONE);
                    fragmentConversationsBinding.tvNoChat.setVisibility(View.VISIBLE);
                }
                fragmentConversationsBinding.rvConversations.post(() -> fragmentConversationsBinding.rvConversations.smoothScrollToPosition(0));
                updateMenuCount();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReLoadConversationEvent event) {
        if(event!=null) {
            if (event.eventType.equalsIgnoreCase("ReloadConversationWhenConnect")) {
                //api for all conversations
                getAllConversation(pageNumber,pageSize,common.getUserId(mcontext),common.getIsSuperAdmin(mcontext),selectMenu,false);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AssignedChatEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("AssignChatToAnother")){
                //Remove item becauase it assigned to another agent by admin side
                if(conversationsListAdapter!=null && !conversationArrayList.isEmpty()){
                    if (isContainConversationForAssignChat(conversationArrayList,event.assignChatListener)){
                        if(addedConversationPos!=-1){
                            conversationArrayList.remove(addedConversationPos);
                            conversationsListAdapter.notifyItemRemoved(addedConversationPos);
                        }
                    }
                }
                if(conversationArrayList.size()>0){
                    fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                    fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
                }else{
                    fragmentConversationsBinding.pullToRefresh.setVisibility(View.GONE);
                    fragmentConversationsBinding.tvNoChat.setVisibility(View.VISIBLE);
                }
                updateMenuCount();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EngagedChatEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("EngageChat")){
                //Remove item becauase it assigned to another agent by admin side
                if(conversationsListAdapter!=null && !conversationArrayList.isEmpty()){
                    if (isContainConversationForEngageChat(conversationArrayList,event.engageListener)){
                        if(addedConversationPos!=-1){
                            conversationArrayList.remove(addedConversationPos);
                            conversationsListAdapter.notifyItemRemoved(addedConversationPos);
                        }
                    }
                }
                if(conversationArrayList.size()>0){
                    fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                    fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
                }else{
                    fragmentConversationsBinding.pullToRefresh.setVisibility(View.GONE);
                    fragmentConversationsBinding.tvNoChat.setVisibility(View.VISIBLE);
                }
                updateMenuCount();
            }
        }

    }


    public boolean isContainConversationForEngageChat(ArrayList<Conversation> conversationArrayList, EngageListener engageListener ){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).conversationUid.equalsIgnoreCase(engageListener.conversationUId)){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }

    public boolean isContainConversationForAssignChat(ArrayList<Conversation> conversationArrayList,AssignChatListener assignChatListener ){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).conversationUid.equalsIgnoreCase(assignChatListener.conversationUId)){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }
    public boolean isContainConversation(ArrayList<Conversation> conversationArrayList, ConversationStatusListenerDataModel conversation){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).conversationUid.equalsIgnoreCase(conversation.conversationUid)){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }
    public boolean isContainConversation(ArrayList<Conversation> conversationArrayList,Conversation conversation){
        for (int i=0;i<conversationArrayList.size();i++){
            if(conversationArrayList.get(i).conversationUid.equalsIgnoreCase(conversation.conversationUid)){
                addedConversationPos = i;
                return true;
            }
        }
        return false;
    }

    public void updateMenuCount(){
        EventBus.getDefault().post(new AllConversationCountEvent(null, "updateMenuCount"));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConversationStatusListenerEvent event) {
        if (event != null) {
            if (event.eventType.equalsIgnoreCase("ConversationStatusListener")) {
                if (conversationsListAdapter != null && !conversationArrayList.isEmpty()) {
                    if (isContainConversation(conversationArrayList, event.message)) {
                        if (addedConversationPos != -1) {
                            conversationArrayList.remove(addedConversationPos);
                            conversationsListAdapter.notifyItemRemoved(addedConversationPos);
                        }
                    }
                }
            }
            if(conversationArrayList.size()>0){
                fragmentConversationsBinding.pullToRefresh.setVisibility(View.VISIBLE);
                fragmentConversationsBinding.tvNoChat.setVisibility(View.GONE);
            }else{
                fragmentConversationsBinding.pullToRefresh.setVisibility(View.GONE);
                fragmentConversationsBinding.tvNoChat.setVisibility(View.VISIBLE);
            }
            updateMenuCount();
        }
    }

    public Conversation getConversationFromResolveListener(ConversationStatusListenerDataModel conversationStatusListenerDataModel){
        Conversation conversation = null;
        if (conversationStatusListenerDataModel!=null){
            conversation = new Conversation();
            conversation.conversationType = "text";
            conversation.type = "system";
            conversation.conversationUid = conversationStatusListenerDataModel.conversationUid;
            conversation.toUserId = 0;
            conversation.customerName = "";
            conversation.sender = "";
            conversation.customerId = 0;
            conversation.content = conversationStatusListenerDataModel.notifyMessage;
            conversation.files = new ArrayList<>();
            conversation.fromUserId = 0;
            conversation.groupId = 0;
            conversation.groupName = "";
            conversation.receiver = "";
            conversation.pageId = "";
            conversation.pageName = "";
            conversation.status = conversationStatusListenerDataModel.isResolved ? "2" : "0";
            conversation.timestamp = conversationStatusListenerDataModel.timestamp;
        }
        return conversation;
    }

    }

