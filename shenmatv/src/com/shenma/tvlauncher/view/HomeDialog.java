package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 主页的Dialog
 * @author drowtram
 *
 */
public class HomeDialog extends Dialog{
	
	public HomeDialog(Context context) {
		super(context);
	}

	public HomeDialog(Context context, boolean cancelable,OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public HomeDialog(Context context, int theme) {
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
		private String title;
        private String message;
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
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
  
        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }
        
        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }
  
        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
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
        public HomeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final HomeDialog dialog = new HomeDialog(context, R.style.DialogStyle);
            View layout = inflater.inflate(R.layout.tv_exit_dialog_layout, null);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if(title != null) {
            	((TextView)layout.findViewById(R.id.tv_exit_msg_titile)).setText(title);
            }
            if(message != null) {
            	((TextView)layout.findViewById(R.id.tv_exit_msg)).setText(message);
            }
            // set the confirm button
            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.tv_exit_confirm)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((LinearLayout) layout.findViewById(R.id.lv_exit_ok)).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.lv_exit_ok).setVisibility(View.GONE);
            }
            // set the other button
            if (neutralButtonText != null) {
                ((TextView) layout.findViewById(R.id.tv_exit_cancle)).setText(neutralButtonText);
                if (neutralButtonClickListener != null) {
                    ((LinearLayout) layout.findViewById(R.id.lv_exit_cancle)).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                               	 neutralButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEUTRAL);
                                }
                            });
                }
            } else {
                // if no other button just set the visibility to GONE
                layout.findViewById(R.id.lv_exit_cancle).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
	}
      
}  