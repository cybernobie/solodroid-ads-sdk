package com.solodroid.ads.sdk.format;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.NONE;
import static com.solodroid.ads.sdk.util.Constant.STARTAPP;
import static com.solodroid.ads.sdk.util.Constant.UNITY;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.solodroid.ads.sdk.R;
import com.solodroid.ads.sdk.util.Constant;
import com.solodroid.ads.sdk.util.NativeTemplateStyle;
import com.solodroid.ads.sdk.util.TemplateView;
import com.solodroid.ads.sdk.util.Tools;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.ArrayList;

public class NativeAdFragment {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        View view;

        LinearLayout native_ad_view_container;
        MediaView mediaView;
        TemplateView admob_native_ad;
        LinearLayout admob_native_background;
        View startapp_native_ad;
        ImageView startapp_native_image;
        TextView startapp_native_title;
        TextView startapp_native_description;
        Button startapp_native_button;
        LinearLayout startapp_native_background;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public NativeAdFragment.Builder build() {
            loadNativeAd();
            return this;
        }

        public NativeAdFragment.Builder setPadding(int left, int top, int right, int bottom) {
            setNativeAdPadding(left, top, right, bottom);
            return this;
        }

        public NativeAdFragment.Builder setView(View view) {
            this.view = view;
            return this;
        }

        public NativeAdFragment.Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public NativeAdFragment.Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public NativeAdFragment.Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public NativeAdFragment.Builder setAdMobNativeId(String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        public NativeAdFragment.Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public NativeAdFragment.Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        public NativeAdFragment.Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadNativeAd() {

            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                native_ad_view_container = view.findViewById(R.id.native_ad_view_container);
                admob_native_ad = view.findViewById(R.id.admob_native_ad_container);
                mediaView = view.findViewById(R.id.media_view);
                admob_native_background = view.findViewById(R.id.background);
                startapp_native_ad = view.findViewById(R.id.startapp_native_ad_container);
                startapp_native_image = view.findViewById(R.id.startapp_native_image);
                startapp_native_title = view.findViewById(R.id.startapp_native_title);
                startapp_native_description = view.findViewById(R.id.startapp_native_description);
                startapp_native_button = view.findViewById(R.id.startapp_native_button);
                startapp_native_button.setOnClickListener(v -> startapp_native_ad.performClick());
                startapp_native_background = view.findViewById(R.id.startapp_native_background);

                switch (adNetwork) {
                    case ADMOB:
                        if (admob_native_ad.getVisibility() != View.VISIBLE) {
                            AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                    .forNativeAd(NativeAd -> {
                                        if (darkTheme) {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundDark));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                        } else {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundLight));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                        }
                                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        admob_native_ad.setNativeAd(NativeAd);
                                        admob_native_ad.setVisibility(View.VISIBLE);
                                        native_ad_view_container.setVisibility(View.VISIBLE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            loadBackupNativeAd();
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        } else {
                            Log.d(TAG, "AdMob Native Ad has been loaded");
                        }
                        break;

                    case STARTAPP:
                        if (startapp_native_ad.getVisibility() != View.VISIBLE) {
                            StartAppNativeAd startAppNativeAd = new StartAppNativeAd(activity);
                            NativeAdPreferences nativePrefs = new NativeAdPreferences()
                                    .setAdsNumber(3)
                                    .setAutoBitmapDownload(true)
                                    .setPrimaryImageSize(Constant.STARTAPP_IMAGE_MEDIUM);
                            AdEventListener adListener = new AdEventListener() {
                                @Override
                                public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad arg0) {
                                    Log.d(TAG, "StartApp Native Ad loaded");
                                    startapp_native_ad.setVisibility(View.VISIBLE);
                                    native_ad_view_container.setVisibility(View.VISIBLE);
                                    //noinspection rawtypes
                                    ArrayList ads = startAppNativeAd.getNativeAds(); // get NativeAds list

                                    // Print all ads details to log
                                    for (Object ad : ads) {
                                        Log.d(TAG, "StartApp Native Ad " + ad.toString());
                                    }

                                    NativeAdDetails ad = (NativeAdDetails) ads.get(0);
                                    if (ad != null) {
                                        startapp_native_image.setImageBitmap(ad.getImageBitmap());
                                        startapp_native_title.setText(ad.getTitle());
                                        startapp_native_description.setText(ad.getDescription());
                                        startapp_native_button.setText(ad.isApp() ? "Install" : "Open");
                                        ad.registerViewForInteraction(startapp_native_ad);
                                    }

                                    if (darkTheme) {
                                        startapp_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                    } else {
                                        startapp_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                    }

                                }

                                @Override
                                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad arg0) {
                                    loadBackupNativeAd();
                                    Log.d(TAG, "StartApp Native Ad failed loaded");
                                }
                            };
                            startAppNativeAd.loadAd(nativePrefs, adListener);
                        } else {
                            Log.d(TAG, "StartApp Native Ad has been loaded");
                        }
                        break;

                    case UNITY:

                    case APPLOVIN:
                        //do nothing
                        break;

                }

            }

        }

        public void loadBackupNativeAd() {

            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                native_ad_view_container = view.findViewById(R.id.native_ad_view_container);
                admob_native_ad = view.findViewById(R.id.admob_native_ad_container);
                mediaView = view.findViewById(R.id.media_view);
                admob_native_background = view.findViewById(R.id.background);
                startapp_native_ad = view.findViewById(R.id.startapp_native_ad_container);
                startapp_native_image = view.findViewById(R.id.startapp_native_image);
                startapp_native_title = view.findViewById(R.id.startapp_native_title);
                startapp_native_description = view.findViewById(R.id.startapp_native_description);
                startapp_native_button = view.findViewById(R.id.startapp_native_button);
                startapp_native_button.setOnClickListener(v -> startapp_native_ad.performClick());
                startapp_native_background = view.findViewById(R.id.startapp_native_background);

                switch (backupAdNetwork) {
                    case ADMOB:
                        if (admob_native_ad.getVisibility() != View.VISIBLE) {
                            AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                    .forNativeAd(NativeAd -> {
                                        if (darkTheme) {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundDark));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                        } else {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundLight));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                        }
                                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        admob_native_ad.setNativeAd(NativeAd);
                                        admob_native_ad.setVisibility(View.VISIBLE);
                                        native_ad_view_container.setVisibility(View.VISIBLE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            admob_native_ad.setVisibility(View.GONE);
                                            native_ad_view_container.setVisibility(View.GONE);
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        } else {
                            Log.d(TAG, "AdMob Native Ad has been loaded");
                        }
                        break;

                    case STARTAPP:
                        if (startapp_native_ad.getVisibility() != View.VISIBLE) {
                            StartAppNativeAd startAppNativeAd = new StartAppNativeAd(activity);
                            NativeAdPreferences nativePrefs = new NativeAdPreferences()
                                    .setAdsNumber(3)
                                    .setAutoBitmapDownload(true)
                                    .setPrimaryImageSize(Constant.STARTAPP_IMAGE_MEDIUM);
                            AdEventListener adListener = new AdEventListener() {
                                @Override
                                public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad arg0) {
                                    Log.d(TAG, "StartApp Native Ad loaded");
                                    startapp_native_ad.setVisibility(View.VISIBLE);
                                    native_ad_view_container.setVisibility(View.VISIBLE);
                                    //noinspection rawtypes
                                    ArrayList ads = startAppNativeAd.getNativeAds(); // get NativeAds list

                                    // Print all ads details to log
                                    for (Object ad : ads) {
                                        Log.d(TAG, "StartApp Native Ad " + ad.toString());
                                    }

                                    NativeAdDetails ad = (NativeAdDetails) ads.get(0);
                                    if (ad != null) {
                                        startapp_native_image.setImageBitmap(ad.getImageBitmap());
                                        startapp_native_title.setText(ad.getTitle());
                                        startapp_native_description.setText(ad.getDescription());
                                        startapp_native_button.setText(ad.isApp() ? "Install" : "Open");
                                        ad.registerViewForInteraction(startapp_native_ad);
                                    }

                                    if (darkTheme) {
                                        startapp_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                    } else {
                                        startapp_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                    }

                                }

                                @Override
                                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad arg0) {
                                    startapp_native_ad.setVisibility(View.GONE);
                                    native_ad_view_container.setVisibility(View.GONE);
                                    Log.d(TAG, "StartApp Native Ad failed loaded");
                                }
                            };
                            startAppNativeAd.loadAd(nativePrefs, adListener);
                        } else {
                            Log.d(TAG, "StartApp Native Ad has been loaded");
                        }
                        break;

                    case UNITY:

                    case APPLOVIN:

                    case NONE:
                        native_ad_view_container.setVisibility(View.GONE);
                        break;

                }

            }

        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            native_ad_view_container = view.findViewById(R.id.native_ad_view_container);
            native_ad_view_container.setPadding(left, top, right, bottom);
        }

    }

}