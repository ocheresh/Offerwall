package com.ocheresh.offerwall;
import android.util.Log;

import org.json.*;

public class Object {

    String id;
    String type;
    String type_info;

    public Object(String json_str)
    {
        try {
            JSONObject obj = new JSONObject(json_str);

            id = obj.getString("id");
            type = obj.getString("type");
            switch (type){
                case ("text"):
                    type_info = obj.getString("message");
                    break;
                case ("webview"):
                    type_info = obj.getString("url");
                    break;
                case ("image"):
                    type_info = obj.getString("url");
                    break;
                default:
                    type_info = "";
                    break;
            }
        }
        catch(Exception e){
            Log.e("Answer error json: ", e.getMessage().toString());
        }
    }

    public String getid() {
        return this.id;
    }

    public String gettype() {
        return this.type;
    }

    public String gettype_info() {
        return this.type_info;
    }

}
