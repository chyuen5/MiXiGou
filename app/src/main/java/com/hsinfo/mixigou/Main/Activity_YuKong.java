package com.hsinfo.mixigou.Main;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hsinfo.mixigou.ActivityLogin;
import com.hsinfo.mixigou.R;
import com.hsinfo.mixigou.Utils.HttpClientUtil;
import com.hsinfo.mixigou.Utils.UrlUtil;
import com.hsinfo.mixigou.WebView.WebViewCanteen;
import com.hsinfo.mixigou.adapter.AdaperItem3;
import com.hsinfo.mixigou.imagecache.LoaderImpl;
import com.hsinfo.mixigou.item.FinishPeddingItem;
import com.hsinfo.mixigou.resideMenu.ResideMenu;
import com.hsinfo.mixigou.resideMenu.ResideMenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_YuKong extends Activity implements OnClickListener
{
    private SharedPreferences sp;

    List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

    private AdaperItem3 listItemAdapter;

    private  TextView tv_sousuo;
    private  TextView tv_shoucang;
    private  TextView tv_jieyue;
    private  TextView tv_xiazai;//外出登记
    private  TextView tv_daiban;//更多
    private  TextView tv_dangan;
    private  TextView tv_faqi;
    private  TextView tv_zhuxiao;
    private  TextView tv_zaixian;
    private  ImageView iv_add;

    ListView listview;

    private int m_rolecode;
    private String m_loginname;
    private String m_diaplayname;
    private String userId;
    private String appUrl;
    private String m_jieyue = "0";
    private String m_xiazai = "0";
    private String m_duban = "0";
    private String m_daiban = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_jindu);

        String strurl = "";
        String strrolecode = "";

        appUrl = UrlUtil.HOST;

        //获取用户登录名
		sp = getSharedPreferences("userdata", 0);
        m_loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		userId = sp.getString("USERDATA.USER.ID", "");
        strrolecode = sp.getString("USERDATA.ROLE.CODE", "");
        m_diaplayname = sp.getString("USERDATA.DISPLAY.NAME", "");
        m_duban = sp.getString("USERDATA.DUBAN.NUM", "0");
        m_daiban = sp.getString("USERDATA.DAIBAN.NUM", "0");

        m_loginname = "admin";
        m_rolecode = Integer.parseInt(strrolecode);

        iv_add = (ImageView) findViewById(R.id.iv_add);
        listview = (ListView) findViewById(R.id.list_dangan);
        tv_zhuxiao = (TextView) findViewById(R.id.tv_title3);

        if(m_rolecode==300)
        {
            iv_add.setVisibility(View.GONE);
            tv_zhuxiao.setVisibility(View.VISIBLE);
        }

        tv_zhuxiao.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(Activity_YuKong.this);
                normalDialog.setTitle("注销");
                normalDialog.setMessage("确定要注销吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 注销
                                Intent intent = new Intent();
                                intent.setClass( Activity_YuKong.this, ActivityLogin.class);
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

        listview.setHeaderDividersEnabled(true);
        listview.setFooterDividersEnabled(true);

        // 侧滑
        setUpMenu();

        iv_add.setOnClickListener(new OnClickListener()
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

        TextView tv_title = (TextView) this.findViewById(R.id.tv_title2);
        tv_title.setText("预控管理");

        // 后退
        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
 		iv_back.setOnClickListener(new OnClickListener()
 		{
 			@Override
 			public void onClick(View v)
 			{
 				finish();
 			}
 		});

        initList_100();
    }

    //领导
    public ListView initList_100()
    {
        FinishPeddingItem listItem1 = new FinishPeddingItem();
        listItem1.setIv_icon1(R.mipmap.home_yukong01);
        listItem1.setTv_title("培训教育");
        //listItem1.setTv_date(m_duban);
        listItems.add(listItem1);

        FinishPeddingItem listItem2 = new FinishPeddingItem();
        listItem2.setIv_icon1(R.mipmap.home_yukong02);
        listItem2.setTv_title("现场交底");
        listItems.add(listItem2);

        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new AdaperItem3( Activity_YuKong.this, listItems );
        listItemAdapter.setListView(listview);
        // 添加并且显示
        listview.setAdapter(listItemAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                String url;
                FinishPeddingItem item = (FinishPeddingItem) listview.getItemAtPosition(position);
                String formId = item.getTv_formid();

                if (position == 0)
                {
                    url=appUrl+"/MxgApp/webSafety/goaqpxlist/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(Activity_YuKong.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","培训教育");
                    startActivity(intent);
                }
                else if( position==1 )
                {
                    url=appUrl+"/MxgApp/webSafety/xcjdlist/"+m_loginname+"/1";
                    Intent intent = new Intent();
                    intent.setClass(Activity_YuKong.this, WebViewCanteen.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","现场交底");
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
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //android.util.Log.d("cjwsjy", "------onResume-------");

        //开线程，发http请求，刷新待办条数
        //Thread loginThread = new Thread(new ThreadDaiBan());
        //loginThread.start();
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
                    msg = handler.obtainMessage();
                    msg.what = 1013;
                    msg.obj = strbuf;
                    handler.sendMessage(msg);
                }

                length = resultStr.length();
                if(length==0)
                {
                    strbuf = "网络连接失败，失败类型1014";
                    msg = handler.obtainMessage();
                    msg.what = 1014;
                    msg.obj = strbuf;
                    handler.sendMessage(msg);
                }

                //待办
                jsonObj = new JSONObject(resultStr);
                m_duban = jsonObj.getString("TheNum");
                m_daiban = jsonObj.getString("TheNum");

                msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                msg = handler.obtainMessage();
                msg.what = 6;
                handler.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                android.util.Log.d("cjwsjy", "------catch="+e.getLocalizedMessage()+"-------");

                //异常错误
                msg = handler.obtainMessage();
                msg.what = 6;
                handler.sendMessage(msg);
            }
        }
    }

    // Handler
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            this.obtainMessage();
            String text;
            switch (msg.what)
            {
                case 1:
                    if(m_rolecode==201 || m_rolecode==308 ) UpdateList(100);  //督办
                    else if(m_rolecode==202 || m_rolecode==307) UpdateList(200);  //待办
                    break;
                case 1015:
                    text = msg.obj.toString();
                    Toast.makeText(getApplicationContext(), text,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void showWin()
    {
    	//MoreListPopupWindow popupWindow = new MoreListPopupWindow(ActivityDangan1.this, 0, 0);

    	// 显示窗口
    	//popupWindow.showAtLocation(findViewById(R.id.re_outofoffice_index),
    		//Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    // 侧滑
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemphone;
    private ResideMenuItem itemSettings;
    protected Bitmap bmp_UserPhoto;

    private LoaderImpl impl;
    private Map<String,SoftReference<Bitmap>> sImageCache;

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

        //itemProfile = new ResideMenuItem(this, R.drawable.img_slider_help, "在岗状态");
        itemCalendar = new ResideMenuItem(this, R.mipmap.img_slider_aboutus,"修改密码");
        itemSettings = new ResideMenuItem(this, R.mipmap.img_slider_exit,"注销");

        //itemProfile.setOnClickListener(this);
        itemCalendar.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        //resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);

        //绑定右上角图标单击事件滑出侧边栏
        iv_add.setOnClickListener(new OnClickListener()
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
    // What good method is to access resideMenu？
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
        else if (view == itemProfile)
        {
            Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();
            // changeFragment(new ProfileFragment());
        }
        else if (view == itemCalendar)
        {
            //修改密码
            //Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();

            String surl=appUrl+"/LHKAppServer/goPassword/"+m_loginname;
            Intent intent = new Intent();
            intent.setClass(Activity_YuKong.this, WebViewCanteen.class);
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

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(Activity_YuKong.this);
            normalDialog.setTitle("注销");
            normalDialog.setMessage("确定要注销吗？");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // 注销
                            Intent intent = new Intent();
                            intent.setClass( Activity_YuKong.this, ActivityLogin.class);
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

}