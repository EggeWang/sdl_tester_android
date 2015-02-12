package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.livio.sdl.enums.SdlCommand;
import com.smartdevicelink.proxy.rpc.Speak;

public class SpeakDialog extends TtsListDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SPEAK;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	public SpeakDialog(Context context){
		super(context, DIALOG_TITLE);
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
		    if(ttsItemList.size() > 0){
		        Speak speak = new Speak();
		        speak.setTtsChunks(ttsItemList);
		        notifyListener(speak);
		    }
		    else{
		        Toast.makeText(context, "Must enter at least 1 item", Toast.LENGTH_LONG).show();
		    }
		}
	};
	
}
