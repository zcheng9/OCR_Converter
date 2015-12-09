package team14.cs442.com;

import android.database.sqlite.SQLiteDatabase;


public class OCRTable {

    //Database table
    public static final String OCR="ocr";
    public static final String COLUMN_ID="_id";//primary key of table
    public static final String COLUMN_NAME="name";
    public static final String COLUMN_CONTENT="content";
    public static final String COLUMN_TITLE="title";
    public static final String COLUMN_REALID="realid";//actual id, it will renew after delete operation

    private static String DATABASE_CREATE="create table "
            +OCR
            +"("
            +COLUMN_ID+" integer primary key autoincrement, "
            +COLUMN_NAME+" text not null, "
            +COLUMN_CONTENT+" text not null, "
            +COLUMN_TITLE+" text not null, "
            +COLUMN_REALID+" integer not null "
            +");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion,
                                 int newVersion){
        database.execSQL("DROP TABLE IF EXISTS "+OCR);
        onCreate(database);
    }

}
