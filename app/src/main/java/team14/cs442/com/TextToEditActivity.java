package team14.cs442.com;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TextToEditActivity extends AppCompatActivity {

    private String textToEdit;
    private Bitmap bm;
    private PhotoViewAttacher mAttacher;

    public final static String MESSAGE1="team14.cs442.com.ImageActivity.text1";
    public final static String IMAGE1="team14.cs442.com.ImageActivity.image1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_edit);

        Intent i=getIntent();
        textToEdit=i.getStringExtra(ImageActivity.MESSAGE);
        byte [] bis=i.getByteArrayExtra(ImageActivity.IMAGE);
        bm= BitmapFactory.decodeByteArray(bis, 0, bis.length);

        ((EditText)findViewById(R.id.text_toedit)).setText(textToEdit);
        ImageView imageView=(ImageView) findViewById(R.id.image_display);
        imageView.setImageBitmap(bm);
        mAttacher=new PhotoViewAttacher(imageView);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_to_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    public void editButton(View v){
        String edit=((EditText)findViewById(R.id.text_toedit)).getText().toString();

        Intent intent=new Intent(this, TextOperationActivity.class);
        intent.putExtra(MESSAGE1, edit);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapByte=baos.toByteArray();
        intent.putExtra(IMAGE1,bitmapByte);
        startActivity(intent);
    }
}
