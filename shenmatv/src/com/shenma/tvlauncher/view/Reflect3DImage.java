package com.shenma.tvlauncher.view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;

public class Reflect3DImage {
	public static Bitmap createReflectedImage(Bitmap paramBitmap, int paramInt) {
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		localMatrix.preScale(1.0F, -1.0F);
		Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j - paramInt,
				i, paramInt, localMatrix, false);
		Bitmap localBitmap2 = Bitmap.createBitmap(i, j + paramInt,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap2);
		Paint localPaint1 = new Paint();
		localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint1);
		localCanvas.drawBitmap(localBitmap1, 0.0F, j, localPaint1);
		Paint localPaint2 = new Paint();
		localPaint2.setShader(new LinearGradient(0.0F, paramBitmap.getHeight(),
				0.0F, localBitmap2.getHeight(), 1895825407, 16777215,
				Shader.TileMode.MIRROR));
		localPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, j, i, localBitmap2.getHeight(), localPaint2);
		localBitmap1.recycle();
		return localBitmap2;
	}

	public static Bitmap skewImage(Bitmap paramBitmap, int paramInt) {
		Bitmap localBitmap1 = createReflectedImage(paramBitmap, paramInt);
		Camera localCamera = new Camera();
		localCamera.save();
		Matrix localMatrix = new Matrix();
		localCamera.rotateY(15.0F);
		localCamera.getMatrix(localMatrix);
		localCamera.restore();
		localMatrix.preTranslate(-localBitmap1.getWidth() >> 1,
				-localBitmap1.getHeight() >> 1);
		Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, 0,
				localBitmap1.getWidth(), localBitmap1.getHeight(), localMatrix,
				true);
		Bitmap localBitmap3 = Bitmap.createBitmap(localBitmap2.getWidth(),
				localBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap3);
		Paint localPaint = new Paint();
		localPaint.setAntiAlias(true);
		localPaint.setFilterBitmap(true);
		localCanvas.drawBitmap(localBitmap2, 0.0F, 0.0F, localPaint);
		localBitmap2.recycle();
		return localBitmap3;
	}
}