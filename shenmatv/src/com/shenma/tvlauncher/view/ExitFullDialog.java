package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.*;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;

/**
 * 退出的Dialog
 * @author drowtram
 *
 */
public class ExitFullDialog extends Dialog{
	
	public ExitFullDialog(Context context) {
		super(context);
	}

	public ExitFullDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public ExitFullDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder{
		private Context context;
//		private String title;
//      private String message;
        private String positiveButtonText;
        private String neutralButtonText;
        private DialogInterface.OnClickListener positiveButtonClickListener, neutralButtonClickListener;
        
        public Builder(Context context) {
            this.context = context;
        }
        
        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
//        public Builder setMessage(String message) {
//            this.message = message;
//            return this;
//        }
  
        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
//        public Builder setMessage(int message) {
//            this.message = (String) context.getText(message);
//            return this;
//        }
        
        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
//        public Builder setTitle(int title) {
//            this.title = (String) context.getText(title);
//            return this;
//        }
  
        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
//        public Builder setTitle(String title) {
//            this.title = title;
//            return this;
//        }
        
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
        public ExitFullDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ExitFullDialog dialog = new ExitFullDialog(context, R.style.Dialog_Fullscreen);
            View layout = inflater.inflate(R.layout.exit_view, null);
            Bitmap bg = BlurUtils.doBlur(BitmapFactory.decodeResource(context.getResources(), R.drawable.bg), 7,false);
            layout.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bg));
            dialog.setContentView(layout);
//            if(title != null) {
//            	((TextView)layout.findViewById(R.id.tv_exit_msg_titile)).setText(title);
//            }
//            if(message != null) {
//            	((TextView)layout.findViewById(R.id.tv_exit_msg)).setText(message);
//            }
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.exit_ok_bt)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.exit_ok_bt)).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.exit_ok_bt).setVisibility(View.GONE);
            }
            // set the other button
            if (neutralButtonText != null) {
                ((Button) layout.findViewById(R.id.exit_cancel_bt)).setText(neutralButtonText);
                if (neutralButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.exit_cancel_bt)).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                               	 neutralButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEUTRAL);
                                }
                            });
                }
            } else {
                // if no other button just set the visibility to GONE
                layout.findViewById(R.id.exit_cancel_bt).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
	}
      
}  