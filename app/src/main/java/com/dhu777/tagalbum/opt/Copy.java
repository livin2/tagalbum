package com.dhu777.tagalbum.opt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import androidx.documentfile.provider.DocumentFile;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.util.FileUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Copy extends ForegoundOperation {
    public static final String TAG = "ForegoundOperation.Copy";
    public static final String ACTION_STR = "com.dhu777.tagalbum.action.Copy";

    public static void start(Context context, final AlbumItem[] selected_items,final Uri uri) {
        Intent intent = new Intent(context, Copy.class);
        intent.setAction(ACTION_STR);
        intent.putExtra(FILES, selected_items);
        intent.putExtra(TARGETS,uri);
        context.startService(intent);
    }
    private String curFileName = "";
    @Override
    public void execute(Intent workIntent) {
        AlbumItem[] itemsToOpt = getFiles(workIntent);
        Uri treeUri = workIntent.getParcelableExtra(TARGETS);
        getContentResolver().takePersistableUriPermission(treeUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        String docId = DocumentsContract.getTreeDocumentId(treeUri);
        Uri dirUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId);

        int suc_cot = 0;
        onProgress(suc_cot, itemsToOpt.length);
        for(AlbumItem item:itemsToOpt) {
            curFileName = item.getName();
            String mimeType = FileUtil.getMimeType(getContentResolver(),item.getUri());
            try{
                Uri newUri =  DocumentsContract.createDocument(getContentResolver(), dirUri, mimeType, item.getName());
                DocumentFile newfile = DocumentFile.fromSingleUri(getApplicationContext(),newUri);
                if(newfile == null){
                    onFail(getString(R.string.fail_create_file));
                    return;
                }
                try (FileInputStream inputStream = new FileInputStream(
                        getContentResolver().openFileDescriptor(item.getUri(),"r").getFileDescriptor())) {
                    try (FileOutputStream outputStream = new FileOutputStream(
                            getContentResolver().openFileDescriptor(newfile.getUri(),"w").getFileDescriptor())) {
                        FileUtil.Copy(inputStream,outputStream);
                        suc_cot++;
                        onProgress(suc_cot, itemsToOpt.length);
                    }
                }
            }catch (FileNotFoundException e) {
//                e.printStackTrace();
                onFail(getString(R.string.fail_create_file));
            } catch (IOException e) {
//                e.printStackTrace();
                onFail(getString(R.string.io_exception));
            }
        }
        curFileName = "";
        onSuccess(suc_cot);
    }

    @Override
    public String getNotificationTitle() {
        return getString(R.string.copy)+" "+curFileName;
    }

    @Override
    public int getNotificationSmallIconRes() {
        return R.drawable.ic_launcher_foreground;
    }


}
