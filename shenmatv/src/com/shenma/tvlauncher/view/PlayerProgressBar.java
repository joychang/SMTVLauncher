package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
/**
 * 自定义缓冲框
 * @author joychang
 *
 */
public class PlayerProgressBar extends ProgressBar{
	String text;  
    Paint mPaint;  
    public PlayerProgressBar(Context context) {  
        super(context);  
        // TODO Auto-generated constructor stub  
        initText();  
    }  
       
    public PlayerProgressBar(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, R.style.CustomProgressStyle);  
        // TODO Auto-generated constructor stub  
        initText();  
    }  
   
   
    public PlayerProgressBar(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
        initText();  
    }  
       
    @Override  
    public synchronized void setProgress(int progress) {  
        // TODO Auto-generated method stub  
        setText(progress);  
        super.setProgress(progress);  
           
    }  
   
    @Override  
    protected synchronized void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
        //this.setText();  
        Rect rect = new Rect();  
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);  
        int x = (getWidth() / 2) - rect.centerX();   
        int y = (getHeight() / 2) - rect.centerY();   
        canvas.drawText(this.text, x, y, this.mPaint);   
    }  
       
    //初始化，画笔  
    private void initText(){  
        this.mPaint = new Paint();
        this.mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.sm_24));
        this.mPaint.setColor(Color.WHITE);  
           
    }  
       
    private void setText(){  
        setText(this.getProgress());  
    }  
       
    //设置文字内容  
    private void setText(int progress){  
        this.text = progress + "%";
    }
}
