package com.gummybeargames.swimfishswim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AndroidLauncher extends AndroidApplication implements AdHandler{
	private static final String Tag="AndroidLauncher";
	private final int showAds=1;
	private final int hideAds=0;

	protected AdView adView;

	Handler handler = new Handler(){

		@Override
		public void handleMessage(@NonNull Message msg) {
			switch (msg.what){
				case showAds:
					adView.setVisibility(View.VISIBLE);
					break;
				case hideAds:
					adView.setVisibility(View.GONE);
					break;

			}
		}
	};
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		//AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//initialize(new SwimFishSwim(), config);
		super.onCreate(savedInstanceState);
		RelativeLayout layout= new RelativeLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gameView= initializeForView(new SwimFishSwim(this), config);

		layout.addView(gameView);
		adView = new AdView(this);
		adView.setAdListener(new AdListener(){
			public void onAdLoaded(){
				int visibilty = adView.getVisibility();
				adView.setVisibility(AdView.GONE);
				adView.setVisibility(visibilty);
			}
		});
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId("ca-app-pub-4694730533816558/5747147598");
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		AdRequest.Builder builder= new AdRequest.Builder();
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layout.addView(adView, adParams);
		adView.loadAd(builder.build());

		setContentView(layout);
		//initialize(new FlappyBird(), config);
	}

	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? showAds : hideAds);
	}
}
