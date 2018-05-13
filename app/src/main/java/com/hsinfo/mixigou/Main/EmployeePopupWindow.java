package com.hsinfo.mixigou.Main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hsinfo.mixigou.R;
import com.hsinfo.mixigou.WebView.WebViewCanteen;

/**
 * 自定义的弹出窗口,主要用于选择手机号码
 * 
 */
public class EmployeePopupWindow extends PopupWindow implements OnClickListener {

	int showView = R.layout.employee_float_view;
	private View mMenuView;
	private Context mContext;
	private int sourceType;
	private WebViewCanteen m_webViewCanteen;

	String mMobile;

	private TextView tel1;
	private TextView tel2;
	private TextView tel3;
	private TextView tel4;

	public static int SOURCE_SMS = 0;
	public static int SOURCE_TEL = 1;

	public EmployeePopupWindow(Activity context, int showView, int source )
	{
		super(context);
		
		String strings;
		mContext = context;
		sourceType = source;
		m_webViewCanteen = (WebViewCanteen)context;

		if (showView != 0) 
		{
			this.showView = showView;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(this.showView, null);
		tel1 = (TextView) mMenuView.findViewById(R.id.tel_1);
		tel2 = (TextView) mMenuView.findViewById(R.id.tel_2);
		tel3 = (TextView) mMenuView.findViewById(R.id.tel_3);
		tel4 = (TextView) mMenuView.findViewById(R.id.tel_4);
		mMenuView.findViewById(R.id.cancle_btn).setOnClickListener(this);

		setViewTextById(R.id.tel_1, "拍 照");
		tel1.setOnClickListener(this);

		setViewTextById(R.id.tel_2, "相 册");
		tel2.setOnClickListener(this);

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// 在输入法退出后重新编排布局
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) 
		{
		case R.id.tel_1:
			m_webViewCanteen.openCameraActivity();
            dismiss();
			break;
		case R.id.tel_2:
			m_webViewCanteen.openImageChooserActivity();
            dismiss();
			break;
		case R.id.tel_3:
			operation(findViewTextById(R.id.tel_3));
			break;
		case R.id.tel_4:
			operation(findViewTextById(R.id.tel_4));
			break;
		case R.id.cancle_btn:
			m_webViewCanteen.cancelfilePathCallback();
			dismiss();
			break;
		}
	}

	public void operation(String phoneNum) 
	{
		String phone = "";
		Intent intent;
		switch (sourceType) 
		{
		case 0:
			phone = "smsto:" + phoneNum;
			Uri smsToUri = Uri.parse(phone);
			intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
			intent.putExtra("sms_body", "");
			mContext.startActivity(intent);
			break;
		case 1:
//			phone = "tel:" + phoneNum;
//			intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
//			mContext.startActivity(intent);
			break;
		default:
			break;
		}
	}

    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    public void onCall(String mobile)
    {
        this.mMobile = mobile;
        if( Build.VERSION.SDK_INT>=23 )
        {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);

            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            else
            {
                //上面已经写好的拨号方法
                callDirectly(mobile);
            }
        }
        else
        {
            //上面已经写好的拨号方法
            callDirectly(mobile);
        }
    }
	
    //打电话
    private void callDirectly( String mobile )
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        mContext.startActivity(intent);
    }
    
	public String findViewTextById(int id) {
		TextView view = (TextView) mMenuView.findViewById(id);
		String text = view.getText().toString();
		return text;
	}

	public void setViewTextById(int id, String text) {
		((TextView) mMenuView.findViewById(id)).setText(text);
		;
	}

	public void setViewBgById(int id, int resourceId) {
		mMenuView.findViewById(id).setBackgroundDrawable(
				mContext.getResources().getDrawable(resourceId));
	}

}
