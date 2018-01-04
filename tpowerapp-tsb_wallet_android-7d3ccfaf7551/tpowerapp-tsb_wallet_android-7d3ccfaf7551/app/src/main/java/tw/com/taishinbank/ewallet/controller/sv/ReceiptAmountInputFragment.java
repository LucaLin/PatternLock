package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class ReceiptAmountInputFragment extends AmountInputFragmentBase {

    private static final String ARG_TITLE_RES_ID = "arg_title_res_id";
    private static final String ARG_FRIEND_LIST = "arg_friend_list";
    private int titleResId;
    private List<LocalContact> friendList;

    /**
     * 用來建立Fragment
     */
    public static ReceiptAmountInputFragment newInstance(int titleResId, ArrayList<LocalContact> contactList) {
        ReceiptAmountInputFragment f = new ReceiptAmountInputFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_RES_ID, titleResId);
        args.putParcelableArrayList(ARG_FRIEND_LIST, contactList);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            titleResId = getArguments().getInt(ARG_TITLE_RES_ID);
            friendList = getArguments().getParcelableArrayList(ARG_FRIEND_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // 其他顯示頭像跟人名
        layoutAccountInfo.setVisibility(View.VISIBLE);
        layoutBankAccountInfo.setVisibility(View.GONE);

        if (friendList != null && !friendList.isEmpty()) {
            if (friendList.size() > 1) {
                imagePhoto.setImageResource(R.drawable.img_taishin_photo_dark);
            } else {
                // 設定頭像
                ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
                imageLoader.loadImage(friendList.get(0).getMemNO(), imagePhoto);
            }
            // 設定接收者的顯示名稱
            textNames.setText(ContactUtil.concatNames(friendList));
            textNamesNumber.setText(ContactUtil.getNamesNumberString(friendList));

            // 金額限制：人數x 單筆交易上限10000（FORCE_MAX_SINGLE_LIMIT）
            maxAmountLimit = friendList.size() * GlobalConst.FORCE_MAX_SINGLE_LIMIT;
        }

        // 金額顯示：單筆交易上限10000（FORCE_MAX_SINGLE_LIMIT）
        String formattedAmount = FormatUtil.toDecimalFormat(GlobalConst.FORCE_MAX_SINGLE_LIMIT);
        textInfo.setText(String.format(getString(R.string.sv_amount_input_info_receipt), formattedAmount));

        inputLengthLimit = String.valueOf(maxAmountLimit).length();

        // 不需詳情，隱藏箭頭，文字不可點擊
        imageInfoArrow.setVisibility(View.GONE);
        layoutInfo.setOnClickListener(null);
        layoutInfo.setClickable(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((ActivityBase) getActivity()).setCenterTitle(titleResId);
    }
}
