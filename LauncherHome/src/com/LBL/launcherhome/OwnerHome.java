package com.LBL.launcherhome;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


//import org.omg.CORBA.portable.CustomValue;

public class OwnerHome extends Activity  implements OnScrollListener {

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;
	
//	private static final int TEXT_DIALOG = 0;

	private Uri mImageCaptureUri;
	private ImageView btnProfile;
	private TextView profileMsg;
	private EditText editText; 
	private String data;
	private Gallery gallery;
	private Button btnDown;
	
	private ScrollAdapter mAdapter;
	private ListView mListView;
	private LayoutInflater mInflater;
	private ArrayList<String> mRowList;
	private boolean mLockListView;
	public int msgCnt = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_owner_home);
		

        mRowList = new ArrayList<String>();
        mLockListView = true;
        
        mAdapter = new ScrollAdapter(this, R.layout.row, mRowList);
        mListView = (ListView) findViewById(R.id.listView);
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView.addFooterView(mInflater.inflate(R.layout.footer, null));
        
        mListView.setOnScrollListener((OnScrollListener) this);
        mListView.setAdapter(mAdapter);
        
        btnProfile = (ImageView) findViewById(R.id.profile);
		profileMsg = (TextView) findViewById(R.id.mainMsg);
		btnDown = (Button)findViewById(R.id.btnDown);
		
	    Gallery gallery = (Gallery)findViewById(R.id.gallery1);
	    gallery.setAdapter(new ImageAdapter(this));
	    
 	
	    addItems(10);	
	    
	    
	    profileMsg.setOnClickListener(new View.OnClickListener() {
	    	@SuppressWarnings("deprecation")
			public void onClick(View v){
	    		Context mContext = OwnerHome.this;
	    		AlertDialog.Builder builder;
	    		AlertDialog dialog;
	    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
	    		View layout = inflater.inflate(R.layout.textdialog, (ViewGroup)findViewById(R.id.setTextDialog));
	    		
	    		editText = (EditText) layout.findViewById(R.id.editProfile);
	    		
	    		builder = new AlertDialog.Builder(mContext);
	    		builder.setView(layout);
	    		dialog = builder.create();
	    		dialog.setTitle("프로필 변경");
	    		
	    		dialog.setButton("확인", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						// TODO Auto-generated method stub
						data = editText.getText().toString();
						profileMsg.setText(data);
						dialog.dismiss();						
					}
				});	 
	    		dialog.setButton2("취소", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();						
					}
				});	 
	    		dialog.show();	    		
	    	}
	    });
	    


		btnProfile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doTakePhotoAction();
					}
				};

				DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doTakeAlbumAction();
					}
				};

				DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};

				new AlertDialog.Builder(OwnerHome.this)
						.setTitle("업로드할 이미지 선택")
						.setPositiveButton("사진촬영", cameraListener)
						.setNeutralButton("앨범선택", albumListener)
						.setNegativeButton("취소", cancelListener).show();

			}
		});

	}

	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		mImageCaptureUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), url));

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);

		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	private void doTakeAlbumAction() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case CROP_FROM_CAMERA: {

			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			if (thumbnail != null) {
				ImageView profile = (ImageView) findViewById(R.id.profile);
				profile.setImageBitmap(thumbnail);
			}
			File f = new File(mImageCaptureUri.getPath());
			if (f.exists()) {
				f.delete();
			}

			break;
		}

		case PICK_FROM_ALBUM: {
			mImageCaptureUri = data.getData();
		}

		case PICK_FROM_CAMERA: {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(mImageCaptureUri, "image/*");

			intent.putExtra("outputX", 90);
			intent.putExtra("outputY", 90);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			startActivityForResult(intent, CROP_FROM_CAMERA);

			break;
		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
	    int visibleItemCount, int totalItemCount)
	    {
	      // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
	      // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
	      int count = totalItemCount - visibleItemCount;
	      
	      if(msgCnt !=0){
		      if(firstVisibleItem >= count && totalItemCount != 0
		        && mLockListView == false)
		      {
		        addItems(5);
		      }  
	      }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	private void addItems(final int size)
	  {
	    // 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
	    mLockListView = true;
	    
	    Runnable run = new Runnable()
	    {
	      @Override
	      public void run()
	      {
	        for(int i = 0 ; i < size ; i++)
	        {
	          mRowList.add("안녕 " + msgCnt );
	          msgCnt--;
	        }
	        
	        // 모든 데이터를 로드하여 적용하였다면 어댑터에 알리고
	        // 리스트뷰의 락을 해제합니다.
	        mAdapter.notifyDataSetChanged();
	        if(msgCnt !=0)
	        mLockListView = false;
	        else
	        mLockListView = true;
	      }
	    };
	    
	    // 속도의 딜레이를 구현하기 위한 꼼수
	    Handler handler = new Handler();
	    handler.postDelayed(run, 5000);
	  }
}