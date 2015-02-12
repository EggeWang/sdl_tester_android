package com.livio.sdltester.dialogs;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.KeyboardProperties;
import com.smartdevicelink.proxy.rpc.SetGlobalProperties;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.VrHelpItem;
import com.smartdevicelink.proxy.rpc.enums.ImageType;


public class SetGlobalPropertiesDialog extends BaseOkCancelDialog{

    private static final SdlCommand SYNC_COMMAND = SdlCommand.SET_GLOBAL_PROPERTIES;
    private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
    
    private List<SdlImageItem> images;
    
    private KeyboardProperties keyboardProperties;
    private List<TTSChunk> timeoutPrompt, helpPrompt;
    private List<VrHelpItem> vrHelp;
    private String menuName, vrHelpTitle;
    private Image menuImage;

    // TODO: re-load dialog based on existing KeyboardProperties object
    
    public SetGlobalPropertiesDialog(Context context, List<SdlImageItem> imageList){
        super(context, DIALOG_TITLE, R.layout.set_global_properties);
        this.images = imageList;
        setPositiveButton(okButton);
        createDialog();
    }

    @Override
    protected void findViews(View parent){
        ((Button) parent.findViewById(R.id.but_editTimeoutPrompt)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                BaseAlertDialog timeoutPromptDialog = new TtsListDialog(context, timeoutPrompt);
                timeoutPromptDialog.setListener(new BaseAlertDialog.Listener(){
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(Object resultData){
                        timeoutPrompt = (List<TTSChunk>) resultData;
                    }
                });
                timeoutPromptDialog.show();
            }
        });
        
        ((Button) parent.findViewById(R.id.but_editHelpPrompt)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                BaseAlertDialog helpPromptDialog = new TtsListDialog(context, helpPrompt);
                helpPromptDialog.setListener(new BaseAlertDialog.Listener(){
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(Object resultData){
                        helpPrompt = (List<TTSChunk>) resultData;
                    }
                });
                helpPromptDialog.show();
            }
        });
        
        ((Button) parent.findViewById(R.id.but_editMenuButton)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                BaseAlertDialog menuDialog = new TextWithImageEditorDialog(context, "Menu Editor", images, menuName, 
                        (menuImage == null) ? null : menuImage.getValue());
                menuDialog.setListener(new BaseAlertDialog.Listener(){
                    @Override
                    public void onResult(Object resultData){
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> result = (HashMap<String, String>) resultData;
                        menuName = TextWithImageEditorDialog.getMenuName(result);
                        
                        String imageName = TextWithImageEditorDialog.getImageName(result);
                        if(imageName != null){
                            menuImage = new Image();
                            menuImage.setImageType(ImageType.DYNAMIC);
                            menuImage.setValue(imageName);
                        }
                        else{
                            menuImage = null;
                        }
                    }
                });
                menuDialog.show();
            }
        });
        
        ((Button) parent.findViewById(R.id.but_editVrHelp)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                BaseAlertDialog vrHelpDialog = new VrHelpListDialog(context, images, vrHelp);
                vrHelpDialog.setListener(new BaseAlertDialog.Listener(){
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(Object resultData){
                        vrHelp = (List<VrHelpItem>) resultData;
                    }
                });
                vrHelpDialog.show();
            }
        });
        
        ((Button) parent.findViewById(R.id.but_editKeyboardProperties)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                BaseAlertDialog keyboardPropertiesDialog = new KeyboardPropertiesDialog(context, keyboardProperties);
                keyboardPropertiesDialog.setListener(new BaseAlertDialog.Listener(){
                    @Override
                    public void onResult(Object resultData){
                        keyboardProperties = (KeyboardProperties) resultData;
                    }
                });
                keyboardPropertiesDialog.show();
            }
        });
    }
    
    private DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            boolean allClear = true; // TODO: determine min requirements for this message
            
            if(allClear){
                SetGlobalProperties result = new SetGlobalProperties();
                result.setHelpPrompt(helpPrompt);
                result.setKeyboardProperties(keyboardProperties);
                result.setMenuIcon(menuImage);
                result.setMenuTitle(menuName);
                result.setTimeoutPrompt(timeoutPrompt);
                result.setVrHelpTitle(vrHelpTitle);
                result.setVrHelp(vrHelp);
                notifyListener(result);
            }
            else{
                Toast.makeText(context, "TTS item must have a valid text field.", Toast.LENGTH_LONG).show();
            }
        }
    };

}
