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
import com.livio.sdl.utils.StringUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.KeyboardProperties;
import com.smartdevicelink.proxy.rpc.enums.KeyboardLayout;
import com.smartdevicelink.proxy.rpc.enums.KeypressMode;
import com.smartdevicelink.proxy.rpc.enums.Language;


public class KeyboardPropertiesDialog extends BaseOkCancelDialog{

    private static final String DIALOG_TITLE = "Keyboard Properties";
    
    List<KeypressMode> keypressList;
    List<KeyboardLayout> keyboardLayoutList;
    List<Language> languageList;
    
    private Spinner spin_keypressMode, spin_keyboardLayout, spin_language;
    private EditText et_limitedCharList, et_autocompleteText;
    
    public KeyboardPropertiesDialog(Context context){
        this(context, null);
    }
    
    public KeyboardPropertiesDialog(Context context, KeyboardProperties keyboardProperties){
        super(context, DIALOG_TITLE, R.layout.keyboard_properties);
        setPositiveButton(okButton);
        
        if(keyboardProperties != null){
            setValues(keyboardProperties);
        }
        
        createDialog();
    }
    
    private void setValues(KeyboardProperties keyboardProperties){
        String autocompleteText = keyboardProperties.getAutoCompleteText();
        List<String> limitedCharList = keyboardProperties.getLimitedCharacterList();
        KeypressMode keypressMode = keyboardProperties.getKeypressMode();
        KeyboardLayout keyboardLayout = keyboardProperties.getKeyboardLayout();
        Language language = keyboardProperties.getLanguage();
        
        if(autocompleteText != null){
            et_autocompleteText.setText(autocompleteText);
        }
        
        if(limitedCharList != null && limitedCharList.size() > 0){
            et_limitedCharList.setText(StringUtils.stringListToCsv(limitedCharList));
        }
        
        if(keypressMode != null){
            int index = Collections.binarySearch(keypressList, keypressMode);
            spin_keypressMode.setSelection(index);
        }
        
        if(keyboardLayout != null){
            int index = Collections.binarySearch(keyboardLayoutList, keyboardLayout);
            spin_keyboardLayout.setSelection(index);
        }
        
        if(language != null){
            int index = Collections.binarySearch(languageList, language);
            spin_language.setSelection(index);
        }
    }

    @Override
    protected void findViews(View parent){
        keypressList = Arrays.<KeypressMode>asList(KeypressMode.values());
        Collections.sort(keypressList);
        
        keyboardLayoutList = Arrays.<KeyboardLayout>asList(KeyboardLayout.values()) ;
        Collections.sort(keyboardLayoutList);
        
        languageList = Arrays.<Language>asList(Language.values()) ;
        Collections.sort(languageList);
        
        spin_keypressMode = (Spinner) parent.findViewById(R.id.spin_keypressMode);
        spin_keypressMode.setAdapter(AndroidUtils.createSpinnerAdapter(context, keypressList));
        spin_keyboardLayout = (Spinner) parent.findViewById(R.id.spin_keyboardLayout);
        spin_keyboardLayout.setAdapter(AndroidUtils.createSpinnerAdapter(context, keyboardLayoutList));
        spin_language = (Spinner) parent.findViewById(R.id.spin_language);
        spin_language.setAdapter(AndroidUtils.createSpinnerAdapter(context, languageList));
        et_limitedCharList = (EditText) parent.findViewById(R.id.et_limited_char_list);
        et_autocompleteText = (EditText) parent.findViewById(R.id.et_autocomplete_text);
    }
    
    private DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String autoCompleteText = et_autocompleteText.getText().toString().trim();
            
            List<String> limitedCharList = null;
            String limitedCharStr = et_limitedCharList.getText().toString().trim();
            if( ! "".equals(limitedCharStr) ){
                String[] split = limitedCharStr.split(",");
                limitedCharList = Arrays.<String>asList(split);
            }
            
            KeypressMode mode = (KeypressMode) spin_keypressMode.getSelectedItem();
            KeyboardLayout layout = (KeyboardLayout) spin_keyboardLayout.getSelectedItem();
            Language language = (Language) spin_language.getSelectedItem();
            
            // TODO: what is required here?
            if(autoCompleteText.length() > 0 && limitedCharList != null && limitedCharList.size() > 0){
                KeyboardProperties result = new KeyboardProperties();
                result.setAutoCompleteText(autoCompleteText);
                result.setLimitedCharacterList(limitedCharList);
                result.setKeyboardLayout(layout);
                result.setKeypressMode(mode);
                result.setLanguage(language);
                notifyListener(result);
            }
            else{
                Toast.makeText(context, "Must enter autocomplete text and limited char list.", Toast.LENGTH_LONG).show();
            }
        }
    };

}
