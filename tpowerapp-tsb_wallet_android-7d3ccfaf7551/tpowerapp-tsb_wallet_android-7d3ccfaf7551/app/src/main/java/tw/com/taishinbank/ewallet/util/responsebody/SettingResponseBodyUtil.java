package tw.com.taishinbank.ewallet.util.responsebody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.model.setting.PushData;

public class SettingResponseBodyUtil {


    /**
     * 轉換成Push設定物件
     *
     * @param object server through server.
     * @return List<SVTransactionOut>
     */
    public static List<PushData> parsePushSettingList(JSONObject object) {
        ArrayList<PushData> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("pushList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<PushData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
