package com.chat.befiler.activities;

import static com.chat.befiler.commons.Constants.COLOR_CODE_KEY;
import static com.chat.befiler.commons.Constants.NAME_KEY;
import static com.chat.befiler.commons.Constants.NAME_LETTER_KEY;
import static com.chat.befiler.commons.Constants.SOURCE_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.chat.befiler.Events.chatEvents.AllConversationCountEvent;
import com.chat.befiler.Events.chatEvents.AssignedChatEvent;
import com.chat.befiler.Events.chatEvents.ConversationEvent;
import com.chat.befiler.Events.chatEvents.ConversationStatusEvent;
import com.chat.befiler.Events.chatEvents.ConversationStatusListenerEvent;
import com.chat.befiler.Events.chatEvents.EngagedChatEvent;
import com.chat.befiler.Events.appEvents.MessageEvent;
import com.chat.befiler.Events.appEvents.MessageEventDetailFragment;
import com.chat.befiler.Events.chatEvents.ReceiveMessageEvent;
import com.chat.befiler.Events.chatEvents.SendChatEvent;
import com.chat.befiler.baseClasses.BaseActivity;
import com.chat.befiler.commons.ConnectionService;
import com.chat.befiler.commons.Constants;
import com.chat.befiler.commons.PermissionHelper;
import com.chat.befiler.commons.SignalRHelper;
import com.chat.befiler.commons.Common;
import com.chat.befiler.fragments.ConversationsDetailFragment;
import com.chat.befiler.fragments.ConversationsFragment;
import com.chat.befiler.fragments.ReLoadConversationEvent;
import com.chat.befiler.model.chat.AssignChatListener;
import com.chat.befiler.model.chat.AssignChatListenerRequest;
import com.chat.befiler.model.chat.Conversation;
import com.chat.befiler.model.chat.ConversationStatusListenerDataModel;
import com.chat.befiler.model.chat.ConversationsCount;
import com.chat.befiler.model.chat.EngageListener;
import com.chat.befiler.model.chat.OnErrorData;
import com.chat.befiler.model.chat.RecieveMessage;
import com.chat.befiler.retrofit.ApiClient;
import com.chat.befiler.retrofit.WebResponse;
import com.example.signalrtestandroid.R;
import com.google.android.material.navigation.NavigationView;
import com.microsoft.signalr.Action;
import com.microsoft.signalr.HubConnection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements ConnectionService.ConnectionServiceCallback {

    Timer timer = null;
    Common common;
    public boolean isAlreadyConnected = true;
    public boolean isSignalRConnected;
    public boolean isReconnecting = true ;
    SignalRHelper signalRHelper;
    HubConnection hubConnection;
    int agentId;
    String isSuperAdmin = "";
    String selectMenu = "0";
    LinearLayout layoutMainAllAssign,layoutMainNew,layoutMainResolved,layoutMainAssignToMe,layoutNew,layoutResolved,layoutAllAssign,layoutAssignToMe,layoutLogout;
    TextView txtResolvedCount,tvSelectedMenu,txtAllAssign,txtAssignToMeCount,txtNewCount;
    RelativeLayout menuClick,action_bar,rlUserProfile,layoutAllAssignCountMain,layoutAssignToMeCountMain,layoutResolvedCountMain,layoutNewCountMain ;

    private DrawerLayout mDrawer;
    private Context mContext;
    private LinearLayout LayoutReconnecting;
    private ImageView icSource;
    private TextView txtStatus;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;
    private String notificationId = "";
    Intent intent1 = null;
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true);
        } else {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        mContext = MainActivity.this;
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        action_bar = findViewById(R.id.action_bar);
        menuClick = findViewById(R.id.menuClick);
        tvSelectedMenu = findViewById(R.id.tvSelectedMenu);
        LayoutReconnecting = findViewById(R.id.LayoutReconnecting);
        icSource = findViewById(R.id.icSource);
        txtStatus = findViewById(R.id.txtStatus);
        Glide.with(mContext).load(R.drawable.connecting).into(icSource);
        common = new Common();
        signalRHelper = new SignalRHelper();

        intent1 = getIntent();
        if (!common.getUserId(this).isEmpty()) {
            agentId = Integer.parseInt(common.getUserId(this));
        }
        if (!common.getIsSuperAdmin(this).isEmpty()) {
            isSuperAdmin = common.getIsSuperAdmin(this);
        }
        if (!common.getIsSuperAdmin(this).isEmpty()) {
            if(intent1.getStringExtra(Constants.CONVERSATION_BY_UID_KEY)!=null && intent1.getExtras().containsKey(Constants.CONVERSATION_BY_UID_KEY)){
                notificationId = intent1.getStringExtra(Constants.CONVERSATION_BY_UID_KEY);
            }
        }
        //create chathub connection
        hubConnection = signalRHelper.createChatHubConnection(common, this);
        //set all chathub listners
        setHubConnectionListeners(hubConnection);

        hubConnection.onClosed(exception -> {
            if (exception != null) {
                isSignalRConnected = false;
                scheduleApiSignalRConnection();
            }
        });

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        menuClick.setOnClickListener(view -> {
            if(mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.openDrawer(GravityCompat.END);
            }else{
                mDrawer.openDrawer(GravityCompat.START);
            }

        });

         NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
         View hView = navigationView.getHeaderView(0);
         rlUserProfile = hView.findViewById(R.id.rlUserProfile);
         layoutLogout = hView.findViewById(R.id.layoutLogout);
         layoutAssignToMe = hView.findViewById(R.id.layoutAssignToMe);
         layoutAllAssign = hView.findViewById(R.id.layoutAllAssign);
         layoutResolved = hView.findViewById(R.id.layoutResolved);
         layoutNew = hView.findViewById(R.id.layoutNew);
         layoutMainAssignToMe = hView.findViewById(R.id.layoutMainAssignToMe);
         layoutMainResolved = hView.findViewById(R.id.layoutMainResolved);
         layoutMainNew = hView.findViewById(R.id.layoutMainNew);
         layoutMainAllAssign = hView.findViewById(R.id.layoutMainAllAssign);
         txtNewCount = hView.findViewById(R.id.txtNewCount);
         txtAssignToMeCount = hView.findViewById(R.id.txtAssignToMeCount);
         txtAllAssign = hView.findViewById(R.id.txtAllAssign);
         txtResolvedCount = hView.findViewById(R.id.txtResolvedCount);
         layoutAllAssignCountMain = hView.findViewById(R.id.layoutAllAssignCountMain);
         layoutAssignToMeCountMain = hView.findViewById(R.id.layoutAssignToMeCountMain);
         layoutResolvedCountMain = hView.findViewById(R.id.layoutResolvedCountMain);
         layoutNewCountMain = hView.findViewById(R.id.layoutNewCountMain);

         layoutMainAllAssign.setOnClickListener(v -> {
                 if(mDrawer!=null && mDrawer.isDrawerOpen(GravityCompat.START)){
                     mDrawer.close();
                 }
                 layoutMainAllAssign.setBackgroundColor(ContextCompat.getColor(mContext,R.color.light_grey));
                 layoutMainNew.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                 layoutMainResolved.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                 layoutMainAssignToMe.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                 tvSelectedMenu.setText(getString(R.string.lbl_all_assign));
                 selectMenu = Constants.ALL_ASSIGNED;
                 common.saveSelectedMenu(mContext,selectMenu);
                 Bundle bundleMenu = new Bundle();
                 bundleMenu.putString(Constants.SELECT_MENU_KEY,selectMenu);
                 ReplaceFragment(new ConversationsFragment(), false, bundleMenu, true);

        });

        layoutNew.setOnClickListener(v -> {
                if(mDrawer!=null && mDrawer.isDrawerOpen(GravityCompat.START)){
                    mDrawer.close();
                }
                layoutMainNew.setBackgroundColor(ContextCompat.getColor(mContext,R.color.light_grey));
                layoutMainAllAssign.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                layoutMainResolved.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                layoutMainAssignToMe.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                tvSelectedMenu.setText(getString(R.string.lbl_new));
                selectMenu = Constants.NEW;
                common.saveSelectedMenu(mContext,selectMenu);
                Bundle bundleMenu = new Bundle();
                bundleMenu.putString(Constants.SELECT_MENU_KEY,selectMenu);
                ReplaceFragment(new ConversationsFragment(), false, bundleMenu, true);


        });

        layoutResolved.setOnClickListener(v -> {
                if(mDrawer!=null && mDrawer.isDrawerOpen(GravityCompat.START)){
                    mDrawer.close();
                }
                layoutMainNew.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                layoutMainResolved.setBackgroundColor(ContextCompat.getColor(mContext,R.color.light_grey));
                layoutMainAssignToMe.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                layoutMainAllAssign.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                tvSelectedMenu.setText(R.string.lbl_resolved);
                selectMenu = Constants.RESOLVED;
                common.saveSelectedMenu(mContext,selectMenu);
                Bundle bundleMenu = new Bundle();
                bundleMenu.putString(Constants.SELECT_MENU_KEY,selectMenu);
                ReplaceFragment(new ConversationsFragment(), false, bundleMenu, true);

        });

        layoutAssignToMe.setOnClickListener(v -> {
                if(mDrawer!=null && mDrawer.isDrawerOpen(GravityCompat.START)){
                    mDrawer.close();
                }
                layoutMainNew.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                layoutMainResolved.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                layoutMainAssignToMe.setBackgroundColor(ContextCompat.getColor(mContext,R.color.light_grey));
                layoutMainAllAssign.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                tvSelectedMenu.setText(R.string.lbl_assigned_to_me);
                selectMenu = Constants.ASSIGNED;
                common.saveSelectedMenu(mContext,selectMenu);
                Bundle bundleMenu = new Bundle();
                bundleMenu.putString(Constants.SELECT_MENU_KEY,selectMenu);
                ReplaceFragment(new ConversationsFragment(), false, bundleMenu, true);

        });

        TextView txtUserFirstLater = hView.findViewById(R.id.txtUserFirstLater);
        TextView txtUserName = hView.findViewById(R.id.txtUserName);
        TextView txtUserEmail = hView.findViewById(R.id.txtUserEmail);
        rlUserProfile.setOnClickListener(v -> Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show());
        txtUserEmail.setText(common.getUserLoginDate(mContext).email);
        txtUserFirstLater.setText(common.firstCharactorCapital(common.getUserLoginDate(mContext).fullName));
        txtUserName.setText(common.getUserLoginDate(mContext).fullName);
        layoutLogout.setOnClickListener(v -> {
            //logout callhere
            common.savePermission(mContext,"");
            common.saveIsSuperAdmin(mContext,"");
            common.isLoggedIn(mContext,false);
            common.saveSelectedMenu(mContext,"0");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra(Constants.CONVERSATION_BY_UID_KEY,"");
            startActivity(intent);
            finish();
        });
        startConnectionCheckService();

        if (Build.VERSION.SDK_INT >= 32) {
            notificationPermission();
        }
        layoutMainNew.setBackgroundColor(ContextCompat.getColor(mContext,R.color.light_grey));
        selectMenu = Constants.NEW;
        common.saveSelectedMenu(mContext,selectMenu);
        tvSelectedMenu.setText(getString(R.string.lbl_new));
        Bundle bundleMenu = new Bundle();
        bundleMenu.putString(Constants.SELECT_MENU_KEY,selectMenu);
        ReplaceFragment(new ConversationsFragment(), false, bundleMenu, true);
        if (!common.getIsSuperAdmin(this).isEmpty()) {
            if(intent1.getStringExtra(Constants.CONVERSATION_BY_UID_KEY)!=null && intent1.getExtras().containsKey(Constants.CONVERSATION_BY_UID_KEY)){
                notificationId = intent1.getStringExtra(Constants.CONVERSATION_BY_UID_KEY);
                if (notificationId != null && !notificationId.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(NAME_KEY, common.getConversationData(mContext).customerName);
                    bundle.putString(NAME_LETTER_KEY, common.firstCharactorCapital(common.getConversationData(mContext).customerName));
                    bundle.putString(SOURCE_KEY, common.getConversationData(mContext).source);
                    bundle.putString(COLOR_CODE_KEY, "000000");
                    bundle.putString(Constants.CONVERSATION_BY_UID_KEY, notificationId);
                    ReplaceFragmentWithoutClearBackStack(new ConversationsDetailFragment(), true, bundle, true);
                }
            }
        }
        getConversationsCount();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
                notificationId = intent.getStringExtra(Constants.CONVERSATION_BY_UID_KEY);
                //switch to notification screen when notificationCategoryId is empty
            if (notificationId != null && !notificationId.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString(NAME_KEY, common.getConversationData(mContext).customerName);
                bundle.putString(NAME_LETTER_KEY, common.firstCharactorCapital(common.getConversationData(mContext).customerName));
                bundle.putString(SOURCE_KEY, common.getConversationData(mContext).source);
                bundle.putString(COLOR_CODE_KEY, "000000");
                bundle.putString(Constants.CONVERSATION_BY_UID_KEY, notificationId);
                ReplaceFragmentWithoutClearBackStack(new ConversationsDetailFragment(), true, bundle, true);
            }
        }
    }

    private void notificationPermission() {

        PermissionHelper.grantPermission(this, Manifest.permission.POST_NOTIFICATIONS, new PermissionHelper.PermissionInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                ReplaceFragment(new ConversationsFragment(), false, null, true);
            }
        });
    }

    public void startConnectionCheckService(){
        Intent intent = new Intent(this, ConnectionService.class);
        // Interval in seconds
        intent.putExtra(ConnectionService.TAG_INTERVAL, 3);
        // URL to ping
        intent.putExtra(ConnectionService.TAG_URL_PING, "http://www.google.com");
        // Name of the class that is calling this service
        intent.putExtra(ConnectionService.TAG_ACTIVITY_NAME, this.getClass().getName());
        // Starts the service
        startService(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReLoadConversationEvent event) {
        if(event!=null) {
             if (event.eventType.equalsIgnoreCase("Reconnecting")){
                if(isReconnecting){
                    isReconnecting = false;
                    txtStatus.setText("Reconnecting..");
                    LayoutReconnecting.setVisibility(View.VISIBLE);
                    isAlreadyConnected = true;
                }
            }
        }
    }


    public void stopIntervalOfConnection(){
        isSignalRConnected = true;
        if(timer!=null){
            timer.cancel();
        }
        runOnUiThread(() -> {
            if (!isReconnecting){
                isReconnecting = true;
                txtStatus.setText("Connected");
                Glide.with(mContext).load(R.drawable.wifi).into(icSource);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        LayoutReconnecting.setVisibility(View.GONE);
                        isAlreadyConnected = true;

                    }
                }, 1000);
            }
        });

    }

    public void setHubConnectionListeners(HubConnection hubConnection) {
        // list of messages update
        hubConnection.on("Notifications", (message) -> {
            runOnUiThread(() -> {
                if (message != null) {
                    EventBus.getDefault().post(new ConversationEvent(message, "AddToList"));
                }
            });
        }, Conversation.class);

        // list of messages
        hubConnection.on("ReceiveMessage", (message) -> {
            runOnUiThread(() -> {
                if (message != null) {
                    EventBus.getDefault().post(new ReceiveMessageEvent(message, "ReceiveMessage"));
                }
            });
        }, RecieveMessage.class);

        // Adds Engagement Listener
        hubConnection.on("EngageListener", (message) -> {
            runOnUiThread(() -> {
                if (message != null) {
                    EventBus.getDefault().post(new EngagedChatEvent(message, "EngageChat"));
                }
            });
        }, EngageListener.class);


        // list of messages
        hubConnection.on("onError", (message) -> {
            runOnUiThread(() -> {
                if (message != null) {
//                        conversation =  message;
//                        arrayAdapter.add(message.content);
//                        arrayAdapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new OnErrorData(message, "OnErrorMessage"));

                }
            });
        }, RecieveMessage.class);

        // list of messages
        hubConnection.on("AssignChatListener", (message) -> {
            runOnUiThread(() -> {
                if (message != null) {
                    common.saveConversationUID(mContext,message.conversationUId);
                    EventBus.getDefault().post(new AssignedChatEvent(message, "AssignChatToAnother"));
                }
            });
        }, AssignChatListener.class);
        // list of messages
        hubConnection.on("ConversationStatusListener", (message) -> {
            runOnUiThread(() -> {
                if (message != null) {
                    EventBus.getDefault().post(new ConversationStatusListenerEvent(message, "ConversationStatusListener"));
                }
            });
        }, ConversationStatusListenerDataModel.class);
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
    public void onMessageEvent(MessageEvent event) {
        if (!event.eventType.isEmpty()) {
            if (event.eventType.equalsIgnoreCase("startSignalR")) {
                if (hubConnection != null && agentId != 0 && signalRHelper != null) {
                    boolean isSignalRConnect =  signalRHelper.startSignalRHubClient(hubConnection, agentId,mContext,MainActivity.this);
                    if(isSignalRConnect){
                        stopIntervalOfConnection();
                    }
                }
            }else if(event.eventType.equalsIgnoreCase("ShowToolbar")){
                   hideNshowToolbar(false);
            }else if (event.eventType.equalsIgnoreCase("HideToolbar")){
                   hideNshowToolbar(true);
            }else if (event.eventType.equalsIgnoreCase("SwitchToConversationList")){
                    popFragment();
            }else if (event.eventType.equalsIgnoreCase("isComingFromUserProfileDetailSwitch")){
                Intent intentUserProfile = new Intent(MainActivity.this,UserProfileActivity.class);
                startActivity(intentUserProfile);
            }
            else if (event.eventType.equalsIgnoreCase("Reload")){
                runOnUiThread(() -> {
                    if (txtStatus!=null){
                        txtStatus.setText("Connected");
                    }
                    if(icSource!=null){
                        Glide.with(mContext).load(R.drawable.wifi).into(icSource);
                    }
                    EventBus.getDefault().post(new ReLoadConversationEvent("ReloadConversationWhenConnect"));
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        //Do something after 100ms
                        LayoutReconnecting.setVisibility(View.GONE);
                        stopIntervalOfConnection();

                    }, 500);
                });
            }

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventDetailFragment event) {
        if (event!=null){
            try {
                if(hubConnection!=null){
                    hubConnection.invoke("ConnectingAgentToHub",Long.parseLong(common.getUserId(context)),event.conversationByUID);
                }
                Bundle bundle = new Bundle();
                bundle.putString(NAME_KEY,event.name);
                bundle.putString(NAME_LETTER_KEY,event.nameFirstLetter);
                bundle.putString(SOURCE_KEY,event.source);
                bundle.putString(COLOR_CODE_KEY,event.colorCodeStr);
                bundle.putString(Constants.CONVERSATION_BY_UID_KEY,event.conversationByUID);
                ReplaceFragmentWithoutClearBackStack(new ConversationsDetailFragment(),true,bundle,true);

            }catch (Exception e){
                    if(e.getMessage().equalsIgnoreCase("The 'invoke' method cannot be called if the connection is not active.") || e.getMessage().equalsIgnoreCase("The 'send' method cannot be called if the connection is not active")|| e.getMessage().contains("is not active")){
                        if (hubConnection != null && agentId != 0 && signalRHelper != null) {
                            boolean isSignalRConnect =  signalRHelper.startSignalRHubClient(hubConnection, agentId,mContext,MainActivity.this);
                            if(isSignalRConnect){
                                stopIntervalOfConnection();
                            }
                        }
                    }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(hubConnection!=null){
            hubConnection.remove("ConversationStatusListener");
            hubConnection.remove("onError");
            hubConnection.remove("AssignChatListener");
            hubConnection.remove("EngageListener");
            hubConnection.remove("ReceiveMessage");
            hubConnection.remove("Notifications");
            hubConnection.stop();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SendChatEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("SendNewMessage")){
                try {
                    if(hubConnection!=null){
                        hubConnection.send("SendPrivateMessage",event.sendMessageModel);
                    }
                } catch (Exception e) {
                    if(e.getMessage().equalsIgnoreCase("The 'invoke' method cannot be called if the connection is not active.") || e.getMessage().equalsIgnoreCase("The 'send' method cannot be called if the connection is not active")|| e.getMessage().contains("is not active")){
                        if (hubConnection != null && agentId != 0 && signalRHelper != null) {
                            boolean isSignalRConnect =  signalRHelper.startSignalRHubClient(hubConnection, agentId,mContext,MainActivity.this);
                            if(isSignalRConnect){
                                stopIntervalOfConnection();
                            }
                        }
                    }
                }

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConversationStatusEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("ResolvedConversation")){
                try {
                    if(hubConnection!=null){
                        hubConnection.send("UpdateConversationStatus",event.conversationStatusModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(e.getMessage().equalsIgnoreCase("The 'invoke' method cannot be called if the connection is not active.") || e.getMessage().equalsIgnoreCase("The 'send' method cannot be called if the connection is not active")|| e.getMessage().contains("is not active")){
                        if (hubConnection != null && agentId != 0 && signalRHelper != null) {
                            boolean isSignalRConnect =  signalRHelper.startSignalRHubClient(hubConnection, agentId,mContext,MainActivity.this);
                            if(isSignalRConnect){
                                stopIntervalOfConnection();
                            }
                        }
                    }
                }

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AssignChatListenerRequest event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("AssignChat")){
                try {
                    if(hubConnection!=null) {
                        try {
                            hubConnection.send("AssignChat", event.groupIds, event.conversationId, event.groupName, event.agentId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(e.getMessage().equalsIgnoreCase("The 'invoke' method cannot be called if the connection is not active.") || e.getMessage().equalsIgnoreCase("The 'send' method cannot be called if the connection is not active")|| e.getMessage().contains("is not active")){
                        if (hubConnection != null && agentId != 0 && signalRHelper != null) {
                            boolean isSignalRConnect =  signalRHelper.startSignalRHubClient(hubConnection, agentId,mContext,MainActivity.this);
                            if(isSignalRConnect){
                                stopIntervalOfConnection();
                            }
                        }
                    }
                }

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllConversationCountEvent event) {
        if(event!=null){
            if(event.eventType.equalsIgnoreCase("updateMenuCount")){
                getConversationsCount();
            }
        }

    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragInstance = fm.findFragmentById(R.id.main_content);
        if (!(fragInstance instanceof ConversationsFragment)) {
            super.onBackPressed();
        }

    }

    public void hideNshowToolbar(boolean isHide){
        if(isHide){
            action_bar.setVisibility(View.GONE);
        }else{
            action_bar.setVisibility(View.VISIBLE);
        }

    }

    public void scheduleApiSignalRConnection() {
        int delay = 0; // delay for 0 sec.
        int period = 2500; // repeat every 10 sec.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run() {
                //Call function
                if(!isSignalRConnected) {
                    //ping signal are every 2 second once connect then upcoming
                    // Reconnect if the connection was lost due to an error
                    if (hubConnection != null && agentId != 0 && signalRHelper != null) {
                        boolean isSignalRConnect =  signalRHelper.startSignalRHubClient(hubConnection, agentId,mContext,MainActivity.this);
                        if(isSignalRConnect){
                            stopIntervalOfConnection();
                        }
                    }
                }
            }
        }, delay, period);
    }

    @Override
    public void hasInternetConnection() {
        runOnUiThread(() -> {
            if (isAlreadyConnected){
                isAlreadyConnected = false;
                EventBus.getDefault().post(new MessageEvent("Reload"));
            }
        });
    }

    @Override
    public void hasNoInternetConnection() {
        runOnUiThread(() -> {
            isAlreadyConnected = true;
            EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));

        });
    }

    public void getConversationsCount(){
        new ApiClient(mContext).getWebService().getConversationCount(Integer.parseInt(common.getUserId(mContext)),common.getIsSuperAdmin(mContext).equalsIgnoreCase("TRUE") ? true : false).enqueue(new Callback<WebResponse<ArrayList<ConversationsCount>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<ConversationsCount>>> call, Response<WebResponse<ArrayList<ConversationsCount>>> response) {
                if (response.body()!=null){
                    if(response.isSuccessful()){
                        if (response.body().getResult().size()>0) {
                            if (response.body().getResult().get(0).assignedCount != 0) {
                                layoutAssignToMeCountMain.setVisibility(View.VISIBLE);
                                txtAssignToMeCount.setText(String.valueOf(response.body().getResult().get(0).assignedCount));
                            } else {
                                layoutAssignToMeCountMain.setVisibility(View.GONE);
                                txtAssignToMeCount.setText("");
                            }
                            if (response.body().getResult().get(0).newCount != 0) {
                                layoutNewCountMain.setVisibility(View.VISIBLE);
                                txtNewCount.setText(String.valueOf(response.body().getResult().get(0).newCount));
                            } else {
                                layoutNewCountMain.setVisibility(View.GONE);
                                txtNewCount.setText("");
                            }
                            if (response.body().getResult().get(0).allAssignedCount != 0) {
                                layoutAllAssignCountMain.setVisibility(View.VISIBLE);
                                txtAllAssign.setText(String.valueOf(response.body().getResult().get(0).allAssignedCount));

                            } else {
                                layoutAllAssignCountMain.setVisibility(View.GONE);
                                txtAllAssign.setText("");
                            }
                            if (response.body().getResult().get(0).resolvedCount != 0) {
                                layoutResolvedCountMain.setVisibility(View.VISIBLE);
                                txtResolvedCount.setText(String.valueOf(response.body().getResult().get(0).resolvedCount));
                            } else {
                                layoutResolvedCountMain.setVisibility(View.GONE);
                                txtResolvedCount.setText("");
                            }
                        }else{
                            layoutResolvedCountMain.setVisibility(View.GONE);
                            txtResolvedCount.setText("");
                            layoutAllAssignCountMain.setVisibility(View.GONE);
                            txtAllAssign.setText("");
                            layoutNewCountMain.setVisibility(View.GONE);
                            txtNewCount.setText("");
                            layoutAssignToMeCountMain.setVisibility(View.GONE);
                            txtAssignToMeCount.setText("");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<ConversationsCount>>> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
