package com.unicom.autoship.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.unicom.autoship.App;
import com.unicom.autoship.MainActivity;
import com.unicom.autoship.R;
import com.unicom.autoship.adapter.VideoRecordListAdapter;
import com.unicom.autoship.bean.BoatDeviceInfo;
import com.unicom.autoship.bean.PatrolHistory;
import com.unicom.autoship.bean.ShipBean;
import com.unicom.autoship.bean.ShipPatrolHistoryResp;
import com.unicom.autoship.bean.ShipRespBean;
import com.unicom.autoship.bean.ShipRespData;
import com.unicom.autoship.common.MarkerInfoUtil;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.ui.GridSpacingItemDecoration;
import com.unicom.autoship.ui.pullToRefresh.PullToRefreshLayout;
import com.unicom.autoship.utils.GsonRequest;
import com.unicom.autoship.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShipVideoRecordListActivity extends BaseActivity {

    @BindView(R.id.view_bar)
    View viewBar;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(R.id.empty)
    ImageView empty;
    @BindView(R.id.state_tv)
    TextView stateTv;
    @BindView(R.id.pull_icon)
    ImageView pullIcon;
    @BindView(R.id.refreshing_icon)
    ImageView refreshingIcon;
    @BindView(R.id.state_iv)
    ImageView stateIv;
    @BindView(R.id.head_view)
    RelativeLayout headView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loadstate_tv)
    TextView loadstateTv;
    @BindView(R.id.pullup_icon)
    ImageView pullupIcon;
    @BindView(R.id.loading_icon)
    ImageView loadingIcon;
    @BindView(R.id.loadstate_iv)
    ImageView loadstateIv;
    @BindView(R.id.loadmore_view)
    RelativeLayout loadmoreView;
    @BindView(R.id.prl)
    PullToRefreshLayout prl;
    private int page=1;
    private int total_page;
    private VideoRecordListAdapter mAdapter=null;
    private List<PatrolHistory> aList = new ArrayList<PatrolHistory>();

    @Override
    public int rootViewRes() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_video_record);
        ButterKnife.bind(this);

        initUi();
        boolean isNetworkEnable = Utils.isNetworkEnable(ShipVideoRecordListActivity.this);
        if (isNetworkEnable) {
            getShipVideoRecord(1,20);
        }else {
            Toast.makeText(ShipVideoRecordListActivity.this,"网络不可用",Toast.LENGTH_SHORT);
        }

    }

    public void initUi(){

        LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.VERTICAL);// 设置 recyclerview 布局方式为横向布局
        recyclerView.setLayoutManager(ms);

        /*DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int spanCount = 1; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));*/



        prl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page =1;
                boolean isNetworkEnable = Utils.isNetworkEnable(ShipVideoRecordListActivity.this);
                if (isNetworkEnable) {
                    getShipVideoRecord(1,20);
                }else {
                    Toast.makeText(ShipVideoRecordListActivity.this,"当前网络不可用，请检查网络配置！",Toast.LENGTH_SHORT);
                }
                prl.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

                boolean isNetworkEnable = Utils.isNetworkEnable(ShipVideoRecordListActivity.this);
                if (isNetworkEnable) {
                    page++;
                    getShipVideoRecord(page,20);
                }else {
                    Toast.makeText(ShipVideoRecordListActivity.this,"当前网络不可用，请检查网络配置！",Toast.LENGTH_SHORT);
                }
                prl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }

    @OnClick({R.id.ll_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    public void getShipVideoRecord(final int page,final int pageSize) {
        showProgressDialog("加载中...");
        Map<String, String> params = new HashMap<>();
        //params.put("token", App.token);
        params.put("page",page+"");
        params.put("pageSize",pageSize+"");

        String url = URL.GET_SHIP_VIDEO_RECORD+"?page="+page+"&pageSize="+pageSize;
        GsonRequest<ShipPatrolHistoryResp> request = new GsonRequest<ShipPatrolHistoryResp>(
                url,
                ShipPatrolHistoryResp.class,
                new Response.Listener<ShipPatrolHistoryResp>() {
                    @Override
                    public void onResponse(ShipPatrolHistoryResp response) {
                        Log.i("TAG","VideoRecordList----Response-----"+response.toString());

                        hideProgressDialog();

                        if(response.isSuccess()){

                            ShipPatrolHistoryResp.ShipPatrolData data = response.getData();
                            Log.i("TAG","页数："+page+"-------------当页条数："+data.getPatrolHistories().size());
                            if(null!=data){
                                if(page==1) {
                                    aList = data.getPatrolHistories();
                                    Log.i("TAG",page+"首次及刷新："+aList.size());
                                    mAdapter = new VideoRecordListAdapter(ShipVideoRecordListActivity.this, aList);
                                    recyclerView.setAdapter(mAdapter);
                                }else{
                                    aList.addAll(data.getPatrolHistories());
                                    mAdapter = new VideoRecordListAdapter(ShipVideoRecordListActivity.this, aList);
                                    recyclerView.setAdapter(mAdapter);
                                    recyclerView.scrollToPosition(aList.size()-1);
                                    //mAdapter.notifyDataSetChanged();
                                    Log.i("TAG",page+"下拉加载更多："+aList.size());
                                }
                                mAdapter.setOnItemClickListener(new VideoRecordListAdapter.OnRecyclerViewItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int section) {

                                        String deviceSerial="C23702836";
                                        String verifyCode="OPLYPV";
                                        String deviceChannel="1";
                                        List<BoatDeviceInfo> boatDeviceInfos=aList.get(section).getBoatDeviceInfo();
                                        if(null!=boatDeviceInfos){
                                            for(BoatDeviceInfo bean:boatDeviceInfos){
                                                if(null!=bean.getDeviceType()&&"camera".equals(bean.getDeviceType())){
                                                    deviceSerial=bean.getDeviceSerial();
                                                    verifyCode=bean.getVerifyCode();
                                                    deviceChannel=bean.getDeviceChannel();
                                                }
                                            }
                                        }
                                        Intent ii = new Intent(ShipVideoRecordListActivity.this,RecordDisplayActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("boatDeviceId",""+aList.get(section).getBoatDeviceId());
                                        bundle.putString("boatName",""+aList.get(section).getBoatName());
                                        bundle.putString("patrolStartTimeStr",""+aList.get(section).getPatrolStartTimeStr());
                                        bundle.putString("patrolEndTimeStr",""+aList.get(section).getPatrolEndTimeStr());
                                        bundle.putLong("patrolStartTime",aList.get(section).getPatrolStartTime());
                                        bundle.putLong("patrolEndTime",aList.get(section).getPatrolEndTime());
                                        bundle.putString("deviceSerial",""+deviceSerial);
                                        bundle.putString("verifyCode",""+verifyCode);
                                        bundle.putInt("deviceChannel",Integer.valueOf(deviceChannel));
                                        ii.putExtras(bundle);
                                        startActivity(ii);

                                    }
                                });
                            }

                        }else{
                            Toast.makeText(ShipVideoRecordListActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        ){
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> header = new HashMap<>();
                header.put("token", App.token);
                header.put("page",page+"");
                header.put("pageSize",pageSize+"");
                return  header;

            }
        };

        App.getInstance().addRequest(request);


    }
}
