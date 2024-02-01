package com.example.googleassistantclone.UI_Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.example.googleassistantclone.R;

public class Utils {

    public static final String[] commands = {
            "Check my mail",
            "What can I do",
            "can we date",
            "How to use google assistant",
            "hi",
            "hello",
            "hey",
            "search___apple__",
            "thanks",
            "welcome",
            "clear",
            "delete",
            "what is time",
            "time now",
            "what is the date",
            "send sms",
            "today date",
            "joke",
            "tell me a joke",
            "ask me a fun question",
            "open whatsapp",
            "open facebook",
            "open gmail",
            "open youtube",
            "open googlemaps",
            "open google",
            "turn on bluetooth",
            "turn off bluetooth",
            "turn on flash",
            "turn off flash",
            "capture image",
            "capture photo",
            "open camera",
            "call__num_",
            "dial phonenumber",
            "any thoughts",
            "do you have any thoughts",
            "play ringtone",
            "play music",
            "stop music",
            "stop ringtone",
            "are you married",
            "stop",
            "haha",
            "read me",
            "read my sms",
            "share file",
            "share a text message to __ that __",
            "get nearby bluetooth devices",
            "read my last clipboard",
            "copy to clipboard",
            "explore",
            "what is your name"
    };

    public static final String LogTTS = "Text to Speech";
    public static final String LogSre = "Speech Recognition";
    public static final String LogKeeper = "keeper";
    public final String table_name = "assistant_message_table";
    public static void setCustomActionBar(ActionBar supportActionBar, Context context) {
        if (supportActionBar == null || context == null) {
            return;
        }
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View custom = inflater.inflate(R.layout.custom_toolbar, null);
        supportActionBar.setCustomView(custom);
        supportActionBar.setDisplayShowCustomEnabled(true);
    }


//    public static void setCustomActionBar(Fragment fragment, Context context){
//        setCustomActionBar(((AppCompatActivity) fragment.requireActivity()).getSupportActionBar(),context);
//    }

}