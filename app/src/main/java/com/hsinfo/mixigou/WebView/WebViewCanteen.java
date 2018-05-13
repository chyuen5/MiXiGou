package com.hsinfo.mixigou.WebView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hsinfo.mixigou.Main.EmployeePopupWindow;
import com.hsinfo.mixigou.R;

import java.io.File;
import java.io.IOException;

/**
 * webView页面，食堂使用，初始化时会调JavaScript函数
 */
public class WebViewCanteen extends AppCompatActivity {

	private int m_downmode = 0;

	private int m_ShiLian = 0;

	private String webUrl;
	private String fileUrl = "";
	private String m_userId;
	private String m_telnumber;
	private String m_JavaScript;
	private String titleName;
	private String attachmentName = "";
	private String m_ChannelName = "";

	private TextView tv_navtitle;
	private WebView mWebView;
	private ImageView m_imagev_back;

	private Message msg;
	private ProgressDialog m_progressDialog;
	private ProgressDialog mDialog;

	private RelativeLayout layout;

	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	private Notification mnotify;

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
	private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    private String mCameraPhotoPath;

	private boolean isRedirect = false;// 是否有重定向的页面

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.activity_webview);

		m_downmode = 0;
		layout = (RelativeLayout) findViewById(R.id.titletop2);

		webUrl = getIntent().getExtras().get("webUrl").toString();

		//title
		tv_navtitle = (TextView) this.findViewById(R.id.tv_navtitle);
		if (getIntent().getExtras().get("titleName") != null) {
			titleName = getIntent().getExtras().get("titleName").toString();
		}
		tv_navtitle.setText(titleName);

		//隐藏标题栏
		//layout.setVisibility(View.GONE);

		//后退
		m_imagev_back = (ImageView) this.findViewById(R.id.iv_back);
		m_imagev_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//onClickButton();
				onBackPressed();
			}
		});

		//搜索
		ImageView iv_serachs = (ImageView) this.findViewById(R.id.iv_add);
		iv_serachs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CallJavaScript3("123");
			}
		});

		//通知栏
		//定义一个PendingIntent点击Notification后启动一个Activity
		//Intent it = new Intent(WebViewBQ.this, OtherActivity.class);
		//PendingIntent pit = PendingIntent.getActivity(WebViewBQ.this, 0, it, 0);

		//获取状态通知栏管理
		mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		//实例化通知栏构造器NotificationCompat.Builder：
		mBuilder = new NotificationCompat.Builder(this);


		mBuilder.setContentTitle("Picture Download")
				.setContentText("Download in progress")
				.setSmallIcon(R.mipmap.ic_launcher);

		//调用Builder的build()方法为notification赋值
		mnotify = mBuilder.build();

		initWebView();

	}

	private void initWebView() {
		WebView webView = new WebView(WebViewCanteen.this);
		mWebView = (WebView) findViewById(R.id.webView_home);
		WebSettings webSettings = mWebView.getSettings();

		mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

		webSettings.setDomStorageEnabled(true);

		//设置页面自适应手机屏幕
		webSettings.setUseWideViewPort(false);
		webSettings.setLoadWithOverviewMode(true);

		//第二种方法，设置满屏显示
		//webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

		//不显示webview缩放按钮
		webSettings.setDisplayZoomControls(false);

		//String ua = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; EmbeddedWB 14.52 from: http://www.bsalsa.com/ EmbeddedWB 14.52; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
		//webSettings.setUserAgentString(ua);
		webSettings.setBlockNetworkImage(false);    //图片下载阻塞

		mWebView.setWebChromeClient(new WebChromeClient()
        {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				//setTitle("页面加载中，请稍候..." + progress + "%");
				setProgress(progress * 100);
				if (progress == 100) {
					setTitle(R.string.app_name);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			// For Android < 3.0
			public void openFileChooser(ValueCallback<Uri> valueCallback) {
				mUploadMessage = valueCallback;
				openImageChooserActivity();
			}

			// For Android  >= 3.0
			public void openFileChooser(ValueCallback valueCallback, String acceptType) {
				mUploadMessage = valueCallback;
				openImageChooserActivity();
			}

			//For Android  >= 4.1
			public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
				mUploadMessage = valueCallback;
				openImageChooserActivity();
			}

			// For Android >= 5.0
			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
			{
				uploadMessage = filePathCallback;
				popupDialog();
				//openImageChooserActivity();
				return true;
			}
		});

		//可以弹出虚拟键盘输入
		//mWebView.requestFocus();
		//mWebView.requestFocusFromTouch();

		// 设置支持JavaScript
		webSettings.setJavaScriptEnabled(true);

		final JsOperation myJavaScriptInterface = new JsOperation(this);

		mWebView.addJavascriptInterface(myJavaScriptInterface, "android");

		//支持通过JS打开新窗口
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		//使用缓存
		webSettings.setAppCacheEnabled(true);
		//UTF-8编码
		webSettings.setDefaultTextEncodingName("UTF-8");

		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDomStorageEnabled(true);

		webSettings.setDatabaseEnabled(true);
		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
		webView.getSettings().setAppCachePath(appCachePath);
		webView.getSettings().setAllowFileAccess(true);

		mWebView.loadUrl(webUrl);

		Log.d("cjwsjy", "------loadUrl=" + webUrl + "-------BQ");

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("cjwsjy", "------Loading=" + url + "-------BQ");

				int result = 0;
				String strurl;

				//电话
				result = url.indexOf("tel:");
				if (result != -1) {
					m_telnumber = url;
					int ret = requestPermissions(Manifest.permission.CALL_PHONE, 103);
					if (ret != 1) return true;

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse(url));
					startActivity(intent);

					//停止加载
					view.stopLoading();
					return true;
				}

				//食堂视频二楼
				result = url.indexOf("/shitangshipin1");
				if (result != -1) {
					m_ChannelName = "1000000012001";
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 106);
					if (ret != 1) return true;

					StartShiLian();

					//停止加载
					view.stopLoading();
					return true;
				}

				//食堂视频三楼
				result = url.indexOf("/shitangshipin2");
				if (result != -1) {
					m_ChannelName = "1000000012002";
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 106);
					if (ret != 1) return true;

					StartShiLian();

					//停止加载
					view.stopLoading();
					return true;
				}

				//食堂视频四楼
				result = url.indexOf("/shitangshipin3");
				if (result != -1) {
					m_ChannelName = "1000000012003";
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 106);
					if (ret != 1) return true;

					StartShiLian();

					//停止加载
					view.stopLoading();
					return true;
				}

				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				//Log.d("cjwsjy", "---------PageStarted="+url+"-------BQ");
				showProgressDialog("正在加载 ，请等待...", true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				closeProgressDialog();
				mWebView.getSettings().setBlockNetworkImage(false);

				//调页面JavaScript函数
				if (m_downmode == 0) {
					CallJavaScript(m_JavaScript);
					m_downmode++;
				}
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				handler.proceed();
			}
		});
	}

	class JsOperation
	{

		Activity mActivity;

		public JsOperation(Activity activity) {
			mActivity = activity;
		}

		@JavascriptInterface
		public void OnHide()
		{
			Log.d("cjwsjy", "------OnHide-------JavascriptInterface");

			msg = handler.obtainMessage();
			msg.what = 113;
			handler.sendMessage(msg);

			//禁用
			//m_imagev_back.setEnabled(false);
			//隐藏标题栏
			//layout.setVisibility(View.GONE);  //View.INVISIBLE
		}

		@JavascriptInterface
		public void OnShow()
		{
			Log.d("cjwsjy", "------OnShow-------JavascriptInterface");

			msg = handler.obtainMessage();
			msg.what = 114;
			handler.sendMessage(msg);

			//启用
			//m_imagev_back.setEnabled(true);

			//显示标题栏
			//layout.setVisibility(View.VISIBLE);
		}

        @JavascriptInterface
        public void OnExit()
        {
            Log.d("cjwsjy", "------OnShow-------JavascriptInterface");

            msg = handler.obtainMessage();
            msg.what = 115;
            handler.sendMessage(msg);
        }
	}


	public void cancelfilePathCallback()
	{
		if(uploadMessage!=null)
		{
			uploadMessage.onReceiveValue(null);
			uploadMessage = null;
		}

		if(mUploadMessage!=null)
		{
			mUploadMessage.onReceiveValue(null);
			mUploadMessage = null;
		}
	}

	private void popupDialog()
	{
		//弹出菜单
		EmployeePopupWindow menuWindow = new EmployeePopupWindow(this, 0, 3);

		// 显示窗口
		menuWindow.showAtLocation(
				this.findViewById(R.id.layout_webview),
				Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	//选择相册
	public void openImageChooserActivity()
	{
        int ret = requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, 104);
        if (ret != 1) return;

        openImageChooserActivity2();
	}

    public void openImageChooserActivity2()
    {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent intent = Intent.createChooser(i, "Image Chooser");
        startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
    }

	//拍照
	public void openCameraActivity()
	{
        int ret = requestPermissions(Manifest.permission.CAMERA, 103);
        if (ret != 1) return;

        openCameraActivity2();

	}

    public void openCameraActivity2()
    {
        File file = new File(Environment.getExternalStorageDirectory(), "/Download/com.cjwsjy.app/zhanghe.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

        Uri imageUri = FileProvider.getUriForFile(WebViewCanteen.this, "com.cizhu.application.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent,1013);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==FILE_CHOOSER_RESULT_CODE || requestCode==1013)
		{
			if (null == mUploadMessage && null == uploadMessage) return;

			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (uploadMessage != null)
			{
				onActivityResultAboveL(requestCode, resultCode, data);
			}
			else if (mUploadMessage != null)
			{
				mUploadMessage.onReceiveValue(result);
				mUploadMessage = null;
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent)
	{
		if( uploadMessage==null ) return;

		Uri[] results = null;
		if(requestCode==FILE_CHOOSER_RESULT_CODE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				if (intent != null)
				{
					String dataString = intent.getDataString();
					ClipData clipData = intent.getClipData();
					if (clipData != null) {
						results = new Uri[clipData.getItemCount()];
						for (int i = 0; i < clipData.getItemCount(); i++) {
							ClipData.Item item = clipData.getItemAt(i);
							results[i] = item.getUri();
						}
					}
					if (dataString != null)
						results = new Uri[]{Uri.parse(dataString)};
				}
			}
		}

		else if(requestCode==1013)
		{
			String imagePath = Environment.getExternalStorageDirectory()+"/Download/com.cjwsjy.app/zhanghe.jpg";
			if (!TextUtils.isEmpty(imagePath))
			{
				results = new Uri[]{Uri.parse("file:///" + imagePath)};
			}
		}

		uploadMessage.onReceiveValue(results);
		uploadMessage = null;

		mUploadMessage = null;
	}

	//在sdcard卡创建缩略图
	//createImageFileInSdcard
	@SuppressLint("SdCardPath")
	private File createImageFile() {
		//mCameraPhotoPath="/mnt/sdcard/tmp.png";
		File file=new File(Environment.getExternalStorageDirectory()+"/","tmp.png");
		mCameraPhotoPath=file.getAbsolutePath();
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public void onResume()
	{
		super.onResume();
	}

	//加载对话框
	private void showProgressDialog(String msg, boolean cancelable) {
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewCanteen.this);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mDialog.setMessage(msg);
			mDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mDialog.setCancelable(cancelable);// 设置进度条是否可以按退回键取消
			// WebViewHome.this
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		} else {
			mDialog.setMessage(msg);
			mDialog.show();
		}
	}

	private void closeProgressDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	protected void CallJavaScript(String filename) {
		String call;

		Log.i("cjwsjy", "------"+filename+"-------CallJavaScript");

		//call = "javascript:getUserInfo('aachenchen123')";
		call = "javascript:getUserInfo('" + filename + "')";

		mWebView.loadUrl(call);
	}

	protected void CallJavaScript2(String filename) {
		String call;

		Log.d("cjwsjy", "------Deletename=" + filename + "-------DeleteFromPhone");
		//call = "javascript:DeleteFromPhone('aachenchen123')";
		call = "javascript:DeleteFromPhone('" + filename + "')";

		mWebView.loadUrl(call);
	}

	protected void CallJavaScript3(String filename) {
		String call;

		call = "javascript:searchHead()";

		mWebView.loadUrl(call);
	}

	private void StartShiLian()
	{
		m_ShiLian = 0;
		m_progressDialog = ProgressDialog.show(WebViewCanteen.this, "加载中...", "正  在  获  取  数  据 ...");

		//Thread loginThread1 = new Thread(new ThreadConnect1());
		//loginThread1.start();

		//Thread loginThread2 = new Thread(new ThreadConnect2());
		//loginThread2.start();
	}

	private void LoginShiLian(String IPaddress, int ports) {
		/*int retState;
		int nport = 0;
		//String IPaddress;
		//String ports;
		String appid;
		String auth;

		Intent i = new Intent(this, NetworkStateService.class);
		startService(i);
		//初始化SDK
		QYSDK.InitSDK(4);
		//创建session会话
		session = QYSDK.CreateSession(getApplicationContext());

		//IPaddress = "117.28.255.16";
		//IPaddress = "218.106.125.147";  //外网
		//IPaddress="172.16.13.70";  //内网
		//IPaddress="zzh.cjwsjy.com.cn";	//域名
		//ports = "39100";

		nport = ports;
		appid = "wholeally";
		auth = "czFYScb5pAu+Ze7rXhGh/+MesDXVJaszykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";

		m_progressDialog = ProgressDialog.show(WebViewCanteen.this, "加载中...", "正  在  获  取  数  据 ...");
		retState = session.SetServer(IPaddress, nport);//连接服务器大于或等于0为成功  否则为失败
		if (retState >= 0) {
			//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
			session.ViewerLogin(appid, auth, new QYSession.OnViewerLogin() {
				@Override
				public void on(int ret) {
					if (ret >= 0) {
						//登录成功
						m_progressDialog.cancel();
						Intent intent = new Intent(WebViewCanteen.this, QyVideoControlActivity2.class);
						intent.putExtra("channelName",m_ChannelName);
						startActivity(intent);
					} else {
						//登录失败
						m_progressDialog.cancel();

						if (m_ShiLian == 0) {
							LoginShiLian("218.106.125.147", 39100);  //外网
							m_ShiLian = 1;
						} else
							Toast.makeText(WebViewCanteen.this, "登录失败:" + String.valueOf(ret) + ";或者ViewerLogin第一个或第二个参数错误", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		else
		{
			m_progressDialog.cancel();
			Toast.makeText(WebViewCanteen.this, "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();
			//return 1013;

		}*/
	}

	// Handler
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			String strmsg = "";
			Intent intent;

            Log.i("cjwsjy", "------msg.what="+msg.what+"-------");

			this.obtainMessage();
			if( m_progressDialog!=null ) m_progressDialog.cancel();

			switch (msg.what)
			{
				case 103:  //成功
					//内网和外网只运行一个
					if(m_ShiLian==0)
					{
						m_ShiLian++;
//						intent = new Intent(WebViewCanteen.this, QyVideoControlActivity2.class);
//						intent.putExtra("channelName", m_ChannelName);
//						intent.putExtra("session", "1");
//						startActivity(intent);
					}
					break;
				case 104:  //失败
					strmsg = "登录失败:"+msg.obj.toString()+";或者ViewerLogin第一个或第二个参数错误";
					//Toast.makeText(WebViewCanteen.this, strmsg, Toast.LENGTH_SHORT).show();
					break;
				case 105:  //失败
					//Toast.makeText(WebViewCanteen.this, "服务器连接失败:"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				case 106:  //成功
					//内网和外网只运行一个
					if(m_ShiLian==0)
					{
						m_ShiLian++;
//						intent = new Intent(WebViewCanteen.this, QyVideoControlActivity2.class);
//						intent.putExtra("channelName",m_ChannelName);
//						intent.putExtra("session","2");
//						startActivity(intent);
					}
					break;
				case 113:  //禁用回退键
					m_imagev_back.setEnabled(false);
					break;
				case 114:  //启用回退键
					m_imagev_back.setEnabled(true);
					break;
                case 115:  //退出
					//ShowDialogNative(3);
					mWebView.loadUrl("about:blank");
					WebViewCanteen.this.finish();
                    break;
				default:
					if( mDialog!=null ) { mDialog.cancel(); }

					Toast.makeText(getApplicationContext(), "登录失败，失败类型1037",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		//清缓存
		mWebView.clearCache(true);
		mWebView.clearHistory();
		mWebView.clearFormData();

	}

	private int requestPermissions(String strpermission, int code )
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
		String url;

		switch (requestCode)
		{
            case 103:  //拍照
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 同意
                    openCameraActivity2();
                }
                else
                {
                    // 拒绝
                    Toast.makeText(WebViewCanteen.this, "没有权限",Toast.LENGTH_SHORT).show();
                }
                break;
			case 104:  //相册
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
                    openImageChooserActivity2();
				}
				else
				{
					// 拒绝
                    Toast.makeText(WebViewCanteen.this, "没有权限",Toast.LENGTH_SHORT).show();
				}
				break;
			case 105:  //取货
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					url = "quhuo";
//					Intent intent = new Intent(WebViewCanteen.this, CaptureActivity.class);
//					intent.putExtra("canteen",url);
//					startActivity(intent);
				}
				else
				{
					// 拒绝
					showdialog("没有权限");
                    Toast.makeText(WebViewCanteen.this, "没有权限",Toast.LENGTH_SHORT).show();
				}
				break;
			case 106:  //食堂视频
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					StartShiLian();
				}
				else
				{
					// 拒绝
                    Toast.makeText(WebViewCanteen.this, "没有权限",Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	protected void onClickButton()
	{
		String call = "javascript:sayHello()";

		call = "javascript:test('aachenchen123')";
		//call = "javascript:test(\"" + "content" + "\")";
		//call = "javascript:toastMessage(\"" + "content" + "\")";
		//call = "javascript:sumToJava(1,2)";

		mWebView.loadUrl(call);
	}

	private int showdialog(String content)
	{
//		CustomDialog.Builder builder = new CustomDialog.Builder(this);
//		builder.setMessage(content);
//		builder.setTitle("提示");
//		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
//		{
//			public void onClick(DialogInterface dialog, int which)
//			{
//				dialog.dismiss();
//				//设置你的操作事项
//				//WebViewCanteen.this.finish();
//			}
//		});
//
//		builder.create().show();
		return 1;
	}

	private int ShowDialogNative(int num)
	{
		//弹出窗口
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		if( num==2 )
		{
			builder.setTitle("返回");
			builder.setMessage("返回将清除页面所有数据");
		}
		else if( num==3 )
		{
			builder.setTitle("取消");
			builder.setMessage("取消将清除页面所有数据");
		}
		else
		{
			builder.setTitle("退出");
			builder.setMessage("确认退出吗？");
		}

		//添加"确认"按钮
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d("kaoyan", "------onClick-------");
				dialog.dismiss();

				mWebView.loadUrl("about:blank");
				WebViewCanteen.this.finish();
			}
		});

		//添加"取消"按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d("kaoyan", "------cancel-------");
				//dialog.dismiss();
				//Main.this.finish();
				//AppManager.getAppManager().AppExit(mActivity);
			}
		});

		builder.create();
		builder.show();

		return 1;
	}

	@Override
	public void onBackPressed()
	{
		int len = 0;
		String urlstr = mWebView.getUrl();
		String buff;

        Log.i("cjwsjy", "--------url="+urlstr+"-------onBackPressed");

		m_imagev_back.setEnabled(true);
        //layout.setVisibility(View.VISIBLE);

		len = urlstr.length();
		if( len>70 ) buff = urlstr.substring(0,70);
		else buff = "1";
        //消缺统计 弹出确认框
//		if( urlstr.contains("/getBasicSituation/") )
//		{
//			ShowDialogNative(1);
//			return;
//		}
		
		//添加消缺 弹出确认框
		if( urlstr.contains("/goAddTQuality/") )
		{
			ShowDialogNative(2);
			return;
		}

		//评定管理，待办
		if( buff.contains("/getDygcqbsList/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//待办
		if( urlstr.contains("/waitDealQuality/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//督办
		if( urlstr.contains("/dubanQuality/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//u_waitdeal_list.jsp
		if( urlstr.contains("u_waitdeal_list") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//菜谱
		if( urlstr.contains("/STManage/caipu/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

        //点评
        if (urlstr.contains("/STManage/dianping/"))
        {
            mWebView.loadUrl("about:blank");
            this.finish();
            return;
        }

		//预定
		if (urlstr.contains("/STManage/yuding_room/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
        if (urlstr.contains("/STManage/yuding_dish/"))
        {
            mWebView.loadUrl("about:blank");
            this.finish();
            return;
        }

		//我的
		if (urlstr.contains("/STManage/my_account/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if (mWebView.canGoBack())
		{
			if (mWebView.getUrl().toLowerCase().contains("main"))
			{ // 列表页直接退出
				mWebView.loadUrl("about:blank");
				this.finish();
			}
			else if (mWebView.getUrl().toLowerCase().contains("fawen") && isRedirect)
			{
				mWebView.goBackOrForward(-2);
			}
			else
			{
				mWebView.goBack();
			}
		}
		else
		{
			mWebView.loadUrl("about:blank");
			this.finish();
		}

		//mWebView.goBack();
	}

}
