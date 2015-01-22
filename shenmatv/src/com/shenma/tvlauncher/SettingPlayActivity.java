package com.shenma.tvlauncher;

import com.baidu.cyberplayer.core.BVideoView;
import com.shenma.tvlauncher.R;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingPlayActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_play);
		findViewById(R.id.setting_play).setBackgroundResource(R.drawable.video_details_bg);
		initView();
		initData();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences("shenma",MODE_PRIVATE);
		loadViewLayout();
		findViewById();
		setListener();
	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		rl_play_setting_content_decode = (RelativeLayout) findViewById(R.id.play_setting_content_decode);
		rl_play_setting_content_definition = (RelativeLayout) findViewById(R.id.play_setting_content_definition);
		rl_play_setting_content_playratio = (RelativeLayout) findViewById(R.id.play_setting_content_playratio);
		rl_play_setting_content_jump = (RelativeLayout) findViewById(R.id.play_setting_content_jump);

		tv_play_setting_content_decode_text = (TextView) findViewById(R.id.play_setting_content_decode_text);
		tv_play_setting_content_definition_text = (TextView) findViewById(R.id.play_setting_content_definition_text);
		tv_play_setting_content_jump_text = (TextView) findViewById(R.id.play_setting_content_jump_text);
		tv_play_setting_content_playratio_text = (TextView) findViewById(R.id.play_setting_content_playratio_text);

		bt_decode_left_arrows = (ImageButton) findViewById(R.id.play_setting_content_decode_left_arrows);
		bt_decode_right_arrows = (ImageButton) findViewById(R.id.play_setting_content_decode_right_arrows);
		bt_definition_left_arrows = (ImageButton) findViewById(R.id.play_setting_content_definition_left_arrows);
		bt_definition_right_arrows = (ImageButton) findViewById(R.id.play_setting_content_definition_right_arrows);
		bt_playratio_left_arrows = (ImageButton) findViewById(R.id.play_setting_content_playratio_left_arrows);
		bt_playratio_right_arrows = (ImageButton) findViewById(R.id.play_setting_content_playratio_right_arrows);
		bt_jump_left_arrows = (ImageButton) findViewById(R.id.play_setting_content_jump_left_arrows);
		bt_jump_right_arrows = (ImageButton) findViewById(R.id.play_setting_content_jump_right_arrows);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		bt_decode_left_arrows.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mtv_play_decode = (String) tv_play_setting_content_decode_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_decode.length; i++) {
					if (mtv_play_decode != null
							&& mtv_play_decode
									.equals(mAll_play_setting_decode[i])) {
						index = i;
					}
				}
				if (index == 0) {
					tv_play_setting_content_decode_text
							.setText(mAll_play_setting_decode[mAll_play_setting_decode.length - 1]);
				} else {
					tv_play_setting_content_decode_text.setText(mAll_play_setting_decode[index - 1]);
				}
				//bt_decode_left_arrows.setImageResource(R.drawable.select_left_arrows_f);
			}
		});
		
		bt_decode_right_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mtv_play_decode = (String) tv_play_setting_content_decode_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_decode.length; i++) {
					if (mtv_play_decode != null
							&& mtv_play_decode
									.equals(mAll_play_setting_decode[i])) {
						index = i;
					}
				}
				if (index == mAll_play_setting_decode.length - 1) {
					tv_play_setting_content_decode_text.setText(mAll_play_setting_decode[0]);
				} else {
					tv_play_setting_content_decode_text.setText(mAll_play_setting_decode[index + 1]);
				}
				//bt_decode_right_arrows.setImageResource(R.drawable.select_right_arrows_f);
			}
		});
		
		bt_definition_left_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tv_play_definition = (String) tv_play_setting_content_definition_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_definition.length; i++) {
					if (tv_play_definition != null
							&& tv_play_definition
									.equals(mAll_play_setting_definition[i])) {
						index = i;
					}
				}
				if (index == 0) {
					tv_play_setting_content_definition_text
							.setText(mAll_play_setting_definition[mAll_play_setting_definition.length - 1]);
				} else {
					tv_play_setting_content_definition_text
							.setText(mAll_play_setting_definition[index - 1]);
				}
				//bt_definition_left_arrows.setImageResource(R.drawable.select_left_arrows_f);
			}
		});
		bt_definition_right_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tv_play_definition = (String) tv_play_setting_content_definition_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_definition.length; i++) {
					if (tv_play_definition != null
							&& tv_play_definition
									.equals(mAll_play_setting_definition[i])) {
						index = i;
					}
				}
				if (index == mAll_play_setting_definition.length - 1) {
					tv_play_setting_content_definition_text.setText(mAll_play_setting_definition[0]);
				} else {
					tv_play_setting_content_definition_text
							.setText(mAll_play_setting_definition[index + 1]);
				}
				//bt_definition_right_arrows.setImageResource(R.drawable.select_right_arrows_f);
			}
		});
		
		bt_playratio_left_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tv_play_playratio = (String) tv_play_setting_content_playratio_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_playratio.length; i++) {
					if (tv_play_playratio != null
							&& tv_play_playratio
									.equals(mAll_play_setting_playratio[i])) {
						index = i;
					}
				}
				if (index == 0) {
					tv_play_setting_content_playratio_text
							.setText(mAll_play_setting_playratio[mAll_play_setting_playratio.length - 1]);
				} else {
					tv_play_setting_content_playratio_text.setText(mAll_play_setting_playratio[index - 1]);
				}
				//bt_playratio_left_arrows.setImageResource(R.drawable.select_left_arrows_f);
			}
		});
		bt_playratio_right_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tv_play_playratio = (String) tv_play_setting_content_playratio_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_playratio.length; i++) {
					if (tv_play_playratio != null
							&& tv_play_playratio
									.equals(mAll_play_setting_playratio[i])) {
						index = i;
					}
				}
				if (index == mAll_play_setting_playratio.length - 1) {
					tv_play_setting_content_playratio_text.setText(mAll_play_setting_playratio[0]);
				} else {
					tv_play_setting_content_playratio_text.setText(mAll_play_setting_playratio[index + 1]);
				}
				//bt_playratio_right_arrows.setImageResource(R.drawable.select_right_arrows_f);
			}
		});
		
		bt_jump_left_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tv_play_jump = (String) tv_play_setting_content_jump_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_jump.length; i++) {
					if (tv_play_jump != null
							&& tv_play_jump.equals(mAll_play_setting_jump[i])) {
						index = i;
					}
				}
				if (index == 0) {
					tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[mAll_play_setting_jump.length - 1]);
				} else {
					tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[index - 1]);
				}
				//bt_jump_left_arrows.setImageResource(R.drawable.select_left_arrows_f);
			}
		});
		
		bt_jump_right_arrows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tv_play_jump = (String) tv_play_setting_content_jump_text.getText();
				int index = 0;
				for (int i = 0; i < mAll_play_setting_jump.length; i++) {
					if (tv_play_jump != null
							&& tv_play_jump.equals(mAll_play_setting_jump[i])) {
						index = i;
					}
				}
				if (index == mAll_play_setting_jump.length - 1) {
					tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[0]);
				} else {
					tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[index + 1]);
				}
				//bt_jump_right_arrows.setImageResource(R.drawable.select_right_arrows_f);
			}
		});
		
		//解码
		rl_play_setting_content_decode.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				String mtv_play_decode = (String) tv_play_setting_content_decode_text.getText();
				int index = 0;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						for (int i = 0; i < mAll_play_setting_decode.length; i++) {
							if (mtv_play_decode != null
									&& mtv_play_decode
											.equals(mAll_play_setting_decode[i])) {
								index = i;
							}
						}
						if (index == 0) {
							tv_play_setting_content_decode_text
									.setText(mAll_play_setting_decode[mAll_play_setting_decode.length - 1]);
						} else {
							tv_play_setting_content_decode_text.setText(mAll_play_setting_decode[index - 1]);
						}
						bt_decode_left_arrows
								.setImageResource(R.drawable.select_left_arrows_f);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						for (int i = 0; i < mAll_play_setting_decode.length; i++) {
							if (mtv_play_decode != null
									&& mtv_play_decode
											.equals(mAll_play_setting_decode[i])) {
								index = i;
							}
						}
						if (index == mAll_play_setting_decode.length - 1) {
							tv_play_setting_content_decode_text.setText(mAll_play_setting_decode[0]);
						} else {
							tv_play_setting_content_decode_text.setText(mAll_play_setting_decode[index + 1]);
						}
						bt_decode_right_arrows
								.setImageResource(R.drawable.select_right_arrows_f);
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						bt_decode_left_arrows
								.setImageResource(R.drawable.select_left_arrows_n);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						bt_decode_right_arrows
								.setImageResource(R.drawable.select_right_arrows_n);
						break;
					}
				}
				return false;
			}
		});
		
		//播放显示比例
		rl_play_setting_content_definition.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				String tv_play_definition = (String) tv_play_setting_content_definition_text.getText();
				int index = 0;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						for (int i = 0; i < mAll_play_setting_definition.length; i++) {
							if (tv_play_definition != null
									&& tv_play_definition
											.equals(mAll_play_setting_definition[i])) {
								index = i;
							}
						}
						if (index == 0) {
							tv_play_setting_content_definition_text
									.setText(mAll_play_setting_definition[mAll_play_setting_definition.length - 1]);
						} else {
							tv_play_setting_content_definition_text
									.setText(mAll_play_setting_definition[index - 1]);
						}
						bt_definition_left_arrows
								.setImageResource(R.drawable.select_left_arrows_f);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						for (int i = 0; i < mAll_play_setting_definition.length; i++) {
							if (tv_play_definition != null
									&& tv_play_definition
											.equals(mAll_play_setting_definition[i])) {
								index = i;
							}
						}
						if (index == mAll_play_setting_definition.length - 1) {
							tv_play_setting_content_definition_text.setText(mAll_play_setting_definition[0]);
						} else {
							tv_play_setting_content_definition_text
									.setText(mAll_play_setting_definition[index + 1]);
						}
						bt_definition_right_arrows
								.setImageResource(R.drawable.select_right_arrows_f);
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						bt_definition_left_arrows
								.setImageResource(R.drawable.select_left_arrows_n);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						bt_definition_right_arrows
								.setImageResource(R.drawable.select_right_arrows_n);
						break;
					}
				}
				return false;
			}
		});
		rl_play_setting_content_playratio.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				String tv_play_playratio = (String) tv_play_setting_content_playratio_text.getText();
				int index = 0;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						for (int i = 0; i < mAll_play_setting_playratio.length; i++) {
							if (tv_play_playratio != null
									&& tv_play_playratio
											.equals(mAll_play_setting_playratio[i])) {
								index = i;
							}
						}
						if (index == 0) {
							tv_play_setting_content_playratio_text
									.setText(mAll_play_setting_playratio[mAll_play_setting_playratio.length - 1]);
						} else {
							tv_play_setting_content_playratio_text.setText(mAll_play_setting_playratio[index - 1]);
						}
						bt_playratio_left_arrows
								.setImageResource(R.drawable.select_left_arrows_f);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						for (int i = 0; i < mAll_play_setting_playratio.length; i++) {
							if (tv_play_playratio != null
									&& tv_play_playratio
											.equals(mAll_play_setting_playratio[i])) {
								index = i;
							}
						}
						if (index == mAll_play_setting_playratio.length - 1) {
							tv_play_setting_content_playratio_text.setText(mAll_play_setting_playratio[0]);
						} else {
							tv_play_setting_content_playratio_text.setText(mAll_play_setting_playratio[index + 1]);
						}
						bt_playratio_right_arrows
								.setImageResource(R.drawable.select_right_arrows_f);
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						bt_playratio_left_arrows
								.setImageResource(R.drawable.select_left_arrows_n);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						bt_playratio_right_arrows
								.setImageResource(R.drawable.select_right_arrows_n);
						break;
					}
				}
				return false;

			}
		});
		
		
		//跳过片头时间
		rl_play_setting_content_jump.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				String tv_play_jump = (String) tv_play_setting_content_jump_text.getText();
				int index = 0;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						for (int i = 0; i < mAll_play_setting_jump.length; i++) {
							if (tv_play_jump != null
									&& tv_play_jump.equals(mAll_play_setting_jump[i])) {
								index = i;
							}
						}
						if (index == 0) {
							tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[mAll_play_setting_jump.length - 1]);
						} else {
							tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[index - 1]);
						}
						bt_jump_left_arrows
								.setImageResource(R.drawable.select_left_arrows_f);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						for (int i = 0; i < mAll_play_setting_jump.length; i++) {
							if (tv_play_jump != null
									&& tv_play_jump.equals(mAll_play_setting_jump[i])) {
								index = i;
							}
						}
						if (index == mAll_play_setting_jump.length - 1) {
							tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[0]);
						} else {
							tv_play_setting_content_jump_text.setText(mAll_play_setting_jump[index + 1]);
						}
						bt_jump_right_arrows
								.setImageResource(R.drawable.select_right_arrows_f);
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						bt_jump_left_arrows
								.setImageResource(R.drawable.select_left_arrows_n);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						bt_jump_right_arrows
								.setImageResource(R.drawable.select_right_arrows_n);
						break;
					}
				}
				return false;
			}
		});
	}

	private void initData() {
		mAll_play_setting_decode = getResources().getStringArray(R.array.play_setting_decode);
		mAll_play_setting_playratio = getResources().getStringArray(R.array.play_setting_playratio);
		mAll_play_setting_definition = getResources().getStringArray(R.array.play_setting_definition);
		mAll_play_setting_jump = getResources().getStringArray(R.array.play_setting_jump);
		
		String play_decode = sp.getString("play_decode", mAll_play_setting_decode[1]);
		String play_ratio = sp.getString("play_ratio", mAll_play_setting_playratio[3]);
		String play_definition = sp.getString("play_definition", mAll_play_setting_definition[0]);
		String play_jump = sp.getString("play_jump", mAll_play_setting_jump[0]);
		
		tv_play_setting_content_decode_text.setText(play_decode);
		tv_play_setting_content_definition_text.setText(play_definition);
		tv_play_setting_content_playratio_text.setText(play_ratio);
		tv_play_setting_content_jump_text.setText(play_jump);
	}
	
	/**
	 * 保存播放设置
	 */
	private void savePlaySettingInfo(){
		Editor editor = sp.edit();
		String play_decode = (String) tv_play_setting_content_decode_text.getText();
		if("软解码".equals(play_decode)){
			editor.putInt("mIsHwDecode", BVideoView.DECODE_SW);
		}else{
			editor.putInt("mIsHwDecode", BVideoView.DECODE_HW);
		}
		editor.putString("play_decode", (String) tv_play_setting_content_decode_text.getText());
		editor.putString("play_ratio", (String) tv_play_setting_content_playratio_text.getText());
		editor.putString("play_definition", (String) tv_play_setting_content_definition_text.getText());
		editor.putString("play_jump", (String) tv_play_setting_content_jump_text.getText());
		editor.commit();
	}
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			// mBackBtnIv.requestFocus();
			savePlaySettingInfo();
			finish();
			return true;
		}
		super.onKeyUp(keyCode, event);
		return false;
	}

	private RelativeLayout rl_play_setting_content_decode,
			rl_play_setting_content_definition,
			rl_play_setting_content_playratio, rl_play_setting_content_jump;
	private TextView tv_play_setting_content_decode_text,
			tv_play_setting_content_definition_text,
			tv_play_setting_content_jump_text,
			tv_play_setting_content_playratio_text;
	private ImageButton bt_decode_left_arrows,bt_decode_right_arrows,
			bt_definition_left_arrows,
			bt_definition_right_arrows,
			bt_playratio_left_arrows,
			bt_playratio_right_arrows,
			bt_jump_left_arrows,
			bt_jump_right_arrows;
	private String[] mAll_play_setting_decode;
	private String[] mAll_play_setting_playratio;
	private String[] mAll_play_setting_definition;
	private String[] mAll_play_setting_jump;
}
