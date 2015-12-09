package team14.cs442.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageActivity extends AppCompatActivity {

    private String convertedText;
    private int pos;
    private Bitmap bitmap;
    private String capturePath="/mnt/sdcard/";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ProgressDialog mLoading;
    private PhotoViewAttacher mAttacher;

    private static final String TESSBASE_PATH="/mnt/sdcard/tesseract/";
    private static final String DEFAULT_LANGUAGE="eng";
    private static final String LANG_FILE_PATH=TESSBASE_PATH+"tessdata/";
    private static final String DEFAULT_LANG_FILENAME=DEFAULT_LANGUAGE+".traineddata";

    public final static String MESSAGE="team14.cs442.com.ImageActivity.text";
    public final static String IMAGE="team14.cs442.com.ImageActivity.image";
    private final String VERSION_KEY="team14.cs442.com.package_version";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        /*
        try{
            PackageInfo info=getPackageManager().getPackageInfo(getPackageName(),0);
            int currentVersion=info.versionCode;
            SharedPreferences sp=getSharedPreferences("ocr", Context.MODE_PRIVATE);
            if(!sp.contains(VERSION_KEY)){
                //app first time install
                //Todo
                sp.edit().putInt(VERSION_KEY,currentVersion).commit();
            }else{
                int lastVersion=sp.getInt(VERSION_KEY,0);
                if(currentVersion>lastVersion){
                    //app updated
                    //Todo
                    sp.edit().putInt(VERSION_KEY,currentVersion).commit();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this,"can not find package name",Toast.LENGTH_LONG).show();
        }
        */

        File langFile=new File(LANG_FILE_PATH+DEFAULT_LANG_FILENAME);
        if(!langFile.exists()){
            installLang();
        }

        if(TextContent.ITEMS.isEmpty()){
            checkEmpty();
        }


    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_camera:
                Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                capturePath=capturePath+System.currentTimeMillis()+".jpg";
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
                intent1.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                startActivityForResult(intent1, REQUEST_CAMERA);
                return true;
            case R.id.action_gallery:
                Intent intent2=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(Intent.createChooser(intent2,"Select File"),SELECT_FILE);
                return true;
            case R.id.action_convert:
                //bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.emp);
                if(bitmap!=null){
                    useOCR();
                }else{
                    Toast.makeText(this,"please upload a photo",Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_history:
                Intent intent3=new Intent(this, TextListActivity.class);
                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    */

    public void useOCR() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ocr();
                Intent intent=new Intent(ImageActivity.this, TextToEditActivity.class);
                intent.putExtra(MESSAGE, convertedText);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapByte=baos.toByteArray();
                intent.putExtra(IMAGE,bitmapByte);

                if(mLoading.isShowing()){
                    mLoading.dismiss();
                }

                startActivity(intent);
            }
        }).start();
    }

    public void ocr(){

            Bitmap bm= bitmap;
            //bm=bm.copy(Bitmap.Config.ARGB_8888,true);
            TessBaseAPI baseAPI=new TessBaseAPI();
            baseAPI.setDebug(true);
            baseAPI.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
            baseAPI.setImage(bm);
            convertedText=baseAPI.getUTF8Text();
            baseAPI.clear();
            baseAPI.end();
        //Toast.makeText(this,convertedText,Toast.LENGTH_LONG).show();
        //if(DEFAULT_LANGUAGE.equalsIgnoreCase("eng")){
            //convertedText=convertedText.replaceAll("[^a-zA-Z0-9]+"," ");
        //}

    }

    public void installLang(){

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            AssetManager assetManager=getAssets();
            String[] files=null;

            try{
                files=assetManager.list("");
            }catch (IOException e){
                e.printStackTrace();
            }

            for(String filename: files){
                InputStream in=null;
                OutputStream out=null;

                try{
                    in=assetManager.open(filename);

                    File file=new File(LANG_FILE_PATH);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    out=new FileOutputStream(LANG_FILE_PATH+filename);
                    byte[] buffer=new byte[1024];
                    int read;
                    while((read=in.read(buffer))!=-1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                    in.close();
                    out.close();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }


        }else{
            Toast.makeText(this,"No SD card found",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=1;
        bitmap = BitmapFactory.decodeFile(capturePath, options);
        if(bitmap.getHeight()>1600&&bitmap.getWidth()>1600||bitmap.getHeight()>3000||bitmap.getWidth()>3000){
            options.inSampleSize=4;
            bitmap = BitmapFactory.decodeFile(capturePath, options);

        }

        rotateImage(capturePath);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=1;
        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        if(bitmap.getHeight()>1600&&bitmap.getWidth()>1600||bitmap.getHeight()>3000||bitmap.getWidth()>3000){
            options.inSampleSize=4;
            bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        }

        rotateImage(selectedImagePath);

    }

    private void rotateImage(String path){
        try{
            ExifInterface exif=new ExifInterface(path);
            int exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

            int rotate=0;
            switch (exifOrientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate=90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate=180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate=270;
                    break;
            }

            if(rotate!=0){
                int w=bitmap.getWidth();
                int h=bitmap.getHeight();

                Matrix mtx=new Matrix();
                mtx.preRotate(rotate);

                bitmap=Bitmap.createBitmap(bitmap,0,0,w,h,mtx,false);
            }

            findViewById(R.id.layout1).setBackgroundResource(R.drawable.ocrscan);
            ImageView imageView=(ImageView)findViewById(R.id.image_show);
            imageView.setImageBitmap(bitmap);
            mAttacher=new PhotoViewAttacher(imageView);



        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void checkEmpty(){
        String[] projection={OCRTable.COLUMN_NAME,OCRTable.COLUMN_REALID,OCRTable.COLUMN_CONTENT,OCRTable.COLUMN_TITLE};
        Uri uri=OCRContentProvider.CONTENT_URI;
        Cursor c=getContentResolver().query(uri,projection,null,null,null);

        if(c.moveToFirst()){
            if(c.getCount()!=0){
                do{
                    String fileName=c.getString(c.getColumnIndex(OCRTable.COLUMN_NAME));
                    String cont=c.getString(c.getColumnIndex(OCRTable.COLUMN_CONTENT));
                    int realID=c.getInt(c.getColumnIndex(OCRTable.COLUMN_REALID));
                    String title=c.getString(c.getColumnIndex(OCRTable.COLUMN_TITLE));
                    TextContent.ITEMS.add(new TextContent.TextItem("" + realID, cont, fileName,title));
                    TextContent.ITEM_MAP.put("" + realID, new TextContent.TextItem("" + realID, cont, fileName,title));
                }while(c.moveToNext());
            }
        }

    }

    public void uploadButton(View v){
        new AlertDialog.Builder(this).setTitle("select image path:").setSingleChoiceItems(new String[]{
                "Camera", "Gallery"}, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pos=which;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(pos==0){
                    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    capturePath=capturePath+System.currentTimeMillis()+".jpg";
                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
                    intent1.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                    startActivityForResult(intent1, REQUEST_CAMERA);
                }
                else if(pos==1){
                    Intent intent2=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent2.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent2, "Select File"), SELECT_FILE);
                }
            }
        }).setNegativeButton("Cancel", null).show();
    }

    public void convertButton(View v){

        mLoading=ProgressDialog.show(this,"","converting...");
        if(bitmap!=null){
            checkSDCard();
        }else{
            new AlertDialog.Builder(this).setMessage("Please upload an image first").setPositiveButton("OK",null).show();
            if(mLoading.isShowing()){
                mLoading.dismiss();
            }
        }
    }

    public void historyButton(View v){
        Intent intent3=new Intent(this, TextListActivity.class);
        startActivity(intent3);
    }

    public void checkSDCard(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            useOCR();
        } else {
            new AlertDialog.Builder(this).setMessage("Please check if you have SD card on your device");
            if(mLoading.isShowing()){
                mLoading.dismiss();
            }
        }
    }


}
