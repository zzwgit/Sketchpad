package com.zzw.sketchpad.Activity;


import com.zzw.sketchpad.Contral.FileOper;
import com.zzw.sketchpad.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class OpenGridViewActivity extends Activity {
	private  GridView my_gridview ;
	private  GridImageAdapter myImageViewAdapter ;
	private FileOper fileOper = new FileOper();
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_dialog);
		/*从xml中获取UI 资源对象 */
		my_gridview = (GridView) findViewById(R.id.grid);
		/* 新建自定义的 ImageAdapter */
		myImageViewAdapter = new GridImageAdapter(OpenGridViewActivity.this);
		/* GridView 对象设置 ImageAdapter */
		my_gridview.setAdapter(myImageViewAdapter);
		
		/* 打开对话框添加图 Items 点击事件监听*/
		my_gridview.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bmp = myImageViewAdapter.getcheckedImageIDPostion(arg2);
				if (null != bmp) {
					Intent intent = new Intent();
					intent = intent.setClass(OpenGridViewActivity.this,
							SketchpadMainActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("bmp", bmp);
					intent.putExtras(bundle);
					OpenGridViewActivity.this.setResult(RESULT_OK, intent); // RESULT_OK是返回状态码
					OpenGridViewActivity.this.finish(); // 会触发onDestroy();
				}
			}
		});

	}
	

	public class GridImageAdapter extends BaseAdapter{
		/*myContext 为上下文 */
		private Context myContext ;
		/*GridView 用来加载图片ImageView*/
		private ImageView the_imageView ;
		// 这是图片资源 路径 的数目
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
			/*设置图像内容 */
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
