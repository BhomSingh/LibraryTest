package com.chat.befiler.commons;

import static android.content.ContentValues.TAG;
import static com.chat.befiler.commons.Constants.ChatHubUrl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chat.befiler.Events.appEvents.MessageEvent;
import com.chat.befiler.activities.LoginActivity;
import com.chat.befiler.activities.MainActivity;
import com.chat.befiler.commons.Common;
import com.chat.befiler.fragments.ReLoadConversationEvent;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.OnClosedCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

public class SignalRHelper {


    public HubConnection createChatHubConnection(Common common, Context context){
        HubConnection hubConnection =
                HubConnectionBuilder.create(ChatHubUrl).withAccessTokenProvider(Single.defer(() -> {
                    // Your logic here.
                    return Single.just(common.getToken(context));
                })).build();
        return hubConnection;
    }

    @SuppressLint("CheckResult")
    public boolean startSignalRHubClient(HubConnection hubConnection , int agentId, Context context, Activity activity){
        if (hubConnection!=null){
            final boolean[] isConnected = new boolean[1];
            try {
                hubConnection.start().doOnComplete(() -> {
                    isConnected[0] = true;
                }).blockingAwait();
                hubConnection.setKeepAliveInterval(2000);
                hubConnection.invoke("AgentJoined",agentId);
                if (isConnected[0]){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().equalsIgnoreCase("Unexpected status code returned from negotiate: 401 Unauthorized.")){
                    //logout callhere
                    Common common = new Common();
                    common.savePermission(context,"");
                    common.saveIsSuperAdmin(context,"");
                    common.isLoggedIn(context,false);
                    common.saveSelectedMenu(context,"0");
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra(Constants.CONVERSATION_BY_UID_KEY,"");
                    context.startActivity(intent);
                    activity.finish();
                }else{
                    EventBus.getDefault().post(new ReLoadConversationEvent("Reconnecting"));
                }
            }
        }
        return false;
    }

}
