package tw.com.taishinbank.ewallet.controller.sv;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.InputFilterUtil;

public class MessageEnterFragment extends Fragment {

    private final static String ARG_PARAMS = "ARG_PARAMS";

    protected ImageView imgPhoto;
    protected TextView txtToSend;
    protected TextView txtToSendCount;
    protected LinearLayout lytAmountArea;
    protected TextView txtAmount;

    protected TextView txtReceivedMessage;
    protected TextView txtCount;
    protected EditText txtMessage;

    protected Button button1;

    protected String numberOfChars;
    protected int lengthLimit = 50;

    // -- Data Model --
    protected Parameters parameters;

    // -- Listener --
    private TextWatcher textWatcher;
    private ButtonsClickListener buttonsClickListener;

    // -- Helper --

    public static MessageEnterFragment createNewInstanceWithParams(Parameters parameters) {
        MessageEnterFragment fragment = new MessageEnterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAMS, parameters);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        parameters = getArguments().getParcelable(ARG_PARAMS);
        numberOfChars = getString(R.string.current_text_count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sv_message_enter, container, false);

        // Set View Hold
        setViewHold(view);

        // Set View Content
        // 依據傳入的參入，來決定畫面呈現
        setViewContent();

        //
        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(parameters.isNeedInputMsg())
        {
            if(txtMessage.getText().length() == 0 )
            {
                button1.setEnabled(false);
            }
            else if(txtMessage.getText().length() > 0 )
            {
                button1.setEnabled(true);
            }
        }

        if(!TextUtils.isEmpty(parameters.getAppbarTitle())){
            ((ActivityBase) getActivity()).setCenterTitle(parameters.getAppbarTitle());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() != null){
            ((ActivityBase)getActivity()).hideKeyboard();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    //  Private method
    // ----
    protected void setViewHold(View view) {
        imgPhoto           = (ImageView    ) view.findViewById(R.id.img_photo);
        txtToSend          = (TextView     ) view.findViewById(R.id.txt_to_send);
        txtToSendCount     = (TextView     ) view.findViewById(R.id.txt_to_send_count);
        lytAmountArea      = (LinearLayout ) view.findViewById(R.id.lyt_amount_area);
        txtAmount          = (TextView     ) view.findViewById(R.id.txt_amount);

        txtReceivedMessage = (TextView     ) view.findViewById(R.id.txt_received_message);
        txtCount           = (TextView     ) view.findViewById(R.id.txt_count);
        txtMessage         = (EditText     ) view.findViewById(R.id.txt_message);

        button1            = (Button       ) view.findViewById(R.id.btn_action_1);
        if(parameters.isNeedInputMsg())
        {
            txtMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(txtMessage.getText().length() == 0 )
                    {
                        button1.setEnabled(false);
                    }
                    else if(txtMessage.getText().length() > 0 )
                    {
                        button1.setEnabled(true);
                    }
                }
            });

        }

        // 20160322 加入下列兩行，搭配layout xml的修改
        // 讓虛擬鍵盤的enter可顯示為Done，且不會變成singleline
        txtMessage.setHorizontallyScrolling(false);
        txtMessage.setMaxLines(Integer.MAX_VALUE);
    }

    protected void setViewContent() {
        txtToSend.setText(ContactUtil.concatNames(parameters.getListToSend()));
        txtToSendCount.setText(ContactUtil.getNamesNumberString(parameters.getListToSend()));

        if(parameters.getListToSend() != null && parameters.getListToSend().size() > 0) {
            if (parameters.getListToSend().size() > 1) {
                imgPhoto.setImageResource(R.drawable.img_taishin_photo_dark);
            } else {
                // 設定頭像
                ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
                imageLoader.loadImage(parameters.getListToSend().get(0).getMemNO(), imgPhoto);
            }
        }

        if (!TextUtils.isEmpty(parameters.getAmount())) {
            lytAmountArea.setVisibility(View.VISIBLE);
            String formatAmount = FormatUtil.toDecimalFormatFromString(parameters.getAmount(), true);
            txtAmount.setText(formatAmount);
            txtReceivedMessage.setText(parameters.getReceivedMessage());
        } else {
            lytAmountArea.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(parameters.getButton1Text())) {
            button1.setText(parameters.getButton1Text());
        }

        if(!TextUtils.isEmpty(parameters.getInputHint())){
            txtMessage.setHint(parameters.getInputHint());
        }

        if(!TextUtils.isEmpty(parameters.getMsgInputBefore())){
            txtMessage.setText(parameters.getMsgInputBefore());
        }
    }

    protected void setListener() {
        // 偵測文字變更
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String countShow;
                // 根據輸入的字串更新輸入字數與按鈕狀態
                if (TextUtils.isEmpty(s)) {
                    countShow = String.format(numberOfChars, 0, lengthLimit);
                } else {
                    countShow = String.format(numberOfChars, s.length(), lengthLimit);
                }
                txtCount.setText(countShow);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        txtMessage.addTextChangedListener(textWatcher);
        InputFilterUtil.setFilter(txtMessage, new InputFilterUtil.ENUM_FILTER_TYPE[]{InputFilterUtil.ENUM_FILTER_TYPE.FILTER_WRAP, InputFilterUtil.ENUM_FILTER_TYPE.FILTER_SPACE});

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonsClickListener != null)
                    buttonsClickListener.onButton1Click(txtMessage.getText().toString());
            }
        });

    }

    public void setButtonsClickListener(ButtonsClickListener buttonsClickListener) {
        this.buttonsClickListener = buttonsClickListener;
    }

    // ----
    // Interface, inner class
    // ----
    public interface ButtonsClickListener {
        void onButton1Click(String inputMessage);
    }


    /**
     * 建立此Fragment所使用的參數Object
     */
    public static class Parameters implements Parcelable{
        private ArrayList<LocalContact> listToSend; // required
        private String button1Text;
        private String amount;
        private String receivedMessage;
        private String inputHint;
        private String msgInputBefore;
        private boolean isNeedInputMsg = false;
        private String appbarTitle = null;

        public Parameters(ArrayList<LocalContact> listToSend) {
            this.listToSend = listToSend;
        }

        protected Parameters(Parcel in) {
            listToSend = in.createTypedArrayList(LocalContact.CREATOR);
            button1Text = in.readString();
            amount = in.readString();
            receivedMessage = in.readString();
            inputHint = in.readString();
            msgInputBefore = in.readString();
            isNeedInputMsg = in.readByte() != 0;
            appbarTitle = in.readString();
        }

        public static final Creator<Parameters> CREATOR = new Creator<Parameters>() {
            @Override
            public Parameters createFromParcel(Parcel in) {
                return new Parameters(in);
            }

            @Override
            public Parameters[] newArray(int size) {
                return new Parameters[size];
            }
        };

        public ArrayList<LocalContact> getListToSend() {
            return listToSend;
        }

        public String getAmount() {
            return amount;
        }

        public String getReceivedMessage() {
            return receivedMessage;
        }

        public String getButton1Text() {
            return button1Text;
        }

        public String getInputHint() {
            return inputHint;
        }

        public String getMsgInputBefore() {
            return msgInputBefore;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setReceivedMessage(String receivedMessage) {
            this.receivedMessage = receivedMessage;
        }

        public void setInputHint(String inputHint) {
            this.inputHint = inputHint;
        }

        public void setMsgInputBefore(String msgInputBefore) {
            this.msgInputBefore = msgInputBefore;
        }

        public boolean isNeedInputMsg() {
            return isNeedInputMsg;
        }

        public void setIsNeedInputMsg(boolean isNeedMsg) {
            this.isNeedInputMsg = isNeedMsg;
        }

        public void setButton1Text(String button1Text) {
            this.button1Text = button1Text;
        }

        public String getAppbarTitle() {
            return appbarTitle;
        }

        public void setAppbarTitle(String appbarTitle) {
            this.appbarTitle = appbarTitle;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(listToSend);
            dest.writeString(button1Text);
            dest.writeString(amount);
            dest.writeString(receivedMessage);
            dest.writeString(inputHint);
            dest.writeString(msgInputBefore);
            dest.writeByte((byte) (isNeedInputMsg ? 1 : 0));
            dest.writeString(appbarTitle);
        }
    }
}
