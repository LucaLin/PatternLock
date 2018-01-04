package com.dbs.omni.tw.controller.bill;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.ElectronicBillItemAdapter;
import com.dbs.omni.tw.adapter.FilterAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.model.element.FilterItemData;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.http.BillHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.bill.EBillFileData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.BillResponseBodyUtil;
import com.dbs.omni.tw.util.sharedMethods;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

public class ElectronicBillFragment extends Fragment {

    public static final String TAG = "ElectronicBillFragment";

    private TextView buttonFilter;
    private ArrayList<FilterItemData> mFilterItemDatas;
    private RelativeLayout mRelativeLayoutFilter;

    private int mSelectedIndex = 0;
    private EBillFileData selectedBillData;

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener) {
        onEventListener = listener;
    }

    public interface OnEventListener {
//        void OnHeaderRightClick();
    }

    private LayoutInflater mInflater;
    private View mView;
    private RelativeLayout mNoBillPage;
    private LinearLayout mHasBillPage;
    private ListView mListView;
    private TextView mTextListHeader;
    private ElectronicBillItemAdapter mElectronicBillItemAdapter;
    private ArrayList<EBillFileData> mElectronicBillDataArrayList;
    private EBillFileData mSelectFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_electronic_bill, container, false);
        mHasBillPage = (LinearLayout) mView.findViewById(R.id.linearLayout_has_bill);
        mNoBillPage = (RelativeLayout) mView.findViewById(R.id.relativeLayout_not_bill);
        mListView = (ListView) mView.findViewById(R.id.list_bill);
        mTextListHeader = (TextView) mView.findViewById(R.id.text_list_header);

//        mElectronicBillDataArrayList = getMockData();

        if(UserInfoUtil.getsStatmFlag().equalsIgnoreCase("Y")) {
            mHasBillPage.setVisibility(View.VISIBLE);
            mNoBillPage.setVisibility(View.GONE);
            toGeteBillList();
//            setList();
        } else {
            setNoActiveBill();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void setNoActiveBill() {
        mHasBillPage.setVisibility(View.GONE);
        mNoBillPage.setVisibility(View.VISIBLE);

        TextView textApply = (TextView) mView.findViewById(R.id.text_apply);
        textApply.setPaintFlags(textApply.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // web view
            }
        });
    }

    private void setList() {

        if (mElectronicBillDataArrayList == null|| mElectronicBillDataArrayList.size() == 0) {
            mTextListHeader.setText(getString(R.string.electronic_bill_not_has_hint));
        } else {
            mListView.setVisibility(View.VISIBLE);
            mNoBillPage.setVisibility(View.GONE);

        //        cardListView.setScrollContainer(false);
        //
            mElectronicBillItemAdapter = new ElectronicBillItemAdapter(getActivity());
            mListView.setAdapter(mElectronicBillItemAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                onCardListItmeClick(position);
                    selectedBillData = mElectronicBillDataArrayList.get(position);
                    getBillFile(selectedBillData);
                }
            });

            setCardItemsData(mElectronicBillDataArrayList);
//            setFilterView(mInflater, mView);
        }
    }

    private void setCardItemsData(ArrayList<EBillFileData> electronicBillDatas) {
        if(mElectronicBillItemAdapter != null) {
            mElectronicBillItemAdapter.setList(electronicBillDatas);
            mElectronicBillItemAdapter.notifyDataSetChanged();
        }
    }


//region Mock
//    private ArrayList<ElectronicBillData> getMockData() {
//        ArrayList<ElectronicBillData>  mocklist = new ArrayList<>();
//
//        mocklist.add(new ElectronicBillData("2017年02月", "http://test.pilot.game.tw/testAPK/10604.pdf"));
//        mocklist.add(new ElectronicBillData("2017年01月", "http://test.pilot.game.tw/testAPK/10604.pdf"));
//        mocklist.add(new ElectronicBillData("2016年12月", "http://test.pilot.game.tw/testAPK/10604.pdf"));
//        mocklist.add(new ElectronicBillData("2016年11月", "http://test.pilot.game.tw/testAPK/10604.pdf"));
//        mocklist.add(new ElectronicBillData("2016年10月", "http://test.pilot.game.tw/testAPK/10604.pdf"));
//        mocklist.add(new ElectronicBillData("2016年09月", "http://test.pilot.game.tw/testAPK/10604.pdf"));
//        return mocklist;
//    }
//endregion



//region Filter View
    private void setFilterView(LayoutInflater inflater, View view) {
        mRelativeLayoutFilter = (RelativeLayout) view.findViewById(R.id.relativeLayout_filter);
        mRelativeLayoutFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRelativeLayoutFilter.setVisibility(View.GONE);
                return true;
            }
        });

        setFilterView(inflater, mRelativeLayoutFilter);
        setActionRightButton();
    }

    private void setActionRightButton() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            buttonFilter = (TextView) toolbar.findViewById(R.id.custom_button_right);
            buttonFilter.setVisibility(View.VISIBLE);
            buttonFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_unselected, 0);
            buttonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRelativeLayoutFilter.setVisibility(View.VISIBLE);
//
//                    Bitmap bitmap = BlurUtil.getBitmapFromView(rootView);
//                    bitmap = blurBitmap(bitmap, 20, getContext());
////                    RenderScript rs = RenderScript.create(getActivity());
////                    BlurUtil.RSBlurProcessor rsb = new BlurUtil.RSBlurProcessor(rs);
////                    bitmap = rsb.blur(bitmap, 100, 100);
//                    BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
//                    mRelativeLayoutFilter.setBackground(ob);

                }
            });
        }
    }

    private void setFilterView(LayoutInflater inflater, RelativeLayout view) {
        View mFilterView = inflater.inflate(R.layout.element_filter, null);

        ListView listFilters = (ListView) mFilterView.findViewById(R.id.list_filters);
        final FilterAdapter filterAdapter = new FilterAdapter(getContext());
        listFilters.setAdapter(filterAdapter);

        listFilters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterItemData olderSelectData = mFilterItemDatas.get(mSelectedIndex);
                olderSelectData.setSelect(false);

                mSelectedIndex = position;
                FilterItemData data = mFilterItemDatas.get(mSelectedIndex);
                data.setSelect(true);
                filterAdapter.notifyDataSetChanged();
                mRelativeLayoutFilter.setVisibility(View.GONE);
            }
        });



        mFilterItemDatas = getFilterMock();
        filterAdapter.setList(mFilterItemDatas);
        filterAdapter.notifyDataSetChanged();
        view.addView(mFilterView);
        view.setVisibility(View.GONE);
    }

    private ArrayList<FilterItemData> getFilterMock(){
        ArrayList<FilterItemData> filterArray = new ArrayList<>();

        filterArray.add(new FilterItemData("所有電子帳單", true));
        filterArray.add(new FilterItemData("2017年電子帳單", false));
        filterArray.add(new FilterItemData("2016年電子帳單", false));

        return filterArray;
    }
//endregion


//region
    private void getBillFile(EBillFileData data) {
        mSelectFile = data;
//        if(TextUtils.isEmpty(data.getFileURL()))
//            return;
//        String fileName = data.getFileURL().substring(data.getFileURL().lastIndexOf('/') + 1);

        String fileName = FormatUtil.toDateHeaderFormatted(data.geteBillYYYY()+data.geteBillMM(), false);
        String saveFilePath = GlobalConst.PDFFileFolderPath + File.separator + fileName + ".pdf";
        File saveFile = new File(saveFilePath);
        if(saveFile.exists()) {
            goToOpenPDF(FormatUtil.toDateHeaderFormatted(data.geteBillYYYY()+data.geteBillMM(), false), saveFilePath);
        } else {
            downloadPDF(data);
        }
    }



//    private FileDownloadEvent.FinishDownloadListener onFinishDownloadListener = new FileDownloadEvent.FinishDownloadListener() {
//        @Override
//        public void onFinishDownload(String filePath) {
//            ((ActivityBase) getActivity()).dismissProgressLoading();
//            Log.v(selectedBillData.getTitle(), filePath);
//            goToOpenPDF(selectedBillData.getTitle(), filePath);
//        }
//
//        @Override
//        public void onErrorDownload() {
//            ((ActivityBase) getActivity()).dismissProgressLoading();
//            Log.v(selectedBillData.getTitle(), "Error");
//        }
//    };

    private void goToOpenPDF(String title, String filePath) {
        Intent intent = new Intent(getActivity(), PDFReaderActivity.class);
        intent.putExtra(PDFReaderActivity.EXTRA_BILL_TITLE, title);
        intent.putExtra(PDFReaderActivity.EXTRA_URL, filePath);
        startActivity(intent);
    }
//endregion



    //region api
    private void toGeteBillList() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                BillHttpUtil.geteBillList(responseListener, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                mElectronicBillDataArrayList = BillResponseBodyUtil.getEBillFileDataList(result.getBody());
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

            setList();

        }
    };

    private void downloadPDF(EBillFileData data) {
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                BillHttpUtil.downloadeBillFile(data.geteBillNO(), data.geteBillYYYY(), data.geteBillMM(), data.geteBillFileName(), responseListener_download, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_download = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                String eBillFileBase64  = BillResponseBodyUtil.getEBillFile(result.getBody());
                File pdfFile = sharedMethods.base64ToFile(eBillFileBase64, FormatUtil.toDateHeaderFormatted(selectedBillData.geteBillYYYY()+selectedBillData.geteBillMM(), false));
                goToOpenPDF(FormatUtil.toDateHeaderFormatted(selectedBillData.geteBillYYYY()+selectedBillData.geteBillMM(), false), pdfFile.getPath());
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };
//endregion
}
