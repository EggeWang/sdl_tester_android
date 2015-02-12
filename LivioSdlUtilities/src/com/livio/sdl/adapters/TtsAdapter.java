package com.livio.sdl.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.livio.sdl.R;
import com.smartdevicelink.proxy.rpc.TTSChunk;


public class TtsAdapter extends ArrayAdapter<TTSChunk>{

    public TtsAdapter(Context context, List<TTSChunk> objects) {
        super(context, R.layout.sdl_message_listview_row, objects);
    }

    public TtsAdapter(Context context) {
        super(context, R.layout.sdl_message_listview_row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }
        
        TTSChunk item = getItem(position);
        
        if(item != null){
            populateView(view, item);
        }
        
        
        return view;
    }
    
    /**
     * Populate the input parent view with information from the SDL log message.
     * 
     * @param view The view to populate
     * @param item The data with which to populate the view
     */
    private void populateView(View view, TTSChunk item){
        TextView tv_text = (TextView) view.findViewById(android.R.id.text1);
        TextView tv_type = (TextView) view.findViewById(android.R.id.text2);
        
        // set text values based on input message
        tv_text.setText(item.getText());
        tv_type.setText(item.getType().toString());
    }

}
