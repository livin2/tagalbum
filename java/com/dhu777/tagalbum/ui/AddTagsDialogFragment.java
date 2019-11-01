package com.dhu777.tagalbum.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dhu777.tagalbum.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Material Design风格实现的对话框,用于添加标签.
 */
public class AddTagsDialogFragment extends DialogFragment {
    /**
     * 对话框确认按钮的回调接口.
     */
    public interface Callback{
        void confirm(String text);
    }
    Callback mCallback;

    public AddTagsDialogFragment(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_tags,null);
        final TextInputEditText editText = view.findViewById(R.id.tag_input_edittext);
        builder.setView(view)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tagT =  editText.getText().toString();
                mCallback.confirm(tagT);
            }
        });
        return builder.create();
    }
}
