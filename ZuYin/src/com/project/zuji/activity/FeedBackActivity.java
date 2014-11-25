package com.project.zuji.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.project.zuji.R;

public class FeedBackActivity extends Activity {
	private View backlayout;
	private EditText editText;
	private Button save_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		backlayout = findViewById(R.id.feedback_back_layout);
		editText = (EditText) findViewById(R.id.et_opinion);
		save_btn = (Button) findViewById(R.id.save_btn);
		backlayout.setOnClickListener(onClick);
		save_btn.setOnClickListener(onClick);
	}

	// 上传反馈
	private void addOpinion(String emailBody) {

		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String[] emailReciver = new String[] { "1009169973@qq.com" };

		String emailSubject = "我的足迹意见反馈";
		// 设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		// 调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.feedback_back_layout:
				finish();
				break;
			case R.id.save_btn:
				if (TextUtils.isEmpty(editText.getText())) {
					// ToastUtil.showErrorMsg(FeedBackActivity.this,
					// R.string.error_opinion_null);
				} else if (editText.getText().toString().length() > 150) {
					// ToastUtil.showErrorMsg(FeedBackActivity.this,
					// R.string.error_opinion_tolong);
				} else {
					String body = editText.getText().toString();
					addOpinion(body);
					finish();
				}
				break;

			default:
				break;
			}
		}
	};

}
