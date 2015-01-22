package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义一个Dialog 小马欢迎界面使用
 * 
 *
 */
public class LoadingDialog extends ProgressDialog {  
	private Context context;
	private String message;
	  
    public LoadingDialog(Context context, int theme, String message) {  
        super(context, theme);  
        this.context = context;
        this.message = message;
    }  
  
    public LoadingDialog(Context context, String message) {  
        super(context);  
        this.context = context;
        this.message = message;
    }  
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.tv_loading_dialog);  
        //LoadingDialog.this.setProgressStyle(R.style.DialogStyle);
        setScreenBrightness();  
        
        LoadingDialog.this.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){
					//System.exit(0);
					((Activity)context).finish();
					return true;
				}
				return false;
			}
		});
        this.setOnShowListener(new OnShowListener(){  
                @Override  
                public void onShow(DialogInterface dialog) {  
                	ImageView image = (ImageView) LoadingDialog.this.findViewById(R.id.iv_tv_loading);
            		TextView msg = (TextView) LoadingDialog.this.findViewById(R.id.tv_tv_loading);
            		msg.setTextSize(20f);
            		final AnimationDrawable drawable = new AnimationDrawable();
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro1), 200);//添加图片帧到AnimationDrawable
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro2), 200);
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro3), 200);
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro4), 200);
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro5), 200);
/*            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro6), 200);
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro7), 200);
            		drawable.addFrame(context.getResources().getDrawable(R.drawable.sm_pro8), 200);*/
            		drawable.setOneShot(false);//设置为循环播放
            		image.setImageDrawable(drawable);//AnimationDrawable对象给imageView
            		drawable.start();//动画播放
            		msg.setText(message);
                }  
            });  
    }
    
    /** 
     *  此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。 
     *  范围是0.0到1.0 
     */ 
    private void setScreenBrightness() {  
        Window window = getWindow();  
        WindowManager.LayoutParams lp = window.getAttributes();  
        lp.dimAmount = 0f;  
        window.setAttributes(lp);  
    }  
}  