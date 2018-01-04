package tw.com.taishinbank.ewallet.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.wallethome.MessageCenterFragment;
import tw.com.taishinbank.ewallet.model.LocalContact;


/**
 *
 */
public class FriendHistoryActivity extends ActivityBase {

    public static final String EXTRA_FRIEND = "EXTRA_FRIEND";
    private LocalContact friendContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_friend_flow);
        setCenterTitle(R.string.sv_receive_request_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra(EXTRA_FRIEND))
        {
            friendContact = getIntent().getParcelableExtra(EXTRA_FRIEND);
        }

        //Set Fragment
        MessageCenterFragment fragment = new MessageCenterFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_FRIEND, friendContact);
        bundle.putBoolean(MessageCenterFragment.EXTRA_FRIEND_MODE, true);
        fragment.setArguments(bundle);

        //Start Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.tabcontent, fragment);
        ft.commit();
    }
}
