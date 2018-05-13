package com.hsinfo.mixigou.item;

public class FinishPeddingItem {
	private int iv_icon1;
	private String tv_title;
	private String tv_state;
	private String tv_date;
	private int iv_icon2;
	private String tv_formid;
	
	public FinishPeddingItem(){
		
	}
	
	public FinishPeddingItem(int iv_icon1, String tv_title, String tv_state, String tv_date, int iv_icon2, String tv_formid) {
		
		this.iv_icon1 =iv_icon1;
		this.tv_title = tv_title;
		this.tv_state = tv_state;
		this.tv_date = tv_date;
		this.iv_icon2 = iv_icon2;
		this.tv_formid =tv_formid;
	}

	public int getIv_icon1() {
		return iv_icon1;
	}

	public void setIv_icon1(int iv_icon1) {
		this.iv_icon1 = iv_icon1;
	}

	public String getTv_title() {
		return tv_title;
	}

	public void setTv_title(String tv_title) {
		this.tv_title = tv_title;
	}

	public String getTv_state() {
		return tv_state;
	}

	public void setTv_state(String tv_state) {
		this.tv_state = tv_state;
	}

	public String getTv_date() {
		return tv_date;
	}

	public void setTv_date(String tv_date) {
		this.tv_date = tv_date;
	}

	public int getIv_icon2() {
		return iv_icon2;
	}

	public void setIv_icon2(int iv_icon2) {
		this.iv_icon2 = iv_icon2;
	}

	public String getTv_formid() {
		return tv_formid;
	}

	public void setTv_formid(String tv_formid) {
		this.tv_formid = tv_formid;
	}

}
