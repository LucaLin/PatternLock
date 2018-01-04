package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.view.TextViewWithUnderLine;

public class ResultFragmentBase extends Fragment {

    // -- View Hold --
    protected TextView        txtResultTitle           ;

    protected TextView        txtDate                  ;
    protected ImageView       imgPhoto                 ;

    protected LinearLayout    lytNameArea             ;
    protected TextView        txtFromToWho            ;
    protected TextView        txtFromToWhoCount        ;
    protected LinearLayout    lytMoneyArea             ;
    protected TextView        txtDollarSign            ;
    protected TextView        txtAmount                ;

    protected FrameLayout     lytSvResult;
    protected ImageView       imgSvResult              ;

    protected FrameLayout     lytSendingMsg            ;
    protected TextView        txtSendingMessage        ;
    protected TextViewWithUnderLine txtSendingMessageLine;

    protected LinearLayout    lytSvResultActionArea   ;
    protected Button          btnSvResultAction       ;

    protected LinearLayout    lytCautionArea          ;
    protected TextView        txtCautionTitle         ;
    protected TextView        txtCautionContent       ;

    protected Button          btnAction1              ;
    protected Button          btnAction2              ;

    protected TextView        txtErrorMessage         ;



    public ResultFragmentBase() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_result, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
    // Self method
    // ----
    protected void setViewHold(View view) {
        txtResultTitle        = (TextView    ) view.findViewById(R.id.txt_result_title          );

        txtDate               = (TextView    ) view.findViewById(R.id.txt_date                  );
        imgPhoto              = (ImageView   ) view.findViewById(R.id.img_photo                 );
        lytNameArea           = (LinearLayout) view.findViewById(R.id.lyt_name_area            );
        txtFromToWho          = (TextView    ) view.findViewById(R.id.txt_from_to_who           );
        txtFromToWhoCount     = (TextView    ) view.findViewById(R.id.txt_from_to_who_count     );
        lytMoneyArea          = (LinearLayout) view.findViewById(R.id.lyt_money_area            );
        txtDollarSign         = (TextView    ) view.findViewById(R.id.txt_dollar_sign           );
        txtAmount             = (TextView    ) view.findViewById(R.id.txt_amount                );

        lytSvResult           = (FrameLayout ) view.findViewById(R.id.lyt_sv_result);
        imgSvResult           = (ImageView   ) view.findViewById(R.id.img_sv_result             );

        lytSendingMsg         = (FrameLayout ) view.findViewById(R.id.lyt_sending_msg);
        txtSendingMessageLine = (TextViewWithUnderLine) view.findViewById(R.id.txt_sending_msg_line);
        txtSendingMessage     = (TextView    ) view.findViewById(R.id.txt_sending_msg);

        lytSvResultActionArea = (LinearLayout) view.findViewById(R.id.lyt_sv_result_action_area );
        btnSvResultAction     = (Button      ) view.findViewById(R.id.btn_sv_result_action      );

        lytCautionArea        = (LinearLayout) view.findViewById(R.id.lyt_caution_area          );
        txtCautionTitle       = (TextView    ) view.findViewById(R.id.txt_caution_title         );
        txtCautionContent     = (TextView    ) view.findViewById(R.id.txt_caution_content       );

        btnAction1            = (Button      ) view.findViewById(R.id.btn_action_1              );
        btnAction2            = (Button      ) view.findViewById(R.id.btn_action_2              );

        txtErrorMessage     = (TextView    ) view.findViewById(R.id.txt_error_message       );
    }

}
