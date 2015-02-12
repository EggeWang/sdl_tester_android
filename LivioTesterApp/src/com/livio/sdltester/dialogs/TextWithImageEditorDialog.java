package com.livio.sdltester.dialogs;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ImageListDialog;
import com.livio.sdltester.R;


public class TextWithImageEditorDialog extends BaseOkCancelDialog{
    
    private List<SdlImageItem> availableImages;
    
    private EditText et_menuName, et_imageName;
    private CheckBox check_image;
    private BaseAlertDialog imagesDialog;
    
    public TextWithImageEditorDialog(Context context, String title, List<SdlImageItem> images){
        this(context, title, images, null, null);
    }
    
    public TextWithImageEditorDialog(Context context, String title, List<SdlImageItem> images, String menuName, String imageName){
        super(context, title, R.layout.menu_editor);
        setPositiveButton(okButton);
        this.availableImages = images;
        if(images == null || images.size() <= 0){
            check_image.setVisibility(View.GONE);
            et_imageName.setVisibility(View.GONE);
        }
        if(imageName != null){
            check_image.setChecked(true);
            if(imagesDialog != null && imagesDialog.isShowing()){
                imagesDialog.dismiss();
            }
            et_imageName.setText(imageName);
        }
        if(menuName != null){
            et_menuName.setText(menuName);
        }
        createDialog();
    }

    @Override
    protected void findViews(View parent){
        et_menuName = (EditText) parent.findViewById(R.id.et_menu_text);
        et_imageName = (EditText) parent.findViewById(R.id.et_menu_imageName);
        check_image = (CheckBox) parent.findViewById(R.id.check_enable_image);
        check_image.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showImagesDialog();
                }
                else{
                    et_imageName.setText("");
                }
            }
        });
    }
    
    private void showImagesDialog(){
        if(imagesDialog == null){
            imagesDialog = new ImageListDialog(context, availableImages);
            imagesDialog.setListener(new BaseAlertDialog.Listener() {
                @Override
                public void onResult(Object resultData) {
                    SdlImageItem selectedItem = (SdlImageItem) resultData;
                    if(selectedItem != null){
                        et_imageName.setText(selectedItem.getImageName());
                    }
                }
            });
        }
        
        imagesDialog.show();
    }
    
    private DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String menuName = et_menuName.getText().toString().trim();
            String imageName = null;
            if(check_image.isChecked()){
                imageName = et_imageName.getText().toString().trim();
            }
            
            if(menuName.length() > 0){
                HashMap<String, String> result = new HashMap<String, String>();
                result.put("name", menuName);
                result.put("imageName", imageName);
                notifyListener(result);
            }
            else{
                Toast.makeText(context, "Menu name must be valid.", Toast.LENGTH_LONG).show();
            }
        }
    };

    public static String getMenuName(HashMap<String, String> map){
        return (String) map.get("name");
    }
    
    public static String getImageName(HashMap<String, String> map){
        return (String) map.get("imageName");
    }
}
