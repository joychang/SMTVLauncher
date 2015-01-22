package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 提示toast
 * @author joychang
 *
 */
public class SmtvToast extends Toast {
	
	private Context context;
	private ImageView iv_smtv_toast;
	private TextView tv_smtv_toast;
	
	public SmtvToast(Context context) {
		super(context);
		this.context = context;
		View v = LayoutInflater.from(context).inflate(R.layout.tv_toast,
				null);
		iv_smtv_toast = (ImageView) v.findViewById(R.id.iv_smtv_toast);
		tv_smtv_toast = (TextView) v.findViewById(R.id.tv_smtv_toast);
		setView(v);
	}

	public static SmtvToast makeText(Context paramContext, int paramInt1,
			int paramInt2) {
		SmtvToast localSmtvToast = new SmtvToast(paramContext);
		localSmtvToast.setText(paramInt1);
		localSmtvToast.setDuration(paramInt2);
		return localSmtvToast;
	}

	public static SmtvToast makeText(Context paramContext, int paramInt1,
			int paramInt2, int paramInt3) {
		SmtvToast localSmtvToast = new SmtvToast(paramContext);
		localSmtvToast.setText(paramInt1);
		localSmtvToast.setDuration(paramInt2);
		localSmtvToast.setIcon(paramInt3);
		return localSmtvToast;
	}

	public static SmtvToast makeText(Context paramContext, int paramInt1,
			int paramInt2, Drawable paramDrawable) {
		SmtvToast localSmtvToast = new SmtvToast(paramContext);
		localSmtvToast.setText(paramInt1);
		localSmtvToast.setDuration(paramInt2);
		localSmtvToast.setIcon(paramDrawable);
		return localSmtvToast;
	}

	public static SmtvToast makeText(Context paramContext, String paramString,
			int paramInt) {
		SmtvToast localSmtvToast = new SmtvToast(paramContext);
		localSmtvToast.setText(paramString);
		localSmtvToast.setDuration(paramInt);
		return localSmtvToast;
	}

	public static SmtvToast makeText(Context paramContext, String paramString,
			int paramInt, Drawable paramDrawable) {
		SmtvToast localSmtvToast = new SmtvToast(paramContext);
		localSmtvToast.setText(paramString);
		localSmtvToast.setDuration(paramInt);
		localSmtvToast.setIcon(paramDrawable);
		return localSmtvToast;
	}

	public void removeIcon() {
		iv_smtv_toast.setImageBitmap(null);
	}

	public void setIcon(int paramInt) {
		iv_smtv_toast.setImageResource(paramInt);
	}

	public void setIcon(Drawable paramDrawable) {
		iv_smtv_toast.setImageDrawable(paramDrawable);
	}

	public void setText(int paramInt) {
		tv_smtv_toast.setText(paramInt);
	}

	public void setText(String paramString) {
		tv_smtv_toast.setText(paramString);
	}

	public void setTextColor(int paramInt) {
		int i = this.context.getResources().getColor(paramInt);
		tv_smtv_toast.setTextColor(i);
	}

	public void setTextSize(float paramFloat) {
		tv_smtv_toast.setTextSize(paramFloat);
	}
}
