package com.hsinfo.mixigou.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hsinfo.mixigou.Main.ActivityMain;
import com.hsinfo.mixigou.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 软件升级帮助类
 */
public class UpdateManager 
{
	private Context mContext;
	private ActivityMain m_mainactivity;

	// 提示语
	private String updateMsg = "有最新的软件包，请下载更新!";
    private String m_text = "";

	// 返回的安装包url
	private String apkUrl = "";
	private Dialog noticeDialog;
	private Dialog downloadDialog;

	/* 下载包安装路径 */

	private static final String savePath = Environment.getExternalStorageDirectory()+"/changjiang/";

	private static final String saveFileName = savePath
			+ "UpdateDemoRelease.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */

	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int progress;
	private Thread downLoadThread;
	private boolean interceptFlag = false;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context, String apkUrl, String text)
	{
		this.mContext = context;
		this.apkUrl = apkUrl;
        this.m_text = text;
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo() 
	{
		showNoticeDialog();
	}

	private void showNoticeDialog()
	{
		// 1. 布局文件转换为View对象
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.dialog_custom3, null);

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("更新");
		builder.setMessage(m_text);
		builder.setPositiveButton("以后再说", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("下载", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownloadDialog();
			}
		});

		noticeDialog = builder.create();
		noticeDialog.show();
		//noticeDialog.getWindow().setContentView(layout);
	}

	public void checkUpdateInfo2()
	{
		showNoticeDialog2();
	}

	public void checkUpdateInfo3()
	{
		showNoticeDialog3();
	}

	private void showNoticeDialog2()
	{
		// 1. 布局文件转换为View对象
		LayoutInflater inflater = LayoutInflater.from(mContext);
		//RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_custom2, null);

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("重要版本更新");
		builder.setMessage(m_text);
		//builder.setMessage("1.侧滑加入通讯录手动更新\n2.通讯录加入数字和字母的查询\n3.修正登录时网络不正常登录山退的问题\n4.修正登录时网络的判断，当网络不不稳定时，对变量的值为空加判断");
		builder.setPositiveButton("下载", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownloadDialog();
			}
		});

//		builder.setNegativeButton("以后再说", new OnClickListener()
//		{
//			@Override
//			public void onClick(DialogInterface dialog, int which)
//			{
//				dialog.dismiss();
//			}
//		});

		//设置Dialog中的setOnKeyListener
		/*builder.setOnKeyListener(new DialogInterface.OnKeyListener()
		{
			//第一个参数是拦截到此事件的对话框对象的引用,第二个参数是此事件对应的keyCode,第三个参数是此事件对象本身。
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				// TODO Auto-generated method stub
				android.util.Log.d("cjwsjy", "--------keyCode="+keyCode+"-------");
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
				{
					android.util.Log.d("cjwsjy", "--------setOnKeyListener-------");
					dialog.dismiss();
					m_mainactivity.finish();
					System.exit(0);
				}
				return false;
			}
		});*/

		noticeDialog = builder.create();
		noticeDialog.setCancelable(false);  //按对话框以外的地方不起作用。按返回键也不起作用
		//noticeDialog.setCanceledOnTouchOutside(false);  //按对话框以外的地方不起作用。按返回键还起作用
		noticeDialog.show();
	}

	private void showNoticeDialog3()
	{
		// 1. 布局文件转换为View对象
		LayoutInflater inflater = LayoutInflater.from(mContext);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_custom2, null);

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("更新");
		builder.setMessage("通讯录更新完成");
		builder.setPositiveButton("确定", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		noticeDialog = builder.create();
		noticeDialog.show();
	}

	//软件更新
	private void showDownloadDialog()
	{
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_manager_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists())
				{
					file.mkdir();
				}

				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				//缓存
				byte buf[] = new byte[1024];
				//写入到文件中
				do
				{
					int numread = is.read(buf);
					count += numread;
					//计算进度条位置
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0)
					{
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				}
				while (!interceptFlag);// 点击取消就停止下载.
				fos.close();
				is.close();
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			downloadDialog.dismiss();
		}
	};

	/**
	 * 
	 * 下载apk
	 * 
	 *
	 */
	private void downloadApk() 
	{
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 
	 * 安装apk
	 * 
	 *
	 */
	private void installApk()
	{
		File apkfile = new File(saveFileName);
		if (!apkfile.exists())
		{
			return;
		}

		// 通过Intent安装APK文件
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//判断是否是AndroidN以及更高的版本
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
			//String buf = BuildConfig.APPLICATION_ID + ".fileProvider";
            //Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", apkfile);
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.jph.takephoto.fileprovider", apkfile);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		}
		else
		{
			intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		mContext.startActivity(intent);
	}

	//开放外部接口，得到主activity
	public void GetMainactivity( ActivityMain ma )
	{
		m_mainactivity = ma;
	}
}
