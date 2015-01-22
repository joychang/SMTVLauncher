package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.HomeActivity;
import com.shenma.tvlauncher.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 退出的Dialog
 * @author drowtram
 *
 */
public class ExitDialog extends Dialog implements View.OnClickListener{  
	private Context context;
	private Boolean isNet;
    private TextView tv_exit_msg_titile;
	private TextView tv_exit_msg;
	private TextView tv_exit_confirm;
	private TextView tv_exit_cancle;
	private LinearLayout lv_exit_ok;
	private LinearLayout lv_exit_cancle;

	public ExitDialog(Context context) {  
        super(context,R.style.DialogStyle);  
        this.context = context;
        View v = LayoutInflater.from(context).inflate(R.layout.tv_exit_dialog_layout, null);
    	tv_exit_msg_titile = (TextView)v.findViewById(R.id.tv_exit_msg_titile);
		tv_exit_msg = (TextView)v.findViewById(R.id.tv_exit_msg);
		tv_exit_confirm = (TextView)v.findViewById(R.id.tv_exit_confirm);
		tv_exit_cancle = (TextView)v.findViewById(R.id.tv_exit_cancle);
		lv_exit_ok = (LinearLayout)v.findViewById(R.id.lv_exit_ok);
		lv_exit_cancle = (LinearLayout)v.findViewById(R.id.lv_exit_cancle);
		setContentView(v);
		lv_exit_ok.setOnClickListener(this);
		lv_exit_cancle.setOnClickListener(this);
		setScreenBrightness();
    }  
  
//    @Override  
//    protected void onCreate(Bundle savedInstanceState) {  
//        super.onCreate(savedInstanceState);  
//        setContentView(R.layout.tv_exit_dialog_layout);  
//        setScreenBrightness();  
//    }
    
    
	public void setMessage(String message) {
		tv_exit_msg.setText(message);
	}

	public void setConfirm(String confirm) {
		tv_exit_confirm.setText(confirm);
	}

	public void setCancle(String cancle) {
		tv_exit_cancle.setText(cancle);
	}

	public void setTitle(String title) {
		tv_exit_msg_titile.setText(title);
	}

	@Override
	public void show() {
		Window window = this.getWindow();
		window.setWindowAnimations(R.style.DialogAnim);
		this.setCanceledOnTouchOutside(true);
		super.show();
	}
	
	public void setIsNet(Boolean isNet) {
		this.isNet = isNet;
	}

	/** 
     *  此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。 
     *  范围是0.0到1.0 
     */ 
    private void setScreenBrightness() {  
        Window window = getWindow();  
        WindowManager.LayoutParams lp = window.getAttributes();  
        lp.dimAmount = 0.5f;  
        window.setAttributes(lp);  
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_exit_cancle:
			dismiss();
			if(isNet){
				Intent intent = new Intent(context,HomeActivity.class);
				context.startActivity(intent);
				((Activity)context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				//((Activity)context).finish();	
			}
			break;
		case R.id.lv_exit_ok:
			//如果设置网络，跳转到设置
			dismiss();
			if(isNet){
		        Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		        context.startActivity(intent);
		        ((Activity)context).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			}else{
//				ActivityManager manager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);       
//				manager.forceStopPackage("com.shenma.tvlauncher.tvlive");
//				manager.forceStopPackage("com.shenma.tvlauncher.vod");
				System.exit(0);
				//((Activity)context).finish();
			}
			break;
		default:
			break;
		}
	}

	private ActivityManager getSystemService(String activityService) {
		// TODO Auto-generated method stub
		return null;
	}  
      
}  