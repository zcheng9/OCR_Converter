package team14.cs442.com;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class OCRContentProvider extends ContentProvider {

    //database
    private OCRDatabaseHelper database;

    //used for uriMatcher
    private static final int MENUS=10;
    private static final int MENU_ID=20;

    private static final String AUTHORITY="team14.cs442.com.contentprovider";

    private static final String BASE_PATH="menus";
    public static final String URI_ITEM="content://"+AUTHORITY+"/"+BASE_PATH+"/";
    public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH);

    public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE;
    public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE;

    private static final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,BASE_PATH,MENUS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH+"/#",MENU_ID);
    }

    @Override
    public boolean onCreate(){
        database=new OCRDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,String[] projection, String selection, String[] selectionArgs, String sortOrder){

        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(OCRTable.OCR);

        int uriType=uriMatcher.match(uri);
        switch (uriType){
            case MENUS:
                break;
            case MENU_ID:
                queryBuilder.appendWhere(OCRTable.COLUMN_REALID+"="
                        +uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);
        }

        SQLiteDatabase db=database.getWritableDatabase();
        Cursor cursor=queryBuilder.query(db, projection, selection,
                selectionArgs,null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri){
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        int uriType=uriMatcher.match(uri);
        SQLiteDatabase db=database.getWritableDatabase();

        long id=0;
        switch (uriType){
            case MENUS:
                id=db.insert(OCRTable.OCR,null,values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH+"/"+id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int uriType=uriMatcher.match(uri);
        SQLiteDatabase db=database.getWritableDatabase();
        int rowDeleted=0;
        switch (uriType){
            case MENUS:
                rowDeleted=db.delete(OCRTable.OCR, selection, selectionArgs);
                break;
            case MENU_ID:
                String id=uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)){
                    rowDeleted=db.delete(OCRTable.OCR,OCRTable.COLUMN_REALID+"="+id,null);
                }
                else {
                    rowDeleted=db.delete(OCRTable.OCR, OCRTable.COLUMN_REALID+"="+id+" and "+selection,selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Override
    public int update(Uri uri,ContentValues values, String selection, String[] selectionArgs){
        int uriType=uriMatcher.match(uri);
        SQLiteDatabase db=database.getWritableDatabase();
        int rowUpdated=0;
        switch (uriType){
            case MENUS:
                rowUpdated=db.update(OCRTable.OCR,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MENU_ID:
                String id=uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)){
                    rowUpdated=db.update(OCRTable.OCR,
                            values,
                            OCRTable.COLUMN_REALID + "=" + id,
                            null);
                }
                else{
                    rowUpdated=db.update(OCRTable.OCR,
                            values,
                            OCRTable.COLUMN_REALID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowUpdated;
    }

    private void checkColumns(String[] projection){
        String[] available={ OCRTable.COLUMN_NAME, OCRTable.COLUMN_CONTENT, OCRTable.COLUMN_ID, OCRTable.COLUMN_REALID,OCRTable.COLUMN_TITLE};
        if(projection!=null){
            HashSet<String> requestColumns=new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns=new HashSet<String>(Arrays.asList(available));
            if(!availableColumns.containsAll(requestColumns)){
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
