package com.zzw.sketchpad.Activity;



import com.zzw.sketchpad.Contral.BitmapUtil;
import com.zzw.sketchpad.Contral.PlygonCtl;
import com.zzw.sketchpad.R;
import com.zzw.sketchpad.view.SketchpadView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SketchpadMainActivity extends Activity implements  View.OnClickListener{
    /** Called when the activity is first created. */
	private Button m_pen=null;
	private Button m_undo=null;
	private Button m_redo=null;
	private Button m_eraser=null;
	private Button m_plygon=null;
	private Button m_rect=null;
	private Button m_cycle=null;
	private Button m_oval=null;
	private Button m_line=null;
	private Button m_spraygun=null;
	private Button m_new=null;
	private Button m_open=null;
	private Button m_save=null;
	private Button m_paintpot=null;
	private Button m_color=null;
	private SeekBar seekBar;
	
	private SketchpadView m_view;

	private static boolean plygon_Click = false; 
	private static boolean save_Click = false; 	

	private static final int REQUEST_TYPE_A = 1;
	private static final int REQUEST_TYPE_B = 2;
	  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketchpad_main);
        
        m_view=(SketchpadView)this.findViewById(R.id.SketchadView);
        
        m_pen=(Button)this.findViewById(R.id.buttonpen_ID);
        m_undo=(Button)this.findViewById(R.id.buttonundo_ID);
        m_redo=(Button)this.findViewById(R.id.buttonredo_ID);
        m_eraser=(Button)this.findViewById(R.id.buttoneraser_ID);
        m_plygon=(Button)this.findViewById(R.id.buttonplygon_ID);
        m_rect=(Button)this.findViewById(R.id.buttonrect_ID);
        m_cycle=(Button)this.findViewById(R.id.buttoncycle_ID);
        m_oval=(Button)this.findViewById(R.id.buttonoval_ID);
        m_line=(Button)this.findViewById(R.id.buttonline_ID);
        m_spraygun=(Button)this.findViewById(R.id.buttonspraygun_ID);
        m_new=(Button)this.findViewById(R.id.buttonnew_ID);
        m_open=(Button)this.findViewById(R.id.buttonopen_ID);
        m_save=(Button)this.findViewById(R.id.buttonsave_ID);
        m_color=(Button)this.findViewById(R.id.buttoncolor_ID);
        m_paintpot=(Button)this.findViewById(R.id.buttonpaintpot_ID);
        m_pen.setOnClickListener(this);
        m_undo.setOnClickListener(this);
        m_redo.setOnClickListener(this);
        m_eraser.setOnClickListener(this);
        m_plygon.setOnClickListener(this);
        m_rect.setOnClickListener(this);
        m_cycle.setOnClickListener(this);
        m_oval.setOnClickListener(this);
        m_line.setOnClickListener(this);
        m_spraygun.setOnClickListener(this);
        m_new.setOnClickListener(this);
        m_open.setOnClickListener(this);
        m_save.setOnClickListener(this);
        m_color.setOnClickListener(this);
        m_paintpot.setOnClickListener(this);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){		
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				SketchpadView.setStrokeSize((seekBar.getProgress() / 40) * 5 + 5, SketchpadView.STROKE_PEN);
                SketchpadView.setStrokeSize((seekBar.getProgress()/40)*5+5, SketchpadView.STROKE_ERASER);
				Log.v("progress1",(seekBar.getProgress())+"");
				Log.v("progress2",((seekBar.getProgress()/20)+3)+"");				
			}			
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}		
			public void onStopTrackingTouch(SeekBar seekBar) {
			}    	
        });
    }

	/*接收打开对话框和保存对话框Activity返回的值，并打开和保存图片*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bmp = null;
					Bundle bundle = data.getExtras();
					bmp = bundle.getParcelable("bmp");				
					//m_view.setForeBitmap(bmp);
					m_view.setBkBitmap(bmp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 2:
			if (resultCode == RESULT_OK) {
				try {
					String filename = null;
					Bundle bundle = data.getExtras();
					filename = bundle.getString("filePath");
					
			        Bitmap bmp = m_view.getCanvasSnapshot();
			        if (null != bmp)
			        {
			            BitmapUtil.saveBitmapToSDCard(bmp, filename);
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.buttonpen_ID:
			OnPenClick(v);
			break;
		case R.id.buttonundo_ID:
			OnUndoClick(v);
			break;
		case R.id.buttoneraser_ID:
			OnEraserClick(v);
			break;
		case R.id.buttonredo_ID:
			onRedoClick(v);
			break;
		case R.id.buttonplygon_ID:
			OnPlygonClick(v);
			break;
		case R.id.buttonrect_ID:
			OnRectClick(v);
			break;
		case R.id.buttoncycle_ID:
			OnCycleClick(v);
			break;
		case R.id.buttonoval_ID:
			OnOvalClick(v);
			break;
		case R.id.buttonline_ID:
			OnLineClick(v);
			break;
		case R.id.buttonspraygun_ID:
			OnSpraygunClick(v);
			break;
		case R.id.buttonnew_ID:
			OnNewClick(v);
			break;
		case R.id.buttonopen_ID:
			OnOpenClick(v);
			break;
		case R.id.buttonsave_ID:
			OnSaveClick(v);
			break;
		case R.id.buttoncolor_ID:
			OnColorClick(v);
			break;
		case R.id.buttonpaintpot_ID:
			SketchpadView.flag=1;
			break;
		}
	}
	
	private void OnNewClick(View v) {
		this.onCreate(null);
		PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
	}
	private void OnSpraygunClick(View v) {
		m_view.setStrokeType(m_view.STROKE_SPRAYGUN);
	}
	private void OnSaveClick(View v) {
		startActivityForResult(new Intent(this, SaveGridViewActivity.class),REQUEST_TYPE_B);
//		SketchpadMainActivity.setSave_Click(true);
	}	
	private void OnOpenClick(View v) {
		startActivityForResult(new Intent(this, OpenGridViewActivity.class),REQUEST_TYPE_A);
		PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
	}	
	private void OnLineClick(View v) {
		m_view.setStrokeType(m_view.STROKE_LINE);	
	}
	private void OnOvalClick(View v) {
		m_view.setStrokeType(m_view.STROKE_OVAL);		
	}
	private void OnCycleClick(View v) {
		m_view.setStrokeType(m_view.STROKE_CIRCLE);		
	}
	private void OnRectClick(View v) {
		m_view.setStrokeType(m_view.STROKE_RECT);
	}
	//设置橡皮擦的类型
	private void OnPlygonClick(View v) {
		m_view.setStrokeType(m_view.STROKE_PLYGON);
		SketchpadMainActivity.setPlygon_Click(true);
	}
	private void OnEraserClick(View v) {
		m_view.setStrokeType(m_view.STROKE_ERASER);
	}
	private void onRedoClick(View v) {
		m_view.redo();//响应redo事件
	}
	private void OnUndoClick(View v) {
		m_view.undo(); // 响应undo事件
	}
	private void OnPenClick(View v) {
		m_view.setStrokeType(m_view.STROKE_PEN);//设置画笔的类型
	}
	private void OnColorClick(View v){
		Intent intent=new Intent(SketchpadMainActivity.this,GridViewColorActivity.class);
		SketchpadMainActivity.this.startActivity(intent);
	}

	//判断是否点击了绘制多边形按钮
	public static boolean isPlygon_Click() {
		return plygon_Click;
	}

	public static void setPlygon_Click(boolean plygon_Click) {
		SketchpadMainActivity.plygon_Click = plygon_Click;
		//设置多边形边数为0
		PlygonCtl.setCountLine(0);
	}

}