package team14.cs442.com;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TextOperationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private String content, translated_content, filename,title;
    private Bitmap bm;
    private CallbackManager cm;
    private LoginManager lm;
    private TextToSpeech tts;
    private int pos=-1, newPos=0,size=0, curSize=0;

    private final String APP_TAG="ocr_convert";
    private int translateLanguage_index=-1;
    private final String TRANSLATE_USERNAME = "ocr_program";
    private final String TRANSLATE_SECRET = "oemhFEUMrNG9TKw4nm293xmhiIHdcsu2OP+qEFbj1B8=";
    private final int default_choice = 0;
    private final String[] languages = new String[]{
            "Italian",
            "German",
            "French"
    };

    private final Language[] translate_languages = new Language[]{
            Language.ITALIAN,
            Language.GERMAN,
            Language.FRENCH
    };

    private final Locale[] tts_languages = new Locale[]{
            Locale.ITALIAN,
            Locale.GERMAN,
            Locale.FRENCH
    };

    private void setTTSLanguage(Locale locale) {
        int result = tts.setLanguage(locale);

        if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(APP_TAG, "Language is not supported.");
        }

    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            setTTSLanguage(Locale.ENGLISH);
        } else {
            Log.e(APP_TAG, "TTS init failed.");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_operation);

        FacebookSdk.sdkInitialize(getApplicationContext());
        cm=CallbackManager.Factory.create();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i=getIntent();
        content=i.getStringExtra(TextToEditActivity.MESSAGE1);
        byte [] bis=i.getByteArrayExtra(TextToEditActivity.IMAGE1);
        bm=BitmapFactory.decodeByteArray(bis, 0, bis.length);
        saveImage(bm);

        TextView textView=(TextView)findViewById(R.id.show_text);
        textView.setText(content);
        size=(int)textView.getTextSize();
        curSize=size;

        tts = new TextToSpeech(this, this);

        getPos();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        tts.shutdown();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_view:
                final ScrollView scrollView=new ScrollView(this);
                final ImageView imageView=new ImageView(this);
                imageView.setImageBitmap(bm);
                PhotoViewAttacher mAttacher=new PhotoViewAttacher(imageView);
                scrollView.addView(imageView);
                new AlertDialog.Builder(this).setView(scrollView).setPositiveButton("OK",null).show();
                return true;
            case R.id.action_edit:

                pos=-1;
                new AlertDialog.Builder(this).setTitle("choose font size options:").setSingleChoiceItems(new String[]{
                        "small(default)", "medium", "large"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos=which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pos==0){
                            TextView textView=(TextView)findViewById(R.id.show_text);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                            TextView textView1=(TextView)findViewById(R.id.show_translate_text);
                            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                            curSize=size;
                        }
                        else if(pos==1){
                            TextView textView=(TextView)findViewById(R.id.show_text);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size+3);
                            TextView textView1=(TextView)findViewById(R.id.show_translate_text);
                            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, size+3);
                            curSize=size+3;
                        }
                        else if(pos==2){
                            TextView textView=(TextView)findViewById(R.id.show_text);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 6);
                            TextView textView1=(TextView)findViewById(R.id.show_translate_text);
                            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, size+6);
                            curSize=size+6;
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
                return true;
            case R.id.action_share:
                pos=-1;
                new AlertDialog.Builder(this).setTitle("share options:").setSingleChoiceItems(new String[]{
                        "Facebook", "E-mail"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos=which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pos==0){
                            shareTo();
                        }
                        else if(pos==1){
                            sendEmail();
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
                return true;
            case R.id.action_save:

                pos=-1;
                new AlertDialog.Builder(this).setTitle("save options:").setSingleChoiceItems(new String[]{
                        "sd card", "database"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos = which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pos == 0) {
                            final EditText e2=new EditText(TextOperationActivity.this);
                            e2.setText("OCR_Converter_Output");
                            new AlertDialog.Builder(TextOperationActivity.this).setMessage("input the filename you want to save:").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        saveToSD(e2.getText().toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).setNegativeButton("Cancel", null).setView(e2).show();
                        } else if (pos == 1) {
                            final EditText e3=new EditText(TextOperationActivity.this);
                            new AlertDialog.Builder(TextOperationActivity.this).setMessage("input the record name you want to save:").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    title=e3.getText().toString();
                                    if(title!=null){
                                        addRecord();
                                        new AlertDialog.Builder(TextOperationActivity.this).setMessage("Now you can check it in history now").setPositiveButton("OK", null).show();
                                    }
                                    else{
                                        new AlertDialog.Builder(TextOperationActivity.this).setMessage("Error, please try again.").setPositiveButton("OK", null).show();
                                    }
                                }
                            }).setNegativeButton("Cancel",null).setView(e3).show();

                        }
                    }
                }).setNegativeButton("Cancel", null).show();

                return true;
            case R.id.action_translate:
                popupTranslate();
                return true;
            case R.id.action_speech:
                (findViewById(R.id.button)).setVisibility(View.VISIBLE);
                if (translateLanguage_index >= 0) {
                    setTTSLanguage(tts_languages[translateLanguage_index]);
                    Log.i("11123", translateLanguage_index + " index");
                }
                if (translated_content==null) {
                    translated_content = content;
                }
                tts.speak(translated_content, TextToSpeech.QUEUE_FLUSH, null);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void saveToSD(String filename)throws IOException{
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            FileOutputStream out=null;
            File file=new File(Environment.getExternalStorageDirectory(),filename+".txt");
            out=new FileOutputStream(file);
            PrintWriter pw=new PrintWriter(out);
            pw.print(content);
            pw.flush();
            pw.close();
            out.close();
        }
        else{
            Toast.makeText(this,"No sd card found",Toast.LENGTH_LONG).show();
        }
    }

    public void sendEmail()
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {
                "" });
        i.putExtra(Intent.EXTRA_SUBJECT, "OCR : converted text");
        i.putExtra(Intent.EXTRA_TEXT, content);
        try
        {
            startActivity(Intent.createChooser(i, "Send mail..."));
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(TextOperationActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String translate(String text, int language_index) {
        Log.i(APP_TAG, TRANSLATE_USERNAME);
        Translate.setClientId(TRANSLATE_USERNAME);
        Translate.setClientSecret(TRANSLATE_SECRET);
        String translated;
        try {
            translated = Translate.execute(text, translate_languages[language_index]);
        } catch (Exception e) {
            translated = "translate failed.";
        }
        return translated;
    }

    private static class TranslateParams {
        String text;
        int language_index;

        TranslateParams(String text, int language_index) {
            this.text = text;
            this.language_index = language_index;
        }
    }

    public class TranslateTask extends AsyncTask<TranslateParams, Void, String> {
        @Override
        protected String doInBackground(TranslateParams... translateParamses) {
            String text = translateParamses[0].text;
            int language_index = translateParamses[0].language_index;
            return translate(text, language_index);
        }

        @Override
        protected void onPostExecute(String s) {
            TextView textView = (TextView)findViewById(R.id.show_translate_text);
            textView.setText(s);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, curSize);
            translated_content = s;
            //Toast.makeText(TextOperationActivity.this, s, Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }

    public void popupTranslate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        translateLanguage_index = default_choice;
        builder.setSingleChoiceItems(
                languages,
                default_choice,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        translateLanguage_index = i;
                    }
                }
        );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TranslateParams translateParams = new TranslateParams(content, translateLanguage_index);
                TranslateTask translateTask = new TranslateTask();
                translateTask.execute(translateParams);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode,int responseCode,Intent data){
        super.onActivityResult(requestCode, responseCode, data);
        cm.onActivityResult(requestCode, responseCode, data);
    }

    public void shareTo(){
        List<String> permission= Arrays.asList("publish_actions");
        lm=LoginManager.getInstance();
        lm.logInWithPublishPermissions(this, permission);
        lm.registerCallback(cm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                shareFacebook();
                new AlertDialog.Builder(TextOperationActivity.this).setMessage("have shared to facebook").setPositiveButton("OK", null).show();
            }

            @Override
            public void onCancel() {
                new AlertDialog.Builder(TextOperationActivity.this).setMessage("failed, please check your network").setPositiveButton("OK", null).show();

            }

            @Override
            public void onError(FacebookException e) {
                new AlertDialog.Builder(TextOperationActivity.this).setMessage("sorry, we met some problem, please check if you have facebook app in your device").setPositiveButton("OK", null).show();

            }
        });
    }

    public void shareFacebook(){
        if(bm!=null){
            SharePhoto photo=new SharePhoto.Builder().setBitmap(bm)
                    .setCaption(content).build();
            SharePhotoContent content=new SharePhotoContent.Builder().addPhoto(photo).build();
            ShareApi.share(content, null);
        }
        else{
            Toast.makeText(this,"wrong photo",Toast.LENGTH_LONG).show();
        }

    }

    private void saveImage(Bitmap bitmap){
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File destination=new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis()+".jpg");
            FileOutputStream fo;
            try{
                destination.createNewFile();
                fo=new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
                filename=destination.getName();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this,"No sd card found",Toast.LENGTH_LONG).show();
        }

    }

    private void getPos(){
        String[] projection={OCRTable.COLUMN_NAME,OCRTable.COLUMN_REALID,OCRTable.COLUMN_CONTENT};
        Uri uri=OCRContentProvider.CONTENT_URI;
        Cursor c=getContentResolver().query(uri,projection,null,null,null);
        newPos=c.getCount()+1;

    }

    private void addRecord(){
        ContentValues values=new ContentValues();
        values.put(OCRTable.COLUMN_NAME,filename);
        values.put(OCRTable.COLUMN_CONTENT,content);
        values.put(OCRTable.COLUMN_TITLE,title);
        values.put(OCRTable.COLUMN_REALID, newPos);
        getContentResolver().insert(OCRContentProvider.CONTENT_URI, values);
        TextContent.TextItem item=new TextContent.TextItem(""+newPos,content,filename,title);
        TextContent.ITEMS.add(item);
        TextContent.ITEM_MAP.put(item.id,item);
        newPos++;
    }

    public void stopButton(View v){
        tts.stop();
        (findViewById(R.id.button)).setVisibility(View.INVISIBLE);
    }

}
