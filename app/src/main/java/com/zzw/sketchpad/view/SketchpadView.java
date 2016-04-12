package com.zzw.sketchpad.view;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zzw.sketchpad.Contral.BitmapCtl;
import com.zzw.sketchpad.Contral.BitmapUtil;
import com.zzw.sketchpad.Contral.Circlectl;
import com.zzw.sketchpad.Contral.EraserCtl;
import com.zzw.sketchpad.Contral.LineCtl;
import com.zzw.sketchpad.Contral.OvaluCtl;
import com.zzw.sketchpad.Contral.PenuCtl;
import com.zzw.sketchpad.Contral.PlygonCtl;
import com.zzw.sketchpad.Contral.RectuCtl;
import com.zzw.sketchpad.Contral.Spraygun;
import com.zzw.sketchpad.R;
import com.zzw.sketchpad.Activity.SketchpadMainActivity;
import com.zzw.sketchpad.data.CommonDef;
import com.zzw.sketchpad.interfaces.ISketchpadDraw;
import com.zzw.sketchpad.interfaces.IUndoRedoCommand;


public class SketchpadView extends View implements IUndoRedoCommand {


	//设置画笔常量
	public static final int STROKE_PEN = 12;       //画笔1
	public static final int STROKE_ERASER = 2;    //橡皮擦2
	public static final int STROKE_PLYGON = 10;   //多边形3
	public static final int STROKE_RECT = 9;      //矩形 4
	public static final int STROKE_CIRCLE = 8;    //圆 5
	public static final int STROKE_OVAL = 7;      //椭圆 6
	public static final int STROKE_LINE = 6;      //直线7
	public static final int STROKE_SPRAYGUN=5;      //喷枪8
	public static final int STROKE_PAINTPOT=4;     //油漆桶9
	public static int   flag =0;                    //油漆桶参数

	public static final int UNDO_SIZE = 20;       //撤销栈的大小
	public static final int BITMAP_WIDTH = 650;		//画布高
	public static final int BITMAP_HEIGHT = 400;	//画布宽

	private int m_strokeType = STROKE_PEN;   //画笔风格
	private static int m_strokeColor = Color.RED;   //画笔颜色
	private static int m_penSize = CommonDef.SMALL_PEN_WIDTH;         //画笔大小
	private static int m_eraserSize = CommonDef.LARGE_ERASER_WIDTH;   //橡皮擦大小

	//实例新画布
	private boolean m_isEnableDraw = true;   //标记是否可以画
	private boolean m_isDirty = false;     //标记
	private boolean m_isTouchUp = false;    //标记是否鼠标弹起
	private boolean m_isSetForeBmp = false;   //标记是否设置了前bitmap
	private int m_bkColor = Color.WHITE;    //背景色

	private int m_canvasWidth = 100;    //画布宽
	private int m_canvasHeight = 100;    //画布高
	private boolean m_canClear = true;   //标记是否可清除

	private Bitmap m_foreBitmap = null;     //用于显示的bitmap
	private Bitmap m_tempForeBitmap = null; //用于缓冲的bitmap
	private Bitmap m_bkBitmap = null;       //用于背后画的bitmap

	private Canvas m_canvas;     //画布
	private Paint m_bitmapPaint = null;   //画笔
	private SketchPadUndoStack m_undoStack = null;//栈存放执行的操作
	private ISketchpadDraw m_curTool = null;   //记录操作的对象画笔类

	int antiontemp = 0;//获取鼠标点击画布的event
	boolean myLoop = false;// 喷枪结束标识符
	private Bitmap bgBitmap = null;
	///////////////// paint and Bk//////////////////////////////
	//画布参数设计
	public boolean isDirty(){
		return m_isDirty;   //
	}
	public void setDrawStrokeEnable(boolean isEnable){
		m_isEnableDraw = isEnable;  //确定是否可绘图
	}
	public void setBkColor(int color){   //设置背景颜色
		if (m_bkColor != color){
			m_bkColor = color;
			invalidate();
		}
	}
	public static void setStrokeSize(int size, int type){   //设置画笔的大小和橡皮擦大小
		switch(type){
			case STROKE_PEN:
				m_penSize = size;
				break;

			case STROKE_ERASER:
				m_eraserSize = size;
				break;
		}
	}

	public static void setStrokeColor(int color){   //设置画笔颜色
		m_strokeColor = color;
	}

	public static int getStrokeSize(){   //得到画笔的大小
		return m_penSize;
	}

	public static int getEraser(){   //得到橡皮擦的大小
		return  m_eraserSize;
	}
	public static int getStrokeColor(){   //得到画笔的大小
		return m_strokeColor;
	}
	////////////////////////////////////////////////////////////
	public void clearAllStrokes(){   //清空设置
		if (m_canClear){
			// 清空撤销栈
			m_undoStack.clearAll();
			// 设置当前的bitmap对象为空
			if (null != m_tempForeBitmap){
				m_tempForeBitmap.recycle();
				m_tempForeBitmap = null;
			}
			// Create a new fore bitmap and set to canvas.
			createStrokeBitmap(m_canvasWidth, m_canvasHeight);

			invalidate();
			m_isDirty = true;
			m_canClear = false;
		}
	}
	///////////////////////bitmap/////////////////////
    /*保存时对当前绘图板的图片进行快照*/
	public Bitmap getCanvasSnapshot(){
		setDrawingCacheEnabled(true);
		buildDrawingCache(true);
		Bitmap bmp = getDrawingCache(true);
		if (null == bmp){
			android.util.Log.d("leehong2", "getCanvasSnapshot getDrawingCache == null");
		}
		return BitmapUtil.duplicateBitmap(bmp);
	}
	/*打开图像文件时，设置当前视图为foreBitmap*/
	public void setForeBitmap(Bitmap foreBitmap){
		if (foreBitmap != m_foreBitmap && null != foreBitmap){
			// Recycle the bitmap.
			if (null != m_foreBitmap){
				m_foreBitmap.recycle();
			}
			// Here create a new fore bitmap to avoid crashing when set bitmap to canvas.
			m_foreBitmap = BitmapUtil.duplicateBitmap(foreBitmap);
			if (null != m_foreBitmap && null != m_canvas){
				m_canvas.setBitmap(m_foreBitmap);
			}
			invalidate();
		}
	}
	public Bitmap getForeBitmap(){
		return m_bkBitmap;
	}
	public void setBkBitmap(Bitmap bmp){   //设置背景bitmap
		if (m_bkBitmap != bmp){
			//m_bkBitmap = bmp;
			m_bkBitmap = BitmapUtil.duplicateBitmap(bmp);
			invalidate();
		}
	}
	public Bitmap getBkBitmap(){
		return m_bkBitmap;
	}
	protected void createStrokeBitmap(int w, int h){
		m_canvasWidth = w;
		m_canvasHeight = h;
		Bitmap bitmap = Bitmap.createBitmap(m_canvasWidth, m_canvasHeight, Bitmap.Config.ARGB_8888);
		if (null != bitmap){
			m_foreBitmap = bitmap;
			// Set the fore bitmap to m_canvas to be as canvas of strokes.
			m_canvas.setBitmap(m_foreBitmap);
		}
	}
	protected void setTempForeBitmap(Bitmap tempForeBitmap){
		if (null != tempForeBitmap){
			if (null != m_foreBitmap){
				m_foreBitmap.recycle();
			}
			m_foreBitmap = BitmapCtl.duplicateBitmap(tempForeBitmap);
			if (null != m_foreBitmap && null != m_canvas) {
				m_canvas.setBitmap(m_foreBitmap);
				invalidate();
			}
		}
	}

	protected void setCanvasSize(int width, int height)
	{//设置画布大小
		if (width > 0 && height > 0){
			if (m_canvasWidth != width || m_canvasHeight != height){
				m_canvasWidth = width;
				m_canvasHeight = height;
				createStrokeBitmap(m_canvasWidth, m_canvasHeight);
			}
		}
	}
	//初始化数据
	protected void initialize(){
		m_canvas = new Canvas();//实例画布用于整个绘图操作
		m_bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  //实例化画笔用于bitmap设置画布canvas
		m_undoStack = new SketchPadUndoStack(this, UNDO_SIZE);//实例化队列
		// Set stroke type and create a stroke tool.
		//setStrokeType(STROKE_PEN);  //开启画笔默认
	}
	//启动设置画笔的颜色和大小
	public void setStrokeType(int type){
		m_strokeColor=SketchpadView.getStrokeColor();
		m_penSize=SketchpadView.getStrokeSize();
		switch(type){
			case STROKE_PEN:
				m_curTool = new PenuCtl(m_penSize, m_strokeColor);
				break;

			case STROKE_ERASER:
				m_curTool = new EraserCtl(m_eraserSize);
				break;
			case STROKE_PLYGON:
				m_curTool = new PlygonCtl(m_penSize,m_strokeColor);
				break;
			case STROKE_RECT:
				m_curTool = new RectuCtl(m_penSize,m_strokeColor);
				break;
			case STROKE_CIRCLE:
				m_curTool = new Circlectl(m_penSize,m_strokeColor);
				break;
			case STROKE_OVAL:
				m_curTool = new OvaluCtl(m_penSize,m_strokeColor);
				break;
			case STROKE_LINE:
				m_curTool = new LineCtl(m_penSize,m_strokeColor);
				break;
			case STROKE_SPRAYGUN:
				m_curTool = new Spraygun(m_penSize,m_strokeColor);
				break;
		}
		//用于记录操作动作名称
		m_strokeType = type;
	}
	/////////////////////////////////////////////////////////////////////
	//构造方法三个 必须的
	public SketchpadView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialize();
	}
	public SketchpadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bgBitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.pic1))).getBitmap();
		// TODO Auto-generated constructor stub
		initialize();
	}
	public SketchpadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initialize();
	}
	public boolean canRedo() {
		// TODO Auto-generated method stub
		if (null != m_undoStack){
			return m_undoStack.canUndo();
		}
		return false;
	}
	public boolean canUndo() {
		// TODO Auto-generated method stub
		if (null != m_undoStack){
			return m_undoStack.canRedo();
		}
		return false;
	}
	public void onDeleteFromRedoStack() {
		// TODO Auto-generated method stub
	}
	public void onDeleteFromUndoStack() {
		// TODO Auto-generated method stub
	}
	public void redo() {
		// TODO Auto-generated method stub
		if (null != m_undoStack){
			m_undoStack.redo();
		}
	}
	public void undo() {
		// TODO Auto-generated method stub
		if (null != m_undoStack){
			m_undoStack.undo();
			Log.i("sada022", "undo00");
		}
	}
	///////////////////////////////////////////
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//canvas.drawBitmap(m_bkBitmap, 0, 0,null);
		//	canvas.drawColor(m_bkColor);
		// Draw background bitmap.
		if (null != m_bkBitmap){
			RectF dst = new RectF(getLeft(), getTop(), getRight(), getBottom());
			Rect  rst = new Rect(0, 0, m_bkBitmap.getWidth(), m_bkBitmap.getHeight());
			canvas.drawBitmap(m_bkBitmap, rst, dst, m_bitmapPaint);
		}
		if (null != m_foreBitmap){
			canvas.drawBitmap(m_foreBitmap, 0, 0, m_bitmapPaint);
		}
		if (null != m_curTool){
			if (STROKE_ERASER != m_strokeType){
				if (!m_isTouchUp){   //调用绘图功能
					m_curTool.draw(canvas);
				}
			}
		}
	}
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if (!m_isSetForeBmp){
			setCanvasSize(w, h);
		}
		m_canvasWidth = w;
		m_canvasHeight = h;
		m_isSetForeBmp = false;
	}
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float  yx=event.getX();
		float  yy=event.getY();
		if (m_isEnableDraw)   //判断是否可绘图
		{
			m_isTouchUp = false;
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if(flag==1){
						seed_fill((int)yx,(int)yy,m_foreBitmap.getPixel((int)yx,(int)yy),m_strokeColor);
						invalidate();
						flag=0;
					}
					//根据m_strokeType进行重新生成对象且记录下操作对象
					setStrokeType(m_strokeType);
					lastDrawPly();		//将未闭合的多边形闭合
					m_curTool.touchDown(event.getX(), event.getY());
					if(STROKE_SPRAYGUN == m_strokeType)  //若当前操作为喷枪则使用线程
					{
						myLoop = true;
						spraygunRun();
					}
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					m_curTool.touchMove(event.getX(), event.getY());
					//若果当前操作为橡皮擦或喷枪则调用绘图操作
					if (STROKE_ERASER == m_strokeType){
						m_curTool.draw(m_canvas);
					}
					if(STROKE_SPRAYGUN == m_strokeType){
						m_curTool.draw(m_canvas);
					}
					invalidate();
					m_isDirty = true;
					m_canClear = true;
					break;
				case MotionEvent.ACTION_UP:
					m_isTouchUp = true;
					if (m_curTool.hasDraw()){
						// Add to undo stack.
						m_undoStack.push(m_curTool);
					}
					m_curTool.touchUp(event.getX(), event.getY());
					// Draw strokes on bitmap which is hold by m_canvas.
					m_curTool.draw(m_canvas);
					invalidate();
					m_isDirty = true;
					m_canClear = true;
					myLoop = false;
					break;
			}
		}
		return true;
	}
	////////////////////undo栈/////////////////////
	public class SketchPadUndoStack{
		private int m_stackSize = 0;   //栈大小
		private SketchpadView m_sketchPad = null;  //视图对象
		private ArrayList<ISketchpadDraw> m_undoStack = new ArrayList<ISketchpadDraw>();
		private ArrayList<ISketchpadDraw> m_redoStack = new ArrayList<ISketchpadDraw>();
		private ArrayList<ISketchpadDraw> m_removedStack = new ArrayList<ISketchpadDraw>();

		public SketchPadUndoStack(SketchpadView sketchPad, int stackSize){
			m_sketchPad = sketchPad;
			m_stackSize = stackSize;
		}
		public void push(ISketchpadDraw sketchPadTool){
			if (null != sketchPadTool){
				if (m_undoStack.size() == m_stackSize && m_stackSize > 0){
					ISketchpadDraw removedTool = m_undoStack.get(0);
					m_removedStack.add(removedTool);
					m_undoStack.remove(0);
				}
				m_undoStack.add(sketchPadTool);
			}
		}
		//清空栈
		public void clearAll(){
			m_redoStack.clear();
			m_undoStack.clear();
			m_removedStack.clear();
		}
		public void undo(){
			if (canUndo() && null != m_sketchPad){
				ISketchpadDraw removedTool = m_undoStack.get(m_undoStack.size() - 1);
				m_redoStack.add(removedTool);
				m_undoStack.remove(m_undoStack.size() - 1);

				if (null != m_tempForeBitmap){
					// Set the temporary fore bitmap to canvas.
					m_sketchPad.setTempForeBitmap(m_sketchPad.m_tempForeBitmap);
				}
				else{
					// Create a new bitmap and set to canvas.
					m_sketchPad.createStrokeBitmap(m_sketchPad.m_canvasWidth, m_sketchPad.m_canvasHeight);
				}
				Canvas canvas = m_sketchPad.m_canvas;
				// First draw the removed tools from undo stack.
				for (ISketchpadDraw sketchPadTool : m_removedStack){
					sketchPadTool.draw(canvas);
				}
				for (ISketchpadDraw sketchPadTool : m_undoStack){
					sketchPadTool.draw(canvas);
				}
				m_sketchPad.invalidate();
			}
		}

		public void redo(){
			if (canRedo() && null != m_sketchPad){
				ISketchpadDraw removedTool = m_redoStack.get(m_redoStack.size() - 1);
				m_undoStack.add(removedTool);
				m_redoStack.remove(m_redoStack.size() - 1);

				if (null != m_tempForeBitmap){
					// Set the temporary fore bitmap to canvas.
					m_sketchPad.setTempForeBitmap(m_sketchPad.m_tempForeBitmap);
				}
				else{
					// Create a new bitmap and set to canvas.
					m_sketchPad.createStrokeBitmap(m_sketchPad.m_canvasWidth, m_sketchPad.m_canvasHeight);
				}
				Canvas canvas = m_sketchPad.m_canvas;

				// First draw the removed tools from undo stack.
				for (ISketchpadDraw sketchPadTool : m_removedStack){
					sketchPadTool.draw(canvas);
				}
				for (ISketchpadDraw sketchPadTool : m_undoStack){
					sketchPadTool.draw(canvas);
				}
				m_sketchPad.invalidate();
			}
		}
		public boolean canUndo(){//
			return (m_undoStack.size() > 0);
		}
		public boolean canRedo(){//判断栈的大小
			return (m_redoStack.size() > 0);
		}
	}
	public void lastDrawPly(){
		//当多边形没有封闭，点击绘制其他图形、多边形或者保存文件按钮时，将多边形封闭
		if(((PlygonCtl.getStartPoint().getX()!= PlygonCtl.getmPoint().getX())
				||(PlygonCtl.getStartPoint().getY()!=PlygonCtl.getmPoint().getY()))
				&& ((STROKE_PLYGON != m_strokeType)||(SketchpadMainActivity.isPlygon_Click()
				&&PlygonCtl.getCountLine()==0))
//						||SketchpadMainActivity.isSave_Click())
				&& STROKE_ERASER != m_strokeType)
		{
			PlygonCtl lastLine = new PlygonCtl(m_penSize,m_strokeColor);
			lastLine.lineDraw(m_canvas);
			SketchpadMainActivity.setPlygon_Click(false);
//			SketchpadMainActivity.setSave_Click(false);
			PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
		}
	}
	//喷枪的线程操作
	public void spraygunRun(){// 匿名内部内，鼠标按下不放时的操作，启动一个线程监控
		new Thread(new Runnable() {
			public void run() {
				while (myLoop) {
					m_curTool.draw(m_canvas);
					try {
						Thread.sleep(50);
						if (antiontemp == MotionEvent.ACTION_UP) {
							myLoop = false;
						}
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					postInvalidate(); //在线程中更新界面
				}
			}
		}).start();
	}
	//队列实现种子递归，用于油漆桶工具   7/28
	public void seed_fill (  int x, int y, int t_color, int r_color){
		int MAX_ROW = 400;
		int MAX_COL = 650;
		int row_size = 400;
		int col_size = 650;
		if (x < 0 || x >= col_size || y < 0 || y >= row_size || m_foreBitmap.getPixel(x,y) == r_color) {
			return;
		}
		int queue[][]=new int[MAX_ROW*MAX_COL+1][2];
		int head = 0, end = 0;
		int tx, ty;
		/* Add node to the end of queue. */
		queue[end][0] = x;
		queue[end][1] = y;
		end++;
		while (head < end) {
			tx = queue[head][0];
			ty = queue[head][1];
			if (m_foreBitmap.getPixel(tx,ty) == t_color) {
				m_foreBitmap.setPixel(tx,ty,r_color);
			}
			/* Remove the first element from queue. */
			head++;

			/* West */
			if (tx-1 >= 0 && m_foreBitmap.getPixel(tx-1,ty) == t_color) {
				m_foreBitmap.setPixel(tx-1,ty,r_color);
				queue[end][0] = tx-1;
				queue[end][1] = ty;
				end++;
			}
			else if(tx-1 >= 0&&m_foreBitmap.getPixel(tx-1,ty)!=t_color){
				m_foreBitmap.setPixel(tx-1,ty,r_color);


			}


			/* East */
			if (tx+1 < col_size && m_foreBitmap.getPixel(tx+1,ty) == t_color) {
				m_foreBitmap.setPixel(tx+1,ty,r_color);
				queue[end][0] = tx+1;
				queue[end][1] = ty;
				end++;
			}
			else if(tx+1 <col_size&&m_foreBitmap.getPixel(tx+1,ty)!=t_color){
				m_foreBitmap.setPixel(tx+1,ty,r_color);


			}
			/* North */
			if (ty-1 >= 0 && m_foreBitmap.getPixel(tx,ty-1) == t_color) {
				m_foreBitmap.setPixel(tx,ty-1,r_color);
				queue[end][0] = tx;
				queue[end][1] = ty-1;
				end++;
			}
			else if(ty-1 >= 0&&m_foreBitmap.getPixel(tx,ty-1)!=t_color){
				m_foreBitmap.setPixel(tx,ty-1,r_color);


			}
			/* South */
			if (ty+1 < row_size &&  m_foreBitmap.getPixel(tx,ty+1) == t_color) {
				m_foreBitmap.setPixel(tx,ty+1,r_color);
				queue[end][0] = tx;
				queue[end][1] = ty+1;
				end++;
			}
			else if(ty+1<row_size&&m_foreBitmap.getPixel(tx,ty+1)!=t_color){
				m_foreBitmap.setPixel(tx,ty+1,r_color);


			}
		}
		return;
	}
}
