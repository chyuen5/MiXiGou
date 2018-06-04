package com.hsinfo.mixigou.Main;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hsinfo.mixigou.ActivityLogin;
import com.hsinfo.mixigou.GridView.Grid_Item;
import com.hsinfo.mixigou.GridView.NewGridAdaper;
import com.hsinfo.mixigou.R;
import com.hsinfo.mixigou.Utils.HttpClientUtil;
import com.hsinfo.mixigou.Utils.UpdateManager;
import com.hsinfo.mixigou.Utils.UrlUtil;
//import com.hsinfo.mixigou.WebView.WebViewCanteen;
//import com.hsinfo.mixigou.adapter.AdaperItem3;
//import com.hsinfo.mixigou.imagecache.LoaderImpl;
//import com.hsinfo.mixigou.item.FinishPeddingItem;
import com.hsinfo.mixigou.WebView.WebViewCanteen;
import com.hsinfo.mixigou.WebView.WebViewHome;
import com.hsinfo.mixigou.imagecache.LoaderImpl;
import com.hsinfo.mixigou.resideMenu.ResideMenu;
import com.hsinfo.mixigou.resideMenu.ResideMenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//import static com.umeng.commonsdk.proguard.g.R;

public class ActivityMain extends Activity implements OnClickListener
{
    private SharedPreferences sp;

    //List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

    //private AdaperItem3 listItemAdapter;

    private TextView tv_sousuo;
    private TextView tv_shoucang;
    private TextView tv_jieyue;
    private TextView tv_xiazai;//外出登记
    private TextView tv_daiban;//更多
    private TextView tv_dangan;
    private TextView tv_faqi;
    private TextView tv_zhuxiao;
    private TextView tv_zaixian;
    private ImageView iv_add;

    private GridView gridView;

    private int m_rolecode;
    private String url;
    private String m_loginname;
    private String m_diaplayname;
    private String m_userid;
    private String appUrl;
    private String m_jieyue = "0";
    private String m_xiazai = "0";
    private String m_duban = "0";
    private String m_daiban = "0";
    private String m_daiban_pingding = "0";

    // 侧滑
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemUpdate;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemphone;
    private ResideMenuItem itemSettings;
    protected Bitmap bmp_UserPhoto;

    private LoaderImpl impl;
    private Map<String,SoftReference<Bitmap>> sImageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        String strurl = "";
        String strrolecode = "";

        appUrl = UrlUtil.HOST;

        //获取用户登录名
		sp = getSharedPreferences("userdata", 0);
        m_loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		m_userid = sp.getString("USERDATA.USER.ID", "");
        strrolecode = sp.getString("USERDATA.ROLE.CODE", "");
        m_diaplayname = sp.getString("USERDATA.DISPLAY.NAME", "");
        m_duban = sp.getString("USERDATA.DUBAN.NUM", "0");
        m_daiban = sp.getString("USERDATA.DAIBAN.NUM", "0");

        m_rolecode = Integer.parseInt(strrolecode);

        gridView = (GridView) findViewById(R.id.main_grid2);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        tv_zhuxiao = (TextView) findViewById(R.id.tv_title3);

        if(m_rolecode==300)
        {
            iv_add.setVisibility(View.GONE);
            tv_zhuxiao.setVisibility(View.VISIBLE);
        }

        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);


        tv_zhuxiao.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ActivityMain.this);
                normalDialog.setTitle("注销");
                normalDialog.setMessage("确定要注销吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 注销
                                Intent intent = new Intent();
                                intent.setClass( ActivityMain.this, ActivityLogin.class);
                                startActivity(intent);

                                finish();
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }
        });

        // 侧滑
        setUpMenu();

        iv_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //线程下载用户头像
//                Thread thread = new Thread(new ThreadDonwImg());
//                thread.start();

                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT,3);
            }
        });

        initList_100();

        //Thread ThreadDevice = new Thread(new ThreadDeviceToken());
        //ThreadDevice.start();
    }

    //领导
    public int initList_100()
    {
        ArrayList<Grid_Item> lists = new ArrayList<Grid_Item>();
        lists.add(new Grid_Item(R.mipmap.homepage_icon09, "安全管理待办"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon01, "进度管理"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon02, "人员管理"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon03, "预控管理"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon04, "质量管理"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon05, "安全管理"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon06, "车辆"));
        lists.add(new Grid_Item(R.mipmap.homepage_icon07, "物资管理"));

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = this.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);

        int pixelsX = metrics.widthPixels;

        NewGridAdaper adaper = new NewGridAdaper( this, lists, pixelsX );
        gridView.setAdapter(adaper);

        //添加消息处理
        gridView.setOnItemClickListener(gridViewListener1);

        return 1;
    }

    //gridview点击事件
    private AdapterView.OnItemClickListener gridViewListener1 = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {

            if( arg3==0 )  //进度管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_AnQuan_DaiBan.class);
                startActivity(intent);
            }
            else if( arg3==1 )  //进度管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_JinDu.class);
                startActivity(intent);
            }
            else if( arg3==2 )  //人员管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_RenYuan.class);
                startActivity(intent);
            }
            else if( arg3==3 )  //预控管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_YuKong.class);
                startActivity(intent);
            }
            else if( arg3==4 )  //质量管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_ZhiLiang.class);
                startActivity(intent);
            }
            else if( arg3==5 )  //安全管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_AnQuan.class);
                startActivity(intent);
            }
            else if( arg3==6 )  //车辆管理
            {
                //Intent intent = new Intent();
                //intent.setClass(ActivityMain.this, Activity_JinDu.class);
                //startActivity(intent);
            }
            else if( arg3==7 )  //物资管理
            {
                Intent intent = new Intent();
                intent.setClass(ActivityMain.this, Activity_WuZhi.class);
                startActivity(intent);
            }
        }
    };

    //一般人员
    /*public ListView initList_200()
    {
        FinishPeddingItem listItem1 = new FinishPeddingItem();
        listItem1.setIv_icon1(R.drawable.home_dangan01);
        listItem1.setTv_title("我的待办");
        listItem1.setTv_date(m_daiban);
        listItems.add(listItem1);

        FinishPeddingItem listItem2 = new FinishPeddingItem();
        listItem2.setIv_icon1(R.drawable.home_dangan02);
        listItem2.setTv_title("我的已办");
        listItems.add(listItem2);

        FinishPeddingItem listItem3 = new FinishPeddingItem();
        listItem3.setIv_icon1(R.drawable.home_dangan05);
        listItem3.setTv_title("我的关注");
        listItems.add(listItem3);

        FinishPeddingItem listItem6 = new FinishPeddingItem();
        listItem6.setIv_icon1(R.drawable.home_dangan07);
        listItem6.setTv_title("发起问题");
        listItems.add(listItem6);

        FinishPeddingItem listItem4 = new FinishPeddingItem();
        listItem4.setIv_icon1(R.drawable.home_dangan04);
        listItem4.setTv_title("消缺记录");
        listItems.add(listItem4);

        FinishPeddingItem listItem5 = new FinishPeddingItem();
        listItem5.setIv_icon1(R.drawable.home_dangan02);
        listItem5.setTv_title("我的统计");
        listItems.add(listItem5);

        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new AdaperItem3( ActivityMain2.this, listItems );
        listItemAdapter.setListView(listview);
        // 添加并且显示
        listview.setAdapter(listItemAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String url;
                FinishPeddingItem item = (FinishPeddingItem) listview.getItemAtPosition(position);
                String formId = item.getTv_formid();

                if (position == 0)
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/waitDealQuality/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的待办");
                    startActivity(intent);
                }
                else if( position==1 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/yibanQuality/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的已办");
                    startActivity(intent);
                }
                else if( position==2 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getAttentionList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的关注");
                    startActivity(intent);
                }
                else if( position==3 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/goAddTQuality/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","发起问题");
                    startActivity(intent);
                }
                else if( position==4 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getQualityList/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","消缺记录");
                    startActivity(intent);
                }
                else if( position==5 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getBasicSituation/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的统计");
                    startActivity(intent);
                }
            }
        });

        return listview;
    }

    //游客
    public ListView initList_300()
    {
        FinishPeddingItem listItem1 = new FinishPeddingItem();
        listItem1.setIv_icon1(R.drawable.home_dangan02);
        listItem1.setTv_title("消缺统计");
        listItems.add(listItem1);

        FinishPeddingItem listItem2 = new FinishPeddingItem();
        listItem2.setIv_icon1(R.drawable.home_dangan04);
        listItem2.setTv_title("消缺记录");
        listItems.add(listItem2);

        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new AdaperItem3( ActivityMain2.this, listItems );
        listItemAdapter.setListView(listview);
        // 添加并且显示
        listview.setAdapter(listItemAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String url;
                FinishPeddingItem item = (FinishPeddingItem) listview.getItemAtPosition(position);
                String formId = item.getTv_formid();

                if( position==0 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getBasicSituation/yk";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","消缺统计");
                    startActivity(intent);
                }
                else if( position==1 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getQualityList/yk/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","消缺记录");
                    startActivity(intent);
                }
            }
        });

        return listview;
    }

    public ListView initList_700()
    {
        FinishPeddingItem listItem1 = new FinishPeddingItem();
        listItem1.setIv_icon1(R.drawable.home_dangan01);
        listItem1.setTv_title("我的待办");
        listItem1.setTv_date(m_daiban);
        listItems.add(listItem1);

        FinishPeddingItem listItem2 = new FinishPeddingItem();
        listItem2.setIv_icon1(R.drawable.home_dangan02);
        listItem2.setTv_title("我的已办");
        listItems.add(listItem2);

        FinishPeddingItem listItem3 = new FinishPeddingItem();
        listItem3.setIv_icon1(R.drawable.home_dangan05);
        listItem3.setTv_title("我的关注");
        listItems.add(listItem3);

        FinishPeddingItem listItem4 = new FinishPeddingItem();
        listItem4.setIv_icon1(R.drawable.home_dangan04);
        listItem4.setTv_title("消缺记录");
        listItems.add(listItem4);

        FinishPeddingItem listItem5 = new FinishPeddingItem();
        listItem5.setIv_icon1(R.drawable.home_dangan02);
        listItem5.setTv_title("我的统计");
        listItems.add(listItem5);

        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new AdaperItem3( ActivityMain2.this, listItems );
        listItemAdapter.setListView(listview);
        // 添加并且显示
        listview.setAdapter(listItemAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String url;
                FinishPeddingItem item = (FinishPeddingItem) listview.getItemAtPosition(position);
                String formId = item.getTv_formid();

                if (position == 0)
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/waitDealQuality/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的待办");
                    startActivity(intent);
                }
                else if( position==1 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/yibanQuality/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的已办");
                    startActivity(intent);
                }
                else if( position==2 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getAttentionList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的关注");
                    startActivity(intent);
                }
                else if( position==3 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getQualityList/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","消缺记录");
                    startActivity(intent);
                }
                else if( position==4 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getBasicSituation/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的统计");
                    startActivity(intent);
                }
            }
        });

        return listview;
    }

    public ListView initList_800()
    {
        FinishPeddingItem listItem1 = new FinishPeddingItem();
        listItem1.setIv_icon1(R.drawable.home_dangan01);
        listItem1.setTv_title("我的督办");
        listItem1.setTv_date(m_duban);
        listItems.add(listItem1);

        FinishPeddingItem listItem2 = new FinishPeddingItem();
        listItem2.setIv_icon1(R.drawable.home_dangan05);
        listItem2.setTv_title("我的关注");
        listItems.add(listItem2);

        FinishPeddingItem listItem3 = new FinishPeddingItem();
        listItem3.setIv_icon1(R.drawable.home_dangan04);
        listItem3.setTv_title("消缺记录");
        listItems.add(listItem3);

        FinishPeddingItem listItem4 = new FinishPeddingItem();
        listItem4.setIv_icon1(R.drawable.home_dangan02);
        listItem4.setTv_title("消缺统计");
        listItems.add(listItem4);

        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new AdaperItem3( ActivityMain2.this, listItems );
        listItemAdapter.setListView(listview);
        // 添加并且显示
        listview.setAdapter(listItemAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String url;
                FinishPeddingItem item = (FinishPeddingItem) listview.getItemAtPosition(position);
                String formId = item.getTv_formid();

                if (position == 0)
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/dubanQuality/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的督办");
                    startActivity(intent);
                }
                else if( position==1 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getAttentionList/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的关注");
                    startActivity(intent);
                }
                else if( position==2 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getQualityList/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","消缺记录");
                    startActivity(intent);
                }
                else if( position==3 )
                {
                    url=""+appUrl+"/LHKAppServer/webQualityClear/getBasicSituation/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityMain2.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","消缺统计");
                    startActivity(intent);
                }
            }
        });

        return listview;
    }

    private void UpdateList(int mark)
    {
        if(mark==100) listItemAdapter.updateItem(0,m_duban);
        if(mark==200) listItemAdapter.updateItem(0,m_daiban);

        if(mark==300) listItemAdapter.updateItem(1,m_daiban_pingding);
        //if(mark==200) listItemAdapter.updateItem(0,"5");

        //listItemAdapter.updateItem(2,m_daiban);
        //listItemAdapter.updateItem(3,m_jieyue);
        //listItemAdapter.updateItem(4,m_xiazai);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        android.util.Log.d("cjwsjy", "------onResume-------");

        //开线程，发http请求，刷新待办条数
        Thread loginThread = new Thread(new ThreadDaiBan());
        loginThread.start();
    }

    @Override
    protected void onDestroy()
    {
        //unregisterReceiver(mMessageReceiver);
        super.onDestroy();

        boolean bresult = false;
        bresult = resideMenu.isOpened();

        if( bresult==true )
        {
            resideMenu.closeMenu();
        }

    }

    //更新
    class ThreadUpdate implements Runnable
    {
        @Override
        public void run()
        {
            boolean bresult = false;
            int length = 0;
            Message msg;
            String resultStr;
            String strbuf;
            String url;
            String apkurl;
            String errorstr;
            String strDuban;
            String strDaiban;
            String oldversion;

            JSONObject jsonObj;

            android.util.Log.d("cjwsjy", "------ThreadDaiBan-------");

            //apkurl = "http://dldir1.qq.com/qqmi/kt/snm/video/tv_video_3.2.0.1057_android_15000.apk";
            apkurl = "http://218.106.125.140:8099/LHKAppServer/suoluetu/2017_9_21.apk";

            //获取旧版本号
            oldversion = sp.getString("curVersion", "1.0");

            SharedPreferences.Editor editor = sp.edit();

            try
            {
                //请求软件版本
                url = appUrl+"/LHKAppServer/webQualityClear/getAppVersion";

                resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
                if(resultStr==null)
                {
                    strbuf = "网络连接失败，失败类型1013";
                    msg = handlers.obtainMessage();
                    msg.what = 1013;
                    msg.obj = strbuf;
                    handlers.sendMessage(msg);
                }

                length = resultStr.length();
                if(length==0)
                {
                    strbuf = "网络连接失败，失败类型1014";
                    msg = handlers.obtainMessage();
                    msg.what = 1014;
                    msg.obj = strbuf;
                    handlers.sendMessage(msg);
                }

                //服务器上APP的版本号
                jsonObj = new JSONObject(resultStr);
                m_duban = jsonObj.getString("androidVersion");

                //比较版本
                //比较通讯录版本
                bresult = oldversion.equals(m_duban);

                if(bresult==false)
                {
                    //软件要升级
                    msg = handlers.obtainMessage();
                    msg.what = 21;
                    msg.obj = apkurl;
                    handlers.SetText("发现新版本，是否下载？");
                    handlers.sendMessage(msg);
                }
                else
                {
                    msg = handlers.obtainMessage();
                    msg.what = 22;
                    handlers.sendMessage(msg);
                }

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                msg = handlers.obtainMessage();
                msg.what = 6;
                handlers.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                android.util.Log.d("cjwsjy", "------catch="+e.getLocalizedMessage()+"-------");

                //异常错误
                msg = handlers.obtainMessage();
                msg.what = 6;
                handlers.sendMessage(msg);
            }
        }
    }

    // 登录，验证用户密码
    class ThreadDaiBan implements Runnable
    {
        @Override
        public void run()
        {
            int length = 0;
            Message msg;

            String resultStr;
            String strbuf;
            String url;
            String errorstr;
            String strDuban;
            String strDaiban;

            JSONObject jsonObj;

            android.util.Log.d("cjwsjy", "------ThreadDaiBan-------");

            SharedPreferences.Editor editor = sp.edit();

            try
            {
                url = appUrl+"/LHKAppServer/webQualityClear/getTaskNum/"+m_loginname;

                resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
                if(resultStr==null)
                {
                    strbuf = "网络连接失败，失败类型1013";
                    msg = handlers.obtainMessage();
                    msg.what = 1013;
                    msg.obj = strbuf;
                    handlers.sendMessage(msg);
                }

                length = resultStr.length();
                if(length==0)
                {
                    strbuf = "网络连接失败，失败类型1014";
                    msg = handlers.obtainMessage();
                    msg.what = 1014;
                    msg.obj = strbuf;
                    handlers.sendMessage(msg);
                }

                //督办
                jsonObj = new JSONObject(resultStr);
                m_duban = jsonObj.getString("dubanNum");

                //待办
                m_daiban = jsonObj.getString("daibanNum");

                msg = handlers.obtainMessage();
                msg.what = 1;
                handlers.sendMessage(msg);

                //评定管理
                url = appUrl+"/LHKAppServer/webQualityClear/getTaskNum/"+m_loginname;
                resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
                if(resultStr==null)
                {
                    strbuf = "网络连接失败，失败类型1013";
                    msg = handlers.obtainMessage();
                    msg.what = 1013;
                    msg.obj = strbuf;
                    handlers.sendMessage(msg);
                }

                length = resultStr.length();
                if(length==0)
                {
                    strbuf = "网络连接失败，失败类型1014";
                    msg = handlers.obtainMessage();
                    msg.what = 1014;
                    msg.obj = strbuf;
                    handlers.sendMessage(msg);
                }

                //待办
                jsonObj = new JSONObject(resultStr);
                m_daiban_pingding = jsonObj.getString("todoNum");

                msg = handlers.obtainMessage();
                msg.what = 1025;
                handlers.sendMessage(msg);

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                msg = handlers.obtainMessage();
                msg.what = 6;
                handlers.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                android.util.Log.d("cjwsjy", "------catch="+e.getLocalizedMessage()+"-------");

                //异常错误
                msg = handlers.obtainMessage();
                msg.what = 6;
                handlers.sendMessage(msg);
            }
        }
    }

    // 登录，验证用户密码
    class ThreadDeviceToken implements Runnable
    {
        @Override
        public void run()
        {
            int length = 0;
            Message msg;

            String str_deviceToken;
            String str_appversion;
            String url;
            String jsonstr;
            String str_machine;
            String str_os;
            String str_createDate;
            JSONObject jsonObject;

            jsonObject = new JSONObject();

            str_appversion = sp.getString("curVersion", "1.0");
            str_deviceToken = sp.getString("deviceToken_id", "1.0");


            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); //获取当前年份
            int mMonth = c.get(Calendar.MONTH)+1;//获取当前月份
            int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

            str_createDate = mYear+"-"+mMonth+"-"+mDay;

            String model = Build.MODEL;//手机型号
            String versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号
            str_os = "android-"+versionos;

            //String model2 = model.replaceAll( " ", "%20");
            str_machine = model.replaceAll( " ", "_");

            try
            {
                jsonObject.put("userId", m_userid);
                jsonObject.put("userName", m_loginname);
                jsonObject.put("userDisplayName", m_diaplayname);
                jsonObject.put("devicesId", str_deviceToken);
                jsonObject.put("devicesType", "android");
                jsonObject.put("createDate", str_createDate);
                jsonObject.put("machine", str_machine);
                jsonObject.put("os", str_os);
                jsonObject.put("appversion", str_appversion);

                jsonstr = jsonObject.toString();

                url = appUrl+"/LHKAppServer/AppPushController/userDevices";

                HttpClientUtil.HttpUrlConnectionPostLog(url, jsonstr);
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                msg = handlers.obtainMessage();
                msg.what = 6;
                handlers.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                android.util.Log.d("cjwsjy", "------catch="+e.getLocalizedMessage()+"-------");

                //异常错误
                msg = handlers.obtainMessage();
                msg.what = 6;
                handlers.sendMessage(msg);
            }
        }
    }

    private static class MyHandler extends Handler
    {
        String m_text;

        private final WeakReference<ActivityMain2> mActivity;

        public MyHandler(ActivityMain2 activity) {
            mActivity = new WeakReference<ActivityMain2>(activity);
        }

        public void SetText(String strtext)
        {
            m_text = strtext;
        }

        @Override
        public void handleMessage(Message msg)
        {
            ActivityMain2 activity = mActivity.get();
            if (activity != null)
            {
                String text;
                UpdateManager upManager;

                switch( msg.what )
                {
                    case 1:
                        if(activity.m_rolecode==201 || activity.m_rolecode==308 ) activity.UpdateList(100);  //督办
                        else if(activity.m_rolecode==202 || activity.m_rolecode==307) activity.UpdateList(200);  //待办
                        break;
                    case 21:
                        //选择升级
                        upManager = new UpdateManager( activity, msg.obj.toString(),m_text );
                        upManager.GetMainactivity(activity);
                        upManager.checkUpdateInfo();
                        break;
                    case 22:
                        //提示当前APP是最新版
                        Toast.makeText( activity, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                        break;
                    case 23:
                        //更新完成
                        upManager = new UpdateManager( activity, msg.obj.toString(),m_text );
                        upManager.checkUpdateInfo3();
                        break;
                    case 1015:
                        text = msg.obj.toString();
                        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        break;
                    case 1025:
                        activity.UpdateList(300);  //评定管理 待办
                        break;
                    default:
                        text = msg.obj.toString();
                        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    private final MyHandler handlers = new MyHandler(this);

    private void showWin()
    {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ActivityMain2.this);
        normalDialog.setTitle("注销");
        normalDialog.setMessage("确定要注销吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 注销
                        Intent intent = new Intent();
                        intent.setClass( ActivityMain2.this, ActivityLogin.class);
                        startActivity(intent);

                        finish();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();

    	//MoreListPopupWindow popupWindow = new MoreListPopupWindow(ActivityDangan1.this, 0, 0);

    	// 显示窗口
    	//popupWindow.showAtLocation(findViewById(R.id.re_outofoffice_index),
    		//Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    // 侧滑
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemUpdate;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemphone;
    private ResideMenuItem itemSettings;
    protected Bitmap bmp_UserPhoto;

    private LoaderImpl impl;
    private Map<String,SoftReference<Bitmap>> sImageCache;*/

    // 侧滑
    private void setUpMenu()
    {
        // 获取当前登录用户登录名
        String userDisplayName = m_diaplayname;
        String jobNumber = "";

        //用户头像下载地址
        String resultStr = "";
        String photoUrl = "";

        //初始化侧边栏
        resideMenu = new ResideMenu(this, bmp_UserPhoto, userDisplayName,jobNumber);
        resideMenu.setBackground(R.mipmap.menu_01);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);

        itemCalendar = new ResideMenuItem(this, R.mipmap.img_slider_aboutus,"修改密码");
        itemUpdate = new ResideMenuItem(this, R.mipmap.img_slider_help, "更新");
        itemSettings = new ResideMenuItem(this, R.mipmap.img_slider_exit,"注销");

        itemCalendar.setOnClickListener(this);
        itemUpdate.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemUpdate, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);

        //绑定右上角图标单击事件滑出侧边栏
        iv_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT,3);
            }
        });
    }

    private Bitmap getimageforurl(String strUrl)
    {
        String resultStr = null;
        Bitmap bitmap = null;

        if( Build.VERSION.SDK_INT>=23 )
        {
            int Permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if( Permission!= PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return bitmap;
            }
        }

        bitmap = impl.getBitmap(strUrl);

        return bitmap;
    }

    // 侧滑
    // What good method is to access com.hsinfo.mixigou.resideMenu？
    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    // 侧滑
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            // Toast.makeText(mContext, "Menu is opened!",
            // Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            // Toast.makeText(mContext, "Menu is closed!",
            // Toast.LENGTH_SHORT).show();
        }
    };

    // 侧滑
    @Override
    public void onClick(View view)
    {
        // TODO 自动生成的方法存根
        if (view == itemHome)
        {
            // changeFragment(new HomeFragment());
            Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();
        }
        else if (view == itemUpdate)
        {

            //更新
            //Toast.makeText(getApplicationContext(), "努力开发中123...", Toast.LENGTH_SHORT).show();

            int ret = requestPermissionsM(Manifest.permission.WRITE_EXTERNAL_STORAGE,1031);
            if(ret!=1) return;

            //Thread upgradeThread = new Thread(new ThreadUpdate());
            //upgradeThread.start();

        }
        else if (view == itemCalendar)
        {
            //修改密码
            //Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();

            String surl=appUrl+"/LHKAppServer/goPassword/"+m_loginname;
            Intent intent = new Intent();
            intent.setClass(ActivityMain.this, WebViewCanteen.class);
            intent.putExtra("webUrl",surl);
            intent.putExtra("titleName","修改密码");
            startActivity(intent);


        }
        else if (view == itemphone)
        {
            //通讯录更新
            //开线程，版本升级
            //Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();
            //updataPhonebook();
        }
        else if (view == itemSettings)
        {

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ActivityMain.this);
            normalDialog.setTitle("注销");
            normalDialog.setMessage("确定要注销吗？");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // 注销
                            Intent intent = new Intent();
                            intent.setClass( ActivityMain.this, ActivityLogin.class);
                            startActivity(intent);

                            finish();
                        }
                    });
            normalDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //...To-do
                        }
                    });
            // 显示
            normalDialog.show();
        }

        resideMenu.closeMenu();
    }

    //申请1个权限
    private int requestPermissionsM(String strpermission, int code )
    {
        if( Build.VERSION.SDK_INT<23 ) return 1;

        //判断该权限
        int Permission = ContextCompat.checkSelfPermission(this, strpermission);
        if( Permission!= PackageManager.PERMISSION_GRANTED )
        {
            //没有权限，申请权限
            ActivityCompat.requestPermissions(this,new String[]{ strpermission}, code);
            return 1013;
        }

        return 1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        int length;
        length = grantResults.length;
        //android.util.Log.i("cjwsjy", "------length="+length+"-------Permissions");

        switch (requestCode)
        {
            case 1031:
                if( grantResults[0]== PackageManager.PERMISSION_GRANTED )
                {
                    // 同意
                    //Thread upgradeThread = new Thread(new ThreadUpdate());
                    //upgradeThread.start();
                }
                else
                {
                    // 拒绝
                    Toast.makeText(ActivityMain.this, "权限申请失败！请到\"设置\"->\"应用程序\"中打开权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1032:
                if( grantResults[0]== PackageManager.PERMISSION_GRANTED )
                {
                    // 同意
                    //login(4);
                }
                else
                {
                    // 拒绝
                    Toast.makeText(ActivityMain.this, "权限申请失败！请到\"设置\"->\"应用程序\"中打开权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}