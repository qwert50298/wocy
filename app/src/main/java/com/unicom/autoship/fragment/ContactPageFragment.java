package com.unicom.autoship.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unicom.autoship.bean.ContactInfo;
import com.unicom.autoship.utils.ContactHelper;
import com.unicom.autoship.view.SearchEditText;
import com.unicom.autoship.adapter.ContactAdapter;

import com.unicom.autoship.R;
import com.unicom.autoship.utils.StatusBarUtil;

import java.util.List;


public class ContactPageFragment extends Fragment {
    public static final String TAG = "CONTACT_FRAGMENT_TAG";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static boolean DEBUG = false;

    private ListView mContactsLv;
    private ContactAdapter mContactAdapter;
    private SearchEditText mSearchEt;
    private List<ContactInfo> mContactInfoList;
    private TextView mLetterDialogTv;

    public ContactPageFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ContactPageFragment newInstance()//(String param1, String param2)
    {
        ContactPageFragment fragment = new ContactPageFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_page, container, false);
        mSearchEt = (SearchEditText) view.findViewById(R.id.et_contacts_search);
        mLetterDialogTv = (TextView) view.findViewById(R.id.tv_letter_dialog);
        mContactsLv = (ListView) view.findViewById(R.id.lv_contacts);

        initEvents();
        initContactAdapter();

        // Inflate the layout for this fragment
        return view ;
    }

    @Override
    public void onResume() {
        super.onResume();
        //StatusBarUtil.setColorNoTranslucent(getActivity(),getResources().getColor(R.color.colorPrimary));
    }

    private void initEvents() {
        // 设置联系人列表的点击事件监听
        mContactsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo contactInfo = (ContactInfo) mContactAdapter.getItem(position);
                Toast.makeText(getActivity(), contactInfo.getRawName(), Toast.LENGTH_SHORT).show();
            }
        });


        // 设置搜索框的文本内容改变事件监听
        mSearchEt.addTextChangedListener(new SearchEditText.MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<ContactInfo> mFilterList = ContactHelper.contactsFilter(s.toString(), mContactInfoList);
                mContactAdapter.updateContactInfoList(mFilterList); // update
                if (DEBUG) {
                    Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initContactAdapter() {
        String[] contacts = getResources().getStringArray(R.array.test);
        mContactInfoList = ContactHelper.setupContactInfoList(contacts);


        // 设置联系人列表的信息
        mContactAdapter = new ContactAdapter(getActivity(), mContactInfoList);
        mContactsLv.setAdapter(mContactAdapter);
    }


}
