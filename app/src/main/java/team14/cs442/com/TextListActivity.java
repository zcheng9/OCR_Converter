package team14.cs442.com;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


public class TextListActivity extends AppCompatActivity implements TextListActivityFragment.Callbacks{

    public final static String MESSAGE="team14.cs442.com.TextListActivity";

    private static final String IMAGE_PATH="/mnt/sdcard/";

    private ArrayAdapter<TextContent.TextItem> aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_list);

        aa=new ArrayAdapter<TextContent.TextItem>(
                this,
                R.layout.fragment_text_list,
                R.id.list_view,
                TextContent.ITEMS
        );

        ((TextListActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment)).setListAdapter(aa);

        aa.notifyDataSetChanged();

        if(TextContent.ITEMS.isEmpty()){
            new AlertDialog.Builder(this).setMessage("no records here now").setPositiveButton("OK",null).show();
        }

    }

    @Override
    public void onItemSelected(String id){
        String a=TextContent.ITEMS.get(Integer.parseInt(id)-1).text;
        Bitmap bitmap=BitmapFactory.decodeFile(IMAGE_PATH+TextContent.ITEMS.get(Integer.parseInt(id)-1).filename);
        ImageView im=new ImageView(this);
        im.setImageBitmap(bitmap);
        new AlertDialog.Builder(this).setMessage(a).setView(im).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_list, menu);
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
            case R.id.action_delete:
                deleteItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void deleteItem(){

        //first dialog to select one item from list
        new AlertDialog.Builder(this).setTitle("Select One").setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(aa, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int deletePos=which;
                        //second dialog to make sure user wanna delete it
                        new AlertDialog.Builder(TextListActivity.this).setTitle("Delete it?").setIcon(android.R.drawable.ic_dialog_info)
                                .setMessage(aa.getItem(deletePos).toString()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                aa.remove(aa.getItem(deletePos));//remove item from adapter
                                getContentResolver().delete(Uri.parse(OCRContentProvider.URI_ITEM + (deletePos + 1)), null, null);
                                int num = aa.getCount();
                                //renew the real id after delete
                                for (int i = deletePos; i < num; i++) {
                                    aa.getItem(i).modifyID();//modify real id in adapter
                                    updateAfterDelete(i,Integer.parseInt(aa.getItem(i).id));//modify in database
                                }
                                aa.notifyDataSetChanged();

                            }
                        }).setNegativeButton("Cancel",null).show();

                    }
                }).setNegativeButton("Cancel", null).show();
    }

    //method to update real id in database after delete
    public void updateAfterDelete(int which,int realID){
        ContentValues values=new ContentValues();
        values.put(OCRTable.COLUMN_REALID,realID);
        getContentResolver().update(Uri.parse(OCRContentProvider.URI_ITEM + (which + 2)), values, null, null);
    }


}
