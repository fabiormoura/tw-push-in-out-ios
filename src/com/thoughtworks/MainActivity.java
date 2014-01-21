package com.thoughtworks;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
		
	protected static final String PHOTO_TAKEN = "photo_taken";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_path = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";
		
		final Button button = (Button) findViewById(R.id.btnOpenCamera);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				File file = new File( _path );
			    Uri outputFileUri = Uri.fromFile( file );
			    	
			    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
			    intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
			    	
			    startActivityForResult( intent, 0 );
//				Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
//				intent.putExtra("routeName", getRouteName());
//				startActivity(intent);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		    
	    switch( resultCode )
	    {
	    	case 0:	    		
	    		break;
	    			
	    	case -1:
	    		onPhotoTaken();
	    		break;
	    }
	}
	
	protected void onPhotoTaken()
	{
	    _taken = true;
	    	
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 4;
	    	
	    Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
	    _image.setImageBitmap(bitmap);
	    	
	    _field.setVisibility( View.GONE );
	}
	
	@Override
	protected void onSaveInstanceState( Bundle outState ) {
	    outState.putBoolean( PHOTO_TAKEN, _taken );
	}
	@Override 
	protected void onRestoreInstanceState( Bundle savedInstanceState)
	{	 
	    if( savedInstanceState.getBoolean( PHOTO_TAKEN ) ) {
	    	onPhotoTaken();
	    }
	}

}
