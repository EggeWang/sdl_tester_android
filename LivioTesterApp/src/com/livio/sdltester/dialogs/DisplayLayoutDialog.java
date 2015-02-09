package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.dialogs.ListViewDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.SetDisplayLayout;


public class DisplayLayoutDialog extends ListViewDialog<String>{

    private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_SUB_MENU;
    private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

    public DisplayLayoutDialog(Context context, List<String> items){
        super(context, DIALOG_TITLE, items);
        createDialog();
    }

    @Override
    protected void findViews(View parent) {
        listView = (ListView) parent.findViewById(R.id.listView);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                final String selectedItem = ((ArrayAdapter<String>) parent.getAdapter()).getItem(position);
                
                SetDisplayLayout request = new SetDisplayLayout();
                request.setDisplayLayout(selectedItem);
                notifyListener(request);
                
                // since this isn't an ok/cancel dialog, we must dismiss the dialog when an item is selected
                dismiss();
            }
        });
    }

}
