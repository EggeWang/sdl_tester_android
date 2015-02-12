package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.livio.sdl.adapters.TtsAdapter;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ListViewDialog;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.TTSChunk;


public class TtsListDialog extends BaseOkCancelDialog{

    private static final String DIALOG_TITLE = "TTS Builder";

    private static final int MAX_BUTTONS = 100;
    
    protected List<TTSChunk> ttsItemList = new ArrayList<TTSChunk>();
    
    private ListView lv_ttsItems;
    private Button but_addItem;
    private TtsAdapter adapter;
    
    private BaseAlertDialog ttsItemDialog = null;
    
    public TtsListDialog(Context context){
        this(context, DIALOG_TITLE);
    }
    
    public TtsListDialog(Context context, List<TTSChunk> ttsList){
        this(context, DIALOG_TITLE, ttsList);
    }
    
    public TtsListDialog(Context context, String title){
        this(context, title, null);
    }
    
    public TtsListDialog(Context context, String title, List<TTSChunk> ttsList){
        super(context, title, R.layout.list_add_items);
        adapter = new TtsAdapter(context, ttsItemList);
        lv_ttsItems.setAdapter(adapter);
        
        if(ttsList != null && ttsList.size() > 0){
            ttsItemList.addAll(ttsList);
            adapter.notifyDataSetChanged();
        }
        
        setPositiveButton(okButtonListener);
        createDialog();
    }

    @Override
    protected void findViews(View parent) {
        lv_ttsItems = (ListView) view.findViewById(R.id.lv_choices);
        lv_ttsItems.setOnItemClickListener(new OnItemClickListener() {
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
                if(ttsItemList.size() < MAX_BUTTONS){
                    showTtsDialog();
                }
                else{
                    Toast.makeText(context, "Reached maximum size", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void editItem(final int position){
        BaseAlertDialog editDialog = new TtsItemDialog(context, ttsItemList.get(position));
        editDialog.setListener(new BaseAlertDialog.Listener(){
            @Override
            public void onResult(Object resultData){
                TTSChunk chunk = (TTSChunk) resultData;
                ttsItemList.add(position, chunk);
                ttsItemList.remove(position+1);
                adapter.notifyDataSetChanged();
            }
        });
        editDialog.show();
    }
    
    private void deleteItem(int position){
     // TODO: test deletion of an item
        ttsItemList.remove(position);
        adapter.notifyDataSetChanged();
    }
    
    private void showTtsDialog(){
        ttsItemDialog = new TtsItemDialog(context);
        ttsItemDialog.setListener(new BaseAlertDialog.Listener() {
            @Override
            public void onResult(Object resultData) {
                TTSChunk chunk = (TTSChunk) resultData;
                addTtsChunkToList(chunk);
            }
        });
        ttsItemDialog.show();
    }
    
    private void addTtsChunkToList(TTSChunk chunk){
        // TODO: test addition of an item
        ttsItemList.add(chunk);
        adapter.notifyDataSetChanged();
    }
    
    //dialog button listeners
    private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(ttsItemList.size() > 0){
                notifyListener(ttsItemList);
            }
        }
    };

}
