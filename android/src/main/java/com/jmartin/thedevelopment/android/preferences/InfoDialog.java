package com.jmartin.thedevelopment.android.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.jmartin.thedevelopment.android.R;


/**
 * Created by jeff on 2014-03-25.
 */
public class InfoDialog extends DialogPreference {

    public InfoDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.info_dialog_layout);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);
        setDialogIcon(null);
    }
}
