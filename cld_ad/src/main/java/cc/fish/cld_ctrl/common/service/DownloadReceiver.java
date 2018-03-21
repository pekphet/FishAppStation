package cc.fish.cld_ctrl.common.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import cc.fish.cld_ctrl.common.net.NetManager;
import cc.fish.cld_ctrl.common.util.DownloadUtils;
import cc.fish.fishhttp.util.ZLog;

/**
 * Created by fish on 16-12-14.
 */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        ZLog.e("receive download", "" + myDwonloadID);

        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri downloadFileUri;
        try {
            downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (downloadFileUri == null) {
            return;
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            info = pm.getPackageArchiveInfo(downloadFileUri.getPath(), PackageManager.GET_ACTIVITIES);
        } else {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(myDwonloadID);
            q.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor c = dManager.query(q);
            if (c != null) {
                if (c.moveToFirst()) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        downloadFileUri = Uri.parse(uriString);
                        info = pm.getPackageArchiveInfo(downloadFileUri.getPath(), PackageManager.GET_ACTIVITIES);
                    }
                    c.close();
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (info == null) {
            return;
        }
        String packageName = info.packageName;
        ZLog.e("DOWNLOAD", info.packageName);
        install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);

    }
}
