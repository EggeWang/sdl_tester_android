package com.livio.sdltester.dialogs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdl.viewhelpers.SeekBarCalculator;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.constants.Jingles;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;


public class TtsItemDialog extends BaseOkCancelDialog {
    
    private static final String DIALOG_TITLE = "Create a Text-to-Speech Item";
    
    private static final int SEEK_BAR_MIN = 0;
    private static final int SEEK_BAR_MAX = 30;
    private static final int SEEK_BAR_DIVISOR = 10;
    private static final int SEEK_BAR_DEFAULT = 5;
    
    private TextView tv_silence_header, tv_silence_text;
    private EditText et_text;
    private Spinner spin_type, spin_jingles;
    private SeekBar seek_silence;
    private float silenceValue; // each "tick" counts for 1/10 second
    
    private SeekBarCalculator silenceCalculator;

    public TtsItemDialog(Context context){
        this(context, null);
    }
    
    public TtsItemDialog(Context context, TTSChunk chunk){
        super(context, DIALOG_TITLE, R.layout.tts_item);
        List<SpeechCapabilities> spinnerList = Arrays.asList(SpeechCapabilities.values());
        Collections.sort(spinnerList);
        spin_type.setAdapter(AndroidUtils.createSpinnerAdapter(context, spinnerList));
        
        List<String> jingleList = Arrays.<String>asList(new String[]{Jingles.HELP_JINGLE, Jingles.INITIAL_JINGLE,
                Jingles.LISTEN_JINGLE, Jingles.NEGATIVE_JINGLE, Jingles.POSITIVE_JINGLE});
        Collections.sort(jingleList);
        spin_jingles.setAdapter(AndroidUtils.createSpinnerAdapter(context, jingleList));
        
        silenceCalculator = new SeekBarCalculator(SEEK_BAR_MIN, SEEK_BAR_MAX, SEEK_BAR_DIVISOR);
        seek_silence.setMax(SEEK_BAR_MAX);
        seek_silence.setProgress(SEEK_BAR_DEFAULT);
        
        // load the incoming chunk if it exists
        if(chunk != null){
            et_text.setText(chunk.getText());
            int index = Collections.binarySearch(spinnerList, chunk.getType());
            if(index >= 0){
                spin_type.setSelection(index);
            }
        }
        
        new TtsTypeSM(spin_type);
        
        setPositiveButton(okButton);
        createDialog();
    }

    @Override
    protected void findViews(View parent){
        et_text = (EditText) parent.findViewById(R.id.et_text);
        spin_type = (Spinner) parent.findViewById(R.id.spin_type);
        spin_jingles = (Spinner) parent.findViewById(R.id.spin_jingles);
        seek_silence = (SeekBar) parent.findViewById(R.id.seek_speak_silence);
        tv_silence_header = (TextView) parent.findViewById(R.id.tv_silence_header);
        tv_silence_text = (TextView) parent.findViewById(R.id.tv_silence_text);
        seek_silence.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            @Override public void onStopTrackingTouch(SeekBar seekBar){}
            @Override public void onStartTrackingTouch(SeekBar seekBar){}
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                silenceValue = silenceCalculator.calculateValue(progress);
                tv_silence_text.setText(silenceValue + " s");
            }
        });
    }
    
    private DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            SpeechCapabilities type = (SpeechCapabilities) spin_type.getSelectedItem();
            String text = "";
            
            switch(type){
            case SAPI_PHONEMES:
            case LHPLUS_PHONEMES:
            case TEXT:
                text = et_text.getText().toString().trim();
                break;
            case PRE_RECORDED:
                text = spin_jingles.getSelectedItem().toString();
                break;
            case SILENCE:
                text = String.valueOf( (int) (silenceValue * 1000) ); // convert seconds to milliseconds
                break;
            }
            
            if(text.length() > 0){
                TTSChunk result = TTSChunkFactory.createChunk(type, text);
                notifyListener(result);
            }
            else{
                Toast.makeText(context, "TTS item must have a valid text field.", Toast.LENGTH_LONG).show();
            }
        }
    };
    
    private void showText(boolean enable){
        int visibility = (enable) ? View.VISIBLE : View.INVISIBLE;
        et_text.setVisibility(visibility);
    }
    
    private void showSilence(boolean enable){
        int visibility = (enable) ? View.VISIBLE : View.INVISIBLE;
        tv_silence_header.setVisibility(visibility);
        tv_silence_text.setVisibility(visibility);
        seek_silence.setVisibility(visibility);
    }
    
    private void showPreRecorded(boolean enable){
        int visibility = (enable) ? View.VISIBLE : View.INVISIBLE;
        spin_jingles.setVisibility(visibility);
    }
    
    private class TtsTypeSM{
        private static final int STATE_INVALID = -1;
        private static final int STATE_TEXT = 0;
        private static final int STATE_SILENCE = 1;
        private static final int STATE_PRE_RECORDED = 2;
        private static final int STATE_INITIAL = STATE_TEXT;
        
        private int state;
        public TtsTypeSM(Spinner typeSpinner){
            this.state = STATE_INITIAL;
            
            typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
                @Override public void onNothingSelected(AdapterView<?> arg0){}

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                    SpeechCapabilities currItem = (SpeechCapabilities) parent.getAdapter().getItem(position);
                    int toState;
                    
                    if(currItem == SpeechCapabilities.TEXT){
                        toState = STATE_TEXT;
                    }
                    else if(currItem == SpeechCapabilities.SILENCE){
                        toState = STATE_SILENCE;
                    }
                    else if(currItem == SpeechCapabilities.PRE_RECORDED){
                        toState = STATE_PRE_RECORDED;
                    }
                    else{
                        toState = STATE_INVALID;
                    }
                    
                    transition(toState);
                }
            });
        }
        
        public void transition(int toState){
            if(this.state == toState){
                return;
            }
            
            switch(toState){
            case STATE_TEXT:
                showText(true);
                showSilence(false);
                showPreRecorded(false);
                break;
            case STATE_SILENCE:
                showText(false);
                showSilence(true);
                showPreRecorded(false);
                break;
            case STATE_PRE_RECORDED:
                showText(false);
                showSilence(false);
                showPreRecorded(true);
                break;
            default:
                break;
            }
            
            this.state = toState;
        }
    }

}
