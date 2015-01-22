package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class WiFiDialog extends Dialog{



	public WiFiDialog(Context context) {
		super(context);
	}

	public WiFiDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public WiFiDialog(Context context, int theme) {
		super(context, theme);
	}
	
	@Override
	public void show() {
		Window window = this.getWindow();
		window.setWindowAnimations(R.style.DialogAnim);
		this.setCanceledOnTouchOutside(true);
		super.show();
	}
	
	public static class Builder{
		 private Context context;
         private String positiveButtonText;
         private String neutralButtonText;
         private View contentView;
         private DialogInterface.OnClickListener positiveButtonClickListener, neutralButtonClickListener;
         
         public Builder(Context context) {
             this.context = context;
         }
         
         public Builder setContentView(View contentView) {
        	 this.contentView = contentView;
             return this;
         }
         
         /**
          * Set the positive button resource and it's listener
          * @param positiveButtonText
          * @param listener
          * @return
          */
         public Builder setPositiveButton(int positiveButtonText,DialogInterface.OnClickListener listener) {
             this.positiveButtonText = (String) context.getText(positiveButtonText);
             this.positiveButtonClickListener = listener;
             return this;
         }

         /**
          * Set the positive button text and it's listener
          * @param positiveButtonText
          * @param listener
          * @return
          */
         public Builder setPositiveButton(String positiveButtonText,DialogInterface.OnClickListener listener) {
             this.positiveButtonText = positiveButtonText;
             this.positiveButtonClickListener = listener;
             return this;
         }

         /**
          * Set the negative button resource and it's listener
          * @param negativeButtonText
          * @param listener
          * @return
          */
         public Builder setNeutralButton(int neutralButtonText,DialogInterface.OnClickListener listener) {
             this.neutralButtonText = (String) context.getText(neutralButtonText);
             this.neutralButtonClickListener = listener;
             return this;
         }
   
         /**
          * Set the negative button text and it's listener
          * @param negativeButtonText
          * @param listener
          * @return
          */
         public Builder setNeutralButton(String neutralButtonText, DialogInterface.OnClickListener listener) {
             this.neutralButtonText = neutralButtonText;
             this.neutralButtonClickListener = listener;
             return this;
         }
         
         /**
          * Create the custom dialog
          */
         public WiFiDialog create() {
             LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             // instantiate the dialog with the custom Theme
             final WiFiDialog dialog = new WiFiDialog(context, R.style.WiFiDialog);
             View v = inflater.inflate(R.layout.wifi_dialog, null);
             dialog.addContentView(v, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
             // set the confirm button
             if (positiveButtonText != null) {
                 ((Button) v.findViewById(R.id.wifi_dialog_bt1)).setText(positiveButtonText);
                 if (positiveButtonClickListener != null) {
                     ((Button) v.findViewById(R.id.wifi_dialog_bt1)) .setOnClickListener(new View.OnClickListener() {
                                 public void onClick(View v) {
                                     positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
                                 }
                             });
                 }
             } else {
                 // if no confirm button just set the visibility to GONE
                 v.findViewById(R.id.wifi_dialog_bt1).setVisibility( View.GONE);
             }
             // set the forget button
             if (neutralButtonText != null) {
                 ((Button) v.findViewById(R.id.wifi_dialog_bt2)).setText(neutralButtonText);
                 if (neutralButtonClickListener != null) {
                     ((Button) v.findViewById(R.id.wifi_dialog_bt2)).setOnClickListener(new View.OnClickListener() {
                                 public void onClick(View v) {
                                	 neutralButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEUTRAL);
                                 }
                             });
                 }
             } else {
                 // if no forget button just set the visibility to GONE
                 v.findViewById(R.id.wifi_dialog_bt2).setVisibility(View.GONE);
             }
             if (contentView != null) {
                 // if no message set
                 // add the contentView to the dialog body
                 ((LinearLayout) v.findViewById(R.id.wifi_dialog_content)).removeAllViews();
                 ((LinearLayout) v.findViewById(R.id.wifi_dialog_content)).addView(contentView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
             }
             dialog.setContentView(v);
             return dialog;
         }
	}

}
