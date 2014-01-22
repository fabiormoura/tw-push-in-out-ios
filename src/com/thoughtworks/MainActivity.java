package com.thoughtworks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {
	protected String _path;
	protected static final String PHOTO_TAKEN = "photo_taken";
	private final String DATA_PATH = Environment.getExternalStorageDirectory()
			.toString() + "/SimpleAndroidOCR/";
	private final String lang = "eng";
	private EditText _field;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		createDir();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// we cannot specify an folder that was not created before when saving
		// the image
		_path = Environment.getExternalStorageDirectory()
				+ "/make_machine_example.jpg";
		// _image = ( ImageView ) findViewById( R.id.image );
		final Button button = (Button) findViewById(R.id.btnOpenCamera);
		_field = (EditText) findViewById(R.id.txtOCRResult);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				File file = new File(_path);
				Uri outputFileUri = Uri.fromFile(file);

				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

				startActivityForResult(intent, 0);
			}

		});
	}

	private void createDir() {
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return;
				} else {

				}
			}

		}

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				// while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				// gin.close();
				out.close();

			} catch (IOException e) {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0:
			break;

		case -1:
			onPhotoTaken();
			break;
		}
	}

	protected void onPhotoTaken() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
		// _image.setImageBitmap(bitmap);

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(_path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int exifOrientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

		int rotate = 0;

		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		}

		if (rotate != 0) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			// Setting pre rotate
			Matrix mtx = new Matrix();
			mtx.preRotate(rotate);

			// Rotating Bitmap & convert to ARGB_8888, required by tess
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
		}
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		TessBaseAPI baseApi = new TessBaseAPI();
		// DATA_PATH = Path to the storage
		// lang = for which the language data exists, usually "eng"
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		// Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata",
		// "eng");
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();

		if (recognizedText.length() != 0) {
			_field.setText(_field.getText().toString().length() == 0 ? recognizedText
					: _field.getText() + " " + recognizedText);
			_field.setSelection(_field.getText().toString().length());
		}
	}
}
