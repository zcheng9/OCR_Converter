package team14.cs442.com;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class OCRDatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="ocrtable.db";
    private static final int DATABASE_VERSION=1;

    public OCRDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        OCRTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        OCRTable.onUpgrade(database,oldVersion,newVersion);
    }
}
