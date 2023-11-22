package com.example.update.receiver;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("ERROR"," NotificationClickReceiver");
        String text = intent.getStringExtra("text");
        ClipboardManager manager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", text);
        manager.setPrimaryClip(mClipData);
    }


}