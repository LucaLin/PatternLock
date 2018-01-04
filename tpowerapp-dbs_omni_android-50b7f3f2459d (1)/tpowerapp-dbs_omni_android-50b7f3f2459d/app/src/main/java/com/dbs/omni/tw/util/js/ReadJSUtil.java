package com.dbs.omni.tw.util.js;


import android.os.AsyncTask;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by siang on 2017/5/26.
 *
 *
 */
//  EX:
//
//   ReadJSUtil readJSUtil = new ReadJSUtil(this);
//   readJSUtil.runScript("encpty", new String[]{"1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdef",
//        "abcdef1234567890", "test"}, new ReadJSUtil.OnRunScriptListener() {
//@Override
//public void OnReturn(String result) {
//        Log.d("JS result", result);
//        showAlertDialog(result);
//        }
//
//@Override
//public void OnNoFound() {
//
//        }
//   });


public class ReadJSUtil {

    private static Context mRhino;
    private static Scriptable mScope;
    // class ScriptAPI

    private OnRunScriptListener onRunScriptListener;
    public interface OnRunScriptListener {
        void OnReturn(String result);
        void OnNoFound();
    }

    public void setOnRunScriptListener(OnRunScriptListener listener) {
        onRunScriptListener = listener;
    }

    public static void encpty(android.content.Context context, String pkey, String rkey, String content , OnRunScriptListener listener) {
        ReadJSUtil readJSUtil = new ReadJSUtil(context);

        String[] params = new String[] {pkey, rkey, content};
//        readJSUtil.runScript("encpty", params, listener);

        RunScriptAsyncTask runScriptAsyncTask = new RunScriptAsyncTask(mRhino, mScope, "encpty", params, listener);
        runScriptAsyncTask.execute();
    }

    public ReadJSUtil(android.content.Context context) {
//        Object[] params = new Object[] { "javaScriptParam" };

        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context
        mRhino = Context.enter();

        // Turn off optimization to make Rhino Android compatible
        mRhino.setOptimizationLevel(-1);
        mScope = mRhino.initStandardObjects();
        // Note the forth argument is 1, which means the JavaScript source has
        // been compressed to only one line using something like YUI
        try {
            mRhino.evaluateReader(mScope, getJSContent(context, "jsbn.js"), "JavaScript", 1, null);
            mRhino.evaluateReader(mScope, getJSContent(context, "3des_obf.js"), "JavaScript", 1, null);
            mRhino.evaluateReader(mScope, getJSContent(context, "util_obf.js"), "JavaScript", 1, null);
            mRhino.evaluateReader(mScope, getJSContent(context, "rsa_obf.js"), "JavaScript", 1, null);
            mRhino.evaluateReader(mScope, getJSContent(context, "RIBLogon.js"), "JavaScript", 1, null);
            mRhino.evaluateReader(mScope, getJSContent(context, "index.js"), "JavaScript", 1, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public void runScript(String functionNameInJavaScriptCode, Object[] params, OnRunScriptListener listener) {
//            // Get the functionName defined in JavaScriptCode
////            String functionNameInJavaScriptCode = "setKeyValue";
//            Object obj = mScope.get(functionNameInJavaScriptCode, mScope);
//
//            if (obj instanceof Function) {
//                Function jsFunction = (Function) obj;
//
//                // Call the function with params
//                Object jsResult = jsFunction.call(mRhino, mScope, mScope, params);
//                // Parse the jsResult object to a String
//                String result = Context.toString(jsResult);
//                listener.OnReturn(result);
//            } else {
//                listener.OnNoFound();
//            }
//    }

    public void onClose() {
        Context.exit();
    }


    private Reader getJSContent(android.content.Context context, String fileName) {
        String content = "";
        Reader reader = null;
        try {

            reader =   new InputStreamReader(context.getAssets().open(fileName), "UTF-8");

            // do reading, usually loop until end of file reading

            return reader;
        } catch (IOException e) {
            //log the exception
        } finally {
//
        }
        return null;
    }



    static class RunScriptAsyncTask extends AsyncTask<String, Void, String> {

        private Context mRhino;
        private Scriptable mScope;

        private String functionNameInJavaScriptCode;
        private Object[] mParams;
        private OnRunScriptListener onRunScriptListener;

        public RunScriptAsyncTask(Context mRhino, Scriptable mScope, String functionNameInJavaScriptCode, Object[] params, OnRunScriptListener listener) {
            this.mRhino = mRhino;
            this.mScope = mScope;

            this.functionNameInJavaScriptCode = functionNameInJavaScriptCode;
            this.mParams = params;
            this.onRunScriptListener = listener;
        }

        @Override
        protected String doInBackground(String... params) {

            Object obj = mScope.get(functionNameInJavaScriptCode, mScope);

            if (obj instanceof Function) {
                Function jsFunction = (Function) obj;

                // Call the function with params
                Object jsResult = jsFunction.call(mRhino, mScope, mScope, mParams);
                // Parse the jsResult object to a String
                String result = Context.toString(jsResult);
                return result;
            } else {
                onRunScriptListener.OnNoFound();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onRunScriptListener.OnReturn(s);
        }
    }
}
