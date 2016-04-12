package com.zzw.sketchpad.interfaces;

import android.graphics.Canvas;
public interface ISketchpadDraw {

	 void draw(Canvas canvas);
	 boolean hasDraw();
	 void cleanAll();
	 void touchDown(float x, float y);
	 void touchMove(float x, float y);
	 void touchUp(float x, float y);
}
