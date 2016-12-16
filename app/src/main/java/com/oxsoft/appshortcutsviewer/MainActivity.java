package com.oxsoft.appshortcutsviewer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root);
        if (linearLayout == null) return;

        final LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        if (!launcherApps.hasShortcutHostPermission()) {
            Log.e(TAG, "hasShortcutHostPermission is false");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.activityInfo == null) continue;
            final ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
            int queryFlags = LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC | LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED | LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST;
            List<ShortcutInfo> shortcutInfoList = launcherApps.getShortcuts(new LauncherApps.ShortcutQuery().setPackage(applicationInfo.packageName).setQueryFlags(queryFlags), UserHandle.getUserHandleForUid(applicationInfo.uid));
            if (shortcutInfoList.isEmpty()) continue;
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
            LinearLayout ll = new LinearLayout(this);
            horizontalScrollView.addView(ll);
            linearLayout.addView(horizontalScrollView);
            final Drawable applicationIcon = getPackageManager().getApplicationIcon(applicationInfo);
            ImageView applicationIconView = new ImageView(this);
            applicationIconView.setImageDrawable(applicationIcon);
            applicationIconView.setPadding(0, 0, 24, 0);
            applicationIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName));
                }
            });
            ll.addView(applicationIconView);
            ll.setPadding(24, 24, 24, 24);
            ll.setGravity(Gravity.CENTER_VERTICAL);
            for (final ShortcutInfo shortcutInfo : shortcutInfoList) {
                Drawable shortcutIcon = launcherApps.getShortcutIconDrawable(shortcutInfo, getResources().getDisplayMetrics().densityDpi);
                ImageView shortcutIconView = new ImageView(this);
                shortcutIconView.setImageDrawable(shortcutIcon);
                shortcutIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launcherApps.startShortcut(shortcutInfo, null, null);
                    }
                });
                ll.addView(shortcutIconView);
            }
        }
    }
}
