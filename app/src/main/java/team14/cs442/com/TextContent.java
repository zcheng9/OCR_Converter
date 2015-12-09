package team14.cs442.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chuang Xie on 2015/10/26.
 */
public class TextContent {

    public static List<TextItem> ITEMS=new ArrayList<TextItem>();

    public static Map<String, TextItem> ITEM_MAP=new HashMap<String,TextItem>();

    public static class TextItem{
        public String id;
        public String name;
        public String filename;
        public String text;

        public TextItem(String id, String text, String filename, String name){
            this.id=id;
            this.text=text;
            this.filename=filename;
            this.name=name;

        }

        public void modifyID(){
            int newID=Integer.parseInt(id);
            newID--;
            id=""+newID;
        }

        @Override
        public String toString(){
            return id+".  "+name;
        }
    }
}
