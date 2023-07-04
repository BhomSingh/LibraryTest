package com.chat.befiler;

import android.app.Application;

import androidx.startup.AppInitializer;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import net.danlew.android.joda.JodaTimeInitializer;


public class ChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new GoogleEmojiProvider());
    }
}