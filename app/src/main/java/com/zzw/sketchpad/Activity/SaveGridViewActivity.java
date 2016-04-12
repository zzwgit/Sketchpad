package com.zzw.sketchpad.Activity;

import com.zzw.sketchpad.Contral.FileOper;
import com.zzw.sketchpad.R;
import com.zzw.sketchpad.view.SketchpadView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class SaveGridViewActivity extends Activity {
	private  GridView my_gridview ;
	private  GridImageAdapter myImageViewAdapter ;
	private  FileOper fileOper = new FileOper();
	@SuppressWarnings("unused")
	private SketchpadView canvasView = null;
	private  EditText fileName;
	private  Button saveButton;
	private  String[] fileNames;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_dialog);
		/*从xml中获的UI资源对象 */
		my_gridview = (GridView) findViewById(R.id.grid1);
		canvasView = (SketchpadView) findViewById(R.id.SketchadView);
		fileName = (EditText) findViewById(R.id.fileName);
		saveButton = (Button) findViewById(R.id.save);
		/*读取指定路径下所有资*/
		fileNames = fileOper.getStrokeFileNames();
		/* 新建自定义的 ImageAdapter */
		myImageViewAdapter = new GridImageAdapter(SaveGridViewActivity.this);
		/*GridView 对象设置ImageAdapter */
		my_gridview.setAdapter(myImageViewAdapter);
		
		/*保存对话框的保存按钮 添加点击事件监听*/
		saveButton.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				
				String filename = fileName.getText().toString().trim();
				/*文件名不能为�*/
				if (filename.equals("") || filename.equals(null)) {
					Toast.makeText(SaveGridViewActivity.this, "文件名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}else{
					filename = filename + ".jpg";
				}
				/*文件名不能重*/
				for(int i=0; i<fileNames.length; i++){
					if(fileNames[i].equals(filename)){
						Toast.makeText(SaveGridViewActivity.this, "文件名不能重复", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				/*返回文件路径，关闭当前对话框*/
				String filePath = fileOper.getStrokeFilePath() + filename;
				Intent intent = new Intent();
				intent = intent.setClass(SaveGridViewActivity.this,
						SketchpadMainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("filePath", filePath);
				intent.putExtras(bundle);
				SaveGridViewActivity.this.setResult(RESULT_OK, intent); // RESULT_OK是返回状态码
				SaveGridViewActivity.this.finish(); // 会触发onDestroy();
			}
		});
	}

	
	public class GridImageAdapter extends BaseAdapter{
		/*myContext 为上下文 */
		private Context myContext ;
		/*GridView 用来加载图片�?ImageView*/
		private ImageView the_imageView ;		
		private Bitmap[] mImageIds = fileOper.getStrokeFilePaths();
		private Bitmap[] mImageResources = null ;
		/* 构造方法 */
		public GridImageAdapter(Context myContext) {
			this.myContext = myContext;
		}		
		public int getCount() {
			
			for(int i=0; i<mImageIds.length; i++){
				if(mImageIds[i] == null){
					mImageResources = new Bitmap[i];
					break;
				}
			}
			for(int i=0; i<mImageResources.length; i++){
				mImageResources[i] = mImageIds[i];
			}
			return mImageResources.length;
		}

		
		public Object getItem(int position) {
			return position;
		}

		
		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			/* 创建ImageView*/
			the_imageView = new  ImageView( myContext );
			/*设置图像内容*/
			the_imageView .setImageBitmap(mImageResources[position]);
			/* ImageView 与边界*/
			the_imageView .setAdjustViewBounds(true );
			/* 设置背景图片的风格*/
			the_imageView .setBackgroundResource(android.R.drawable. picture_frame );
			/* 返回带有多个图片 ID 的ImageView*/
			return the_imageView ;
		}

		/* 自定义获取对应位置的图片 */
		public Bitmap getcheckedImageIDPostion( int theindex) {
			return mImageResources [theindex];
		}
	}
}
