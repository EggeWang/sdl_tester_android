package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlImageItem.SdlImageItemComparator;
import com.livio.sdl.adapters.SdlImageAdapter;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ListViewDialog;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.VrHelpItem;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;


public class VrHelpListDialog extends BaseOkCancelDialog{

    private static final String DIALOG_TITLE = "Create VR Help Items";

    private static final int MAX_BUTTONS = 100;
    
    private List<VrHelpItem> vrHelpItemList = new ArrayList<VrHelpItem>();
    
    private Button but_addItem;
    private ListView lv_vrHelpItems;
    private ArrayAdapter<SdlImageItem> adapter;
    private List<SdlImageItem> adapterList = new ArrayList<SdlImageItem>();
    
    private BaseAlertDialog vrHelpDialog = null;
    private List<SdlImageItem> allImages;
    
    public VrHelpListDialog(Context context, List<SdlImageItem> images){
        this(context, images, null);
    }
    
    public VrHelpListDialog(Context context, List<SdlImageItem> images, List<VrHelpItem> items){
        super(context, DIALOG_TITLE, R.layout.list_add_items);
        setPositiveButton(okButtonListener);
        adapter = new SdlImageAdapter(context, adapterList);
        lv_vrHelpItems.setAdapter(adapter);
        
        this.allImages = images;
        
        if(items != null && items.size() > 0){
            for(VrHelpItem item : items){
                addVrHelpToList(-1, item);
            }
        }
        createDialog();
    }

    @Override
    protected void findViews(View parent) {
        lv_vrHelpItems = (ListView) view.findViewById(R.id.lv_choices);
        lv_vrHelpItems.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                BaseAlertDialog editDeleteDialog = new ListViewDialog<String>(context, "Edit Item", Arrays.asList(new String[]{"Edit", "Delete"}));

                editDeleteDialog.setListener(new BaseAlertDialog.Listener() {
                    @Override
                    public void onResult(Object resultData) {
                        String result = (String) resultData;
                        if("Edit".equals(result)){
                            editItem(position);
                        }
                        else if("Delete".equals(result)){
                            deleteItem(position);
                        }
                    }
                });
                editDeleteDialog.show();
            }
        });
        but_addItem = (Button) view.findViewById(R.id.but_addItem);
        but_addItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getCount() < MAX_BUTTONS){
                    showVrHelpDialog();
                }
                else{
                    Toast.makeText(context, "Reached maximum number of VR help items.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void editItem(final int position){
        VrHelpItem item = vrHelpItemList.get(position);
        Image image = item.getImage();
        BaseAlertDialog editDialog = new TextWithImageEditorDialog(context, "Edit VR Help Item", allImages, item.getText(), (image == null) ? null : image.getValue());
        editDialog.setListener(new BaseAlertDialog.Listener(){
            @Override
            public void onResult(Object resultData){
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) resultData;
                String name = TextWithImageEditorDialog.getMenuName(map);
                String imageName = TextWithImageEditorDialog.getImageName(map);
                Image image = null;
                if(imageName != null){
                    image = new Image();
                    image.setValue(imageName);
                    image.setImageType(ImageType.DYNAMIC);
                }
                
                VrHelpItem item = new VrHelpItem();
                item.setImage(image);
                item.setText(name);
                
                addVrHelpToList(position, item);
                vrHelpItemList.remove(position+1);
                adapterList.remove(position+1);
                adapter.notifyDataSetChanged();
            }
        });
        editDialog.show();
    }
    
    private void deleteItem(int position){
     // TODO: test deletion of an item
        vrHelpItemList.remove(position);
        adapterList.remove(position);
        adapter.notifyDataSetChanged();
    }
    
    private void showVrHelpDialog(){
        vrHelpDialog = new TextWithImageEditorDialog(context, "Create VR Help Item", allImages);
        vrHelpDialog.setListener(new BaseAlertDialog.Listener() {
            @Override
            public void onResult(Object resultData) {
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) resultData;
                String name = TextWithImageEditorDialog.getMenuName(map);
                String imageName = TextWithImageEditorDialog.getImageName(map);
                Image image = null;
                if(imageName != null){
                    image = new Image();
                    image.setValue(imageName);
                    image.setImageType(ImageType.DYNAMIC);
                }
                
                VrHelpItem item = new VrHelpItem();
                item.setImage(image);
                item.setText(name);
                addVrHelpToList(-1, item);
            }
        });
        vrHelpDialog.show();
    }
    
    private void addVrHelpToList(int position, VrHelpItem item){
        if(position == -1){
            vrHelpItemList.add(item);
        }
        else{
            vrHelpItemList.add(position, item);
        }
        
        if(item.getImage() != null){
            // if user selected an image, figure out which one they selected
            int imageIndex = Collections.binarySearch(allImages, new SdlImageItem(null, item.getImage().getValue(), null), new SdlImageItemComparator());
            if(imageIndex >= 0 && imageIndex < allImages.size()){
                SdlImageItem imageItem = allImages.get(imageIndex);
                SdlImageItem adapterItem = new SdlImageItem(imageItem.getBitmap(), item.getText(), FileType.GRAPHIC_JPEG);
                if(position == -1){
                    adapterList.add(adapterItem);
                }
                else{
                    adapterList.add(position, adapterItem);
                }
                adapter.notifyDataSetChanged();
            }
        }
        else{
            // if the user didn't select an image, we'll create an empty one to display in the adapter
            Bitmap image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            SdlImageItem adapterItem = new SdlImageItem(image, item.getText(), FileType.GRAPHIC_JPEG);
            if(position == -1){
                adapterList.add(adapterItem);
            }
            else{
                adapterList.add(position, adapterItem);
            }
            adapter.notifyDataSetChanged();
        }
    }
    
    //dialog button listeners
    private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(vrHelpItemList.size() > 0){
                notifyListener(vrHelpItemList);
            }
        }
    };

}
