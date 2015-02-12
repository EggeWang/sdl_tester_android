package com.livio.sdltester.dialogs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;


public class TtsItemDialog extends BaseOkCancelDialog {

    private static final String DIALOG_TITLE = "Create a Text-to-Speech Item";
    
    private EditText et_text;
    private Spinner spin_type;

    public TtsItemDialog(Context context){
        this(context, null);
    }
    
    public TtsItemDialog(Context context, TTSChunk chunk){
        super(context, DIALOG_TITLE, R.layout.tts_item);
        List<SpeechCapabilities> spinnerList = Arrays.asList(SpeechCapabilities.values());
        Collections.sort(spinnerList);
        spin_type.setAdapter(AndroidUtils.createSpinnerAdapter(context, SpeechCapabilities.values()));
        
        // load the incoming chunk if it exists
        if(chunk != null){
            et_text.setText(chunk.getText());
            int index = Collections.binarySearch(spinnerList, chunk.getType());
            if(index >= 0){
                spin_type.setSelection(index);
            }
        }
        
        setPositiveButton(okButton);
        createDialog();
    }

    @Override
    protected void findViews(View parent){
        et_text = (EditText) parent.findViewById(R.id.et_text);
        spin_type = (Spinner) parent.findViewById(R.id.spin_type);
    }
    
    private DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String text = et_text.getText().toString().trim();
            SpeechCapabilities type = (SpeechCapabilities) spin_type.getSelectedItem();
            
            if(text.length() > 0){
                TTSChunk result = TTSChunkFactory.createChunk(type, text);
                notifyListener(result);
            }
            else{
                Toast.makeText(context, "TTS item must have a valid text field.", Toast.LENGTH_LONG).show();
            }
        }
    };

}
