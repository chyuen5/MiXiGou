package com.hsinfo.mixigou;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hsinfo.mixigou.Main.ActivityMain;
import com.hsinfo.mixigou.Utils.HttpClientUtil;
import com.hsinfo.mixigou.Utils.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class ActivityLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private String chuchaistr;
    private String qianbaostr;
    private String userid;
    private String m_departid;
    private String loginName;
    private String password;
    private String isMeeting;
    private String m_phonesim;
    private String m_versionapp;
    private String m_urlhost;

    private int retState;
    private ProgressDialog dialog;

    private TextView TextV_youke;
    private EditText edit_text_ips;
    private EditText edit_text_ports;
    private Button loginBtn;
    private EditText edit_user;
    private EditText edit_password;
    private ProgressDialog mDialog;
    private SharedPreferences sp;

    private double lat1;
    private double lat2;
    private double lng1;
    private double lng2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        chuchaistr = "";
        qianbaostr = "";
        isMeeting = "";
        userid = "";
        m_departid = "";

        sp = getSharedPreferences("userdata", 0);
        //m_urlhost = UrlUtil.HOST;
        m_urlhost = "http://218.106.125.140:8099";

        edit_user = (EditText)findViewById(R.id.et_usertel);
        edit_password = (EditText) findViewById(R.id.et_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        TextV_youke = (TextView) findViewById(R.id.tv_youke);

        loginName = sp.getString("USERDATA.LOGIN.NAME", "");
        password = sp.getString("USERDATA.LOGIN.PASSWORD", "");

        edit_user.setText(loginName);
        edit_password.setText(password);
        edit_password.requestFocus();

        PackageManager manager = this.getPackageManager();
        PackageInfo info;

        //获得版本号
        try
        {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;

            //保存
			Editor editor = sp.edit();
			editor.putString("curVersion", version);
			editor.commit();
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }

        m_versionapp = sp.getString("curVersion", "1.0");  //APP版本
        //m_versionapp = "1.0";

        // 登录
        loginBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                onLogin1(1);
            }
        });

        TextV_youke.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                onLogin_youke(1);
            }
        });

    }

    //登录
    public void onLogin_youke(int debug)
    {
        Editor editor = sp.edit();

        //保存
        editor.putString("USERDATA.ROLE.CODE", "300");
        editor.commit();

        gotoMainActivity(1);
    }

    //登录
    public void onLogin1(int debug)
    {
        int oldmouth = 0;
        String mouth;
        String number = "";

        int mHour;
        int mMinute;
        int mYear;
        int mMonth;
        int mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR); //获取当前年份
        mMonth = c.get(Calendar.MONTH);//获取当前月份
        mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
        mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
        mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数

        Calendar cale = Calendar.getInstance();
        int nmouth = cale.get(Calendar.MONTH);


        loginName = edit_user.getText().toString().trim();
        password = edit_password.getText().toString();

        //loginName = "liangzhengang";
        //loginName = "zhangbingbing";
        //password = "123456";

        //GetPhoneState();

        // 判断账号和用户名不能为空
        if (loginName.trim().equals(""))
        {
            Toast.makeText(getApplicationContext(), "用户名或者密码不能为空!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mDialog = new ProgressDialog(ActivityLogin.this);
            mDialog.setTitle("登录");
            mDialog.setMessage("正在登录服务器，请稍后...");
            mDialog.setCancelable(true);  // 设置是否可以通过点击Back键取消
            mDialog.setCanceledOnTouchOutside(false);  // 设置在点击Dialog外是否取消Dialog进度条
            mDialog.show();

            Thread Thread_DaiBan = new Thread(new ThreadDaiBan());
            Thread_DaiBan.start();

            //登录验证用户密码
            Thread loginThread = new Thread(new ThreadLogin());
            loginThread.start();
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

            SharedPreferences.Editor editor = sp.edit();

            try
            {
                url = m_urlhost+"/LHKAppServer/webQualityClear/getTaskNum/"+loginName;

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

                //获取名字汉字
                jsonObj = new JSONObject(resultStr);
                strDuban = jsonObj.getString("dubanNum");

                //获取角色
                strDaiban = jsonObj.getString("daibanNum");

                //保存
                editor.putString("USERDATA.DUBAN.NUM", strDuban);
                editor.putString("USERDATA.DAIBAN.NUM", strDaiban);
                editor.commit();
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

    // 登录，验证用户密码
    class ThreadLogin implements Runnable
    {
        @Override
        public void run()
        {
            Message msg;
            boolean bresult = false;
            boolean bresult2 = false;
            boolean bresult3 = false;
            int i = 0;
            int result = 0;
            int version = 0;
            int length = 0;
            int flag = 0;
            String url = "";
            String strurl2 = "";
            String resultStr = "";
            String resultStr2 = "";
            String errorstr;
            String strbuf = "";
            String model;
            String model2 = "";
            String versionos = "";
            String roles = "";
            String realname = "";
            String userid = "";

            JSONObject jsonObj;

            model = Build.MODEL;//手机型号
            version = Build.VERSION.SDK_INT;//SDK版本号
            versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号

            model2 = model.replaceAll( " ", "%20");
            strbuf = "/Android-"+versionos+"-"+model2+"-"+m_versionapp;

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("meeting", isMeeting);
            editor.commit();

            if(false)
            {
                msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
                return;
            }

            //外网
            url = m_urlhost+"/LHKAppServer/login/"+loginName+"/"+password;

            //内网
            //url = "http://10.6.189.67/XSAppServer/login/"+loginName+"/"+password;

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

            try
            {
                if(length<30)
                {
                    jsonObj = new JSONObject(resultStr);
                    errorstr = jsonObj.getString("error");

                    //用户名或者密码错误
                    msg = handler.obtainMessage();
                    msg.what = 1015;
                    msg.obj = errorstr;
                    handler.sendMessage(msg);
                    return;
                }

                //获取名字汉字
                jsonObj = new JSONObject(resultStr);
                realname = jsonObj.getString("realname");

                //获取角色
                roles = jsonObj.getString("roleCode");

                userid = jsonObj.getString("id");

                android.util.Log.i("cjwsjy", "--------roles="+roles+"-------onBackPressed");

                //loginName = "admin";
                //保存
                editor.putBoolean("SAVE_INFO", true);
                editor.putString("USERDATA.LOGIN.NAME", loginName);
                editor.putString("USERDATA.LOGIN.PASSWORD", password);
                editor.putString("USERDATA.DISPLAY.NAME", realname);
                editor.putString("USERDATA.ROLE.CODE", roles);
                editor.putString("USERDATA.USER.ID", userid);
                editor.commit();

                //登录成功
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
                    mDialog.dismiss();
                    gotoMainActivity(1);
                    break;
                case 1013:
                    mDialog.dismiss();
                    text = msg.obj.toString();
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    break;
                case 1014:
                    mDialog.dismiss();
                    text = msg.obj.toString();
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    break;
                case 1015:
                    mDialog.dismiss();
                    text = msg.obj.toString();
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if( mDialog!=null ) { mDialog.cancel(); }

                    Toast.makeText(getApplicationContext(), "登录失败，失败类型1037", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void showToast(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void gotoMainActivity(int debug)
    {
        //跳转123
        Intent intent = new Intent();
        if(debug==1) intent.setClass(ActivityLogin.this, ActivityMain.class);
        else if(debug==2) intent.setClass(ActivityLogin.this, ActivityMain.class);
        else if(debug==3) intent.setClass(ActivityLogin.this, ActivityMain.class);

        //intent.putExtra("chuchai", chuchaistr);
        //intent.putExtra("qianbao", qianbaostr);
        startActivity(intent);

        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        //mDialog.dismiss();
        //gotoMainActivity(2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
