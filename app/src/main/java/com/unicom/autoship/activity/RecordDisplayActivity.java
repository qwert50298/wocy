package com.unicom.autoship.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.unicom.autoship.App;
import com.unicom.autoship.bean.WayBean;
import com.videogo.constant.Config;
import com.videogo.constant.Constant;
import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.exception.InnerException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZConstants.EZPTZAction;
import com.videogo.openapi.EZConstants.EZPTZCommand;
import com.videogo.openapi.EZConstants.EZRealPlayConstants;
import com.videogo.openapi.EZConstants.EZVideoLevel;
import com.videogo.openapi.EZOpenSDKListener;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceRecordFile;
import com.videogo.realplay.RealPlayStatus;

import com.videogo.remoteplayback.RemoteFileInfo;
import com.videogo.remoteplayback.RemotePlayBackMsg;
import com.unicom.autoship.list.RemoteListContant;
import com.unicom.autoship.list.RemoteListUtil;
import com.unicom.autoship.ui.common.ScreenOrientationHelper;
import com.unicom.autoship.ui.util.ActivityUtils;
import com.unicom.autoship.ui.util.AudioPlayUtil;
import com.unicom.autoship.ui.util.DataManager;
import com.unicom.autoship.ui.util.EZUtils;
import com.unicom.autoship.ui.util.VerifyCodeInput;
import com.unicom.autoship.ui.remoteplayback.RemoteFileTimeBar;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LocalInfo;
import com.videogo.util.LogUtil;
import com.videogo.util.MediaScanner;
import com.videogo.util.RotateViewUtil;
import com.videogo.util.SDCardUtil;
import com.videogo.util.Utils;
import com.videogo.widget.CheckTextButton;
import com.videogo.widget.CustomRect;
import com.videogo.widget.CustomTouchListener;
import com.videogo.widget.TimeBarHorizontalScrollView;
import com.videogo.widget.TimeBarHorizontalScrollView.TimeScrollBarScrollListener;
import com.videogo.widget.TitleBar;



import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.unicom.autoship.App;
import com.unicom.autoship.R;
import com.unicom.autoship.adapter.VideoRecordListAdapter;
import com.unicom.autoship.bean.BoatDeviceInfo;
import com.unicom.autoship.bean.ShipPatrolHistoryResp;
import com.unicom.autoship.bean.ShipRouteResp;
import com.unicom.autoship.net.URL;
import com.unicom.autoship.utils.GsonRequest;
import com.unicom.autoship.widget.loading.LoadingView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordDisplayActivity extends BaseActivity implements OnClickListener, SurfaceHolder.Callback,
        Handler.Callback, TimeScrollBarScrollListener, VerifyCodeInput.VerifyCodeInputListener{

    private static final String TAG = "EZRemotePlayBackActivity";
    //UI消息
    public static final int MSG_PLAY_UI_UPDATE = 100;
    //搜索文件成功
    public static final int MSG_SEARCH_CLOUD_FILE_SUCCUSS = 101;
    //搜索文件失败
    public static final int MSG_SEARCH_CLOUD_FILE_FAIL = 102;

    public static final int ALARM_MAX_DURATION = 45;

    //播放状态
    /**
     * 初始状态
     */
    public static final int STATUS_INIT = 0;
    /**
     * 链接状态
     */
    public static final int STATUS_START = 1;
    /**
     * 停止状态
     */
    public static final int STATUS_STOP = 2;
    /**
     * 播放状态
     */
    public static final int STATUS_PLAY = 3;
    /**
     * 暂停状态
     */
    public static final int STATUS_PAUSE = 4;

    // 云存储录像
    private List<EZCloudRecordFile> mCloudFileList = null;
    // 设备本地录像
    //private List<RemoteFileInfo> mDeviceFileList = null;
    private Calendar mStartTime = null;
    private Calendar mEndTime = null;
    private Calendar mAlarmStartTime = null;
    private Calendar mAlarmStopTime = null;
    private Calendar mPlayStartTime = null;  // 实际的播放时间，这个时间应该是比alarmstarttime提前的

    private AudioPlayUtil mAudioPlayUtil = null;
    private LocalInfo mLocalInfo = null;
    private Handler mHandler = null;
    private EZPlayer mEZMediaPlayer = null;

    private float mRealRatio = Constant.LIVE_VIEW_RATIO;
    /**
     * 标识是否正在播放
     */
    private int mStatus = STATUS_INIT;
    private boolean mIsOnStop = false;
    /**
     * 屏幕当前方向
     */
    private int mOrientation = Configuration.ORIENTATION_PORTRAIT;
    /**
     * 存放上一次的流量
     */
    private long mStreamFlow = 0;
    private long mTotalStreamFlow = 0;
    private Rect mRemotePlayBackRect = null;

    private RelativeLayout mRemotePlayBackPageLy = null;

    private SurfaceView mRemotePlayBackSv = null;
    private SurfaceHolder mRemotePlayBackSh = null;
    private CustomTouchListener mRemotePlayBackTouchListener = null;
    // 播放比例
    private float mPlayScale = 1;

    private LinearLayout mRemotePlayBackLoadingLy = null;
    private LinearLayout mRemotePlayBackLoadingPbLy = null;
    private TextView mRemotePlayBackLoadingTv = null;
    private TextView mRemotePlayBackTipTv = null;
    private ImageButton mRemotePlayBackReplayBtn = null;
    private ImageButton mRemotePlayBackLoadingPlayBtn = null;

    private RelativeLayout mRemotePlayBackControlRl = null;
    private ImageButton mRemotePlayBackBtn = null;
    private ImageButton mRemotePlayBackSoundBtn = null;
    private TextView mRemotePlayBackFlowTv = null;
    private int mControlDisplaySec = 0;
    private long mPlayTime = 0;

    private LinearLayout mRemotePlayBackProgressLy = null;
    private TextView mRemotePlayBackBeginTimeTv = null;
    private TextView mRemotePlayBackEndTimeTv = null;
    private SeekBar mRemotePlayBackSeekBar = null;
    private ProgressBar mRemotePlayBackProgressBar = null;

    private RelativeLayout mRemotePlayBackCaptureRl = null;
    private RelativeLayout.LayoutParams mRemotePlayBackCaptureRlLp = null;
    private ImageView mRemotePlayBackCaptureIv = null;
    private ImageView mRemotePlayBackCaptureWatermarkIv = null;
    private int mCaptureDisplaySec = 0;
    private LinearLayout mRemotePlayBackRecordLy = null;
    private ImageView mRemotePlayBackRecordIv = null;
    private TextView mRemotePlayBackRecordTv = null;

    /**
     * 录像文件路径
     */
    private String mRecordFilePath = null;
    private String mRecordTime = null;
    /**
     * 录像时间
     */
    private int mRecordSecond = 0;

    private LinearLayout mRemotePlayBackOperateBar = null;
    private ImageButton mRemotePlayBackCaptureBtn = null;
    private ImageButton mRemotePlayBackRecordBtn = null;
    private ImageButton mRemotePlayBackRecordStartBtn = null;
    private View mRemotePlayBackRecordContainer = null;
    private RotateViewUtil mRecordRotateViewUtil = null;

    private ImageButton mRemotePlayBackSmallRecordBtn = null;
    private ImageButton mRemotePlayBackSmallRecordStartBtn = null;
    private View mRemotePlayBackSmallRecordContainer = null;
    private ImageButton mRemotePlayBackSmallCaptureBtn = null;

    private RelativeLayout mRemotePlayBackFullOperateBar = null;
    private ImageButton mRemotePlayBackFullPlayBtn = null;
    private ImageButton mRemotePlayBackFullSoundBtn = null;
    private ImageButton mRemotePlayBackFullCaptureBtn = null;
    private ImageButton mRemotePlayBackFullRecordBtn = null;
    private ImageButton mRemotePlayBackFullRecordStartBtn = null;
    private View mRemotePlayBackFullRecordContainer = null;

    private ImageButton mRemotePlayBackFullDownBtn = null;
    private LinearLayout mRemotePlayBackFullFlowLy = null;
    private TextView mRemotePlayBackFullRateTv = null;
    private TextView mRemotePlayBackFullFlowTv = null;
    private TextView mRemotePlayBackRatioTv = null;

    private RelativeLayout mRemotePlayBackTimeBarRl = null;
    private TimeBarHorizontalScrollView mRemotePlayBackTimeBar = null;
    private RemoteFileTimeBar mRemoteFileTimeBar = null;
    private TextView mRemotePlayBackTimeTv = null;

    /**
     * 监听锁屏解锁的事件
     */
    private ScreenBroadcastReceiver mScreenBroadcastReceiver = null;
    /**
     * 定时器
     */
    private Timer mUpdateTimer = null;
    /**
     * 定时器执行的任务
     */
    private TimerTask mUpdateTimerTask = null;

    private boolean mNeedAutoPlaySearchResult = false;
    private final static int USER_MESSAGE_PLAYBACK_BASE = 1;
    private final static int USER_MESSAGE_PLAYBACK_ONETIME = USER_MESSAGE_PLAYBACK_BASE + 1;

    // 全屏按钮
    private CheckTextButton mFullscreenButton;
    private CheckTextButton mFullscreenFullButton;
    private ScreenOrientationHelper mScreenOrientationHelper;
    private boolean bIsRecording = false;
   // private List<EZDeviceRecordFile> mEZDeviceFileList = null;
    private EZCloudRecordFile mAlarmRecordFile = null;
    //private EZDeviceRecordFile mAlarmRecordDeviceFile = null;

    //private EZAlarmInfo mEZAlarmInfo;
    private String mRecordFile;

    private String deviceSerial;
    private int cameraNo;
    private String verifyCode;
    private Calendar recordSrartTime;
    private Calendar recordEndTime;

    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;

    private int i;
    private int size;

    double mLatitude;
    double mLongitude;
    private List<WayBean> ways;

    private Timer timer;
    private TimerTask task;
    private int durationTime;

    @Override
    public int rootViewRes() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record_display);
        ButterKnife.bind(this);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initData();
        initViews();

    }

    @OnClick({R.id.ib_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                if (mStatus != STATUS_STOP) {
                    stopRemotePlayBack();
                }
                finish();
                break;
        }
    }

    public void initData(){

        Intent ii = this.getIntent();
        Bundle bundle = ii.getExtras();

        String boatDeviceId=bundle.getString("boatDeviceId");
        String boatName=bundle.getString("boatName");
        String patrolStartTimeStr=bundle.getString("patrolStartTimeStr");
        String patrolEndTimeStr=bundle.getString("patrolEndTimeStr");
        long patrolStartTime=bundle.getLong("patrolStartTime");
        long patrolEndTime=bundle.getLong("patrolEndTime");
        deviceSerial=bundle.getString("deviceSerial");
        verifyCode=bundle.getString("verifyCode");
        cameraNo=bundle.getInt("deviceChannel");

        boolean isNetworkEnable = com.unicom.autoship.utils.Utils.isNetworkEnable(RecordDisplayActivity.this);
        if (isNetworkEnable == true) {
            getShipRouteRecord(boatDeviceId,patrolStartTime,patrolEndTime);
        }else {
            Toast.makeText(RecordDisplayActivity.this,R.string.networkenable_fail,Toast.LENGTH_SHORT);
        }


        recordSrartTime = Calendar.getInstance();
        recordSrartTime.setTimeInMillis(patrolStartTime);

        recordEndTime = Calendar.getInstance();
        recordEndTime.setTimeInMillis(patrolEndTime);

        Log.v("获取时间","开始时间"+recordSrartTime+"\n结束时间"+recordEndTime);

        mAlarmStartTime = recordSrartTime;
        mAlarmStopTime = recordEndTime;
        durationTime = (int) (recordEndTime.getTimeInMillis() - recordSrartTime.getTimeInMillis())/1000;

        if (mAlarmStartTime != null) {
            mAlarmStartTime.add(Calendar.SECOND, 0/*-5*/);
            mAlarmStopTime = (Calendar) mAlarmStartTime.clone();
            mAlarmStopTime.add(Calendar.SECOND, durationTime);
        } else {
            mStartTime = Calendar.getInstance();
            mStartTime.set(Calendar.AM_PM, 0);
            mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH),
                    mStartTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            mEndTime = Calendar.getInstance();
            mEndTime.set(Calendar.AM_PM, 0);
            mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                    mEndTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        }
        // 获取本地信息
        Application application = (Application) getApplication();
        //mRemotePlayBackHelper = RemotePlayBackHelper.getInstance(application);
        mAudioPlayUtil = AudioPlayUtil.getInstance(application);
        // 获取配置信息操作对象
        mLocalInfo = LocalInfo.getInstance();
        // 获取屏幕参数
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        mLocalInfo.setNavigationBarHeight((int) Math.ceil(25 * getResources().getDisplayMetrics().density));

        mHandler = new Handler(this);
        mRecordRotateViewUtil = new RotateViewUtil();

        mScreenBroadcastReceiver = new ScreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenBroadcastReceiver, filter);

/*        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 333;
                mHandler.sendMessage(message);
            }
        };*/
    }

    //初始化界面
    private void initViews() {

        mMapView = (TextureMapView) findViewById(R.id.mmap);
        // 不显示缩放比例尺
        mMapView.showZoomControls(false);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        mBaiduMap = mMapView.getMap();
        // 改变地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(13).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // TODO Auto-generated method stub


                mMapView.setScaleControlPosition(new Point(250, 180));
            }
        });



        mRemotePlayBackPageLy = (RelativeLayout) findViewById(R.id.remoteplayback_page_ly);
        /** 测量状态栏高度 **/
        ViewTreeObserver viewTreeObserver = mRemotePlayBackPageLy.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mRemotePlayBackRect == null) {
                    // 获取状况栏高度
                    mRemotePlayBackRect = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(mRemotePlayBackRect);
                }
            }
        });
        mRemotePlayBackSv = (SurfaceView) findViewById(R.id.remoteplayback_sv);
        mRemotePlayBackSv.getHolder().addCallback(this);
        mRemotePlayBackTouchListener = new CustomTouchListener() {

            @Override
            public boolean canZoom(float scale) {
                if (mStatus == STATUS_PLAY) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean canDrag(int direction) {
                if (mPlayScale != 1) {
                    return true;
                }
                return false;
            }

            @Override
            public void onSingleClick() {
                onRemotePlayBackSvClick();
            }

            @Override
            public void onDoubleClick(MotionEvent e) {
            }

            @Override
            public void onZoom(float scale) {
            }

            @Override
            public void onDrag(int direction, float distance, float rate) {
                LogUtil.debugLog(TAG, "onDrag:" + direction);
            }

            @Override
            public void onEnd(int mode) {
                LogUtil.debugLog(TAG, "onEnd:" + mode);
            }

            @Override
            public void onZoomChange(float scale, CustomRect oRect, CustomRect curRect) {
                LogUtil.debugLog(TAG, "onZoomChange:" + scale);
                if (mStatus == STATUS_PLAY) {
                    if (scale > 1.0f && scale < 1.1f) {
                        scale = 1.1f;
                    }
                    setPlayScaleUI(scale, oRect, curRect);
                }
            }
        };
        mRemotePlayBackSv.setOnTouchListener(mRemotePlayBackTouchListener);

        mRemotePlayBackLoadingLy = (LinearLayout) findViewById(R.id.remoteplayback_loading_ly);
        mRemotePlayBackLoadingPbLy = (LinearLayout) findViewById(R.id.remoteplayback_loading_pb_ly);
        mRemotePlayBackLoadingTv = (TextView) findViewById(R.id.remoteplayback_loading_tv);
        mRemotePlayBackTipTv = (TextView) findViewById(R.id.remoteplayback_tip_tv);
        mRemotePlayBackReplayBtn = (ImageButton) findViewById(R.id.remoteplayback_replay_btn);
        mRemotePlayBackLoadingPlayBtn = (ImageButton) findViewById(R.id.remoteplayback_loading_play_btn);

        mRemotePlayBackControlRl = (RelativeLayout) findViewById(R.id.remoteplayback_control_rl);
        mRemotePlayBackBtn = (ImageButton) findViewById(R.id.remoteplayback_play_btn);
        mRemotePlayBackSoundBtn = (ImageButton) findViewById(R.id.remoteplayback_sound_btn);
        mRemotePlayBackFlowTv = (TextView) findViewById(R.id.remoteplayback_flow_tv);
        mRemotePlayBackFlowTv.setText("0k/s 0MB");

        mRemotePlayBackProgressLy = (LinearLayout) findViewById(R.id.remoteplayback_progress_ly);
        mRemotePlayBackBeginTimeTv = (TextView) findViewById(R.id.remoteplayback_begin_time_tv);
        mRemotePlayBackEndTimeTv = (TextView) findViewById(R.id.remoteplayback_end_time_tv);
        mRemotePlayBackSeekBar = (SeekBar) findViewById(R.id.remoteplayback_progress_seekbar);
        mRemotePlayBackProgressBar = (ProgressBar) findViewById(R.id.remoteplayback_progressbar);

        mRemotePlayBackCaptureRl = (RelativeLayout) findViewById(R.id.remoteplayback_capture_rl);
        mRemotePlayBackCaptureRlLp = (RelativeLayout.LayoutParams) mRemotePlayBackCaptureRl.getLayoutParams();
        mRemotePlayBackCaptureIv = (ImageView) findViewById(R.id.remoteplayback_capture_iv);
        mRemotePlayBackCaptureWatermarkIv = (ImageView) findViewById(R.id.remoteplayback_capture_watermark_iv);
        mRemotePlayBackRecordLy = (LinearLayout) findViewById(R.id.remoteplayback_record_ly);
        mRemotePlayBackRecordIv = (ImageView) findViewById(R.id.remoteplayback_record_iv);
        mRemotePlayBackRecordTv = (TextView) findViewById(R.id.remoteplayback_record_tv);

        mRemotePlayBackOperateBar = (LinearLayout) findViewById(R.id.remoteplayback_operate_bar);
        mRemotePlayBackCaptureBtn = (ImageButton) findViewById(R.id.remoteplayback_previously_btn);
        mRemotePlayBackRecordBtn = (ImageButton) findViewById(R.id.remoteplayback_video_btn);
        mRemotePlayBackRecordContainer = findViewById(R.id.remoteplayback_video_container);
        mRemotePlayBackRecordStartBtn = (ImageButton) findViewById(R.id.remoteplayback_video_start_btn);

        mRemotePlayBackSmallCaptureBtn = (ImageButton) findViewById(R.id.remoteplayback_small_previously_btn);
        mRemotePlayBackSmallRecordBtn = (ImageButton) findViewById(R.id.remoteplayback_small_video_btn);
        mRemotePlayBackSmallRecordContainer = findViewById(R.id.remoteplayback_small_video_container);
        mRemotePlayBackSmallRecordStartBtn = (ImageButton) findViewById(R.id.remoteplayback_small_video_start_btn);

        mRemotePlayBackFullOperateBar = (RelativeLayout) findViewById(R.id.remoteplayback_full_operate_bar);
        mRemotePlayBackFullPlayBtn = (ImageButton) findViewById(R.id.remoteplayback_full_play_btn);
        mRemotePlayBackFullSoundBtn = (ImageButton) findViewById(R.id.remoteplayback_full_sound_btn);
        mRemotePlayBackFullCaptureBtn = (ImageButton) findViewById(R.id.remoteplayback_full_previously_btn);
        mRemotePlayBackFullRecordBtn = (ImageButton) findViewById(R.id.remoteplayback_full_video_btn);
        mRemotePlayBackFullRecordContainer = findViewById(R.id.remoteplayback_full_video_container);
        mRemotePlayBackFullRecordStartBtn = (ImageButton) findViewById(R.id.remoteplayback_full_video_start_btn);
        mRemotePlayBackFullDownBtn = (ImageButton) findViewById(R.id.remoteplayback_full_down_btn);

        mRemotePlayBackFullFlowLy = (LinearLayout) findViewById(R.id.remoteplayback_full_flow_ly);
        mRemotePlayBackFullRateTv = (TextView) findViewById(R.id.remoteplayback_full_rate_tv);
        mRemotePlayBackFullFlowTv = (TextView) findViewById(R.id.remoteplayback_full_flow_tv);
        mRemotePlayBackRatioTv = (TextView) findViewById(R.id.remoteplayback_ratio_tv);
        mRemotePlayBackFullRateTv.setText("0k/s");
        mRemotePlayBackFullFlowTv.setText("0MB");

        mFullscreenButton = (CheckTextButton) findViewById(R.id.fullscreen_button);
        mFullscreenFullButton = (CheckTextButton) findViewById(R.id.fullscreen_full_button);

        mRemotePlayBackTimeBarRl = (RelativeLayout) findViewById(R.id.remoteplayback_timebar_rl);
        mRemotePlayBackTimeBar = (TimeBarHorizontalScrollView) findViewById(R.id.remoteplayback_timebar);
        mRemotePlayBackTimeBar.setTimeScrollBarScrollListener(this);
        mRemotePlayBackTimeBar.smoothScrollTo(0, 0);
        mRemoteFileTimeBar = (RemoteFileTimeBar) findViewById(R.id.remoteplayback_file_time_bar);
        mRemoteFileTimeBar.setX(0, mLocalInfo.getScreenWidth() * 6);
        mRemotePlayBackTimeTv = (TextView) findViewById(R.id.remoteplayback_time_tv);
        mRemotePlayBackTimeTv.setText("00:00:00");

        setRemotePlayBackSvLayout();

        if (mAlarmStartTime != null) {
            mRemotePlayBackTimeBarRl.setVisibility(View.GONE);
            mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            mRemotePlayBackProgressBar.setMax(durationTime);
            mRemotePlayBackProgressBar.setProgress(0);
            mRemotePlayBackSeekBar.setMax(durationTime);
            mRemotePlayBackSeekBar.setProgress(0);
            mRemotePlayBackSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                /**
                 * 拖动条停止拖动的时候调用
                 */
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    if (mStatus != STATUS_STOP) {
//                        stopRemotePlayBack();
                        stopUpdateTimer();
                        if (mEZMediaPlayer != null) {
                            stopRemotePlayBackRecord();
                        }
                    }
                    Calendar seletedTime = (Calendar) mPlayStartTime.clone();
                    seletedTime.add(Calendar.SECOND, progress);
                    mPlayTime = seletedTime.getTimeInMillis();
                    mRemotePlayBackProgressBar.setProgress(progress);
                    //startRemotePlayBack(seletedTime);
                    if (mEZMediaPlayer != null) {
                        mEZMediaPlayer.seekPlayback(seletedTime);
                    }
                }

                /**
                 * 拖动条开始拖动的时候调用
                 */
                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                /**
                 * 拖动条进度改变的时候调用
                 */
                @Override
                public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                }
            });
        } else {
            mRemotePlayBackTimeBarRl.setVisibility(View.VISIBLE);
        }

        mScreenOrientationHelper = new ScreenOrientationHelper(this, mFullscreenButton, mFullscreenFullButton);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }



    // 将时间戳转为字符串
    public static String getStrTime(long cc_time) {

        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        re_StrTime = sdf.format(new Date(cc_time));

        return re_StrTime;
    }

    public static String timeStamp2Date(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }



    public void getShipRouteRecord(String boatId,final long start,final long end) {
       // showProgressDialog("加载中...");
        /*Map<String, String> params = new HashMap<>();
        //params.put("token", App.token);
        params.put("page",page+"");
        params.put("pageSize",pageSize+"");*/

        String url = URL.GET_SHIP_ROUTE+"?boatId="+boatId+"&start="+start+"&end="+end;
        /*try {
            url = URLEncoder.encode(url, "UTF-8");
        }catch (Exception e){}*/
        GsonRequest<ShipRouteResp> request = new GsonRequest<ShipRouteResp>(
                url,
                ShipRouteResp.class,
                new Response.Listener<ShipRouteResp>() {
                    @Override
                    public void onResponse(ShipRouteResp response) {
                        Log.i("TAG","VideoRecordList----Response-----"+response.toString());

                        hideProgressDialog();

                        if(response.isSuccess()){
                            ShipRouteResp.RouteData data=response.getData();
                            ways = data.getWay();

                        }else{
                            Toast.makeText(RecordDisplayActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        )/*{
           *//* protected Map<String, String> getParams() throws AuthFailureError{
                super.getParams();
                Map<String, String> params = new HashMap<>();
                params.put("token", App.token);
                params.put("start",start+"");
                params.put("end",end+"");
                return params;
            }*//*

            *//*public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> header = new HashMap<>();
               *//**//* header.put("token", App.token);
                header.put("page",page+"");
                header.put("pageSize",pageSize+"");*//**//*
                return  header;

            }*//*
        }*/;

        App.getInstance().addRequest(request);


    }

    public void setMark(){
        if (ways != null && mStatus == STATUS_PLAY){
            Calendar osdTime = mEZMediaPlayer.getOSDTime();
            if(null==osdTime) return;
            Long tmpPlayTime = osdTime.getTimeInMillis();
            int waySize = ways.size();
            Log.v("setmark","start"+"共有"+waySize);
            for (int j = 0 ; j < waySize; j++){
                WayBean way = ways.get(j);
                Long tmpShipTime = way.getTimeStampLong();
                Calendar recordShipTime = Calendar.getInstance();
                recordShipTime.setTimeInMillis(tmpShipTime);
                Long shipTime = recordShipTime.getTimeInMillis();
                //LogUtil.verboseLog(TAG, "startTime:" + tmpPlayTime + " endTime:" + shipTime);
                if (tmpPlayTime - shipTime <1000 && tmpPlayTime - shipTime > -1000){
                    mLatitude = way.getLat();
                    mLongitude = way.getLon();
                    mark();
                    return;
                }
            }
        }
        Log.v("setmark","fail");

    }

    public void mark() {
        mBaiduMap.clear();
        LatLng point = new LatLng(mLatitude, mLongitude);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_geo);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        mBaiduMap.addOverlay(option);
        //设置经纬度（参数一是纬度，参数二是经度）
        MapStatusUpdate mapstatusupdate = MapStatusUpdateFactory.newLatLng(point);
        //对地图的中心点进行更新，
        mBaiduMap.setMapStatus(mapstatusupdate);
    }
    @Override
    public void onBackPressed() {

            //onExitCurrentPage();
            if (mStatus != STATUS_STOP) {
                stopRemotePlayBack();
            }
            finish();

    }


    private void showDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, mStartTime.get(Calendar.YEAR),
                mStartTime.get(Calendar.MONTH), mStartTime.get(Calendar.DAY_OF_MONTH));

        dpd.setCancelable(true);
        dpd.setTitle("选择日期");
        dpd.setCanceledOnTouchOutside(true);
        dpd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.certain),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dg, int which) {
                        DatePicker dp = null;
                        Field[] fields = dg.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (field.getName().equals("mDatePicker")) {
                                try {
                                    dp = (DatePicker) field.get(dg);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        dp.clearFocus();

                        if (mStatus != STATUS_STOP) {
                            stopRemotePlayBack();
                        }

                        mEZCloudFileList = null;
                        mCloudFileList = null;
                        mPlayTime = 0;
                        mStartTime = Calendar.getInstance();
                        mStartTime.set(Calendar.AM_PM, 0);
                        mStartTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 0, 0, 0);
                        mEndTime = Calendar.getInstance();
                        mEndTime.set(Calendar.AM_PM, 0);
                        mEndTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 23, 59, 59);
                        startRemotePlayBack(null);
                    }
                });
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtil.debugLog("Picker", "Cancel!");
                        if (!isFinishing()) {
                            dialog.dismiss();
                        }

                    }
                });

        dpd.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRemotePlayBackSv.setVisibility(View.VISIBLE);
        //initUI();
        if (mStatus == STATUS_INIT || mStatus == STATUS_PAUSE) {
            // 开始播放
            startRemotePlayBack(getTimeBarSeekTime());
        } else if (mIsOnStop) {
            if (mStatus != STATUS_STOP) {
                stopRemotePlayBack();
            }
            // 开始播放
            startRemotePlayBack(getTimeBarSeekTime());
        }

        mIsOnStop = false;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mScreenOrientationHelper.postOnStart();

        // On first entering, search the current day file list
        Message msg = Message.obtain();
        msg.what = USER_MESSAGE_PLAYBACK_ONETIME;
        mHandler.sendMessage(msg);
    }

    /**
     * 获取时间轴定位时间
     *
     * @see
     * @since V1.0
     */

    private Calendar getTimeBarSeekTime() {
        if (mAlarmStartTime != null) {
            int progress = mRemotePlayBackSeekBar.getProgress();
            Calendar seletedTime = (Calendar) mAlarmStartTime.clone();
            if (progress < durationTime) {
                seletedTime.add(Calendar.SECOND, progress);
            }
            return seletedTime;
        } else {
            return mRemoteFileTimeBar.pos2Calendar(mPlayTime == 0 ? 0 : mRemotePlayBackTimeBar.getScrollX(), mOrientation);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScreenOrientationHelper.postOnStop();
/*        if (mEZAlarmInfo == null) {
            return;
        }*/
        if (mStatus != STATUS_STOP) {
            mIsOnStop = true;
            stopRemotePlayBack();
            setRemotePlayBackStopUI();
        }
        mRemotePlayBackSv.setVisibility(View.INVISIBLE);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScreenBroadcastReceiver != null) {
            // 取消锁屏广播的注册
            unregisterReceiver(mScreenBroadcastReceiver);
        }

        if (mEZMediaPlayer != null) {
            mEZMediaPlayer.release();
        }
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (mStatus != STATUS_STOP) {
                    if (mStatus == STATUS_PLAY) {
                        pauseRemotePlayBack();
                    } else {
                        stopRemotePlayBack();
                    }
                    setRemotePlayBackStopUI();
                }
            }
        }
    }

/*    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mOrientation = newConfig.orientation;
        onOrientationChanged();
        super.onConfigurationChanged(newConfig);
    }*/

    private void updateSoundUI() {
        if (mLocalInfo.isSoundOpen()) {
            mRemotePlayBackSoundBtn.setBackgroundResource(R.drawable.remote_list_soundon_btn_selector);
            mRemotePlayBackFullSoundBtn.setBackgroundResource(R.drawable.play_full_soundon_btn_selector);
        } else {
            mRemotePlayBackSoundBtn.setBackgroundResource(R.drawable.remote_list_soundoff_btn_selector);
            mRemotePlayBackFullSoundBtn.setBackgroundResource(R.drawable.play_full_soundoff_btn_selector);
        }
    }

    private void updateTimeBarUI() {
        if (mAlarmStartTime != null) {
            if (mRemotePlayBackControlRl.getVisibility() == View.VISIBLE) {
                //RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mRemotePlayBackProgressLy.getLayoutParams();
                //lp.setMargins(0, 0, 0, Utils.dip2px(this, 40));
                //mRemotePlayBackProgressLy.setLayoutParams(lp);
            } else {
                //RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mRemotePlayBackProgressLy.getLayoutParams();
                //lp.setMargins(0, 0, 0, 0);
                //mRemotePlayBackProgressLy.setLayoutParams(lp);
            }
        } else {
            float pos = mRemoteFileTimeBar.getScrollPosByPlayTime(mPlayTime, mOrientation);
            mRemotePlayBackTimeBar.smoothScrollTo((int) pos, 0);
        }
    }

    private void updateOperatorUI() {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            // 显示状态栏
            fullScreen(false);
            mRemotePlayBackPageLy.setBackgroundColor(getResources().getColor(R.color.common_bg));
            mRemotePlayBackOperateBar.setVisibility(View.VISIBLE);
            mRemotePlayBackFullOperateBar.setVisibility(View.GONE);
            mRemotePlayBackControlRl.setVisibility(View.VISIBLE);
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                mRemotePlayBackProgressBar.setVisibility(View.GONE);
            }
            mRemotePlayBackSmallCaptureBtn.setVisibility(View.GONE);
            mRemotePlayBackSmallRecordContainer.setVisibility(View.GONE);
            if (mAlarmStartTime == null) {
                mRemoteFileTimeBar.setX(0, mLocalInfo.getScreenWidth() * 6);
                mRemotePlayBackTimeBarRl.setBackgroundColor(getResources().getColor(R.color.transparent));
                mRemotePlayBackTimeBarRl.setVisibility(View.VISIBLE);
            }
            if (mRecordFilePath != null) {
                mRemotePlayBackRecordBtn.setVisibility(View.GONE);
                mRemotePlayBackRecordStartBtn.setVisibility(View.VISIBLE);
            } else {
                mRemotePlayBackRecordBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackRecordStartBtn.setVisibility(View.GONE);
            }
        } else {
            mRemotePlayBackControlRl.setVisibility(View.GONE);
            // 隐藏状态栏
            fullScreen(true);
            mRemotePlayBackPageLy.setBackgroundColor(getResources().getColor(R.color.black_bg));
            mRemotePlayBackOperateBar.setVisibility(View.GONE);
            if (mAlarmStartTime == null) {
                mRemotePlayBackFullOperateBar.setVisibility(View.GONE);
                mRemoteFileTimeBar.setX(0, mLocalInfo.getScreenHeight() * 6);
                mRemotePlayBackTimeBarRl.setBackgroundColor(getResources().getColor(R.color.play_translucent_bg));
                mRemotePlayBackTimeBarRl.setVisibility(View.GONE);
                mRemotePlayBackFullDownBtn.setBackgroundResource(R.drawable.palyback_full_up);
                mRemotePlayBackFullOperateBar.setPadding(0, 0, 0, Utils.dip2px(this, 5));
            } else {
                mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                mRemotePlayBackProgressBar.setVisibility(View.GONE);
                mRemotePlayBackSmallCaptureBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackSmallRecordContainer.setVisibility(View.VISIBLE);
            }
            if (mRecordFilePath != null) {
                if (mAlarmStartTime != null) {
                    mRemotePlayBackSmallRecordBtn.setVisibility(View.GONE);
                    mRemotePlayBackSmallRecordStartBtn.setVisibility(View.VISIBLE);
                } else {
                    mRemotePlayBackFullRecordBtn.setVisibility(View.GONE);
                    mRemotePlayBackFullRecordStartBtn.setVisibility(View.VISIBLE);
                }
            } else {
                if (mAlarmStartTime != null) {
                    mRemotePlayBackSmallRecordBtn.setVisibility(View.VISIBLE);
                    mRemotePlayBackSmallRecordStartBtn.setVisibility(View.GONE);
                } else {
                    mRemotePlayBackFullRecordBtn.setVisibility(View.VISIBLE);
                    mRemotePlayBackFullRecordStartBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void onOrientationChanged() {
//        mRemotePlayBackSv.setVisibility(View.INVISIBLE);
        setRemotePlayBackSvLayout();
//        mRemotePlayBackSv.setVisibility(View.VISIBLE);

        updateOperatorUI();
        updateCaptureUI();
        updateTimeBarUI();

    }

    private void updateLoadingProgress(final int progress) {
        mRemotePlayBackLoadingTv.setText(progress + "%");
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Random r = new Random();
                mRemotePlayBackLoadingTv.setText((progress + r.nextInt(20)) + "%");
            }

        }, 500);
    }

    /* (non-Javadoc)
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
    @Override
    public boolean handleMessage(Message msg) {
        LogUtil.infoLog(TAG, "handleMessage:" + msg.what);
        switch (msg.what) {
            case 333:
                setMark();
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_PLAY_START:
                updateLoadingProgress(60);
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_CONNECTION_START:
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_CONNECTION_SUCCESS:
                updateLoadingProgress(80);
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_PLAY_FINISH:
                handlePlayFinish();
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_RATIO_CHANGED:
                if (msg.arg1 != 0) {
                    mRealRatio = (float) msg.arg2 / msg.arg1;
                }
                setRemotePlayBackSvLayout();
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_CONNECTION_EXCEPTION:
                handleConnectionException(msg.arg1);
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_PLAY_SUCCUSS:
                handlePlaySuccess(msg);
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_PLAY_FAIL:
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_SEARCH_FILE_FAIL:
                handlePlayFail(msg.arg1, msg.obj);
                break;
            case RemotePlayBackMsg.MSG_START_RECORD_SUCCESS:
                handleStartRecordSuccess((String) msg.obj);
                break;
            case RemotePlayBackMsg.MSG_START_RECORD_FAIL:
                Utils.showToast(this, R.string.remoteplayback_record_fail);
                break;
            case RemotePlayBackMsg.MSG_CAPTURE_PICTURE_SUCCESS:
                handleCapturePictureSuccess((String) msg.obj);
                break;
            case RemotePlayBackMsg.MSG_CAPTURE_PICTURE_FAIL:
                // 提示抓图失败
                Utils.showToast(this, R.string.remoteplayback_capture_fail);
                break;
            case MSG_SEARCH_CLOUD_FILE_SUCCUSS:
                updateLoadingProgress(20);
                //handleSearchCloudFileSuccess((Calendar)msg.obj);
                handleSearchEZCloudFileSuccess((Calendar) msg.obj);
                break;
            case MSG_SEARCH_CLOUD_FILE_FAIL:
                handleSearchCloudFileFail(msg.arg1);
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_SEARCH_FILE_SUCCUSS:
                updateLoadingProgress(40);
                //handleSearchDeviceFileSuccess();
//                handleSearchEZDeviceFileSuccess();
                break;
            case RemotePlayBackMsg.MSG_REMOTEPLAYBACK_SEARCH_NO_FILE:
                handleSearchNoFile();
                break;
            case MSG_PLAY_UI_UPDATE:
                updateRemotePlayBackUI();
                break;
            case USER_MESSAGE_PLAYBACK_ONETIME:
                // first entering activity, initiate a search and auto play back.
                break;
            default:
                break;
        }
        return false;
    }



    private Calendar getFileStartTime() {
        Calendar cloudStartTime = null;
        if (mCloudFileList != null && mCloudFileList.size() > 0) {
            cloudStartTime = mCloudFileList.get(0).getStartTime();
        }
        return cloudStartTime;
/*        Calendar deviceStartTime = null;
        if (mDeviceFileList != null && mDeviceFileList.size() > 0) {
            deviceStartTime = mDeviceFileList.get(0).getStartTime();
        }

        if (cloudStartTime != null && deviceStartTime != null) {
            return (cloudStartTime.getTimeInMillis() > deviceStartTime.getTimeInMillis()) ? deviceStartTime : cloudStartTime;
        } else if (cloudStartTime != null) {
            return cloudStartTime;
        } else {
            return deviceStartTime;
        }*/
    }

    private Calendar getFileStopTime() {
        Calendar cloudStopTime = null;
        if (mCloudFileList != null && mCloudFileList.size() > 0) {
            cloudStopTime = mCloudFileList.get(mCloudFileList.size() - 1).getStopTime();
        }
        return cloudStopTime;
/*        Calendar deviceStopTime = null;
        if (mDeviceFileList != null && mDeviceFileList.size() > 0) {
            deviceStopTime = mDeviceFileList.get(mDeviceFileList.size() - 1).getStopTime();
        }

        if (cloudStopTime != null && deviceStopTime != null) {
            return (cloudStopTime.getTimeInMillis() > deviceStopTime.getTimeInMillis()) ? cloudStopTime : deviceStopTime;
        } else if (cloudStopTime != null) {
            return cloudStopTime;
        } else {
            return deviceStopTime;
        }*/
    }

    private void handleSearchNoFile() {
        stopRemotePlayBack();

        if (mAlarmStartTime != null) {
            setRemotePlayBackFailUI(getString(R.string.remoteplayback_norecordfile_alarm));
        } else {
            setRemotePlayBackFailUI(getString(R.string.remoteplayback_norecordfile));
        }
    }

    private void handleSearchEZCloudFileSuccess(Calendar seletedTime) {
        if (mEZMediaPlayer == null) {
            mEZMediaPlayer = App.getOpenSDK().createPlayer(deviceSerial,cameraNo);
            if (mEZMediaPlayer == null)
                return;

            mEZMediaPlayer.setPlayVerifyCode(verifyCode);

            mEZMediaPlayer.setHandler(mHandler);
            mEZMediaPlayer.setSurfaceHold(mRemotePlayBackSh);
        }

        if (mAlarmStartTime != null) {
            if (mAlarmRecordFile != null) {
                mEZMediaPlayer.startPlayback(mAlarmRecordFile);
                mPlayStartTime = mAlarmRecordFile.getStartTime();
            }
        } else {
            EZCloudRecordFile ezCloudFile = null;
            if (mEZCloudFileList.size() > 0) {
                ezCloudFile = mEZCloudFileList.get(0);
            } else {
                Toast.makeText(this, "No record files found!", Toast.LENGTH_LONG).show();
                return;
            }

            if (ezCloudFile != null) {
                mEZMediaPlayer.startPlayback(ezCloudFile);
                mPlayStartTime = ezCloudFile.getStartTime();
            }
        }
    }



    private void handleSearchCloudFileSuccess(Calendar seletedTime) {

        if (mEZMediaPlayer == null) {
            mEZMediaPlayer = App.getOpenSDK().createPlayer(deviceSerial,cameraNo);
            if (mEZMediaPlayer == null)
                return;
            mEZMediaPlayer.setPlayVerifyCode(verifyCode);

            mEZMediaPlayer.setHandler(mHandler);
            mEZMediaPlayer.setSurfaceHold(mRemotePlayBackSh);
        }
    }

    // 搜索文件异常处理
    private void handleSearchCloudFileFail(int errorCode) {
        LogUtil.debugLog(TAG, "handleSearchFileFail:" + errorCode);

        stopRemotePlayBack();

        String txt = null;
        // 判断返回的错误码
        switch (errorCode) {
            case ErrorCode.ERROR_WEB_SESSION_ERROR:
            case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
            case ErrorCode.ERROR_CAS_VERIFY_SESSION_ERROR:
                ActivityUtils.goToLoginAgain(RecordDisplayActivity.this);
                return;
            default:
                txt = Utils.getErrorTip(this, R.string.remoteplayback_searchfile_fail_for_device, errorCode);
                //startRemotePlayBack(getTimeBarSeekTime());
                break;
        }

        setRemotePlayBackFailUI(txt);
    }

    private void handleConnectionException(int errorCode) {
        LogUtil.debugLog(TAG, "handleConnectionException:" + errorCode);
        Calendar startTime = Calendar.getInstance();
        Toast.makeText(this, "network connection exception, will restart playback", Toast.LENGTH_SHORT).show();

        Calendar seekTime = getTimeBarSeekTime();
        if (seekTime == null) {
            handlePlayFail(errorCode, null);
            return;
        }

        if (mPlayTime == 0) {
            Calendar osdTime = mEZMediaPlayer.getOSDTime();
            if (osdTime != null) {
                mPlayTime = osdTime.getTimeInMillis() + 5000;
            } else {
                mPlayTime = seekTime.getTimeInMillis() + 5000;
            }
        } else {
            mPlayTime = mPlayTime + 5000;
        }

        startTime.setTimeInMillis(mPlayTime);
        LogUtil.debugLog(TAG, "handleConnectionException replay:" + startTime.toString());
        stopRemotePlayBack();
        startRemotePlayBack(startTime);
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mEZMediaPlayer != null) {
            mEZMediaPlayer.setSurfaceHold(holder);
        }
        mRemotePlayBackSh = holder;
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mEZMediaPlayer != null) {
            mEZMediaPlayer.setSurfaceHold(null);
        }
        mRemotePlayBackSh = null;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remoteplayback_loading_play_btn:
                startRemotePlayBack(getTimeBarSeekTime());
                break;
            case R.id.remoteplayback_play_btn:
            case R.id.remoteplayback_full_play_btn:
                if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
                    if (mStatus == STATUS_PLAY) {
                        pauseRemotePlayBack();
                    } else {
                        stopRemotePlayBack();
                    }
                    setRemotePlayBackStopUI();
                } else {
                    startRemotePlayBack(getTimeBarSeekTime());
                }
                break;
            case R.id.remoteplayback_replay_btn:
                if (mStatus != STATUS_STOP) {
                    stopRemotePlayBack();
                }
                startRemotePlayBack(null);
                break;
            case R.id.remoteplayback_sound_btn:
            case R.id.remoteplayback_full_sound_btn:
                onSoundBtnClick();
                break;
            case R.id.remoteplayback_previously_btn:
            case R.id.remoteplayback_full_previously_btn:
            case R.id.remoteplayback_small_previously_btn:
                onCapturePicBtnClick();
                break;
            case R.id.remoteplayback_capture_rl:
                onCaptureRlClick();
                break;
            case R.id.remoteplayback_video_btn:
            case R.id.remoteplayback_full_video_btn:
            case R.id.remoteplayback_small_video_btn:
            case R.id.remoteplayback_video_start_btn:
            case R.id.remoteplayback_full_video_start_btn:
            case R.id.remoteplayback_small_video_start_btn:
                onRecordBtnClick();
                break;
            case R.id.remoteplayback_full_down_btn:
                onTimeBarDownBtnClick();
                break;
            default:
                break;
        }
    }

    private void onTimeBarDownBtnClick() {
        if (mRemotePlayBackTimeBarRl.getVisibility() == View.VISIBLE) {
            mRemotePlayBackTimeBarRl.setVisibility(View.GONE);
            mRemotePlayBackFullDownBtn.setBackgroundResource(R.drawable.palyback_full_up);
            mRemotePlayBackFullOperateBar.setPadding(0, 0, 0, Utils.dip2px(this, 5));
        } else {
            mRemotePlayBackTimeBarRl.setVisibility(View.VISIBLE);
            mRemotePlayBackFullDownBtn.setBackgroundResource(R.drawable.palyback_full_down);
            mRemotePlayBackFullOperateBar.setPadding(0, 0, 0, Utils.dip2px(this, 92));
        }
        updateCaptureUI();
    }

    /**
     * 开始录像
     *
     * @see
     * @since V1.0
     */
    private void onRecordBtnClick() {
        mControlDisplaySec = 0;

        if (bIsRecording) {
            stopRemotePlayBackRecord();
            bIsRecording = !bIsRecording;
            return;
        }

        if (!SDCardUtil.isSDCardUseable()) {
            // 提示SD卡不可用
            Utils.showToast(this, R.string.remoteplayback_SDCard_disable_use);
            return;
        }

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            Utils.showToast(this, R.string.remoteplayback_record_fail_for_memory);
            return;
        }

        if (mEZMediaPlayer != null) {
            mCaptureDisplaySec = 4;
            updateCaptureUI();
            mAudioPlayUtil.playAudioFile(AudioPlayUtil.RECORD_SOUND);
            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
            java.util.Date date = new java.util.Date();
            String strRecordFile = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/Records/" + String.format("%tY", date)
                    + String.format("%tm", date) + String.format("%td", date) + "/"
                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) + ".mp4";
            if (mEZMediaPlayer.startLocalRecordWithFile(strRecordFile))
            {
                bIsRecording = !bIsRecording;
                handleStartRecordSuccess(strRecordFile);
            } else {
                bIsRecording = !bIsRecording;
                handleRecordFail();
            }
        }
    }

    /**
     * 停止录像
     *
     * @see
     * @since V1.0
     */
    private void stopRemotePlayBackRecord() {
        if (mRecordFilePath == null) {
            return;
        }
        if (!bIsRecording) {
            return;
        }
        // 设置录像按钮为check状态
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (!mIsOnStop) {
                mRecordRotateViewUtil.applyRotation(mRemotePlayBackRecordContainer, mRemotePlayBackRecordStartBtn,
                        mRemotePlayBackRecordBtn, 0, 90);
            } else {
                mRemotePlayBackRecordBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackRecordStartBtn.setVisibility(View.GONE);
            }
            if (mAlarmStartTime != null) {
                mRemotePlayBackSmallRecordBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackSmallRecordStartBtn.setVisibility(View.GONE);
            } else {
                mRemotePlayBackFullRecordBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackFullRecordStartBtn.setVisibility(View.GONE);
            }
        } else {
            if (mAlarmStartTime != null) {
                if (!mIsOnStop) {
                    mRecordRotateViewUtil.applyRotation(mRemotePlayBackSmallRecordContainer,
                            mRemotePlayBackSmallRecordStartBtn, mRemotePlayBackSmallRecordBtn, 0, 90);
                } else {
                    mRemotePlayBackSmallRecordBtn.setVisibility(View.VISIBLE);
                    mRemotePlayBackSmallRecordStartBtn.setVisibility(View.GONE);
                }
                mRemotePlayBackSmallRecordBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackSmallRecordStartBtn.setVisibility(View.GONE);
            } else {
                if (!mIsOnStop) {
                    mRecordRotateViewUtil.applyRotation(mRemotePlayBackFullRecordContainer,
                            mRemotePlayBackFullRecordStartBtn, mRemotePlayBackFullRecordBtn, 0, 90);
                } else {
                    mRemotePlayBackFullRecordBtn.setVisibility(View.VISIBLE);
                    mRemotePlayBackFullRecordStartBtn.setVisibility(View.GONE);
                }
                mRemotePlayBackRecordBtn.setVisibility(View.VISIBLE);
                mRemotePlayBackRecordStartBtn.setVisibility(View.GONE);
            }
        }

        mAudioPlayUtil.playAudioFile(AudioPlayUtil.RECORD_SOUND);
        mEZMediaPlayer.stopLocalRecord();

        // 计时按钮不可见
        mRemotePlayBackRecordLy.setVisibility(View.GONE);
//        mRemotePlayBackCaptureRl.setVisibility(View.VISIBLE);
        mCaptureDisplaySec = 0;
        try {
            mRemotePlayBackCaptureIv.setImageURI(Uri.parse(mRecordFilePath));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        mRemotePlayBackCaptureWatermarkIv.setTag(mRecordFilePath);
        mRecordFilePath = null;
        updateCaptureUI();
    }

    /**
     * 进入图像管理页面
     *
     * @see
     * @since V2.0
     */
    private void onCaptureRlClick() {
        mRemotePlayBackCaptureRl.setVisibility(View.GONE);
        mRemotePlayBackCaptureIv.setImageURI(null);
        mRemotePlayBackCaptureWatermarkIv.setTag(null);
        mRemotePlayBackCaptureWatermarkIv.setVisibility(View.GONE);
    }

    /**
     * <p>
     * 声音控制
     * </p>
     *
     * @author hanlieng 2014-8-5 下午6:57:41
     */
    private void onSoundBtnClick() {
        if (mLocalInfo.isSoundOpen()) {
            mLocalInfo.setSoundOpen(false);
            mRemotePlayBackSoundBtn.setBackgroundResource(R.drawable.remote_list_soundoff_btn_selector);
            mRemotePlayBackFullSoundBtn.setBackgroundResource(R.drawable.play_full_soundoff_btn_selector);
        } else {
            mLocalInfo.setSoundOpen(true);
            mRemotePlayBackSoundBtn.setBackgroundResource(R.drawable.remote_list_soundon_btn_selector);
            mRemotePlayBackFullSoundBtn.setBackgroundResource(R.drawable.play_full_soundon_btn_selector);
        }

        setRemotePlaySound();
    }

    private void setRemotePlaySound() {
        if (mEZMediaPlayer != null) {
            if (mLocalInfo.isSoundOpen()) {
                mEZMediaPlayer.openSound();
            } else {
                mEZMediaPlayer.closeSound();
            }
        }
    }

    /**
     * 抓拍按钮响应函数
     *
     * @since V1.0
     */
    private void onCapturePicBtnClick() {
        mControlDisplaySec = 0;
        if (!SDCardUtil.isSDCardUseable()) {
            // 提示SD卡不可用
            Utils.showToast(this, R.string.remoteplayback_SDCard_disable_use);
            return;
        }

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            Utils.showToast(this, R.string.remoteplayback_capture_fail_for_memory);
            return;
        }

        if (mEZMediaPlayer != null) {
            mCaptureDisplaySec = 4;
            updateCaptureUI();
            Thread thr = new Thread() {
                @Override
                public void run() {
                    if (mEZMediaPlayer == null) {
                        return;
                    }
                    String serial = !TextUtils.isEmpty(deviceSerial) ? deviceSerial : "123456789";
                    Bitmap bmp = mEZMediaPlayer.capturePicture();
                    if (bmp != null) {
                        try {
                            mAudioPlayUtil.playAudioFile(AudioPlayUtil.CAPTURE_SOUND);

                            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
                            java.util.Date date = new java.util.Date();
                            final String path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/CapturePicture/" + String.format("%tY", date)
                                    + String.format("%tm", date) + String.format("%td", date) + "/"
                                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";

                            if (TextUtils.isEmpty(path)) {
                                bmp.recycle();
                                bmp = null;
                                return;
                            }
                            EZUtils.saveCapturePictrue(path, bmp);

                            MediaScanner mMediaScanner = new MediaScanner(RecordDisplayActivity.this);
                            mMediaScanner.scanFile(path, "jpg");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecordDisplayActivity.this, getResources().getString(R.string.already_saved_to_volume)+path, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (InnerException e) {
                            e.printStackTrace();
                        } finally {
                            if (bmp != null) {
                                bmp.recycle();
                                bmp = null;
                                return;
                            }
                        }
                    }
                    super.run();
                }
            };
            thr.start();
            //mEZMediaPlayer.capturePicture();
        }
    }

    private void onRemotePlayBackSvClick() {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mRemotePlayBackControlRl.getVisibility() == View.VISIBLE) {
                mRemotePlayBackControlRl.setVisibility(View.GONE);
                if (mAlarmStartTime != null) {
                    mRemotePlayBackProgressLy.setVisibility(View.GONE);
                    mRemotePlayBackProgressBar.setVisibility(View.VISIBLE);
                }
            } else {
                mRemotePlayBackControlRl.setVisibility(View.VISIBLE);
                if (mAlarmStartTime != null) {
                    mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                    mRemotePlayBackProgressBar.setVisibility(View.GONE);
                }
                mControlDisplaySec = 0;
            }
            updateTimeBarUI();
        } else {
            mRemotePlayBackControlRl.setVisibility(View.GONE);
            if (mAlarmStartTime == null) {
                if (mRemotePlayBackFullOperateBar.getVisibility() == View.VISIBLE) {
                    mRemotePlayBackFullOperateBar.setVisibility(View.GONE);
                } else {
                    mRemotePlayBackFullOperateBar.setVisibility(View.VISIBLE);
                    mControlDisplaySec = 0;
                }
            } else {
                mRemotePlayBackFullOperateBar.setVisibility(View.GONE);
                if (mRemotePlayBackProgressLy.getVisibility() == View.GONE) {
                    mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                    mRemotePlayBackProgressBar.setVisibility(View.GONE);
                } else {
                    mRemotePlayBackProgressLy.setVisibility(View.GONE);
                    mRemotePlayBackProgressBar.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void searchCloudFileList(final Calendar seletedTime) {
        new Thread() {
            @Override
            public void run() {
                Calendar starttime = null;
                Calendar endtime = null;
                try {
                    if (mAlarmStartTime != null) {
                        starttime = mAlarmStartTime;
                        endtime = mAlarmStopTime;
                    } else {
                        starttime = mStartTime;
                        endtime = mEndTime;
                    }
                    mCloudFileList = App.getOpenSDK().searchRecordFileFromCloud(deviceSerial,cameraNo,starttime,endtime);
                    sendMessage(MSG_SEARCH_CLOUD_FILE_SUCCUSS, 0, seletedTime);
                } catch (BaseException e) {
                    e.printStackTrace();
                    sendMessage(MSG_SEARCH_CLOUD_FILE_FAIL, e.getErrorCode());
                }
            }
        }.start();
    }

    private void searchEZCloudFileList(final Calendar seletedTime) {
        new Thread() {
            @Override
            public void run() {
                try {
                    mEZCloudFileList = App.getOpenSDK().searchRecordFileFromCloud(deviceSerial,cameraNo, mStartTime, mEndTime);
                    LogUtil.debugLog(TAG, "searchEZCloudFileList ends: " + mEZCloudFileList);
                    sendMessage(MSG_SEARCH_CLOUD_FILE_SUCCUSS, 0, seletedTime);
                } catch (BaseException e) {
                    e.printStackTrace();

                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                    LogUtil.debugLog(TAG, errorInfo.toString());
                    sendMessage(MSG_SEARCH_CLOUD_FILE_FAIL, errorInfo.errorCode);
                }
            }
        }.start();
    }

    private void searchEZAlarmFile(final Calendar seletedTime) {
        new Thread() {
            @Override
            public void run() {
                try {
/*                    Calendar startTime = (Calendar) seletedTime.clone();
                    Calendar endTime = (Calendar) seletedTime.clone();
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.HOUR_OF_DAY, 23);
                    endTime.set(Calendar.MINUTE, 59);
                    endTime.set(Calendar.SECOND, 59);*/

                    Calendar startTime = recordSrartTime;
                    Calendar endTime = recordEndTime;


                    size = 0;

                    LogUtil.i(TAG, "searchEZAlarmFile seletedTime:" + seletedTime.getTime());
/*                    mEZDeviceFileList = App.getOpenSDK().searchRecordFileFromDevice(deviceSerial,cameraNo, startTime, endTime);
                    if (mEZDeviceFileList != null && mEZDeviceFileList.size() > 0) {
                        size = mEZDeviceFileList.size();
                        LogUtil.i(TAG, "searchEZDeviceFileList size:" + size);
                        for (int i = 0; i < size; i++) {
                            EZDeviceRecordFile ezDeviceFile = mEZDeviceFileList.get(i);
                            Calendar tmpStartTime = (ezDeviceFile.getStartTime());
                            Calendar tmpEndTime = (ezDeviceFile.getStopTime());
                            LogUtil.verboseLog(TAG, "startTime:" + tmpStartTime.getTime() + " endTime:" + tmpEndTime.getTime());

                            if (seletedTime.compareTo(tmpStartTime) >= 0 && seletedTime.compareTo(tmpEndTime) <= 0) {
                                mAlarmRecordDeviceFile = ezDeviceFile;
                                mAlarmRecordDeviceFile.setStartTime(mAlarmStartTime);
                                mAlarmRecordDeviceFile.setStopTime(mAlarmStopTime);

                                LogUtil.debugLog(TAG, "searchEZDeviceFileList success: start, " + mAlarmRecordDeviceFile.getStartTime());
                                sendMessage(MSG_SEARCH_CLOUD_FILE_SUCCUSS, 0, seletedTime);
                                return;
                            }
                        }
                        LogUtil.debugLog(TAG, "no matching device record file for alarm");
                    }*/

                    mEZCloudFileList = App.getOpenSDK().searchRecordFileFromCloud(deviceSerial,cameraNo, startTime, endTime);
                    if (mEZCloudFileList != null && mEZCloudFileList.size() > 0) {
                        size = mEZCloudFileList.size();
                        durationTime =(int) (mEZCloudFileList.get(0).getStopTime().getTimeInMillis() - mAlarmStartTime.getTimeInMillis())/1000;
                        LogUtil.debugLog(TAG, "searchEZCloudFileList size:" + size+"Time="+durationTime);
/*                        for(int j =0 ; j <size; j++){
                            EZCloudRecordFile endCloudFile = mEZCloudFileList.get(j);
                            Calendar tmpEndStartTime = endCloudFile.getStartTime();
                            if (recordEndTime.compareTo(tmpEndStartTime) >= 0){
                                Log.v("录像截止","截止录像"+j);*/
                                for ( i =(size-1) ; i >=0; i--) {
                                    EZCloudRecordFile ezCloudFile = mEZCloudFileList.get(i);
                                    Calendar tmpStartTime = ezCloudFile.getStartTime();
                                    Calendar tmpEndTime = ezCloudFile.getStopTime();
                                    LogUtil.verboseLog(TAG, "startTime:" + tmpStartTime.getTime() + " endTime:" + tmpEndTime.getTime());

                                    if (/*seletedTime.compareTo(tmpStartTime) >= 0 &&*/ seletedTime.compareTo(tmpEndTime) <= 0) {
                                        Log.v("录像开始","开始录像"+i);

                                            mAlarmRecordFile = ezCloudFile;
//                    			mAlarmRecordFile.setStartTime(mAlarmStartTime);
//                    			mAlarmRecordFile.setStopTime(mAlarmStopTime);
//                    			String startT = new SimpleDateFormat("yyyyMMddHHmmss").format(mAlarmStartTime.getTime());
//                    			String endT = new SimpleDateFormat("yyyyMMddHHmmss").format(mAlarmStopTime.getTime());
                                            mAlarmRecordFile.setStartTime(tmpStartTime);
                                            mAlarmRecordFile.setStopTime(tmpEndTime);
                                            //durationTime = (int) (mPlayTime - mPlayStartTime.getTimeInMillis()) / 1000;

                                            LogUtil.debugLog(TAG, "searchEZCloudFileList success: start, " + mAlarmRecordFile.getStartTime());
                                            sendMessage(MSG_SEARCH_CLOUD_FILE_SUCCUSS, 0, seletedTime);
                                            return;

                                    }
                                }
                            //}
                       // }

                        LogUtil.debugLog(TAG, "no matching cloud record file for alarm");
                    }

                    sendMessage(MSG_SEARCH_CLOUD_FILE_FAIL, -1);
                } catch (BaseException e) {
                    e.printStackTrace();

                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                    LogUtil.debugLog(TAG, "search file list failed. error " + errorInfo.toString());
                    sendMessage(MSG_SEARCH_CLOUD_FILE_FAIL, e.getErrorCode());
                }
            }
        }.start();
    }


    /**
     * 播放下一个视频
     *
     * @see
     * @since V2.0
     */

    private void nextRemotePlayback(){
        startLocationTimer();
       // mPlayTime = 0;
        EZCloudRecordFile ezCloudFile2 = mEZCloudFileList.get(i);
        //mAlarmStartTime = ezCloudFile2.getStartTime();
        //mAlarmStopTime = ezCloudFile2.getStopTime();
        mEZMediaPlayer = App.getOpenSDK().createPlayer(deviceSerial,cameraNo);
        mEZMediaPlayer.setPlayVerifyCode(verifyCode);
        mEZMediaPlayer.setHandler(mHandler);
        mEZMediaPlayer.setSurfaceHold(mRemotePlayBackSh);
        mEZMediaPlayer.startPlayback(ezCloudFile2);
        mAlarmRecordFile = ezCloudFile2;
    }

    /**
     * 开始回放
     *
     * @see
     * @since V2.0
     */
    private List<EZCloudRecordFile> mEZCloudFileList = null;

    private void startRemotePlayBack(final Calendar selectedTime) {
        LogUtil.debugLog(TAG, "startRemotePlayBack:" + selectedTime);

        if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
            return;
        }

        // 检查网络是否可用
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            // 提示没有连接网络
            setRemotePlayBackFailUI(getString(R.string.remoteplayback_searchfile_fail_for_network));
            return;
        }

        if (mEZMediaPlayer != null && mStatus == STATUS_PAUSE) {
            resumeRemotePlayBack();
            setRemotePlayBackSuccessUI();
            return;
        }
        startLocationTimer();

        mStatus = STATUS_START;
        setRemotePlayBackLoadingUI();
        updateLoadingProgress(0);


        if (mEZCloudFileList == null) {
            if (mAlarmStartTime != null) {
                searchEZAlarmFile(mAlarmStartTime);
            } else
                searchEZCloudFileList(selectedTime);
            return;
        }
        EZCloudRecordFile ezCloudFile = null;
        if (mAlarmStartTime != null && mAlarmRecordFile != null) {
            ezCloudFile = mAlarmRecordFile;
        } else if (mEZCloudFileList.size() > 0) {
            ezCloudFile = mEZCloudFileList.get(0);
        } else {
            return;
        }

        if (mEZMediaPlayer == null) {
            mEZMediaPlayer = App.getOpenSDK().createPlayer(deviceSerial,cameraNo);
            if (mEZMediaPlayer == null)
                return;

            mEZMediaPlayer.setPlayVerifyCode(verifyCode);

            mEZMediaPlayer.setHandler(mHandler);
            mEZMediaPlayer.setSurfaceHold(mRemotePlayBackSh);
        }


        if (mAlarmStartTime != null) {
            if (ezCloudFile != null) {
                mEZMediaPlayer.startPlayback(ezCloudFile);
                mPlayStartTime = ezCloudFile.getStartTime();
            }
        } else {
            if (ezCloudFile != null) {
                mEZMediaPlayer.startPlayback(ezCloudFile);
                mPlayStartTime = ezCloudFile.getStartTime();
            }
        }
    }

    /**
     * 停止回放
     *
     * @see
     * @since V1.0
     */
    private void stopRemotePlayBack() {
        LogUtil.debugLog(TAG, "stopRemotePlayBack");
        mStatus = STATUS_STOP;

        stopUpdateTimer();
        if (mEZMediaPlayer != null) {
            stopRemotePlayBackRecord();

            mEZMediaPlayer.stopPlayback();
            //mj mTotalStreamFlow += mEZMediaPlayer.getStreamFlow();
        }
        mStreamFlow = 0;
        stopLocationTimer();

    }

    /**
     * 暂停回放
     *
     * @see
     * @since V1.0
     */
    private void pauseRemotePlayBack() {
        LogUtil.debugLog(TAG, "pauseRemotePlayBack");
        mStatus = STATUS_PAUSE;

        if (mEZMediaPlayer != null) {
            stopRemotePlayBackRecord();

            mEZMediaPlayer.pausePlayback();
        }
    }

    /**
     * 暂停回放
     *
     * @see
     * @since V1.0
     */
    private void resumeRemotePlayBack() {
        LogUtil.debugLog(TAG, "resumeRemotePlayBack");
        i=size-1;

        EZCloudRecordFile ezCloudFile = mEZCloudFileList.get(i);
        mEZMediaPlayer = App.getOpenSDK().createPlayer(deviceSerial,cameraNo);
        mEZMediaPlayer.setPlayVerifyCode(verifyCode);
        mEZMediaPlayer.setHandler(mHandler);
        mEZMediaPlayer.setSurfaceHold(mRemotePlayBackSh);
        mEZMediaPlayer.startPlayback(ezCloudFile);
/*        mStatus = STATUS_PLAY;

        if (mEZMediaPlayer != null) {
            mEZMediaPlayer.openSound();
            mEZMediaPlayer.resumePlayback();
        }*/
    }


    private void setRemotePlayBackLoadingUI() {
        mRemotePlayBackSv.setVisibility(View.INVISIBLE);
        mRemotePlayBackSv.setVisibility(View.VISIBLE);
        mRemotePlayBackTipTv.setVisibility(View.GONE);
        mRemotePlayBackReplayBtn.setVisibility(View.GONE);
        mRemotePlayBackLoadingPlayBtn.setVisibility(View.GONE);

        mRemotePlayBackLoadingLy.setVisibility(View.VISIBLE);
        mRemotePlayBackLoadingPbLy.setVisibility(View.VISIBLE);

        if (mOrientation == Configuration.ORIENTATION_PORTRAIT || mAlarmStartTime != null) {
            mRemotePlayBackControlRl.setVisibility(View.VISIBLE);
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                mRemotePlayBackProgressBar.setVisibility(View.GONE);
            }
            mRemotePlayBackFullOperateBar.setVisibility(View.GONE);
        } else {
            mRemotePlayBackFullOperateBar.setVisibility(View.VISIBLE);
            mRemotePlayBackControlRl.setVisibility(View.GONE);
        }
        mRemotePlayBackCaptureBtn.setEnabled(false);
        mRemotePlayBackRecordBtn.setEnabled(false);
        mRemotePlayBackSmallCaptureBtn.setEnabled(false);
        mRemotePlayBackSmallRecordBtn.setEnabled(false);

        mRemotePlayBackFullPlayBtn.setEnabled(false);
        mRemotePlayBackFullCaptureBtn.setEnabled(false);
        mRemotePlayBackFullRecordBtn.setEnabled(false);
        mRemotePlayBackFullFlowLy.setVisibility(View.GONE);

        updateSoundUI();

        updateTimeBarUI();
    }

    private void setRemotePlayBackStopUI() {
        stopUpdateTimer();
        setRemotePlayBackSvLayout();
        mRemotePlayBackTipTv.setVisibility(View.GONE);
        mRemotePlayBackReplayBtn.setVisibility(View.GONE);
        mRemotePlayBackLoadingLy.setVisibility(View.GONE);
        mRemotePlayBackLoadingPlayBtn.setVisibility(View.VISIBLE);
//        if (mTotalStreamFlow > 0) {
//            mRemotePlayBackFlowTv.setVisibility(View.VISIBLE);
//            mRemotePlayBackFullFlowLy.setVisibility(View.VISIBLE);
//            updateRemotePlayBackFlowTv(mStreamFlow);
//        } else {
//            mRemotePlayBackFlowTv.setVisibility(View.GONE);
//            mRemotePlayBackFullFlowLy.setVisibility(View.GONE);
//        }
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT || mAlarmStartTime != null) {
            mRemotePlayBackControlRl.setVisibility(View.VISIBLE);
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                mRemotePlayBackProgressBar.setVisibility(View.GONE);
            }
        } else {
            mRemotePlayBackFullOperateBar.setVisibility(View.VISIBLE);
        }
        mRemotePlayBackBtn.setBackgroundResource(R.drawable.remote_list_play_btn_selector);

        mRemotePlayBackCaptureBtn.setEnabled(false);
        mRemotePlayBackRecordBtn.setEnabled(false);
        mRemotePlayBackSmallCaptureBtn.setEnabled(false);
        mRemotePlayBackSmallRecordBtn.setEnabled(false);

        mRemotePlayBackFullPlayBtn.setEnabled(true);
        mRemotePlayBackFullPlayBtn.setBackgroundResource(R.drawable.play_full_play_selector);
        mRemotePlayBackFullCaptureBtn.setEnabled(false);
        mRemotePlayBackFullRecordBtn.setEnabled(false);
        updateSoundUI();

        updateTimeBarUI();
    }

    private void setRemotePlayBackFailUI(String errorStr) {
        stopUpdateTimer();
        mScreenOrientationHelper.disableSensorOrientation();
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (TextUtils.isEmpty(errorStr)) {
            mRemotePlayBackTipTv.setVisibility(View.GONE);
            mRemotePlayBackReplayBtn.setVisibility(View.VISIBLE);
        } else {
            mRemotePlayBackTipTv.setVisibility(View.VISIBLE);
            mRemotePlayBackTipTv.setText(errorStr);
            mRemotePlayBackReplayBtn.setVisibility(View.GONE);
        }

        mRemotePlayBackLoadingPlayBtn.setVisibility(View.GONE);
        mRemotePlayBackLoadingLy.setVisibility(View.GONE);
        mRemotePlayBackFlowTv.setVisibility(View.GONE);
        mRemotePlayBackFullFlowLy.setVisibility(View.GONE);
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT || mAlarmStartTime != null) {
            mRemotePlayBackControlRl.setVisibility(View.VISIBLE);
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressLy.setVisibility(View.VISIBLE);
                mRemotePlayBackProgressBar.setVisibility(View.GONE);
            }
        } else {
            mRemotePlayBackFullOperateBar.setVisibility(View.VISIBLE);
        }
        mRemotePlayBackBtn.setBackgroundResource(R.drawable.remote_list_play_btn_selector);

        mRemotePlayBackCaptureBtn.setEnabled(false);
        mRemotePlayBackRecordBtn.setEnabled(false);
        mRemotePlayBackSmallCaptureBtn.setEnabled(false);
        mRemotePlayBackSmallRecordBtn.setEnabled(false);

        mRemotePlayBackFullPlayBtn.setEnabled(true);
        mRemotePlayBackFullPlayBtn.setBackgroundResource(R.drawable.play_full_play_selector);
        mRemotePlayBackFullCaptureBtn.setEnabled(false);
        mRemotePlayBackFullRecordBtn.setEnabled(false);

        updateSoundUI();

        updateTimeBarUI();
    }

    /**
     * 这里对方法做描述
     * @see
     * @since V1.8.2
     */
    /**
     * 这里对方法做描述
     *
     * @see
     * @since V1.8.2
     */
    private void setRemotePlayBackSuccessUI() {
        // 允许屏幕旋转
        //mScreenOrientationHelper.enableSensorOrientation();
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        mRemotePlayBackTipTv.setVisibility(View.GONE);
        mRemotePlayBackReplayBtn.setVisibility(View.GONE);
        mRemotePlayBackLoadingPlayBtn.setVisibility(View.GONE);

        mRemotePlayBackLoadingLy.setVisibility(View.GONE);
//        mRemotePlayBackFlowTv.setVisibility(View.VISIBLE);
        mRemotePlayBackFullFlowLy.setVisibility(View.VISIBLE);
        mRemotePlayBackBtn.setBackgroundResource(R.drawable.remote_list_pause_btn_selector);

        mRemotePlayBackCaptureBtn.setEnabled(true);
        mRemotePlayBackRecordBtn.setEnabled(true);
        mRemotePlayBackSmallCaptureBtn.setEnabled(true);
        mRemotePlayBackSmallRecordBtn.setEnabled(true);

        mRemotePlayBackFullPlayBtn.setEnabled(true);
        mRemotePlayBackFullPlayBtn.setBackgroundResource(R.drawable.play_full_pause_selector);
        mRemotePlayBackFullCaptureBtn.setEnabled(true);
        mRemotePlayBackFullRecordBtn.setEnabled(true);
        updateTimeBarUI();
        if (mAlarmStartTime != null) {
            timeBucketUIInit(mAlarmStartTime.getTimeInMillis(), mAlarmStopTime.getTimeInMillis());
        }

        updateSoundUI();

        startUpdateTimer();
    }

    /**
     * 更新流量统计
     *
     * @see
     * @since V1.0
     */
    private void checkRemotePlayBackFlow() {
//        if (mEZMediaPlayer != null && mRemotePlayBackFlowTv.getVisibility() == View.VISIBLE) {
//            // 更新流量数据
//            long streamFlow = 0l;//mj mEZMediaPlayer.getStreamFlow();
//
//            updateRemotePlayBackFlowTv(streamFlow);
//        }
    }

    private void updateRemotePlayBackFlowTv(long streamFlow) {
        long streamFlowUnit = streamFlow - mStreamFlow;
        if (streamFlowUnit < 0)
            streamFlowUnit = 0;
        float fKBUnit = (float) streamFlowUnit / (float) Constant.KB;
        String descUnit = String.format("%.2f k/s ", fKBUnit);
        String desc = null;
        float fMB = 0;
        if (streamFlow >= Constant.GB) {
            float fGB = (float) streamFlow / (float) Constant.GB;
            desc = String.format("%.2f GB ", fGB);
        } else {
            fMB = (float) streamFlow / (float) Constant.MB;
            desc = String.format("%.2f MB ", fMB);
        }

        // 显示流量
//        mRemotePlayBackFlowTv.setText(descUnit + " " + desc);
        mRemotePlayBackFullRateTv.setText(descUnit);
        mRemotePlayBackFullFlowTv.setText(desc);
        mStreamFlow = streamFlow;
    }


    /**
     * 开始录像成功
     *
     * @param recordFilePath
     * @see
     * @since V2.0
     */
    private void handleStartRecordSuccess(String recordFilePath) {
        // 设置录像按钮为check状态
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (!mIsOnStop) {
                mRecordRotateViewUtil.applyRotation(mRemotePlayBackRecordContainer, mRemotePlayBackRecordBtn,
                        mRemotePlayBackRecordStartBtn, 0, 90);
            } else {
                mRemotePlayBackRecordBtn.setVisibility(View.GONE);
                mRemotePlayBackRecordStartBtn.setVisibility(View.VISIBLE);
            }
            if (mAlarmStartTime != null) {
                mRemotePlayBackSmallRecordBtn.setVisibility(View.GONE);
                mRemotePlayBackSmallRecordStartBtn.setVisibility(View.VISIBLE);
            } else {
                mRemotePlayBackFullRecordBtn.setVisibility(View.GONE);
                mRemotePlayBackFullRecordStartBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (mAlarmStartTime != null) {
                if (!mIsOnStop) {
                    mRecordRotateViewUtil.applyRotation(mRemotePlayBackSmallRecordContainer,
                            mRemotePlayBackSmallRecordBtn, mRemotePlayBackSmallRecordStartBtn, 0, 90);
                } else {
                    mRemotePlayBackSmallRecordBtn.setVisibility(View.GONE);
                    mRemotePlayBackSmallRecordStartBtn.setVisibility(View.VISIBLE);
                }
            } else {
                if (!mIsOnStop) {
                    mRecordRotateViewUtil.applyRotation(mRemotePlayBackFullRecordContainer,
                            mRemotePlayBackFullRecordBtn, mRemotePlayBackFullRecordStartBtn, 0, 90);
                } else {
                    mRemotePlayBackFullRecordBtn.setVisibility(View.GONE);
                    mRemotePlayBackFullRecordStartBtn.setVisibility(View.VISIBLE);
                }
            }
            mRemotePlayBackRecordBtn.setVisibility(View.GONE);
            mRemotePlayBackRecordStartBtn.setVisibility(View.VISIBLE);
        }

        mRecordFilePath = recordFilePath;
        // 计时按钮可见
        mRemotePlayBackRecordLy.setVisibility(View.VISIBLE);
        mRemotePlayBackRecordTv.setText("00:00");
        mRecordSecond = 0;
    }

    private void handleRecordFail() {
        Utils.showToast(RecordDisplayActivity.this, R.string.remoteplayback_record_fail);
        if (bIsRecording) {
            stopRemotePlayBackRecord();
            bIsRecording = !bIsRecording;
            return;
        }
    }

    /**
     * 抓图成功，返回图片文件路径
     *
     * @param filePath
     * @see
     * @since V2.0
     */
    private void handleCapturePictureSuccess(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        // 播放抓拍音频
        mAudioPlayUtil.playAudioFile(AudioPlayUtil.CAPTURE_SOUND);

//        mRemotePlayBackCaptureRl.setVisibility(View.VISIBLE);
        mCaptureDisplaySec = 0;
        try {
            mRemotePlayBackCaptureIv.setImageURI(Uri.parse(filePath));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        updateCaptureUI();
    }

    private void updateRemotePlayBackUI() {
        if (isFinishing()) {
            return;
        }
        if (mControlDisplaySec == 5) {
            mControlDisplaySec = 0;
            mRemotePlayBackControlRl.setVisibility(View.GONE);
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressLy.setVisibility(View.GONE);
                mRemotePlayBackProgressBar.setVisibility(View.VISIBLE);
            }
            mRemotePlayBackFullOperateBar.setVisibility(View.GONE);
            updateTimeBarUI();
        }

        updateCaptureUI();

        if (bIsRecording || mRecordFilePath != null) {
            updateRecordTime();
        }

        checkRemotePlayBackFlow();

        Calendar OSDTime = mEZMediaPlayer.getOSDTime();
        if (OSDTime != null) {
            mPlayTime = OSDTime.getTimeInMillis();
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressBar.setProgress((int) (mPlayTime - mPlayStartTime.getTimeInMillis()) / 1000);
                mRemotePlayBackSeekBar.setProgress((int) (mPlayTime - mPlayStartTime.getTimeInMillis()) / 1000);
                int progress = (int) (mPlayTime - mAlarmStartTime.getTimeInMillis());
                LogUtil.i(TAG, "updateRemotePlayBackUI mPlayTime:" + mPlayTime + "mAlarmStartTime:" + mAlarmStartTime.getTime()
                        + " mAlarmStartTime:" + mAlarmStartTime.getTimeInMillis() + " startPlayTime:" + mPlayStartTime.getTimeInMillis());
                LogUtil.i(TAG, "updateRemotePlayBackUI progress:" + progress);
                handlePlayProgress(OSDTime);
//                if(mPlayTime + 1000 > mAlarmStopTime.getTimeInMillis()) {
//                    handlePlayFinish();
//                }
            } else {
                float pos = mRemoteFileTimeBar.getScrollPosByPlayTime(mPlayTime, mOrientation);
                mRemotePlayBackTimeBar.smoothScrollTo((int) pos, 0);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                mRemotePlayBackTimeTv.setText(sdf.format(mPlayTime));
            }
        }
    }

    private void handlePlayProgress(Calendar osdTime) {
        long osd = osdTime.getTimeInMillis();
        long begin = mPlayStartTime.getTimeInMillis();

        int beginTimeClock = (int) ((osd - begin) / 1000);
        updateTimeBucketBeginTime(beginTimeClock);
    }

    private void updateTimeBucketBeginTime(int beginTimeClock) {
        String convToUIDuration = RemoteListUtil.convToUIDuration(beginTimeClock);
        mRemotePlayBackBeginTimeTv.setText(convToUIDuration);
    }

    private void timeBucketUIInit(long beginTime, long endTime) {
        int diffSeconds = (int) (endTime - beginTime) / 1000;
        String convToUIDuration = RemoteListUtil.convToUIDuration(diffSeconds);
        mRemotePlayBackBeginTimeTv.setText(RemoteListContant.VIDEO_DUAR_BEGIN_INIT);
        mRemotePlayBackEndTimeTv.setText(convToUIDuration);
    }

    //更新抓图/录像显示UI
    private void updateCaptureUI() {
        if (isFinishing()) {
            return;
        }
        if (mRemotePlayBackCaptureRl.getVisibility() == View.VISIBLE) {
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT || mRemotePlayBackTimeBarRl.getVisibility() == View.GONE) {
                if (mRemotePlayBackControlRl.getVisibility() == View.VISIBLE) {
                    mRemotePlayBackCaptureRlLp.setMargins(0, 0, 0, Utils.dip2px(this, mAlarmStartTime != null ? 60 : 40));
                } else {
                    mRemotePlayBackCaptureRlLp.setMargins(0, 0, 0, mAlarmStartTime != null ? Utils.dip2px(this, 2) : 0);
                }
                mRemotePlayBackCaptureRl.setLayoutParams(mRemotePlayBackCaptureRlLp);
            } else {
                if (mAlarmStartTime != null) {
                    if (mRemotePlayBackControlRl.getVisibility() == View.VISIBLE) {
                        mRemotePlayBackCaptureRlLp.setMargins(0, 0, 0, Utils.dip2px(this, 60));
                    } else {
                        mRemotePlayBackCaptureRlLp.setMargins(0, 0, 0, Utils.dip2px(this, 2));
                    }
                } else {
                    mRemotePlayBackCaptureRlLp.setMargins(0, 0, 0, Utils.dip2px(this, 87));
                }
                mRemotePlayBackCaptureRl.setLayoutParams(mRemotePlayBackCaptureRlLp);
            }
            if (mRemotePlayBackCaptureWatermarkIv.getTag() != null) {
                mRemotePlayBackCaptureWatermarkIv.setVisibility(View.VISIBLE);
                mRemotePlayBackCaptureWatermarkIv.setTag(null);
            }
        }
        if (mCaptureDisplaySec >= 4) {
            mCaptureDisplaySec = 0;
            mRemotePlayBackCaptureRl.setVisibility(View.GONE);
            mRemotePlayBackCaptureIv.setImageURI(null);
            mRemotePlayBackCaptureWatermarkIv.setTag(null);
            mRemotePlayBackCaptureWatermarkIv.setVisibility(View.GONE);
        }
    }

    /**
     * 更新录像时间
     *
     * @see
     * @since V1.0
     */
    private void updateRecordTime() {
        if (mRemotePlayBackRecordIv.getVisibility() == View.VISIBLE) {
            mRemotePlayBackRecordIv.setVisibility(View.INVISIBLE);
        } else {
            mRemotePlayBackRecordIv.setVisibility(View.VISIBLE);
        }
        // 计算分秒
        int leftSecond = mRecordSecond % 3600;
        int minitue = leftSecond / 60;
        int second = leftSecond % 60;

        // 显示录像时间
        String recordTime = String.format("%02d:%02d", minitue, second);
        mRemotePlayBackRecordTv.setText(recordTime);
    }

    // 处理密码错误
    private void handlePasswordError(int title_resid, int msg1_resid, int msg2_resid) {
        stopRemotePlayBack();
        setRemotePlayBackStopUI();

        if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
            return;
        }

        // 检查网络是否可用
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            // 提示没有连接网络
            setRemotePlayBackFailUI(getString(R.string.remoteplayback_searchfile_fail_for_network));
            return;
        }

        if (mEZMediaPlayer != null && mStatus == STATUS_PAUSE) {
            resumeRemotePlayBack();
            setRemotePlayBackSuccessUI();
            return;
        }

        mStatus = STATUS_START;
        setRemotePlayBackLoadingUI();
        updateLoadingProgress(0);

        Calendar seletedTime = getTimeBarSeekTime();
        LogUtil.debugLog(TAG, "startRemotePlayBack:" + seletedTime);

        if (mCloudFileList == null) {
            searchCloudFileList(seletedTime);
            return;
        }

        if (mAlarmStartTime != null) {
        } else {
        }
    }

    /**
     * 处理播放成功的情况
     *
     * @see
     * @since V1.0
     */
    private void handlePlaySuccess(Message msg) {
        LogUtil.debugLog(TAG, "handlePlaySuccess:" + msg.arg1);
        mStatus = STATUS_PLAY;

        if (msg.arg1 != 0) {
            mRealRatio = (float) msg.arg2 / msg.arg1;
        } else {
            mRealRatio = Constant.LIVE_VIEW_RATIO;
        }
        setRemotePlayBackSvLayout();

        setRemotePlayBackSuccessUI();
        setRemotePlaySound();
    }

    private void setRemotePlayBackSvLayout() {
        // 设置播放窗口位置
        final int screenWidth = mLocalInfo.getScreenWidth();
        final int screenHeight = (mOrientation == Configuration.ORIENTATION_PORTRAIT) ? (mLocalInfo.getScreenHeight() - mLocalInfo
                .getNavigationBarHeight()) : mLocalInfo.getScreenHeight();
        final RelativeLayout.LayoutParams realPlaySvlp = Utils.getPlayViewLp(mRealRatio, mOrientation,
                mLocalInfo.getScreenWidth(), (int) (mLocalInfo.getScreenWidth() * Constant.LIVE_VIEW_RATIO),
                screenWidth, screenHeight);

        //RelativeLayout.LayoutParams svLp = new RelativeLayout.LayoutParams(realPlaySvlp.width, realPlaySvlp.height);
        RelativeLayout.LayoutParams svLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //svLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRemotePlayBackSv.setLayoutParams(svLp);

        mRemotePlayBackTouchListener.setSacaleRect(Constant.MAX_SCALE, 0, 0, realPlaySvlp.width, realPlaySvlp.height);
        setPlayScaleUI(1, null, null);
    }

    private void setPlayScaleUI(float scale, CustomRect oRect, CustomRect curRect) {
        if (scale == 1) {
            if (mPlayScale == scale) {
                return;
            }
            mRemotePlayBackRatioTv.setVisibility(View.GONE);
            try {
                mEZMediaPlayer.setDisplayRegion(false, null, null);
            } catch (BaseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            if (mPlayScale == scale) {
                try {
                    mEZMediaPlayer.setDisplayRegion(true, oRect, curRect);
                } catch (BaseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }
            RelativeLayout.LayoutParams realPlayRatioTvLp = (RelativeLayout.LayoutParams) mRemotePlayBackRatioTv
                    .getLayoutParams();
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                realPlayRatioTvLp.setMargins(Utils.dip2px(this, 10), Utils.dip2px(this, 10), 0, 0);
            } else {
                realPlayRatioTvLp.setMargins(Utils.dip2px(this, 70), Utils.dip2px(this, 20), 0, 0);
            }
            mRemotePlayBackRatioTv.setLayoutParams(realPlayRatioTvLp);
            String sacleStr = String.valueOf(scale);
            mRemotePlayBackRatioTv.setText(sacleStr.subSequence(0, Math.min(3, sacleStr.length())) + "X");
            mRemotePlayBackRatioTv.setVisibility(View.VISIBLE);

            mRemotePlayBackControlRl.setVisibility(View.GONE);
            if (mAlarmStartTime != null) {
                mRemotePlayBackProgressLy.setVisibility(View.GONE);
                mRemotePlayBackProgressBar.setVisibility(View.VISIBLE);
            }
            mRemotePlayBackFullOperateBar.setVisibility(View.GONE);

            try {
                mEZMediaPlayer.setDisplayRegion(true, oRect, curRect);
            } catch (BaseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        mPlayScale = scale;
    }

    /**
     * 处理播放失败的情况
     *
     * @param errorCode - 错误码
     * @see
     * @since V1.0
     */
    private void handlePlayFail(int errorCode, Object obj) {
        if (obj != null) {
            ErrorInfo errorInfo = (ErrorInfo) obj;
            LogUtil.debugLog(TAG, "handlePlayFail:" + errorCode);
        }

        stopRemotePlayBack();

        String txt = null;
        // 判断返回的错误码
        switch (errorCode) {
            case ErrorCode.ERROR_WEB_SESSION_ERROR:
            case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
            case ErrorCode.ERROR_CAS_PLATFORM_CLIENT_NO_SIGN_RELEATED:
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
            case ErrorCode.ERROR_CAS_VERIFY_SESSION_ERROR:
                ActivityUtils.goToLoginAgain(RecordDisplayActivity.this);
                return;
            case ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE:
                txt = getString(R.string.remoteplayback_over_link);
                break;
            case ErrorCode.ERROR_WEB_DEVICE_NOT_ONLINE:
            case ErrorCode.ERROR_CAS_PLATFORM_CLIENT_REQUEST_NO_PU_FOUNDED:
                txt = getString(R.string.realplay_fail_device_not_exist);
                if (mStatus != STATUS_STOP) {
                    stopRemotePlayBack();
                }
                setRemotePlayBackFailUI(getString(R.string.camera_not_online));
                mIsOnStop = false;
                break;
            case ErrorCode.ERROR_WEB_DEVICE_SO_TIMEOUT:
                txt = getString(R.string.realplay_fail_connect_device);
                break;
            case ErrorCode.ERROR_INNER_DEVICE_NOT_EXIST:
                txt = getString(R.string.camera_not_online);
                break;
            case ErrorCode.ERROR_CAS_CONNECT_FAILED:
                txt = getString(R.string.remoteplayback_connect_server_error);
                break;
            case ErrorCode.ERROR_WEB_CODE_ERROR:
                //VerifySmsCodeUtil.openSmsVerifyDialog(Constant.SMS_VERIFY_LOGIN, this, this);
                //txt = Utils.getErrorTip(this, R.string.check_feature_code_fail, errorCode);
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR:
                //VerifySmsCodeUtil.openSmsVerifyDialog(Constant.SMS_VERIFY_HARDWARE, this, this);
//                SecureValidate.secureValidateDialog(this, this);
                //txt = Utils.getErrorTip(this, R.string.check_feature_code_fail, errorCode);
                break;
            // 收到这两个错误码，可以弹出对话框，让用户输入密码后，重新取流预览
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR: {
                DataManager.getInstance().setDeviceSerialVerifyCode(deviceSerial,null);
                VerifyCodeInput.VerifyCodeInputDialog(this,this).show();
            }
            break;
            default:
                txt = Utils.getErrorTip(this, R.string.remoteplayback_fail, errorCode);
                Log.i("TAG","009----"+txt);
                startRemotePlayBack(getTimeBarSeekTime());
                break;
        }

        if (!TextUtils.isEmpty(txt)) {
            setRemotePlayBackFailUI(txt);
        } else {
            setRemotePlayBackStopUI();
        }

    }

    /**
     * 处理播放失败的情况
     * *            - 错误码
     *
     * @see
     * @since V1.0
     */
    private void handlePlayFinish() {
        LogUtil.debugLog(TAG, "handlePlayFinish");
        i=i-1;
        Log.v("tag","i="+i);

        stopRemotePlayBack();
        if(i>=0) {
            nextRemotePlayback();
        }

        if (mAlarmStartTime != null) {
            mRemotePlayBackProgressBar.setProgress(mRemotePlayBackProgressBar.getMax());
            mRemotePlayBackSeekBar.setProgress(mRemotePlayBackSeekBar.getMax());
            setRemotePlayBackFailUI(null);
        } else {
            setRemotePlayBackFailUI(null);
        }
    }

    /**
     * 启动定时器
     *
     * @see
     * @since V1.0
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {
                if ((mRemotePlayBackControlRl.getVisibility() == View.VISIBLE || mRemotePlayBackFullOperateBar.getVisibility() == View.VISIBLE)
                        && mControlDisplaySec < 5) {
                    mControlDisplaySec++;
                }
                if (mRemotePlayBackCaptureRl.getVisibility() == View.VISIBLE && mCaptureDisplaySec < 4) {
                    mCaptureDisplaySec++;
                }

                // 更新录像时间
                if (mRecordFilePath != null) {
                    // 更新录像时间
                    Calendar OSDTime = mEZMediaPlayer.getOSDTime();
                    if (OSDTime != null) {
                        String playtime = Utils.OSD2Time(OSDTime);
                        if (!TextUtils.equals(playtime, mRecordTime)) {
                            mRecordSecond++;
                            mRecordTime = playtime;
                        }
                    }
                }

                sendMessage(MSG_PLAY_UI_UPDATE, 0);
            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 1000, 1000);
    }

    /**
     * 停止定时器
     *
     * @see
     * @since V1.0
     */
    private void stopUpdateTimer() {
        mCaptureDisplaySec = 4;
        updateCaptureUI();
        mHandler.removeMessages(MSG_PLAY_UI_UPDATE);
        // 停止录像计时
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    /**
     * 启动定位定时器
     *
     * @see
     * @since V1.0
     */
    private void startLocationTimer() {
        stopUpdateTimer();
        // 开始录像计时
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 333;
                mHandler.sendMessage(message);
            }
        };
        // 延时2000ms后执行，3000ms执行一次
        timer.schedule(task, 2000, 3000);
    }

    /**
     * 停止定位定时器
     *
     * @see
     * @since V1.0
     */
    private void stopLocationTimer() {

        // 停止录像计时
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * 发送消息
     *
     * @param msg  - 消息对象
     * @param arg1 - 错误码
     * @since V1.0
     */
    public void sendMessage(int msg, int arg1) {
        if (mHandler != null) {
            Message message = Message.obtain();
            message.what = msg;
            message.arg1 = arg1;
            mHandler.sendMessage(message);
        }
    }

    public void sendMessage(int msg, int arg1, Object obj) {
        if (mHandler != null) {
            Message message = Message.obtain();
            message.what = msg;
            message.arg1 = arg1;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
    }

    private void dismissPopDialog(AlertDialog popDialog) {
        if (popDialog != null && popDialog.isShowing() && !isFinishing()) {
            try {
                popDialog.dismiss();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    /* (non-Javadoc)
     * @see com.videogo.widget.TimeBarHorizontalScrollView.TimeScrollBarScrollListener#onScrollChanged(int, int, int, int, android.widget.HorizontalScrollView)
     */
    @Override
    public void onScrollChanged(int left, int top, int oldLeft, int oldTop, HorizontalScrollView scrollView) {
        Calendar startCalendar = mRemoteFileTimeBar.pos2Calendar(left, mOrientation);
        if (startCalendar != null) {
            mPlayTime = startCalendar.getTimeInMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            mRemotePlayBackTimeTv.setText(sdf.format(mPlayTime));
        }
    }

    /* (non-Javadoc)
     * @see com.videogo.widget.TimeBarHorizontalScrollView.TimeScrollBarScrollListener#onScrollStart(android.widget.HorizontalScrollView)
     */
    @Override
    public void onScrollStart(HorizontalScrollView scrollView) {
        if (mStatus != STATUS_STOP) {
            //stopRemotePlayBack();
        }
    }

    /* (non-Javadoc)
     * @see com.videogo.widget.TimeBarHorizontalScrollView.TimeScrollBarScrollListener#onScrollStop(android.widget.HorizontalScrollView)
     */
    @Override
    public void onScrollStop(HorizontalScrollView scrollView) {
        if (mStatus != STATUS_STOP) {
            //stopRemotePlayBack();
        }
        // startRemotePlayBack(getTimeBarSeekTime());
    }

    @Override
    public void onInputVerifyCode(final String verifyCode) {
        LogUtil.debugLog(TAG, "verify code is " + verifyCode);

        LogUtil.debugLog(TAG, "verify code is " + verifyCode);
        DataManager.getInstance().setDeviceSerialVerifyCode(deviceSerial,verifyCode);
        if (mEZMediaPlayer != null) {
            mEZMediaPlayer.setPlayVerifyCode(DataManager.getInstance().getDeviceSerialVerifyCode(deviceSerial));
            startRemotePlayBack(getTimeBarSeekTime());
        }
    }
}
