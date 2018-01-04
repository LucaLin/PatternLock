package com.dbs.omni.tw.setted;

import android.os.Environment;

import java.io.File;

public interface GlobalConst {
//    boolean UseLocalMock = false;
    /**
     * Sit Server setting: false
     */
    boolean DisableEncode = true;


    /**
     * Setting connection is official or mock server.  true: official | false: mock
     */
    boolean UseOfficialServer = true;

    /**
     * Server IP for Mock url;
     */
    String MOCK_SERVER_URL = "http://192.168.0.180:3300/api/";

    /**
     * PWeb url
     */
//    String PWEB_SERVER_URL = "https://www.dbs.com.tw/mobile/ccds/cards";
    String PWEB_SERVER_URL = "http://192.168.50.182:3300/api/cards";

//    /**
//     * Server IP for SIT url;
//     */
//    String OFFICIAL_SERVER_URL = "https://ccds-sit.dbs.com.tw/ccds/";
////    String OFFICIAL_SERVER_URL = "https://omni-sit.dbs.com.tw/ccds/";

    /**
     * Server IP for feature url;
     */
    String OFFICIAL_SERVER_URL = "http://192.168.50.107:8080/ccds/";


    String APP_UPDATE_URL = "https://play.google.com/store/apps/details?id="; // + package name

    /**
     *  File save path in local
     */
    String FolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OMNI";
    String PDFFileFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OMNI" + File.separator + "BillPDFFile";
}
