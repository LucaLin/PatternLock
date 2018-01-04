package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.extra.MyeTicketAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.TicketListData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.event.DownloadEvent;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

public class MyTicketFragment extends Fragment {

    public interface OnEventListener
    {
        void OnListItemClickEvent(TicketListData selectTicket);
    }

    private static final String TAG = "MyeTicketFragment";
    public static final String EXTRA_SWITCH_TO = "EXTRA_SWITCH_TO";

    public static final int TICKET_UNUSED = 0;
    public static final int TICKET_USED = 1;
    public static final int TICKET_RETURN = 2;

    // -- View Hold --
    private ListView lstMyeTicket;
    private FrameLayout btnUnused;
    private FrameLayout btnUsed;
    private FrameLayout btnReturn;
    private TextView txtUnused;
    private TextView txtUsed;
    private TextView txtReturn;
    private ImageView imgUnusedNew, imgUsedNew, imgReturnNew;

    // -- List View Adapter --
    private MyeTicketAdapter adapterItem;

    // -- Data Model --
    private int idxSwitchList = TICKET_UNUSED;
    private boolean isEditing = false;

    private List<TicketListData> listTicketUnused;
    private List<TicketListData> listTicketUsed;
    private List<TicketListData> listTicketReturn;
    private ArrayList<String> downloadlist;
    private int downloadIndex = 0;

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener)
    {
        onEventListener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getArguments() != null)
            idxSwitchList = getArguments().getInt(EXTRA_SWITCH_TO, TICKET_UNUSED);

//        createSample();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_my_eticket, container, false);

        // Set View Hold
        lstMyeTicket = (ListView) view.findViewById(R.id.list);
        btnUnused = (FrameLayout) view.findViewById(R.id.btn_unused);
        btnUsed = (FrameLayout) view.findViewById(R.id.btn_used);
        btnReturn = (FrameLayout) view.findViewById(R.id.btn_return);
        txtUnused = (TextView) view.findViewById(R.id.txt_unused);
        txtUsed = (TextView) view.findViewById(R.id.txt_used);
        txtReturn = (TextView) view.findViewById(R.id.txt_return);
        imgUnusedNew = (ImageView) view.findViewById(R.id.img_unused_new);
        imgUsedNew = (ImageView) view.findViewById(R.id.img_used_new);
        imgReturnNew = (ImageView) view.findViewById(R.id.img_return_new);
        // List Adpater
        adapterItem = new MyeTicketAdapter(getContext());

        // Set Listener
        btnUnused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToUnused();
            }
        });
        btnUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToUsed();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToReturn();
            }
        });


        lstMyeTicket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
      //  lstMyeTicket.setAdapter(adapterItem);
        // Default: show the expend transaction...
        updateViewBySwitch();

        // Load the Data
        inquiryList();
        updateListView(); //TODO should process if after mock

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        GeneralHttpUtil.stopDownloadCoupon();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView()");

        if (getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if (item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        } else if (item.getItemId() == android.R.id.edit) {
            isEditing = !isEditing;

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // User interaction
    // ----
    public void switchToUnused() {
        idxSwitchList = TICKET_UNUSED;
        updateViewBySwitch();
        updateListView();
    }

    public void switchToUsed() {
        idxSwitchList = TICKET_USED;
        updateViewBySwitch();
        updateListView();
    }

    public void switchToReturn() {
        idxSwitchList = TICKET_RETURN;
        updateViewBySwitch();
        updateListView();
    }

    public void onListItemClick(int position) {
        TicketListData ticket;
        if (idxSwitchList == TICKET_USED) {
            ticket = listTicketUsed.get(position);
        } else if (idxSwitchList == TICKET_UNUSED) {
            ticket = listTicketUnused.get(position);
        } else {
            ticket = listTicketReturn.get(position);
        }

        onEventListener.OnListItemClickEvent(ticket);
       //((MyeTicketActivity) getActivity()).gotoDetail(ticket);
    }

    // ----
    // Http
    // ----
    private void inquiryList() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ((ActivityBase) getActivity()).showProgressLoading();
               // ExtraHttpUtil.queryUnusedCoupon(responseListenerUnused ,getActivity(), TAG);
                ExtraHttpUtil.queryTickets(0, responseListenerUnused, getActivity(), TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerUnused = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                listTicketUnused = ExtraResponseBodyUtil.parseToTicketList(result.getBody());

            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showDialog(result.getReturnMessage());
                }
            }

            // 呼叫api取得發送的紅包清單
            try {
                //ExtraHttpUtil.queryUsedCoupon(responseListenerUsed, getActivity(), TAG);
                ExtraHttpUtil.queryTickets(1, responseListenerUsed, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 呼叫發出紅包api的listener
    private ResponseListener responseListenerUsed = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                listTicketUsed = ExtraResponseBodyUtil.parseToTicketList(result.getBody());
               // updateListView();

            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showDialog(result.getReturnMessage());
                }
            }

            try {
                //ExtraHttpUtil.queryUsedCoupon(responseListenerUsed, getActivity(), TAG);
                ExtraHttpUtil.queryTickets(2, responseListenerReturn, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private ResponseListener responseListenerReturn = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                listTicketReturn = ExtraResponseBodyUtil.parseToTicketList(result.getBody());
                updateListView();

            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showDialog(result.getReturnMessage());
                }
            }

        }
    };


    // ----
    // Private method
    // ----
    private void showDialog(String message) {
        ((ActivityBase) getActivity()).showAlertDialog(message, android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }

    private void updateListView() {
        updateListView(true);
    }

    private void updateListView(boolean isCheckImage) {

        if(isCheckImage) {
            checkImages();
        }

        if (idxSwitchList == TICKET_UNUSED) {
            adapterItem.setTicketList(listTicketUnused);
        } else if (idxSwitchList == TICKET_USED) {
            adapterItem.setTicketList(listTicketUsed);
        } else {
            adapterItem.setTicketList(listTicketReturn);
        }
        adapterItem.notifyDataSetChanged();

    }

    private void updateViewBySwitch() {
        if (idxSwitchList == TICKET_UNUSED) {
            btnUnused.setBackgroundResource(R.drawable.tab_history_p);
            btnUsed.setBackgroundDrawable(null);
            btnReturn.setBackgroundDrawable(null);
            idxSwitchList = TICKET_UNUSED;
            lstMyeTicket.setAdapter(adapterItem);
            txtUnused.setTextColor(getResources().getColor(android.R.color.white));
            txtUsed.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtReturn.setTextColor(getResources().getColor(R.color.radio_tab_text));
        } else if(idxSwitchList == TICKET_USED) {
            btnUnused.setBackgroundDrawable(null);
            btnUsed.setBackgroundResource(R.drawable.tab_history_p_m);
            btnReturn.setBackgroundDrawable(null);
            idxSwitchList = TICKET_USED;
            lstMyeTicket.setAdapter(adapterItem);
            txtUnused.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtUsed.setTextColor(getResources().getColor(android.R.color.white));
            txtReturn.setTextColor(getResources().getColor(R.color.radio_tab_text));
        } else {
            //TICKET_RETURN
            btnUnused.setBackgroundDrawable(null);
            btnUsed.setBackgroundDrawable(null);
            btnReturn.setBackgroundResource(R.drawable.tab_history_p_right);
            idxSwitchList = TICKET_RETURN;
            lstMyeTicket.setAdapter(adapterItem);
            txtUnused.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtUsed.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtReturn.setTextColor(getResources().getColor(android.R.color.white));
        }

    }


    private void checkImages()
    {
        //TODO 須確認getIconUrl的格式, 以及下載的方式
        downloadlist = new ArrayList<>();

        File imgFile;
        File imgURL;
        if(listTicketUnused != null) {
            imgUnusedNew.setVisibility(View.GONE);
            for (TicketListData ticket : listTicketUnused) {
                if (ticket.getReadFlag().equals("0")) {
                    imgUnusedNew.setVisibility(View.VISIBLE);
                }

                if(!TextUtils.isEmpty(ticket.getIconUrl())) {
                    imgURL = new File(ticket.getIconUrl());
                    imgFile = new File(ContactUtil.TicketFolderPath + File.separator + imgURL.getName());
//                    Log.d(TAG, "Image URL:" + ticket.getIconUrl() + "   FileName:"+ imgURL.getName());
                    if(!imgFile.exists() && !hasExistList(ticket.getIconUrl())) {
                        downloadlist.add(ticket.getIconUrl());
                    }
                }
            }
        }

        if(listTicketUsed != null) {
            imgUsedNew.setVisibility(View.GONE);
            for (TicketListData ticket : listTicketUsed) {
                if (ticket.getReadFlag().equals("0")) {
                    imgUsedNew.setVisibility(View.VISIBLE);
                }
                if(!TextUtils.isEmpty(ticket.getIconUrl())) {
                    imgURL = new File(ticket.getIconUrl());
                    imgFile = new File(ContactUtil.TicketFolderPath + File.separator + imgURL.getName());

                    if (!imgFile.exists() && !hasExistList(ticket.getIconUrl())) {
                        downloadlist.add(ticket.getIconUrl());
                    }
                }

            }
        }

        if(listTicketReturn != null) {
            imgReturnNew.setVisibility(View.GONE);
            for (TicketListData ticket : listTicketReturn) {
                if (ticket.getReadFlag().equals("0")) {
                    imgReturnNew.setVisibility(View.VISIBLE);
                }
                if(!TextUtils.isEmpty(ticket.getIconUrl())) {
                    imgURL = new File(ticket.getIconUrl());
                    imgFile = new File(ContactUtil.TicketFolderPath + File.separator + imgURL.getName());

                    if (!imgFile.exists() && !hasExistList(ticket.getIconUrl())) {
                        downloadlist.add(ticket.getIconUrl());
                    }
                }
            }
        }

        if(downloadlist.size() != 0)
        {
            downloadIndex = 0;
            startImageDownload();

        }
    }

    private void startImageDownload() {
        File imgURL = new File(downloadlist.get(downloadIndex));
        File imgFile = new File(ContactUtil.TicketFolderPath + File.separator + imgURL.getName());
        if (!imgFile.exists()) {
            imageDownload(downloadlist.get(downloadIndex));
        }
    }

    private boolean hasExistList(String inputURL)
    {
        for(String url: downloadlist)
        {
            if(inputURL.equals(url))
            {
                return true;
            }
        }
        return false;

    }

    private void imageDownload(String fileName)
    {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                GeneralHttpUtil.downloadTicket(fileName, finishDownloadListener, getActivity());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private DownloadEvent.FinishDownloadListener finishDownloadListener = new DownloadEvent.FinishDownloadListener() {
        @Override
        public void onFinishDownload() {

            GeneralHttpUtil.stopDownloadCoupon();
            if(getActivity() == null)
            {
                return;
            }

            downloadIndex++;
            if(downloadIndex < downloadlist.size() ) {
                startImageDownload();
            }
            else
            {
              //  downloadlist.clear();
                updateListView(false);
            }
        }
    };

}
