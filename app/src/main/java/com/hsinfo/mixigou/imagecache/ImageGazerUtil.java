package com.hsinfo.mixigou.imagecache;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class ImageGazerUtil 
{	
	public static Bitmap createCircleImage(Bitmap bitmap)
	{
		if(bitmap==null) return bitmap;
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);  
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int radius = 0;
		if(width>height){
			radius=height/2;
		}else{
			radius=width/2;
		}
		
		Bitmap target = Bitmap.createBitmap(radius*2, radius*2, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);  
		canvas.drawCircle(width/2, width/2, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, 0, -width*0.05f, paint); 
		
		return target;
	}

}
