package com.saftv.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Rational;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnInfoListener {

    private static final String PREFS_NAME = "saf_tv_prefs";
    private static final String KEY_LAST_CHANNEL = "last_channel_index";
    private static final String KEY_ASPECT_RATIO = "aspect_ratio_mode"; // 0: Stretch, 1: Fit
    private static final String KEY_QUALITY = "stream_quality_pref"; // auto, 1080p, 720p, 480p, 360p
    private static final int OSD_TIMEOUT_MS = 4000;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    private ProgressBar loadingSpinner;
    private TextView tvStatus;

    // Overlay & OSD Views
    private FrameLayout layoutOverlay;
    private LinearLayout layoutOsd;
    private TextView tvOsdNum;
    private TextView tvOsdName;
    private TextView tvOsdCat;
    private TextView tvOsdFav;
    private TextView tvOsdQuality;
    private TextView tvOsdTime;
    private TextView tvOsdHint;

    // Touch Controls
    private View viewScrim;
    private View layoutTouchActions;
    private Button btnTouchChannels;
    private Button btnTouchFav;
    private Button btnTouchQuality;
    private Button btnTouchAspect;
    private Button btnTouchPip;
    private Button btnTouchSettings;
    private Button btnTouchExit;
    private TextView btnTouchPrev;
    private TextView btnTouchNext;
    private Button btnCloseDrawer;
    private Button btnCloseSettingsTop;
    private Button btnCloseQuickQuality;

    // UI Scale Views & State (%80 - %150)
    private static final String KEY_UI_SCALE = "pref_ui_scale";
    private TextView tvScaleStatus;
    private Button btnScaleAuto;
    private Button btnScale80;
    private Button btnScale90;
    private Button btnScale100;
    private Button btnScale115;
    private Button btnScale130;
    private Button btnScale150;
    private String currentUiScale = "auto";

    // Sleep Timer Views & State
    private TextView tvSleepStatus;
    private Button btnSleepOff;
    private Button btnSleep15;
    private Button btnSleep30;
    private Button btnSleep60;
    private Button btnSleep90;
    private Button btnSleep120;
    private long sleepTimerEndTime = 0;
    private final Handler sleepTimerHandler = new Handler(Looper.getMainLooper());
    private Runnable sleepTimerRunnable;
    private final Runnable sleepTimerTickRunnable = new Runnable() {
        @Override
        public void run() {
            updateSleepTimerDisplay();
            if (sleepTimerEndTime > System.currentTimeMillis()) {
                sleepTimerHandler.postDelayed(this, 1000);
            }
        }
    };

    // Background Audio Mode Views & State
    private static final String KEY_BG_AUDIO = "pref_bg_audio";
    private Button btnBgAudioToggle;
    private boolean isBackgroundAudioEnabled = false;
    private boolean isScreenOff = false;
    private BroadcastReceiver screenReceiver;

    // Play/Pause Touch Control (Option 2A)
    private View btnTouchPlayPause;
    private TextView tvPlayPauseIcon;
    private boolean isPaused = false;

    // Exit Confirmation Dialog (Option 1B)
    private View layoutExitDialog;
    private Button btnExitCancel;
    private Button btnExitConfirm;
    private boolean isExitDialogOpen = false;

    // Drawer Views
    private LinearLayout layoutDrawer;
    private TextView tvDrawerSubtitle;
    private ListView lvChannels;
    private ChannelAdapter channelAdapter;

    // Quick Quality Views
    private LinearLayout layoutQuickQuality;

    // Settings Views
    private LinearLayout layoutSettings;
    private ListView lvSettingsChannels;
    private SettingsChannelAdapter settingsChannelAdapter;

    // Network Alert Views
    private View layoutNetworkAlert;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isNetworkAvailable = true;

    // Search Views
    private EditText etChannelSearch;
    private Button btnClearSearch;

    private ChannelManager channelManager;
    private int currentChannelIndex = 0;
    private boolean isSurfaceCreated = false;
    private boolean isDrawerOpen = false;
    private boolean isSettingsOpen = false;
    private boolean isQuickQualityOpen = false;
    private boolean isTouchDevice = false;

    private int aspectRatioMode = 0; // 0: Stretch, 1: Fit 16:9
    private int videoWidth = 0;
    private int videoHeight = 0;
    private String currentQualityPref = "auto";
    private String activeStreamQualityLabel = "Otomatik";
    private int playbackRetryCount = 0;

    private GestureDetector gestureDetector;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable hideOsdRunnable = new Runnable() {
        @Override
        public void run() {
            if (layoutOverlay != null) {
                layoutOverlay.setVisibility(View.GONE);
            }
        }
    };

    private final Runnable hideQuickQualityRunnable = new Runnable() {
        @Override
        public void run() {
            closeQuickQuality();
        }
    };

    private long lastBackPressTime = 0;
    private String numKeyBuffer = "";
    private final Runnable jumpChannelRunnable = new Runnable() {
        @Override
        public void run() {
            if (!numKeyBuffer.isEmpty()) {
                try {
                    int targetNum = Integer.parseInt(numKeyBuffer);
                    int targetIndex = -1;
                    List<Channel> all = channelManager.getActiveChannels();
                    for (int i = 0; i < all.size(); i++) {
                        if (all.get(i).getId() == targetNum) {
                            targetIndex = i;
                            break;
                        }
                    }
                    if (targetIndex != -1) {
                        playChannel(targetIndex);
                    } else {
                        Toast.makeText(MainActivity.this, "Kanal " + targetNum + " bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException ignored) {}
                numKeyBuffer = "";
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        if (newBase != null) {
            Configuration config = new Configuration(newBase.getResources().getConfiguration());
            if (config.fontScale > 1.20f) {
                config.fontScale = 1.20f;
                Context context = newBase.createConfigurationContext(config);
                super.attachBaseContext(context);
                return;
            }
        }
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Dokunmatik ekran tespiti
        isTouchDevice = getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);

        channelManager = ChannelManager.getInstance();
        channelManager.syncChannelSettings(this); // İlk kuruluşta hepsi aktif

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentChannelIndex = prefs.getInt(KEY_LAST_CHANNEL, 0);
        aspectRatioMode = prefs.getInt(KEY_ASPECT_RATIO, 0);
        currentQualityPref = prefs.getString(KEY_QUALITY, "auto");

        if (currentChannelIndex < 0 || currentChannelIndex >= channelManager.getChannelCount()) {
            currentChannelIndex = 0;
        }

        initViews();
        updateHeaderLayoutOrientation();
        setupGestureDetector();
        setupCategories();
        setupSettingsControls();
        setupQuickQualityControls();
        setupTouchControls();
        setupNetworkMonitoring();
        setupChannelSearch();

        hideSystemUI();
    }

    private void hideSystemUI() {
        try {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception ignored) {}
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void initViews() {
        surfaceView = findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        loadingSpinner = findViewById(R.id.loading_spinner);
        tvStatus = findViewById(R.id.tv_status);

        layoutOverlay = findViewById(R.id.layout_overlay);
        layoutOsd = findViewById(R.id.layout_osd);
        tvOsdNum = findViewById(R.id.tv_osd_num);
        tvOsdName = findViewById(R.id.tv_osd_name);
        tvOsdCat = findViewById(R.id.tv_osd_cat);
        tvOsdFav = findViewById(R.id.tv_osd_fav);
        tvOsdQuality = findViewById(R.id.tv_osd_quality);
        tvOsdTime = findViewById(R.id.tv_osd_time);
        tvOsdHint = findViewById(R.id.tv_osd_hint);

        viewScrim = findViewById(R.id.view_scrim);
        layoutTouchActions = findViewById(R.id.layout_touch_actions);
        btnTouchChannels = null;
        btnTouchFav = null;
        btnTouchQuality = findViewById(R.id.btn_touch_quality);
        btnTouchAspect = findViewById(R.id.btn_touch_aspect);
        btnTouchPip = findViewById(R.id.btn_touch_pip);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (btnTouchPip != null) btnTouchPip.setVisibility(View.GONE);
        }
        btnTouchSettings = findViewById(R.id.btn_touch_settings);
        btnTouchExit = findViewById(R.id.btn_touch_exit);
        btnTouchPrev = findViewById(R.id.btn_touch_prev);
        btnTouchNext = findViewById(R.id.btn_touch_next);
        btnCloseDrawer = findViewById(R.id.btn_close_drawer);
        btnCloseSettingsTop = findViewById(R.id.btn_close_settings_top);
        btnCloseQuickQuality = findViewById(R.id.btn_close_quick_quality);

        tvScaleStatus = findViewById(R.id.tv_scale_status);
        btnScaleAuto = findViewById(R.id.btn_scale_auto);
        btnScale80 = findViewById(R.id.btn_scale_80);
        btnScale90 = findViewById(R.id.btn_scale_90);
        btnScale100 = findViewById(R.id.btn_scale_100);
        btnScale115 = findViewById(R.id.btn_scale_115);
        btnScale130 = findViewById(R.id.btn_scale_130);
        btnScale150 = findViewById(R.id.btn_scale_150);

        tvSleepStatus = findViewById(R.id.tv_sleep_status);
        btnSleepOff = findViewById(R.id.btn_sleep_off);
        btnSleep15 = findViewById(R.id.btn_sleep_15);
        btnSleep30 = findViewById(R.id.btn_sleep_30);
        btnSleep60 = findViewById(R.id.btn_sleep_60);
        btnSleep90 = findViewById(R.id.btn_sleep_90);
        btnSleep120 = findViewById(R.id.btn_sleep_120);

        btnBgAudioToggle = findViewById(R.id.btn_bg_audio_toggle);

        btnTouchPlayPause = findViewById(R.id.btn_touch_play_pause);
        tvPlayPauseIcon = findViewById(R.id.tv_play_pause_icon);

        layoutExitDialog = findViewById(R.id.layout_exit_dialog);
        btnExitCancel = findViewById(R.id.btn_exit_cancel);
        btnExitConfirm = findViewById(R.id.btn_exit_confirm);

        if (btnExitCancel != null) {
            btnExitCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeExitDialog();
                }
            });
        }

        if (btnExitConfirm != null) {
            btnExitConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });
        }

        if (layoutExitDialog != null) {
            layoutExitDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeExitDialog();
                }
            });
        }

        layoutNetworkAlert = findViewById(R.id.layout_network_alert);
        etChannelSearch = findViewById(R.id.et_channel_search);
        btnClearSearch = findViewById(R.id.btn_clear_search);

        layoutDrawer = findViewById(R.id.layout_drawer);
        tvDrawerSubtitle = null;
        lvChannels = findViewById(R.id.lv_channels);

        layoutQuickQuality = findViewById(R.id.layout_quick_quality);

        layoutSettings = findViewById(R.id.layout_settings);
        lvSettingsChannels = findViewById(R.id.lv_settings_channels);

        channelAdapter = new ChannelAdapter(this, channelManager.getActiveChannels());
        lvChannels.setAdapter(channelAdapter);

        settingsChannelAdapter = new SettingsChannelAdapter(this, channelManager.getAllChannels());
        lvSettingsChannels.setAdapter(settingsChannelAdapter);

        // 1. Kanal listesinde tıklama
        lvChannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < channelAdapter.getCount()) {
                    Channel selected = channelAdapter.getItem(position);
                    selectAndPlayChannel(selected);
                    closeDrawer();
                }
            }
        });

        // 2. Kanal listesinde uzun tıklama (Favori)
        lvChannels.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < channelAdapter.getCount()) {
                    Channel selected = channelAdapter.getItem(position);
                    toggleFavoriteChannel(selected);
                    return true;
                }
                return false;
            }
        });

        // 3. Ayarlar ekranında kanal tıklama (Aktif/Pasif)
        lvSettingsChannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleChannelSettingAt(position);
            }
        });

        updateTouchUiVisibility();
    }

    private void updateTouchUiVisibility() {
        int touchVis = isTouchDevice ? View.VISIBLE : View.GONE;
        if (layoutTouchActions != null) {
            layoutTouchActions.setVisibility(touchVis);
        }
        if (btnTouchPrev != null) {
            btnTouchPrev.setVisibility(touchVis);
        }
        if (btnTouchNext != null) {
            btnTouchNext.setVisibility(touchVis);
        }
        if (tvOsdHint != null) {
            if (isTouchDevice) {
                tvOsdHint.setText("👆 Dokunarak / Kaydırarak kanal değiştirebilirsiniz | [☰] Liste");
            } else {
                tvOsdHint.setText("▲▼ Kanal | [OK] Liste | [Yeşil] Kalite | [Sarı] Favori | [Mavi] Ekran | [Menü] Ayarlar");
            }
        }
    }

    private void setupGestureDetector() {
        final int swipeThreshold = (int) (50 * getResources().getDisplayMetrics().density);
        final int swipeVelocityThreshold = (int) (80 * getResources().getDisplayMetrics().density);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (!isTouchDevice) {
                    isTouchDevice = true;
                    updateTouchUiVisibility();
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleOverlay();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!isDrawerOpen && !isSettingsOpen && !isQuickQualityOpen) {
                    toggleAspectRatio();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                // Yatay kaydırma
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > swipeThreshold && Math.abs(velocityX) > swipeVelocityThreshold) {
                        if (diffX > 0) {
                            // Soldan sağa kaydır -> Kanal listesi aç
                            if (!isDrawerOpen && !isSettingsOpen && !isQuickQualityOpen) {
                                openDrawer();
                                return true;
                            }
                        } else {
                            // Sağdan sola kaydır -> Açık panelleri kapat
                            if (isDrawerOpen) {
                                closeDrawer();
                                return true;
                            } else if (isSettingsOpen) {
                                closeSettings();
                                return true;
                            } else if (isQuickQualityOpen) {
                                closeQuickQuality();
                                return true;
                            }
                        }
                    }
                } else {
                    // Dikey kaydırma
                    if (Math.abs(diffY) > swipeThreshold && Math.abs(velocityY) > swipeVelocityThreshold) {
                        if (!isDrawerOpen && !isSettingsOpen && !isQuickQualityOpen) {
                            if (diffY < 0) {
                                // Yukarı kaydır -> Sonraki kanal
                                nextChannel();
                            } else {
                                // Aşağı kaydır -> Önceki kanal
                                prevChannel();
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void setupTouchControls() {
        View.OnTouchListener gestureTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isTouchDevice) {
                    isTouchDevice = true;
                    updateTouchUiVisibility();
                }
                boolean handled = gestureDetector.onTouchEvent(event);
                return true;
            }
        };

        surfaceView.setOnTouchListener(gestureTouchListener);
        layoutOverlay.setOnTouchListener(gestureTouchListener);

        // OSD Banner'a tıklanırsa kanal listesini aç
        layoutOsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        // Üst Dokunmatik Bar Butonları
        if (btnTouchChannels != null) {
            btnTouchChannels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDrawer();
                }
            });
        }

        if (btnTouchFav != null) {
            btnTouchFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavoriteChannel(channelManager.getChannelByIndex(currentChannelIndex));
                    resetOsdTimer();
                }
            });
        }

        if (btnTouchQuality != null) {
            btnTouchQuality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuickQuality();
                }
            });
        }

        if (btnTouchAspect != null) {
            btnTouchAspect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleAspectRatio();
                    resetOsdTimer();
                }
            });
        }

        if (btnTouchPip != null) {
            btnTouchPip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterPipMode();
                }
            });
        }

        if (btnTouchSettings != null) {
            btnTouchSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSettings();
                }
            });
        }

        if (btnTouchExit != null) {
            btnTouchExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExitDialog();
                }
            });
        }

        if (btnTouchPlayPause != null) {
            btnTouchPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    togglePlayPause();
                }
            });
        }

        // Ekran Yan Ok Butonları (◀ / ▶)
        if (btnTouchPrev != null) {
            btnTouchPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prevChannel();
                    resetOsdTimer();
                }
            });
        }

        if (btnTouchNext != null) {
            btnTouchNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextChannel();
                    resetOsdTimer();
                }
            });
        }

        // Kapatma [✕] Butonları
        if (btnCloseDrawer != null) {
            btnCloseDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                }
            });
        }

        if (btnCloseSettingsTop != null) {
            btnCloseSettingsTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeSettings();
                }
            });
        }

        if (btnCloseQuickQuality != null) {
            btnCloseQuickQuality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeQuickQuality();
                }
            });
        }

        // Karartma katmanına tıklandığında açık panelleri kapat
        if (viewScrim != null) {
            viewScrim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    closeSettings();
                    closeQuickQuality();
                    closeExitDialog();
                }
            });
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        try {
            if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
                if (tvPlayPauseIcon != null) tvPlayPauseIcon.setText("⏸");
                Toast.makeText(this, "▶ Yayın devam ediyor", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.pause();
                isPaused = true;
                if (tvPlayPauseIcon != null) tvPlayPauseIcon.setText("▶");
                Toast.makeText(this, "⏸ Yayın duraklatıldı", Toast.LENGTH_SHORT).show();
            }
            resetOsdTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExitDialog() {
        closeDrawer();
        closeSettings();
        closeQuickQuality();
        if (layoutOverlay != null) {
            layoutOverlay.setVisibility(View.GONE);
            handler.removeCallbacks(hideOsdRunnable);
        }
        if (layoutExitDialog != null) {
            layoutExitDialog.setVisibility(View.VISIBLE);
            isExitDialogOpen = true;
            if (btnExitCancel != null) {
                btnExitCancel.requestFocus();
            }
        }
    }

    private void closeExitDialog() {
        if (layoutExitDialog != null) {
            layoutExitDialog.setVisibility(View.GONE);
            isExitDialogOpen = false;
        }
        if (surfaceView != null) {
            surfaceView.requestFocus();
        }
    }

    private void toggleOverlay() {
        if (isExitDialogOpen) {
            closeExitDialog();
            return;
        }
        if (isDrawerOpen || isSettingsOpen || isQuickQualityOpen) {
            closeDrawer();
            closeSettings();
            closeQuickQuality();
            return;
        }
        if (layoutOverlay.getVisibility() == View.VISIBLE) {
            layoutOverlay.setVisibility(View.GONE);
            handler.removeCallbacks(hideOsdRunnable);
        } else {
            Channel cur = channelManager.getChannelByIndex(currentChannelIndex);
            if (cur != null) {
                showOsd(cur);
            }
        }
    }

    private void resetOsdTimer() {
        handler.removeCallbacks(hideOsdRunnable);
        handler.postDelayed(hideOsdRunnable, OSD_TIMEOUT_MS);
    }

    private void selectAndPlayChannel(Channel selected) {
        if (selected == null) return;
        List<Channel> all = channelManager.getActiveChannels();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == selected.getId()) {
                playChannel(i);
                break;
            }
        }
    }

    private void toggleChannelSettingAt(int position) {
        if (position < 0 || position >= settingsChannelAdapter.getCount()) return;
        Channel ch = settingsChannelAdapter.getItem(position);
        boolean active = channelManager.toggleChannelActive(ch.getId(), MainActivity.this);
        settingsChannelAdapter.notifyDataSetChanged();
        channelAdapter.updateList(channelManager.getActiveChannels());
        if (tvDrawerSubtitle != null) {
            tvDrawerSubtitle.setText("Aktif Kanallar (" + channelManager.getChannelCount() + ")");
        }
        String statusStr = active ? "AKTİF edildi ✓" : "PASİF yapıldı (Listeden gizlendi) ✕";
        Toast.makeText(MainActivity.this, ch.getName() + " " + statusStr, Toast.LENGTH_SHORT).show();
    }

    private void setupCategories() {
        int[] buttonIds = {
                R.id.btn_cat_all,
                R.id.btn_cat_fav,
                R.id.btn_cat_national,
                R.id.btn_cat_news,
                R.id.btn_cat_sports,
                R.id.btn_cat_doc,
                R.id.btn_cat_kids,
                R.id.btn_cat_music,
                R.id.btn_cat_culture
        };

        final String[] categories = {
                "Tümü",
                "Favoriler",
                "Ulusal",
                "Haber",
                "Spor",
                "Belgesel",
                "Çocuk",
                "Müzik",
                "Kültür"
        };

        for (int i = 0; i < buttonIds.length; i++) {
            Button btn = findViewById(buttonIds[i]);
            if (btn == null) continue;
            final String cat = categories[i];
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Channel> filtered = channelManager.getChannelsByCategory(cat, MainActivity.this);
                    channelAdapter.updateList(filtered);
                    if (tvDrawerSubtitle != null) {
                        tvDrawerSubtitle.setText(cat + " (" + filtered.size() + ")");
                    }
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateHeaderLayoutOrientation();
    }

    private void updateHeaderLayoutOrientation() {
        View headerRow = findViewById(R.id.layout_header_row);
        View scrollTouch = findViewById(R.id.scroll_touch_actions);
        if (headerRow instanceof LinearLayout) {
            LinearLayout row = (LinearLayout) headerRow;
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Dar dikey ekran: Sıkışma olmaması için alt alta
                row.setOrientation(LinearLayout.VERTICAL);
                if (scrollTouch != null) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) scrollTouch.getLayoutParams();
                    lp.leftMargin = 0;
                    lp.topMargin = (int) (6 * getResources().getDisplayMetrics().density);
                    scrollTouch.setLayoutParams(lp);
                }
            } else {
                // TV, Tablet ve Yatay Telefon: Geniş alan var, yan yana
                row.setOrientation(LinearLayout.HORIZONTAL);
                if (scrollTouch != null) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) scrollTouch.getLayoutParams();
                    lp.leftMargin = (int) (10 * getResources().getDisplayMetrics().density);
                    lp.topMargin = 0;
                    scrollTouch.setLayoutParams(lp);
                }
            }
        }
    }

    private void setupSettingsControls() {
        // Kapatma Butonları (Üst ve Alt)
        View btnCloseTop = findViewById(R.id.btn_close_settings_top);
        if (btnCloseTop != null) {
            btnCloseTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeSettings();
                }
            });
        }

        findViewById(R.id.btn_settings_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSettings();
            }
        });

        // 4 Sekmeli Ayarlar Paneli (2B)
        final Button btnTabChannels = findViewById(R.id.btn_tab_channels);
        final Button btnTabQuality = findViewById(R.id.btn_tab_quality);
        final Button btnTabBackup = findViewById(R.id.btn_tab_backup);
        final Button btnTabAbout = findViewById(R.id.btn_tab_about);

        final View contentChannels = findViewById(R.id.tab_content_channels);
        final View contentQuality = findViewById(R.id.tab_content_quality);
        final View contentBackup = findViewById(R.id.tab_content_backup);
        final View contentAbout = findViewById(R.id.tab_content_about);

        final Button[] tabBtns = { btnTabChannels, btnTabQuality, btnTabBackup, btnTabAbout };
        final View[] tabContents = { contentChannels, contentQuality, contentBackup, contentAbout };

        View.OnClickListener tabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedId = v.getId();
                for (int i = 0; i < tabBtns.length; i++) {
                    Button b = tabBtns[i];
                    View c = tabContents[i];
                    if (b == null || c == null) continue;
                    if (b.getId() == clickedId) {
                        c.setVisibility(View.VISIBLE);
                        b.setTextColor(Color.WHITE);
                        b.setTypeface(null, Typeface.BOLD);
                    } else {
                        c.setVisibility(View.GONE);
                        b.setTextColor(Color.parseColor("#8b949e"));
                        b.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }
        };

        for (Button b : tabBtns) {
            if (b != null) {
                b.setOnClickListener(tabClickListener);
            }
        }

        // Tümünü Aktif Et Butonu
        findViewById(R.id.btn_settings_reset_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelManager.resetAllChannelsActive(MainActivity.this);
                settingsChannelAdapter.notifyDataSetChanged();
                channelAdapter.updateList(channelManager.getActiveChannels());
                if (tvDrawerSubtitle != null) {
                    tvDrawerSubtitle.setText("Aktif Kanallar (" + channelManager.getActiveChannels().size() + ")");
                }
                Toast.makeText(MainActivity.this, "Tüm kanallar AKTİF hale getirildi ✓", Toast.LENGTH_SHORT).show();
            }
        });

        // Tümünü Kapat / Devre Dışı Bırak Butonu (Yeni)
        Button btnDisableAll = findViewById(R.id.btn_settings_disable_all);
        if (btnDisableAll != null) {
            btnDisableAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    channelManager.disableAllChannels(MainActivity.this);
                    settingsChannelAdapter.notifyDataSetChanged();
                    channelAdapter.updateList(channelManager.getActiveChannels());
                    if (tvDrawerSubtitle != null) {
                        tvDrawerSubtitle.setText("Aktif Kanallar (" + channelManager.getActiveChannels().size() + ")");
                    }
                    Toast.makeText(MainActivity.this, "Tüm kanallar KAPATILDI (İstediklerinizi tek tek açabilirsiniz)", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Kalite Butonları (Sekme 2)
        int[] qBtnIds = {
                R.id.btn_set_q_auto,
                R.id.btn_set_q_1080,
                R.id.btn_set_q_720,
                R.id.btn_set_q_480,
                R.id.btn_set_q_360
        };
        final String[] qPrefs = { "auto", "1080p", "720p", "480p", "360p" };

        for (int i = 0; i < qBtnIds.length; i++) {
            Button b = findViewById(qBtnIds[i]);
            if (b == null) continue;
            final String q = qPrefs[i];
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStreamQuality(q);
                    Toast.makeText(MainActivity.this, "Varsayılan kalite: " + q.toUpperCase(java.util.Locale.ROOT), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Ayarları Dışa Aktar (Yedekle - Sekme 3)
        View btnExport = findViewById(R.id.btn_settings_export);
        if (btnExport != null) {
            btnExport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exportSettings();
                }
            });
        }

        // Ayarları İçe Aktar (Geri Yükle - Sekme 3)
        View btnImport = findViewById(R.id.btn_settings_import);
        if (btnImport != null) {
            btnImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    importSettings();
                }
            });
        }

        // Güncellemeleri Denetle (Sekme 4)
        View btnCheckUpdate = findViewById(R.id.btn_check_updates);
        if (btnCheckUpdate != null) {
            btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUpdatesOnline();
                }
            });
        }

        // Sürüm metnini güncelle (Sekme 4)
        TextView tvAboutVer = findViewById(R.id.tv_about_version);
        if (tvAboutVer != null) {
            tvAboutVer.setText("📺 Saf TV v" + getAppVersionName());
        }

        // GitHub web sayfasını aç butonu (Sekme 4)
        Button btnOpenArchiveOrg = findViewById(R.id.btn_open_archive_org);
        if (btnOpenArchiveOrg != null) {
            btnOpenArchiveOrg.setText("🌐 GitHub Proje Sayfası");
            btnOpenArchiveOrg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/devezir/SafTV"));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Tarayıcı açılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        setupUiScaleControls();
        setupSleepTimerControls();
        setupBackgroundAudio();
    }

    private boolean isTvHardware() {
        try {
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            if (uiModeManager != null && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
                return true;
            }
        } catch (Exception ignored) {}
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
                return true;
            }
        } catch (Exception ignored) {}
        try {
            Configuration config = getResources().getConfiguration();
            if ((config.uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION) {
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private float getScaleFactor() {
        if ("80".equals(currentUiScale)) return 0.80f;
        if ("90".equals(currentUiScale)) return 0.90f;
        if ("100".equals(currentUiScale)) return 1.00f;
        if ("115".equals(currentUiScale)) return 1.15f;
        if ("130".equals(currentUiScale)) return 1.30f;
        if ("150".equals(currentUiScale)) return 1.50f;
        if ("normal".equals(currentUiScale)) return 1.00f;
        if ("tv".equals(currentUiScale)) return 1.30f;
        // "auto": TV ise %130, Mobil ise %100
        return isTvHardware() ? 1.30f : 1.00f;
    }

    private void setupUiScaleControls() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUiScale = sp.getString(KEY_UI_SCALE, "auto");

        if (btnScaleAuto != null) {
            btnScaleAuto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUiScale("auto");
                }
            });
        }
        if (btnScale80 != null) {
            btnScale80.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setUiScale("80"); }
            });
        }
        if (btnScale90 != null) {
            btnScale90.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setUiScale("90"); }
            });
        }
        if (btnScale100 != null) {
            btnScale100.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setUiScale("100"); }
            });
        }
        if (btnScale115 != null) {
            btnScale115.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setUiScale("115"); }
            });
        }
        if (btnScale130 != null) {
            btnScale130.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setUiScale("130"); }
            });
        }
        if (btnScale150 != null) {
            btnScale150.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setUiScale("150"); }
            });
        }

        updateUiScaleButtons();
        applyUiScale();
    }

    private void setUiScale(String scale) {
        currentUiScale = scale;
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_UI_SCALE, scale)
                .apply();
        updateUiScaleButtons();
        applyUiScale();
        String label = "auto".equals(scale)
                ? "Otomatik (" + (isTvHardware() ? "TV %130" : "Mobil %100") + ")"
                : "%" + scale;
        Toast.makeText(this, "Arayüz Ölçeği: " + label, Toast.LENGTH_SHORT).show();
    }

    private void updateUiScaleButtons() {
        if (tvScaleStatus != null) {
            if ("auto".equals(currentUiScale)) {
                tvScaleStatus.setText("● Otomatik (" + (isTvHardware() ? "TV %130" : "Mobil %100") + ")");
            } else {
                tvScaleStatus.setText("● %" + currentUiScale);
            }
            tvScaleStatus.setTextColor(Color.parseColor("#34D399"));
        }

        if (btnScaleAuto != null) {
            boolean active = "auto".equals(currentUiScale);
            btnScaleAuto.setText(active ? "● Otomatik (Akıllı Cihaz Tespiti)" : "Otomatik (Akıllı Cihaz Tespiti)");
            btnScaleAuto.setTextColor(active ? Color.parseColor("#34D399") : Color.WHITE);
            btnScaleAuto.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
        }

        Button[] btns = { btnScale80, btnScale90, btnScale100, btnScale115, btnScale130, btnScale150 };
        String[] scales = { "80", "90", "100", "115", "130", "150" };
        String[] titles = {
            "%80 (Ultra Kompakt)",
            "%90 (Kompakt)",
            "%100 (Standart Mobil)",
            "%115 (Tablet / Yakın TV)",
            "%130 (Büyük TV / 3m)",
            "%150 (Dev / Uzak TV)"
        };

        for (int i = 0; i < btns.length; i++) {
            Button b = btns[i];
            if (b == null) continue;
            boolean active = scales[i].equals(currentUiScale);
            b.setText(active ? "● " + titles[i] : titles[i]);
            b.setTextColor(active ? Color.parseColor("#34D399") : Color.WHITE);
            b.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private void applyUiScale() {
        float scale = getScaleFactor();
        if (channelAdapter != null) {
            channelAdapter.setUiScale(scale);
        }
        if (settingsChannelAdapter != null) {
            settingsChannelAdapter.setUiScale(scale);
        }

        // OSD Text sizes
        if (tvOsdNum != null) tvOsdNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(15 * scale));
        if (tvOsdName != null) tvOsdName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(17 * scale));
        if (tvOsdCat != null) tvOsdCat.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(11 * scale));
        if (tvOsdQuality != null) tvOsdQuality.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(11 * scale));
        if (tvOsdTime != null) tvOsdTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(13 * scale));
        if (tvOsdHint != null) tvOsdHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.round(11 * scale));

        // Sol Çekmece Genişliği (Drawer Width)
        if (layoutDrawer != null) {
            ViewGroup.LayoutParams lp = layoutDrawer.getLayoutParams();
            if (lp != null) {
                float density = getResources().getDisplayMetrics().density;
                int newWidth = (int) (270 * scale * density);
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int maxWidth = (int) (screenWidth * 0.52f);
                if (newWidth > maxWidth && maxWidth > 0) newWidth = maxWidth;
                lp.width = newWidth;
                layoutDrawer.setLayoutParams(lp);
            }
        }

        // Üst Bar Buton Fontları
        int btnFontSz = Math.max(9, Math.min(15, Math.round(11 * scale)));
        if (btnTouchQuality != null) btnTouchQuality.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnFontSz);
        if (btnTouchAspect != null) btnTouchAspect.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnFontSz);
        if (btnTouchPip != null) btnTouchPip.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnFontSz);
        if (btnTouchSettings != null) btnTouchSettings.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnFontSz);
        if (btnTouchExit != null) btnTouchExit.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnFontSz);
    }

    private void setupSleepTimerControls() {
        if (btnSleepOff != null) {
            btnSleepOff.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setSleepTimer(0); }
            });
        }
        if (btnSleep15 != null) {
            btnSleep15.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setSleepTimer(15); }
            });
        }
        if (btnSleep30 != null) {
            btnSleep30.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setSleepTimer(30); }
            });
        }
        if (btnSleep60 != null) {
            btnSleep60.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setSleepTimer(60); }
            });
        }
        if (btnSleep90 != null) {
            btnSleep90.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setSleepTimer(90); }
            });
        }
        if (btnSleep120 != null) {
            btnSleep120.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { setSleepTimer(120); }
            });
        }
        updateSleepTimerButtons(0);
        updateSleepTimerDisplay();
    }

    private void setSleepTimer(int minutes) {
        sleepTimerHandler.removeCallbacks(sleepTimerTickRunnable);
        if (sleepTimerRunnable != null) {
            sleepTimerHandler.removeCallbacks(sleepTimerRunnable);
            sleepTimerRunnable = null;
        }

        if (minutes <= 0) {
            sleepTimerEndTime = 0;
            updateSleepTimerButtons(0);
            updateSleepTimerDisplay();
            Toast.makeText(this, "Uyku zamanlayıcısı kapatıldı", Toast.LENGTH_SHORT).show();
            return;
        }

        sleepTimerEndTime = System.currentTimeMillis() + (minutes * 60 * 1000L);
        sleepTimerRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "🌙 Uyku zamanlayıcısı: Saf TV kapatılıyor...", Toast.LENGTH_LONG).show();
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception ignored) {}
                    mediaPlayer = null;
                }
                finishAffinity();
            }
        };

        sleepTimerHandler.postDelayed(sleepTimerRunnable, minutes * 60 * 1000L);
        sleepTimerHandler.post(sleepTimerTickRunnable);
        updateSleepTimerButtons(minutes);
        Toast.makeText(this, "🌙 Uyku zamanlayıcısı: " + minutes + " dakika ayarlandı", Toast.LENGTH_SHORT).show();
    }

    private void updateSleepTimerDisplay() {
        if (tvSleepStatus == null) return;
        if (sleepTimerEndTime <= System.currentTimeMillis()) {
            tvSleepStatus.setText("● Kapalı");
            tvSleepStatus.setTextColor(Color.parseColor("#8b949e"));
        } else {
            long remainingSec = (sleepTimerEndTime - System.currentTimeMillis()) / 1000;
            if (remainingSec < 0) remainingSec = 0;
            long mins = remainingSec / 60;
            long secs = remainingSec % 60;
            tvSleepStatus.setText(String.format(Locale.ROOT, "⏳ %02d:%02d kaldı", mins, secs));
            tvSleepStatus.setTextColor(Color.parseColor("#FBBF24"));
        }
    }

    private void updateSleepTimerButtons(int activeMinutes) {
        Button[] btns = { btnSleepOff, btnSleep15, btnSleep30, btnSleep60, btnSleep90, btnSleep120 };
        int[] mins = { 0, 15, 30, 60, 90, 120 };
        for (int i = 0; i < btns.length; i++) {
            Button b = btns[i];
            if (b == null) continue;
            if (mins[i] == activeMinutes) {
                b.setTextColor(Color.parseColor("#34D399"));
                b.setTypeface(null, Typeface.BOLD);
            } else {
                b.setTextColor(Color.WHITE);
                b.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void setupBackgroundAudio() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isBackgroundAudioEnabled = sp.getBoolean(KEY_BG_AUDIO, false);
        updateBgAudioButton();

        if (btnBgAudioToggle != null) {
            btnBgAudioToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBackgroundAudioEnabled = !isBackgroundAudioEnabled;
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putBoolean(KEY_BG_AUDIO, isBackgroundAudioEnabled)
                            .apply();
                    updateBgAudioButton();
                    String msg = isBackgroundAudioEnabled 
                            ? "✓ Arka planda ses AKTİF (Ekran kilitlendiğinde ses çalmaya devam eder)"
                            : "✕ Arka planda ses KAPALI (Standart)";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) return;
                if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    isScreenOff = true;
                } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                    isScreenOff = false;
                }
            }
        };
        registerReceiver(screenReceiver, filter);
    }

    private void updateBgAudioButton() {
        if (btnBgAudioToggle == null) return;
        if (isBackgroundAudioEnabled) {
            btnBgAudioToggle.setText("● Arka Planda Ses: AKTİF (Açık)");
            btnBgAudioToggle.setTextColor(Color.parseColor("#34D399"));
            btnBgAudioToggle.setTypeface(null, Typeface.BOLD);
        } else {
            btnBgAudioToggle.setText("✕ Arka Planda Ses: Kapalı (Standart)");
            btnBgAudioToggle.setTextColor(Color.WHITE);
            btnBgAudioToggle.setTypeface(null, Typeface.NORMAL);
        }
    }

    public void enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                closeDrawer();
                closeSettings();
                closeQuickQuality();
                closeExitDialog();
                if (layoutOverlay != null) {
                    layoutOverlay.setVisibility(View.GONE);
                }

                Rational rational;
                if (videoWidth > 0 && videoHeight > 0) {
                    float ratio = (float) videoWidth / (float) videoHeight;
                    if (ratio < 0.4184f) ratio = 0.4184f;
                    if (ratio > 2.39f) ratio = 2.39f;
                    rational = new Rational((int) (ratio * 1000), 1000);
                } else {
                    rational = new Rational(16, 9);
                }

                PictureInPictureParams params = new PictureInPictureParams.Builder()
                        .setAspectRatio(rational)
                        .build();
                enterPictureInPictureMode(params);
            } catch (Exception e) {
                Toast.makeText(this, "PiP başlatılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "PiP modu Android 8.0 ve üzeri gerektirir.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mediaPlayer != null && mediaPlayer.isPlaying()) {
            enterPipMode();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            if (layoutOverlay != null) layoutOverlay.setVisibility(View.GONE);
            if (layoutDrawer != null) layoutDrawer.setVisibility(View.GONE);
            if (layoutSettings != null) layoutSettings.setVisibility(View.GONE);
            if (layoutQuickQuality != null) layoutQuickQuality.setVisibility(View.GONE);
            if (viewScrim != null) viewScrim.setVisibility(View.GONE);
            if (btnTouchPrev != null) btnTouchPrev.setVisibility(View.GONE);
            if (btnTouchNext != null) btnTouchNext.setVisibility(View.GONE);
        } else {
            updateTouchUiVisibility();
            Channel cur = channelManager.getChannelByIndex(currentChannelIndex);
            if (cur != null) {
                showOsd(cur);
            }
        }
    }

    private void setupQuickQualityControls() {
        int[] qqIds = {
                R.id.btn_quick_q_auto,
                R.id.btn_quick_q_1080,
                R.id.btn_quick_q_720,
                R.id.btn_quick_q_480,
                R.id.btn_quick_q_360
        };
        final String[] qPrefs = { "auto", "1080p", "720p", "480p", "360p" };

        for (int i = 0; i < qqIds.length; i++) {
            Button b = findViewById(qqIds[i]);
            if (b == null) continue;
            final String q = qPrefs[i];
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStreamQuality(q);
                    closeQuickQuality();
                }
            });
        }
    }

    private void setStreamQuality(String quality) {
        currentQualityPref = quality;
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_QUALITY, quality)
                .apply();

        String label = quality.equals("auto") ? "Otomatik" : quality;
        Toast.makeText(this, "Yayın Kalitesi: " + label, Toast.LENGTH_SHORT).show();
        playChannel(currentChannelIndex);
    }

    private void playChannel(int index) {
        playChannel(index, false);
    }

    private void playChannel(int index, boolean isRetry) {
        isPaused = false;
        if (tvPlayPauseIcon != null) {
            tvPlayPauseIcon.setText("⏸");
        }
        if (!isRetry) {
            playbackRetryCount = 0;
        }
        if (index < 0 || index >= channelManager.getChannelCount()) {
            index = 0;
        }
        currentChannelIndex = index;
        final Channel channel = channelManager.getChannelByIndex(index);

        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_LAST_CHANNEL, currentChannelIndex)
                .apply();

        channelAdapter.setCurrentPlayingId(channel.getId());

        showOsd(channel);

        loadingSpinner.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.GONE);

        if (!isSurfaceCreated) {
            return;
        }

        final Map<String, String> headers = new HashMap<>();
        if (channel.getUserAgent() != null) {
            headers.put("User-Agent", channel.getUserAgent());
        }
        if (channel.getReferer() != null) {
            headers.put("Referer", channel.getReferer());
        }

        HlsParser.resolveQualityUrl(channel.getUrl(), currentQualityPref, headers, new HlsParser.QualityCallback() {
            @Override
            public void onResolved(String targetUrl, String resolvedQuality) {
                activeStreamQualityLabel = resolvedQuality;
                tvOsdQuality.setText("● CANLI [" + activeStreamQualityLabel + "]");
                startMediaPlayer(targetUrl, headers);
            }
        });
    }

    private void startMediaPlayer(String streamUrl, Map<String, String> headers) {
        try {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                } catch (Exception ignored) {}
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setOnVideoSizeChangedListener(this);
                mediaPlayer.setOnInfoListener(this);
            }

            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(this, Uri.parse(streamUrl), headers);
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            handlePlaybackError("Yayın başlatılamadı, yeniden deneniyor...");
        }
    }

    private void showOsd(Channel channel) {
        tvOsdNum.setText(String.valueOf(channel.getId()));
        tvOsdName.setText(channel.getName());
        tvOsdCat.setText(channel.getCategory().toUpperCase());
        tvOsdQuality.setText("● CANLI [" + activeStreamQualityLabel + "]");

        boolean isFav = channelManager.isFavorite(channel.getId(), this);
        tvOsdFav.setVisibility(isFav ? View.VISIBLE : View.GONE);
        if (btnTouchFav != null) {
            btnTouchFav.setText(isFav ? "★ Favori" : "☆ Favori");
            btnTouchFav.setTextColor(isFav ? 0xFFF59E0B : 0xFFFFFFFF);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvOsdTime.setText(sdf.format(new Date()));
        } catch (Exception ignored) {}

        updateTouchUiVisibility();

        layoutOverlay.setVisibility(View.VISIBLE);

        handler.removeCallbacks(hideOsdRunnable);
        handler.postDelayed(hideOsdRunnable, OSD_TIMEOUT_MS);
    }

    private void toggleFavoriteChannel(Channel channel) {
        if (channel == null) return;
        boolean added = channelManager.toggleFavorite(channel.getId(), this);
        channelAdapter.notifyDataSetChanged();
        if (currentChannelIndex < channelManager.getChannelCount()) {
            Channel cur = channelManager.getChannelByIndex(currentChannelIndex);
            if (cur.getId() == channel.getId()) {
                tvOsdFav.setVisibility(added ? View.VISIBLE : View.GONE);
                if (btnTouchFav != null) {
                    btnTouchFav.setText(added ? "★ Favori" : "☆ Favori");
                    btnTouchFav.setTextColor(added ? 0xFFF59E0B : 0xFFFFFFFF);
                }
            }
        }
        String msg = channel.getName() + (added ? " favorilere eklendi ⭐" : " favorilerden çıkarıldı");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toggleAspectRatio() {
        aspectRatioMode = (aspectRatioMode + 1) % 2;
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_ASPECT_RATIO, aspectRatioMode)
                .apply();

        applyAspectRatio();

        String label = (aspectRatioMode == 0) ? "Tam Ekran (Ekranı Doldur)" : "Orantılı (16:9 Fit)";
        Toast.makeText(this, "Görüntü: " + label, Toast.LENGTH_SHORT).show();
    }

    private void applyAspectRatio() {
        if (surfaceView == null) return;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        if (aspectRatioMode == 0 || videoWidth == 0 || videoHeight == 0) {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
        } else {
            int viewWidth = findViewById(R.id.root_view).getWidth();
            int viewHeight = findViewById(R.id.root_view).getHeight();
            if (viewWidth > 0 && viewHeight > 0) {
                float videoRatio = (float) videoWidth / (float) videoHeight;
                float screenRatio = (float) viewWidth / (float) viewHeight;

                if (videoRatio > screenRatio) {
                    lp.width = viewWidth;
                    lp.height = (int) (viewWidth / videoRatio);
                } else {
                    lp.height = viewHeight;
                    lp.width = (int) (viewHeight * videoRatio);
                }
                lp.gravity = Gravity.CENTER;
            }
        }
        surfaceView.setLayoutParams(lp);
    }

    private void openDrawer() {
        isDrawerOpen = true;
        closeSettings();
        closeQuickQuality();
        if (viewScrim != null) viewScrim.setVisibility(View.VISIBLE);
        layoutOverlay.setVisibility(View.GONE);
        layoutDrawer.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideOsdRunnable);

        if (etChannelSearch != null && etChannelSearch.getText().length() > 0) {
            etChannelSearch.setText("");
        }

        // Aktif kanal listesini ve başlık sayısını anlık güncelle
        List<Channel> activeList = channelManager.getActiveChannels();
        channelAdapter.updateList(activeList);
        if (tvDrawerSubtitle != null) {
            tvDrawerSubtitle.setText("Tüm Kanallar (" + activeList.size() + ")");
        }

        lvChannels.requestFocus();

        Channel current = channelManager.getChannelByIndex(currentChannelIndex);
        if (current != null) {
            for (int i = 0; i < channelAdapter.getCount(); i++) {
                if (channelAdapter.getItem(i).getId() == current.getId()) {
                    final int selPos = i;
                    lvChannels.setSelection(selPos);
                    lvChannels.post(new Runnable() {
                        @Override
                        public void run() {
                            lvChannels.setSelection(selPos);
                        }
                    });
                    break;
                }
            }
        }
    }

    private void closeDrawer() {
        isDrawerOpen = false;
        layoutDrawer.setVisibility(View.GONE);
        if (etChannelSearch != null && etChannelSearch.getText().length() > 0) {
            etChannelSearch.setText("");
        }
        hideKeyboard(etChannelSearch);
        if (!isSettingsOpen && !isQuickQualityOpen && viewScrim != null) {
            viewScrim.setVisibility(View.GONE);
        }
        surfaceView.requestFocus();
    }

    private void openSettings() {
        isSettingsOpen = true;
        closeDrawer();
        closeQuickQuality();
        if (viewScrim != null) viewScrim.setVisibility(View.VISIBLE);
        layoutOverlay.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideOsdRunnable);
        settingsChannelAdapter.notifyDataSetChanged();
        lvSettingsChannels.requestFocus();
        if (settingsChannelAdapter.getCount() > 0) {
            lvSettingsChannels.setSelection(0);
        }
    }

    private void closeSettings() {
        isSettingsOpen = false;
        layoutSettings.setVisibility(View.GONE);
        if (!isDrawerOpen && !isQuickQualityOpen && viewScrim != null) {
            viewScrim.setVisibility(View.GONE);
        }
        surfaceView.requestFocus();
    }

    private void openQuickQuality() {
        isQuickQualityOpen = true;
        closeDrawer();
        closeSettings();
        if (viewScrim != null) viewScrim.setVisibility(View.VISIBLE);
        layoutOverlay.setVisibility(View.GONE);
        layoutQuickQuality.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_quick_q_auto).requestFocus();

        handler.removeCallbacks(hideQuickQualityRunnable);
        handler.postDelayed(hideQuickQualityRunnable, 5000);
    }

    private void closeQuickQuality() {
        isQuickQualityOpen = false;
        layoutQuickQuality.setVisibility(View.GONE);
        if (!isDrawerOpen && !isSettingsOpen && viewScrim != null) {
            viewScrim.setVisibility(View.GONE);
        }
        surfaceView.requestFocus();
    }

    private void nextChannel() {
        int count = channelManager.getChannelCount();
        if (count == 0) return;
        int next = (currentChannelIndex + 1) % count;
        playChannel(next);
    }

    private void prevChannel() {
        int count = channelManager.getChannelCount();
        if (count == 0) return;
        int prev = (currentChannelIndex - 1 + count) % count;
        playChannel(prev);
    }

    private void jumpChannels(int delta) {
        int count = channelManager.getChannelCount();
        if (count == 0) return;
        int target = (currentChannelIndex + delta) % count;
        if (target < 0) target += count;
        playChannel(target);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isTouchDevice) {
            isTouchDevice = true;
            updateTouchUiVisibility();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (isExitDialogOpen) {
            closeExitDialog();
            return;
        }
        if (isQuickQualityOpen) {
            closeQuickQuality();
            return;
        }
        if (isSettingsOpen) {
            closeSettings();
            return;
        }
        if (isDrawerOpen) {
            closeDrawer();
            return;
        }
        if (layoutOverlay != null && layoutOverlay.getVisibility() == View.VISIBLE) {
            layoutOverlay.setVisibility(View.GONE);
            handler.removeCallbacks(hideOsdRunnable);
            return;
        }
        showExitDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isExitDialogOpen) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                closeExitDialog();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PLAY ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            togglePlayPause();
            return true;
        }

        boolean isEnterKey = (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                              keyCode == KeyEvent.KEYCODE_ENTER ||
                              keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER);

        // 1. Ayarlar açıkken OK / ENTER basıldıysa:
        if (isSettingsOpen) {
            if (isEnterKey) {
                View focused = getCurrentFocus();
                if (!(focused instanceof Button)) {
                    int pos = lvSettingsChannels.getSelectedItemPosition();
                    if (pos < 0) {
                        View focusedChild = lvSettingsChannels.getFocusedChild();
                        if (focusedChild != null) {
                            pos = lvSettingsChannels.getPositionForView(focusedChild);
                        }
                    }
                    if (pos >= 0 && pos < settingsChannelAdapter.getCount()) {
                        toggleChannelSettingAt(pos);
                        return true;
                    }
                }
            }
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_M) {
                closeSettings();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        // 2. Hızlı Kalite paneli açıkken:
        if (isQuickQualityOpen) {
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_PROG_GREEN || keyCode == KeyEvent.KEYCODE_G) {
                closeQuickQuality();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        // 3. Kanal listesi açıkken:
        if (isDrawerOpen) {
            if (isEnterKey) {
                View focused = getCurrentFocus();
                if (!(focused instanceof Button)) {
                    int pos = lvChannels.getSelectedItemPosition();
                    if (pos < 0) {
                        View focusedChild = lvChannels.getFocusedChild();
                        if (focusedChild != null) {
                            pos = lvChannels.getPositionForView(focusedChild);
                        }
                    }
                    if (pos >= 0 && pos < channelAdapter.getCount()) {
                        Channel selected = channelAdapter.getItem(pos);
                        selectAndPlayChannel(selected);
                        closeDrawer();
                        return true;
                    }
                }
            }
            if (keyCode == KeyEvent.KEYCODE_PROG_YELLOW || keyCode == KeyEvent.KEYCODE_Y || keyCode == KeyEvent.KEYCODE_F) {
                int pos = lvChannels.getSelectedItemPosition();
                if (pos < 0) {
                    View focusedChild = lvChannels.getFocusedChild();
                    if (focusedChild != null) {
                        pos = lvChannels.getPositionForView(focusedChild);
                    }
                }
                if (pos >= 0 && pos < channelAdapter.getCount()) {
                    toggleFavoriteChannel(channelAdapter.getItem(pos));
                    return true;
                }
            }
            if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_M) {
                closeDrawer();
                openSettings();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                closeDrawer();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        // 4. Canlı yayın izlenirken kumanda kontrolleri:
        if (isEnterKey) {
            openDrawer();
            return true;
        }

        // Green Key (Hızlı Kalite Seçimi)
        if (keyCode == KeyEvent.KEYCODE_PROG_GREEN || keyCode == KeyEvent.KEYCODE_G) {
            openQuickQuality();
            return true;
        }

        // Yellow Key (Favori)
        if (keyCode == KeyEvent.KEYCODE_PROG_YELLOW || keyCode == KeyEvent.KEYCODE_Y || keyCode == KeyEvent.KEYCODE_F) {
            toggleFavoriteChannel(channelManager.getChannelByIndex(currentChannelIndex));
            return true;
        }

        // Blue Key (Aspect Ratio)
        if (keyCode == KeyEvent.KEYCODE_PROG_BLUE || keyCode == KeyEvent.KEYCODE_B) {
            toggleAspectRatio();
            return true;
        }

        // Menu Key (Ayarlar)
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_M) {
            openSettings();
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                nextChannel();
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                prevChannel();
                return true;

            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
                jumpChannels(5);
                return true;

            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                jumpChannels(-5);
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_INFO:
                showOsd(channelManager.getChannelByIndex(currentChannelIndex));
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                openDrawer();
                return true;

            case KeyEvent.KEYCODE_BACK:
                showExitDialog();
                return true;

            default:
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    int digit = keyCode - KeyEvent.KEYCODE_0;
                    numKeyBuffer += digit;
                    tvStatus.setText("Kanal: " + numKeyBuffer);
                    tvStatus.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(jumpChannelRunnable);
                    handler.postDelayed(jumpChannelRunnable, 1200);
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector != null && gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playbackRetryCount = 0;
        isPaused = false;
        if (tvPlayPauseIcon != null) {
            tvPlayPauseIcon.setText("⏸");
        }
        loadingSpinner.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        mp.start();
        applyAspectRatio();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        loadingSpinner.setVisibility(View.VISIBLE);
        handlePlaybackError("Bağlantı koptu, yeniden deneniyor...");
        return true;
    }

    private void handlePlaybackError(String msg) {
        playbackRetryCount++;
        if (playbackRetryCount > 5) {
            loadingSpinner.setVisibility(View.GONE);
            tvStatus.setText("⚠️ Yayın açılamadı (Kanal şu an devre dışı veya internet bağlantısı yok).\nFarklı bir kanala geçebilirsiniz.");
            tvStatus.setVisibility(View.VISIBLE);
            return;
        }

        tvStatus.setText(msg + " (" + playbackRetryCount + "/5)");
        tvStatus.setVisibility(View.VISIBLE);
        long delayMs = 1500 + (playbackRetryCount * 800); // 2.3s, 3.1s, 3.9s...
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    playChannel(currentChannelIndex, true);
                }
            }
        }, delayMs);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            loadingSpinner.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            loadingSpinner.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        this.videoWidth = width;
        this.videoHeight = height;
        applyAspectRatio();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDisplay(holder);
                applyAspectRatio();
                return;
            } catch (Exception ignored) {}
        }
        playChannel(currentChannelIndex);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreated = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInPictureInPictureMode()) {
            return;
        }
        if (isBackgroundAudioEnabled && isScreenOff && mediaPlayer != null) {
            try {
                mediaPlayer.setDisplay(null);
            } catch (Exception ignored) {}
            return;
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (isSurfaceCreated) {
            if (mediaPlayer == null) {
                playbackRetryCount = 0;
                playChannel(currentChannelIndex);
            } else {
                try {
                    if (!mediaPlayer.isPlaying()) {
                        playbackRetryCount = 0;
                        playChannel(currentChannelIndex);
                    }
                } catch (Exception e) {
                    playbackRetryCount = 0;
                    playChannel(currentChannelIndex);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(hideOsdRunnable);
        handler.removeCallbacks(hideQuickQualityRunnable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInPictureInPictureMode()) {
            return;
        }

        if (isBackgroundAudioEnabled && isScreenOff) {
            return;
        }

        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    private void setupNetworkMonitoring() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        try {
            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
            isNetworkAvailable = (activeInfo != null && activeInfo.isConnectedOrConnecting());
            if (!isNetworkAvailable && layoutNetworkAlert != null) {
                layoutNetworkAlert.setVisibility(View.VISIBLE);
            }
        } catch (Exception ignored) {}

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isNetworkAvailable) {
                            isNetworkAvailable = true;
                            if (layoutNetworkAlert != null) layoutNetworkAlert.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "İnternet bağlantısı sağlandı ✓", Toast.LENGTH_SHORT).show();
                            if (isSurfaceCreated && (mediaPlayer == null || !mediaPlayer.isPlaying())) {
                                playbackRetryCount = 0;
                                playChannel(currentChannelIndex);
                            }
                        }
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isNetworkAvailable = false;
                        if (layoutNetworkAlert != null) layoutNetworkAlert.setVisibility(View.VISIBLE);
                        tvStatus.setText("📶 İnternet bağlantısı kesildi!");
                        tvStatus.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        try {
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterNetworkCallback() {
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) {}
        }
    }

    private void setupChannelSearch() {
        if (etChannelSearch == null) return;

        etChannelSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (query.trim().isEmpty()) {
                    if (btnClearSearch != null) btnClearSearch.setVisibility(View.GONE);
                    List<Channel> active = channelManager.getActiveChannels();
                    channelAdapter.updateList(active);
                    if (tvDrawerSubtitle != null) {
                        tvDrawerSubtitle.setText("Tüm Kanallar (" + active.size() + ")");
                    }
                } else {
                    if (btnClearSearch != null) btnClearSearch.setVisibility(View.VISIBLE);
                    List<Channel> results = channelManager.searchChannels(query);
                    channelAdapter.updateList(results);
                    if (tvDrawerSubtitle != null) {
                        tvDrawerSubtitle.setText("Sonuçlar (" + results.size() + ")");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (btnClearSearch != null) {
            btnClearSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etChannelSearch.setText("");
                    hideKeyboard(etChannelSearch);
                }
            });
        }
    }

    private void hideKeyboard(View view) {
        if (view == null) return;
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ignored) {}
    }

    private int getAppVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 10;
        }
    }

    private String getAppVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.7.0";
        }
    }

    private void exportSettings() {
        try {
            JSONObject root = new JSONObject();
            root.put("app", "SafTV");
            root.put("versionCode", getAppVersionCode());
            root.put("versionName", getAppVersionName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            root.put("backupDate", sdf.format(new Date()));

            JSONObject prefsObj = new JSONObject();
            prefsObj.put(KEY_QUALITY, currentQualityPref);
            prefsObj.put(KEY_ASPECT_RATIO, aspectRatioMode);
            prefsObj.put(KEY_LAST_CHANNEL, currentChannelIndex);
            prefsObj.put(KEY_UI_SCALE, currentUiScale);
            prefsObj.put(KEY_BG_AUDIO, isBackgroundAudioEnabled);
            root.put("preferences", prefsObj);

            JSONArray favArray = new JSONArray();
            for (String f : channelManager.getFavoritesSet(this)) {
                try {
                    favArray.put(Integer.parseInt(f));
                } catch (NumberFormatException ignored) {}
            }
            root.put("favorites", favArray);

            JSONArray disabledArray = new JSONArray();
            for (String d : channelManager.getDisabledChannelsSet(this)) {
                try {
                    disabledArray.put(Integer.parseInt(d));
                } catch (NumberFormatException ignored) {}
            }
            root.put("disabled_channels", disabledArray);

            SimpleDateFormat fileDateSdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String fileName = "saf_tv_yedek_" + fileDateSdf.format(new Date()) + ".json";

            File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dir == null) dir = getFilesDir();
            if (!dir.exists()) dir.mkdirs();

            File destFile = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(destFile);
            fos.write(root.toString(2).getBytes("UTF-8"));
            fos.close();

            Toast.makeText(this, "💾 Ayarlar yedeklendi:\n" + destFile.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Yedekleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void importSettings() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dir == null) dir = getFilesDir();

            File[] candidates = dir.listFiles(new java.io.FilenameFilter() {
                @Override
                public boolean accept(File d, String name) {
                    return name.startsWith("saf_tv_yedek") && name.endsWith(".json");
                }
            });

            if (candidates == null || candidates.length == 0) {
                File intDir = getFilesDir();
                candidates = intDir.listFiles(new java.io.FilenameFilter() {
                    @Override
                    public boolean accept(File d, String name) {
                        return name.startsWith("saf_tv_yedek") && name.endsWith(".json");
                    }
                });
            }

            if (candidates == null || candidates.length == 0) {
                Toast.makeText(this, "⚠️ Yedek dosyası bulunamadı!\nÖnce 'Dışa Aktar' yapmalısınız.", Toast.LENGTH_LONG).show();
                return;
            }

            File newest = candidates[0];
            for (int i = 1; i < candidates.length; i++) {
                if (candidates[i].lastModified() > newest.lastModified()) {
                    newest = candidates[i];
                }
            }

            FileInputStream fis = new FileInputStream(newest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();

            JSONObject root = new JSONObject(sb.toString());

            if (root.has("preferences")) {
                JSONObject p = root.getJSONObject("preferences");
                if (p.has(KEY_QUALITY)) {
                    currentQualityPref = p.getString(KEY_QUALITY);
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_QUALITY, currentQualityPref).apply();
                }
                if (p.has(KEY_ASPECT_RATIO)) {
                    aspectRatioMode = p.getInt(KEY_ASPECT_RATIO);
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putInt(KEY_ASPECT_RATIO, aspectRatioMode).apply();
                    applyAspectRatio();
                }
                if (p.has(KEY_UI_SCALE)) {
                    currentUiScale = p.getString(KEY_UI_SCALE);
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_UI_SCALE, currentUiScale).apply();
                    updateUiScaleButtons();
                    applyUiScale();
                }
                if (p.has(KEY_BG_AUDIO)) {
                    isBackgroundAudioEnabled = p.getBoolean(KEY_BG_AUDIO);
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean(KEY_BG_AUDIO, isBackgroundAudioEnabled).apply();
                    updateBgAudioButton();
                }
            }

            if (root.has("favorites")) {
                JSONArray fArr = root.getJSONArray("favorites");
                Set<String> newFavs = new HashSet<>();
                for (int i = 0; i < fArr.length(); i++) {
                    newFavs.add(String.valueOf(fArr.getInt(i)));
                }
                channelManager.setFavoritesSet(newFavs, this);
            }

            if (root.has("disabled_channels")) {
                JSONArray dArr = root.getJSONArray("disabled_channels");
                Set<String> newDisabled = new HashSet<>();
                for (int i = 0; i < dArr.length(); i++) {
                    newDisabled.add(String.valueOf(dArr.getInt(i)));
                }
                channelManager.setDisabledChannelsSet(newDisabled, this);
            }

            channelManager.syncChannelSettings(this);
            channelAdapter.updateList(channelManager.getActiveChannels());
            settingsChannelAdapter.notifyDataSetChanged();
            if (tvDrawerSubtitle != null) {
                tvDrawerSubtitle.setText("Aktif Kanallar (" + channelManager.getChannelCount() + ")");
            }
            playChannel(currentChannelIndex);

            Channel cur = channelManager.getChannelByIndex(currentChannelIndex);
            if (cur != null) {
                showOsd(cur);
            }

            Toast.makeText(this, "📂 Ayarlar geri yüklendi:\n" + newest.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "İçe aktarma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUpdatesOnline() {
        Toast.makeText(this, "🔄 Güncellemeler denetleniyor...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] endpoints = new String[] {
                    "https://raw.githubusercontent.com/devezir/SafTV/main/version.json?t=" + System.currentTimeMillis(),
                    "https://archive.org/download/saftv-android/version.json?t=" + System.currentTimeMillis()
                };

                JSONObject json = null;
                for (String endpointUrl : endpoints) {
                    try {
                        URL url = new URL(endpointUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setUseCaches(false);
                        conn.setDefaultUseCaches(false);
                        conn.setConnectTimeout(6000);
                        conn.setReadTimeout(6000);
                        conn.setRequestProperty("User-Agent", "SafTV-Updater");
                        conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                        conn.setRequestProperty("Pragma", "no-cache");

                        if (conn.getResponseCode() == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            reader.close();
                            conn.disconnect();
                            json = new JSONObject(sb.toString());
                            break;
                        }
                        conn.disconnect();
                    } catch (Exception ignored) {
                    }
                }

                if (json != null) {
                    final int remoteVc = json.optInt("versionCode", 0);
                    final String remoteVn = json.optString("versionName", "");
                    final String downloadUrl = json.optString("apkUrl",
                            "https://github.com/devezir/SafTV/releases/download/v" + remoteVn + "/SafTV_v" + remoteVn + "_vc" + remoteVc + ".apk");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (remoteVc > getAppVersionCode()) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("🚀 Yeni Sürüm Mevcut!")
                                        .setMessage("Saf TV v" + remoteVn + " yayında.\nYeni sürümü indirmek ister misiniz?")
                                        .setPositiveButton("İndir", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                    Toast.makeText(MainActivity.this, "Tarayıcı açılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Daha Sonra", null)
                                        .show();
                            } else {
                                Toast.makeText(MainActivity.this, "✓ Saf TV güncel (v" + getAppVersionName() + ")", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Güncelleme sunucusuna ulaşılamadı (İnterneti kontrol edin)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkCallback();
        if (screenReceiver != null) {
            try {
                unregisterReceiver(screenReceiver);
            } catch (Exception ignored) {}
            screenReceiver = null;
        }
        if (sleepTimerHandler != null) {
            sleepTimerHandler.removeCallbacksAndMessages(null);
        }
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }
}
