package com.unicom.autoship.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unicom.autoship.App;
import com.unicom.autoship.MainActivity;
import com.unicom.autoship.R;
import com.unicom.autoship.activity.LoginActivity;
import com.unicom.autoship.common.BaseFragment;
import com.unicom.autoship.ui.RatioImageView;
import com.unicom.autoship.ui.dialoglikeios.DialogClickListener;
import com.unicom.autoship.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MinePageFragment extends BaseFragment {
    public static final String TAG = "MINE_FRAGMENT_TAG";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.rimg_banner)
    RatioImageView rimgBanner;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.iv_my_mall)
    ImageView ivMyMall;
    @BindView(R.id.tv_my_mall)
    TextView tvMyMall;
    @BindView(R.id.rl_my_mall)
    RelativeLayout rlMyMall;
    @BindView(R.id.iv_tag01)
    ImageView ivTag01;
    @BindView(R.id.tv01)
    TextView tv01;
    @BindView(R.id.iv_arrow01)
    ImageView ivArrow01;
    @BindView(R.id.rl01)
    RelativeLayout rl01;
    @BindView(R.id.iv_tag02)
    ImageView ivTag02;
    @BindView(R.id.tv02)
    TextView tv02;
    @BindView(R.id.iv_arrow02)
    ImageView ivArrow02;
    @BindView(R.id.rl02)
    RelativeLayout rl02;
    @BindView(R.id.iv_tag03)
    ImageView ivTag03;
    @BindView(R.id.tv03)
    TextView tv03;
    @BindView(R.id.iv_arrow03)
    ImageView ivArrow03;
    @BindView(R.id.rl03)
    RelativeLayout rl03;
    @BindView(R.id.iv_tag04)
    ImageView ivTag04;
    @BindView(R.id.tv04)
    TextView tv04;
    @BindView(R.id.iv_arrow04)
    ImageView ivArrow04;
    @BindView(R.id.rl04)
    RelativeLayout rl04;
    @BindView(R.id.tv_quit)
    TextView tvQuit;
    @BindView(R.id.rl_quit)
    RelativeLayout rlQuit;
    Unbinder unbinder;

    @Override
    public int rootViewRes() {
        return 0;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MinePageFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MinePageFragment newInstance()//(String param1, String param2)
    {
        MinePageFragment fragment = new MinePageFragment();
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_mine_page, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.rl01, R.id.rl02, R.id.rl03, R.id.rl04,R.id.rl_quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl01:
                break;
            case R.id.rl02:
                break;
            case R.id.rl03:
                break;
            case R.id.rl04:
                break;
            case R.id.rl_quit:
                showIosChoiceDialog("确认退出应用？","取消","确定",new DialogClickListener(){
                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                        App.isOnline=false;
                        SharedPreferences.Editor editor = getDefaultPref().edit();
                        editor.putString("accessToken", "");
                        editor.putLong("expireTime", 0);
                        editor.putString("token","");
                        editor.putString("refreshToken","");
                        editor.putString("account","");
                        editor.putString("pswd","");
                        editor.putBoolean("isOnline", false);
                        editor.commit();
                        //EZOpenSDK.getInstance().logout();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                });
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
