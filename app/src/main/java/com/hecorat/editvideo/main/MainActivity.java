package com.hecorat.editvideo.main;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.addimage.FloatImage;
import com.hecorat.editvideo.addtext.AlphaColorDrawable;
import com.hecorat.editvideo.addtext.ColorPickerView;
import com.hecorat.editvideo.addtext.FloatText;
import com.hecorat.editvideo.addtext.FontAdapter;
import com.hecorat.editvideo.addtext.FontManager;
import com.hecorat.editvideo.audio.VolumeEditor;
import com.hecorat.editvideo.export.ExportTask;
import com.hecorat.editvideo.filemanager.FragmentAudioGallery;
import com.hecorat.editvideo.filemanager.FragmentImagesGallery;
import com.hecorat.editvideo.filemanager.FragmentVideosGallery;
import com.hecorat.editvideo.helper.Utils;
import com.hecorat.editvideo.preview.CustomVideoView;
import com.hecorat.editvideo.timeline.AudioTL;
import com.hecorat.editvideo.timeline.AudioTLControl;
import com.hecorat.editvideo.timeline.BigTimeMark;
import com.hecorat.editvideo.timeline.CustomHorizontalScrollView;
import com.hecorat.editvideo.timeline.ExtraTL;
import com.hecorat.editvideo.timeline.ExtraTLControl;
import com.hecorat.editvideo.timeline.SmallTimeMark;
import com.hecorat.editvideo.timeline.TimeText;
import com.hecorat.editvideo.timeline.VideoTL;
import com.hecorat.editvideo.timeline.VideoTLControl;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements VideoTLControl.OnControlTimeLineChanged,
        ExtraTLControl.OnExtraTimeLineControlChanged, AudioTLControl.OnAudioControlTimeLineChanged,
        ColorPickerView.OnColorChangedListener, VolumeEditor.OnVolumeChangedListener {
    private RelativeLayout mVideoViewLayout;
    private RelativeLayout mLayoutVideo, mLayoutImage, mLayoutText, mLayoutAudio;
    private RelativeLayout mTimeLineVideo, mTimeLineImage, mTimeLineText, mTimeLineAudio;
    private LinearLayout mLayoutScrollView;
    private ImageView mTLShadow;
    private RelativeLayout.LayoutParams mTLShadowParams;
    private ImageView mShadowIndicator;
    private RelativeLayout.LayoutParams mShadowIndicatorParams;
    private ViewPager mViewPager;
    private TextView mFolderName;
    private ImageView mBtnBack, mBtnAdd, mBtnUndo, mBtnExport,
            mBtnPlay, mBtnDelete, mBtnEditText, mBtnVolume;
    private LinearLayout mFileManager;
    private ImageView mVideoTab, mImageTab, mAudioTab;
    private LinearLayout mVideoTabLayout, mImageTabLayout, mAudioTabLayout;
    private CustomVideoView mActiveVideoView, mInActiveVideoView, mVideoView1, mVideoView2;
    private RelativeLayout mLayoutTimeLine;
    private FrameLayout mLimitTimeLineVideo, mLimitTimeLineImage, mLimitTimeLineText,
            mLimitTimeLineAudio, mSeperateLineVideo, mSeperateLineImage, mSeperateLineText;
    private LinearLayout mLayoutAdd;
    private TextView mBtnAddMedia, mBtnAddText;
    private LinearLayout mLayoutEditText;
    private EditText mEditText, mEdtColorHex;
    private Spinner mFontSpinner;
    private ImageView mBtnBold, mBtnItalic, mBtnTextColor, mBtnTextBgrColor;
    private LinearLayout mLayoutBtnBold, mLayoutBtnItalic;
    private LinearLayout mLayoutColorPicker;
    private RelativeLayout mLayoutBtnTextColor, mLayoutBtnTextBgrColor;
    private Button mBtnCloseColorPicker;
    private ImageView mIndicatorTextColor, mIndicatorTextBgr;

    private Thread mThreadPreviewVideo;
    private ArrayList<VideoTL> mVideoList;
    private ArrayList<ExtraTL> mImageList;
    private ArrayList<ExtraTL> mTextList;
    private ArrayList<AudioTL> mAudioList;
    private ArrayList<ExtraTL> mListInLayoutImage, mListInLayoutText;
    public ArrayList<String> mFontPath, mFontName;

    private MainActivity mActivity;
    private FontAdapter mFontAdapter;
    private MediaPlayer mMediaPlayer;
    private VideoTL mSelectedVideoTL, mCurrentVideoTL;
    private VideoTLControl mVideoTLControl;
    private ExtraTL mSelectedExtraTL;
    private ExtraTLControl mExtraTLControl;
    private AudioTLControl mAudioTLControl;
    private AudioTL mSelectedAudioTL, mCurrentAudio;
    private ColorPickerView mColorPicker;
    private FragmentVideosGallery mFragmentVideosGallery;
    private FragmentImagesGallery mFragmentImagesGallery;
    private FragmentAudioGallery mFragmentAudioGallery;
    private CustomHorizontalScrollView mScrollView;
    private RelativeLayout mLayoutTimeMark;

    private int mDragCode = DRAG_VIDEO;
    private int mCountVideo = 0;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;
    private int mFragmentCode;
    private boolean mOpenFileManager;
    public boolean mOpenVideoSubFolder,
            mOpenImageSubFolder, mOpenAudioSubFolder;
    private boolean mRunThread;
    private int mCurrentVideoId, mCurrentPositionMs;
    private int mPreviewStatus;
    private int mMaxTimeLine;
    public int mLeftMarginTimeLine = Constants.MARGIN_LEFT_TIME_LINE;
    private boolean mScroll;
    private boolean mOpenLayoutAdd, mOpenLayoutEditText;
    private boolean mStyleBold, mStyleItalic;
    private boolean mShowColorPicker, mChooseTextColor;
    private int mTimelineCode;
    private int mChangePosition, mSelectedPosition;

    public static final int TIMELINE_VIDEO = 0;
    public static final int TIMELINE_EXTRA = 1;
    public static final int TIMELINE_AUDIO = 2;
    public static final int DRAG_VIDEO = 0;
    public static final int DRAG_EXTRA = 1;
    public static final int DRAG_AUDIO = 2;
    public static final int VIDEO_TAB = 0;
    public static final int IMAGE_TAB = 1;
    public static final int AUDIO_TAB = 2;
    public static final int MSG_CURRENT_POSITION = 0;
    public static final int BEGIN = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int END = 3;
    public static final int UPDATE_STATUS_PERIOD = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoViewLayout = (RelativeLayout) findViewById(R.id.video_view_layout);
        mLayoutVideo = (RelativeLayout) findViewById(R.id.layout_video);
        mTimeLineVideo = (RelativeLayout) findViewById(R.id.timeline_video);
        mScrollView = (CustomHorizontalScrollView) findViewById(R.id.scroll_view);
        mLayoutImage = (RelativeLayout) findViewById(R.id.layout_image);
        mTimeLineImage = (RelativeLayout) findViewById(R.id.timeline_image);
        mLayoutText = (RelativeLayout) findViewById(R.id.layout_text);
        mTimeLineText = (RelativeLayout) findViewById(R.id.timeline_text);
        mLayoutAudio = (RelativeLayout) findViewById(R.id.layout_audio);
        mTimeLineAudio = (RelativeLayout) findViewById(R.id.timeline_audio);
        mLayoutScrollView = (LinearLayout) findViewById(R.id.layout_scrollview);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnAdd = (ImageView) findViewById(R.id.btn_add);
        mBtnUndo = (ImageView) findViewById(R.id.btn_undo);
        mBtnExport = (ImageView) findViewById(R.id.btn_export);
        mBtnPlay = (ImageView) findViewById(R.id.btn_play);
        mFileManager = (LinearLayout) findViewById(R.id.layout_file_manager);
        mVideoTab = (ImageView) findViewById(R.id.video_tab);
        mImageTab = (ImageView) findViewById(R.id.image_tab);
        mAudioTab = (ImageView) findViewById(R.id.audio_tab);
        mVideoTabLayout = (LinearLayout) findViewById(R.id.video_tab_layout);
        mImageTabLayout = (LinearLayout) findViewById(R.id.image_tab_layout);
        mAudioTabLayout = (LinearLayout) findViewById(R.id.audio_tab_layout);
        mVideoView1 = (CustomVideoView) findViewById(R.id.video_view1);
        mVideoView2 = (CustomVideoView) findViewById(R.id.video_view2);
        mLayoutTimeLine = (RelativeLayout) findViewById(R.id.layout_timeline);
        mLimitTimeLineVideo = (FrameLayout) findViewById(R.id.limit_timeline_video);
        mLimitTimeLineImage = (FrameLayout) findViewById(R.id.limit_timeline_image);
        mLimitTimeLineText = (FrameLayout) findViewById(R.id.limit_timeline_text);
        mLimitTimeLineAudio = (FrameLayout) findViewById(R.id.limit_timeline_audio);
        mSeperateLineVideo = (FrameLayout) findViewById(R.id.seperate_line_video);
        mSeperateLineImage = (FrameLayout) findViewById(R.id.seperate_line_image);
        mSeperateLineText = (FrameLayout) findViewById(R.id.seperate_line_text);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mBtnDelete = (ImageView) findViewById(R.id.btn_delete);
        mFolderName = (TextView) findViewById(R.id.text_folder_name);
        mLayoutAdd = (LinearLayout) findViewById(R.id.layout_add);
        mBtnAddMedia = (TextView) findViewById(R.id.btn_add_media);
        mBtnAddText = (TextView) findViewById(R.id.btn_add_text);
        mBtnEditText = (ImageView) findViewById(R.id.btn_edit_text);
        mLayoutEditText = (LinearLayout) findViewById(R.id.layout_edit_text);
        mEditText = (EditText) findViewById(R.id.edittext_input);
        mFontSpinner = (Spinner) findViewById(R.id.font_spinner);
        mBtnBold = (ImageView) findViewById(R.id.btn_bold);
        mBtnItalic = (ImageView) findViewById(R.id.btn_italic);
        mLayoutBtnBold = (LinearLayout) findViewById(R.id.layout_btn_bold);
        mLayoutBtnItalic = (LinearLayout) findViewById(R.id.layout_btn_italic);
        mColorPicker = (ColorPickerView) findViewById(R.id.color_picker);
        mBtnTextColor = (ImageView) findViewById(R.id.btn_text_color);
        mBtnTextBgrColor = (ImageView) findViewById(R.id.btn_text_background_color);
        mEdtColorHex = (EditText) findViewById(R.id.edt_color_hex);
        mLayoutBtnTextColor = (RelativeLayout) findViewById(R.id.layout_btn_text_color);
        mLayoutBtnTextBgrColor = (RelativeLayout) findViewById(R.id.layout_btn_text_bgr_color);
        mLayoutColorPicker = (LinearLayout) findViewById(R.id.layout_color_picker);
        mBtnCloseColorPicker = (Button) findViewById(R.id.btn_close_colorpicker);
        mIndicatorTextColor = (ImageView) findViewById(R.id.indicator_textcolor);
        mIndicatorTextBgr = (ImageView) findViewById(R.id.indicator_textbackground);
        mBtnVolume = (ImageView) findViewById(R.id.btn_volume);
        mLayoutTimeMark = (RelativeLayout) findViewById(R.id.layout_timemark);

        mColorPicker.setAlphaSliderVisible(true);
        mColorPicker.setOnColorChangedListener(this);

        mVideoTabLayout.setOnClickListener(onTabLayoutClick);
        mImageTabLayout.setOnClickListener(onTabLayoutClick);
        mAudioTabLayout.setOnClickListener(onTabLayoutClick);

        mTLShadow = new ImageView(this);
        mTLShadow.setBackgroundResource(R.drawable.shadow);
        mTLShadowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mShadowIndicator = new ImageView(this);
        mShadowIndicator.setBackgroundResource(R.drawable.shadow_indicator);
        mShadowIndicatorParams = new RelativeLayout.LayoutParams(10, mTimeLineVideoHeight);
        mShadowIndicator.setLayoutParams(mShadowIndicatorParams);

        mActivity = this;
        setVideoRatio();
        mVideoList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mTextList = new ArrayList<>();
        mAudioList = new ArrayList<>();
        mListInLayoutImage = new ArrayList<>();
        mListInLayoutText = new ArrayList<>();

        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(galleryPagerAdapter);
        mViewPager.addOnPageChangeListener(onViewPagerChanged);
        mFragmentVideosGallery = new FragmentVideosGallery();
        mFragmentImagesGallery = new FragmentImagesGallery();
        mFragmentAudioGallery = new FragmentAudioGallery();

        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnAdd.setOnClickListener(onBtnAddClick);
        mBtnPlay.setOnClickListener(onBtnPlayClick);
        mBtnExport.setOnClickListener(onBtnExportClick);
        mBtnUndo.setOnClickListener(onBtnUndoClick);
        mBtnDelete.setOnClickListener(onBtnDeleteClick);
        mBtnAddMedia.setOnClickListener(onBtnAddMediaClick);
        mBtnAddText.setOnClickListener(onBtnAddTextClick);
        mBtnEditText.setOnClickListener(onBtnEditTextClick);
        mLayoutBtnBold.setOnClickListener(onLayoutBtnBold);
        mLayoutBtnItalic.setOnClickListener(onLayoutBtnItalic);
        mLayoutBtnTextColor.setOnClickListener(onLayoutBtnTextColorClick);
        mLayoutBtnTextBgrColor.setOnClickListener(onLayoutBtnTextBgrColorClick);
        mBtnCloseColorPicker.setOnClickListener(onBtnCloseColorPickerClick);
        mBtnVolume.setOnClickListener(onBtnVolumeClick);

        mEditText.setOnEditorActionListener(onEditTextActionListener);
        mEdtColorHex.setOnEditorActionListener(onEditColorActionListener);

        mTimeLineImage.setOnDragListener(onExtraDragListener);
        mTimeLineVideo.setOnDragListener(onExtraDragListener);
        mTimeLineText.setOnDragListener(onExtraDragListener);

        mThreadPreviewVideo = new Thread(runnablePreview);
        mCurrentVideoId = -1;
        mMaxTimeLine = 0;
        mPreviewStatus = BEGIN;
        mRunThread = true;
        mThreadPreviewVideo.start();
        mActiveVideoView = mVideoView1;
        mInActiveVideoView = mVideoView2;
        mScrollView.setOnCustomScrollChanged(onCustomScrollChanged);
        mLayoutTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutTimeLineCreated);
        mScroll = true;

        mFontPath = FontManager.getFontPaths();
        mFontName = FontManager.getFontName();
        mFontAdapter = new FontAdapter(this, android.R.layout.simple_spinner_item, mFontName);
        mFontSpinner.setAdapter(mFontAdapter);
        mFontSpinner.setOnItemSelectedListener(onFontSelectedListener);

        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onVolumeChanged(int volume) {
        log("Volume: " + volume);
        if (mTimelineCode == TIMELINE_VIDEO) {
            mSelectedVideoTL.volume = convertVolumeToFloat(volume);
        }

        if (mTimelineCode == TIMELINE_AUDIO) {
            mSelectedAudioTL.volume = convertVolumeToFloat(volume);
        }
    }

    private float convertVolumeToFloat(int volume) {
        int max = 100;
        double numerator = max - volume > 0 ? Math.log(max - volume) : 0;
        float value = (float) (1 - (numerator / Math.log(max)));
        return value;
    }

    private int convertVolumeToInt(float volume) {
        int max = 100;
        double numerator = (1f - volume) * Math.log(max);
        double value = max - Math.exp(numerator);
        if (value > 98) {
            value = 100;
        }
        return (int) Math.round(value);
    }

    View.OnClickListener onBtnVolumeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int volume;
            if (mTimelineCode == TIMELINE_VIDEO) {
                volume = convertVolumeToInt(mSelectedVideoTL.volume);
            } else {
                volume = convertVolumeToInt(mSelectedAudioTL.volume);
            }
            VolumeEditor.newInstance(mActivity, volume).show(getFragmentManager()
                    .beginTransaction(), "volume");
        }
    };

    private void setToolbarVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
        TranslateAnimation animation;
        if (show) {
            animation = new TranslateAnimation(-Utils.dpToPixel(this, 50), 0, 0, 0);
        } else {
            animation = new TranslateAnimation(0, -Utils.dpToPixel(this, 50), 0, 0);
        }
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    View.OnClickListener onBtnCloseColorPickerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showColorPicker(false, true);
        }
    };

    TextView.OnEditorActionListener onEditColorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                int color = convertToIntegerColor(mEdtColorHex.getText().toString());
                mColorPicker.setColor(color);
                setColorForViews(color);
                mEdtColorHex.clearFocus();
                hideStatusBar();
            }
            return false;
        }
    };

    View.OnClickListener onLayoutBtnTextBgrColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mShowColorPicker) {
                if (!mChooseTextColor) {
                    showColorPicker(false, true);
                } else {
                    mChooseTextColor = false;
                    showColorPicker(true, false);
                }
            } else {
                mChooseTextColor = false;
                showColorPicker(true, true);
            }
        }
    };

    View.OnClickListener onLayoutBtnTextColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mShowColorPicker) {
                if (mChooseTextColor) {
                    showColorPicker(false, true);
                } else {
                    mChooseTextColor = true;
                    showColorPicker(true, false);
                }
            } else {
                mChooseTextColor = true;
                showColorPicker(true, true);
            }
        }
    };

    private void showColorPicker(boolean show, boolean animation) {
        mShowColorPicker = show;
        if (show) {
            mLayoutColorPicker.setVisibility(View.VISIBLE);
            int color;
            if (mChooseTextColor) {
                color = mSelectedExtraTL.floatText.mColor;
                mIndicatorTextColor.setVisibility(View.VISIBLE);
                mIndicatorTextBgr.setVisibility(View.INVISIBLE);
            } else {
                color = mSelectedExtraTL.floatText.mBackgroundColor;
                mIndicatorTextBgr.setVisibility(View.VISIBLE);
                mIndicatorTextColor.setVisibility(View.INVISIBLE);
            }
            mColorPicker.setColor(color);
            mEdtColorHex.setText(convertToHexColor(color, false));
        } else {
            mLayoutColorPicker.setVisibility(View.GONE);
            mIndicatorTextBgr.setVisibility(View.INVISIBLE);
            mIndicatorTextColor.setVisibility(View.INVISIBLE);
        }
        if (animation) {
            slideColorPicker(show);
        }
    }

    private void slideColorPicker(boolean show) {
        TranslateAnimation animation;
        if (show) {
            animation = new TranslateAnimation(mScrollView.getWidth() / 2, 0, 0, 0);
        } else {
            animation = new TranslateAnimation(0, mScrollView.getWidth() / 2, 0, 0);
        }
        animation.setDuration(300);
        mLayoutColorPicker.startAnimation(animation);
    }

    @Override
    public void onColorChanged(int color) {
        mEdtColorHex.setText(convertToHexColor(color, false));
        setColorForViews(color);
    }

    private void setColorForViews(int color) {
        FloatText floatText = mSelectedExtraTL.floatText;
        if (mChooseTextColor) {
            floatText.setTextColor(color);
            mBtnTextColor.setBackground(new AlphaColorDrawable(color));
        } else {
            floatText.setTextBgrColor(color);
            mBtnTextBgrColor.setBackground(new AlphaColorDrawable(color));
        }
    }

    private int convertToIntegerColor(String hexColor) {
        return Color.parseColor("#" + hexColor);
    }

    public String convertToHexColor(int color, boolean export) {
        String resultColor = "";
        String s = String.format("%08X", (0xFFFFFFFF & color));
        if (export) {
            resultColor += s.substring(2) + "@0x" + s.substring(0, 2);
        } else {
            resultColor += s;
        }
        return resultColor;
    }

    View.OnClickListener onLayoutBtnBold = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStyleBold) {
                setBoldStyle(false);
            } else {
                setBoldStyle(true);
            }
        }
    };

    private void setBoldStyle(boolean bold) {
        int color = bold ? Color.CYAN : Color.TRANSPARENT;
        mBtnBold.setBackgroundColor(color);
        mStyleBold = bold;
        updateTextStyle();
    }

    private void updateTextStyle() {
        FloatText floatText = mSelectedExtraTL.floatText;
        if (mStyleBold) {
            if (mStyleItalic) {
                floatText.setStyle(Typeface.BOLD_ITALIC);
            } else {
                floatText.setStyle(Typeface.BOLD);
            }
        } else {
            if (mStyleItalic) {
                floatText.setStyle(Typeface.ITALIC);
            } else {
                floatText.setStyle(Typeface.NORMAL);
            }
        }
    }

    private void setItalicStyle(boolean italic) {
        int color = italic ? Color.CYAN : Color.TRANSPARENT;
        mBtnItalic.setBackgroundColor(color);
        mStyleItalic = italic;
        updateTextStyle();
    }

    View.OnClickListener onLayoutBtnItalic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStyleItalic) {
                setItalicStyle(false);
            } else {
                setItalicStyle(true);
            }
        }
    };

    AdapterView.OnItemSelectedListener onFontSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String font = mFontPath.get(position);
            if (mSelectedExtraTL != null) {
                mSelectedExtraTL.floatText.setFont(font);
            }
            mFontAdapter.setSelectedItem(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    TextView.OnEditorActionListener onEditTextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = mEditText.getText().toString();
                mSelectedExtraTL.setText(text);
                mSelectedExtraTL.floatText.setText(text);
                mEditText.clearFocus();
                hideStatusBar();
            }
            return false;
        }
    };

    View.OnClickListener onBtnEditTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOpenLayoutEditText) {
                openEditText(false);
            } else {
                openEditText(true);
            }
        }
    };

    private void openEditText(boolean open) {
        int visible = open ? View.VISIBLE : View.GONE;
        mLayoutEditText.setVisibility(visible);
        slideEditText(open);
        mOpenLayoutEditText = open;
        if (open) {
            mEditText.setText(mSelectedExtraTL.text);
            FloatText floatText = mSelectedExtraTL.floatText;
            mBtnTextColor.setBackground(new AlphaColorDrawable(floatText.mColor));
            mBtnTextBgrColor.setBackground(new AlphaColorDrawable(floatText.mBackgroundColor));
        } else {
            showColorPicker(false, false);
        }
    }

    private void slideEditText(boolean open) {
        TranslateAnimation animation;
        if (open) {
            animation = new TranslateAnimation(0, 0, mScrollView.getHeight(), 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, mScrollView.getHeight());
        }
        animation.setDuration(300);
        mLayoutEditText.startAnimation(animation);
    }

    View.OnClickListener onBtnAddMediaClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFileManager(true);
            openAddLayout(false);

        }
    };

    View.OnClickListener onBtnAddTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addText();
            openAddLayout(false);
        }
    };

    View.OnClickListener onBtnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimelineCode == TIMELINE_VIDEO) {
                deleteMainTimeLine();
            } else if (mTimelineCode == TIMELINE_EXTRA) {
                deleteExtraTimeline();
            } else {
                deleteAudioTimeLine();
            }
        }
    };

    private void deleteAudioTimeLine() {
        mAudioList.remove(mSelectedAudioTL);
        mLayoutAudio.removeView(mSelectedAudioTL);
        invisibleAudioControl();
    }

    private void deleteExtraTimeline() {
        if (mSelectedExtraTL.isImage) {
            mImageList.remove(mSelectedExtraTL);
            mVideoViewLayout.removeView(mSelectedExtraTL.floatImage);
        } else {
            mTextList.remove(mSelectedExtraTL);
            mVideoViewLayout.removeView(mSelectedExtraTL.floatText);
        }
        if (mSelectedExtraTL.inLayoutImage) {
            mLayoutImage.removeView(mSelectedExtraTL);
        } else {
            mLayoutText.removeView(mSelectedExtraTL);
        }
        invisibleExtraControl();
    }

    private void deleteMainTimeLine() {
        mVideoList.remove(mSelectedVideoTL);
        mLayoutVideo.removeView(mSelectedVideoTL);
        invisibleMainControl();
        mCountVideo--;
        mCurrentVideoId--;
        if (mCountVideo > 0) {
            getLeftMargin(mCountVideo - 1);
            mMaxTimeLine = mVideoList.get(mCountVideo - 1).endInTimeLine;
            if (mCurrentVideoId < 0) {
                mCurrentVideoId = 0;
            }
            mSelectedVideoTL = mVideoList.get(mCurrentVideoId);
            mActiveVideoView.setVideoPath(mSelectedVideoTL.videoPath);
            mScrollView.scrollTo(mSelectedVideoTL.startInTimeLine / Constants.SCALE_VALUE, 0);
        } else {
            mMaxTimeLine = 0;
            mActiveVideoView.stopPlayback();
            mActiveVideoView.setVisibility(View.GONE);
            mActiveVideoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void seekTo(int value, boolean scroll) {
        mActiveVideoView.seekTo(value);
        mScroll = scroll;
    }

    ViewTreeObserver.OnGlobalLayoutListener onLayoutTimeLineCreated = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mLeftMarginTimeLine = mLayoutTimeLine.getWidth() / 2 - Utils.dpToPixel(mActivity, 45);
            updateLayoutTimeLine();
            addControler();
            setTimeMark();
            mLayoutTimeLine.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    private void setTimeMark(){
        for (int i=0; i<=40 ; i++){
            if (i%5 == 0){
                BigTimeMark bigTimeMark = new BigTimeMark(this);
                mLayoutTimeMark.addView(bigTimeMark);
                bigTimeMark.getParams().leftMargin = mLeftMarginTimeLine+i*50;
                TimeText timeText = new TimeText(this, i);
                timeText.getParams().leftMargin = mLeftMarginTimeLine+i*50+3;
                timeText.getParams().bottomMargin = 10;
                mLayoutTimeMark.addView(timeText);
            } else {
                SmallTimeMark smallTimeMark = new SmallTimeMark(this);
                mLayoutTimeMark.addView(smallTimeMark);
                smallTimeMark.getParams().leftMargin = mLeftMarginTimeLine+i*50;
            }
        }
    }

    View.OnClickListener onBtnUndoClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            resetVideoView();
        }
    };

    View.OnClickListener onBtnExportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ExportTask exportTask = new ExportTask(mActivity, mVideoList, mImageList, mTextList, mAudioList);
            exportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunThread = false;
        stopPlayAudio();
    }

    Runnable runnablePreview = new Runnable() {
        @Override
        public void run() {

            while (mRunThread) {
                try {
                    Thread.sleep(UPDATE_STATUS_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_CURRENT_POSITION;
                mHandler.sendMessage(msg);
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CURRENT_POSITION:
                    updateCurrentPosition();
                    updatePreviewStatus();
                    updateBtnPlay();
                    updateVideoView();
                    updateImageVisibility();
                    updateTextVisibility();
                    updateMediaPlayer();
                    break;
            }
        }
    };

    private void updateMediaPlayer() {
        if (mActiveVideoView.isPlaying()) {
            startMediaPlayer(true);
        } else {
            startMediaPlayer(false);
        }

        if (mMediaPlayer != null && mCurrentAudio != null) {
            float volume = mCurrentAudio.volume;
            mMediaPlayer.setVolume(volume, volume);
        }
        for (int i = 0; i < mAudioList.size(); i++) {
            AudioTL audio = mAudioList.get(i);

            if (mCurrentPositionMs >= audio.startInTimeline && mCurrentPositionMs <= audio.endInTimeline) {
                if (!audio.equals(mCurrentAudio)) {
                    mCurrentAudio = audio;
                    stopPlayAudio();
                    playAudio(audio.audioPath, audio.startTime);
                    log("change");
                    return;
                } else {
                    int seekTime = mCurrentPositionMs - audio.startInTimeline + audio.startTime;
                    if (mMediaPlayer == null) {
                        playAudio(audio.audioPath, seekTime);
                        log("again");
                    }
                }
                return;
            }
        }
        stopPlayAudio();
    }

    private void updateImageVisibility() {
        for (int i = 0; i < mImageList.size(); i++) {
            ExtraTL image = mImageList.get(i);
            FloatImage floatImage = image.floatImage;
            if (mCurrentPositionMs >= image.startInTimeLine && mCurrentPositionMs <= image.endInTimeLine) {
                floatImage.setVisibility(View.VISIBLE);
            } else {
                floatImage.setVisibility(View.GONE);
            }
        }
    }

    private void updateTextVisibility() {
        for (int i = 0; i < mTextList.size(); i++) {
            ExtraTL text = mTextList.get(i);
            FloatText floatText = text.floatText;
            if (mCurrentPositionMs >= text.startInTimeLine && mCurrentPositionMs <= text.endInTimeLine) {
                floatText.setVisibility(View.VISIBLE);
            } else {
                floatText.setVisibility(View.GONE);
            }
        }
    }

    CustomHorizontalScrollView.OnCustomScrollChanged onCustomScrollChanged = new CustomHorizontalScrollView.OnCustomScrollChanged() {
        @Override
        public void onScrollChanged() {
            if (mCountVideo < 1) {
                return;
            }
            int scrollPosition = mScrollView.getScrollX();
            mCurrentPositionMs = scrollPosition * Constants.SCALE_VALUE;

            VideoTL videoTL = null;
            int timelineId = 0;
            for (int i = 0; i < mVideoList.size(); i++) {
                videoTL = mVideoList.get(i);
                if (mCurrentPositionMs >= videoTL.startInTimeLine && mCurrentPositionMs <= videoTL.endInTimeLine) {
                    timelineId = i;
                    break;
                }
            }
            int positionInVideo = 0;
            if (timelineId > 0) {
                VideoTL previousTimeLine = mVideoList.get(timelineId - 1);
                positionInVideo = mCurrentPositionMs - previousTimeLine.endInTimeLine + videoTL.startTime;
            } else {
                positionInVideo = mCurrentPositionMs + videoTL.startTime;
            }

            if (mCurrentVideoId != timelineId && videoTL != null) {
                mCurrentVideoId = timelineId;
                mActiveVideoView.setVideoPath(videoTL.videoPath);
            }

            mActiveVideoView.seekTo(positionInVideo);
            if (mMediaPlayer != null) {
                int seekTime = mCurrentPositionMs - mCurrentAudio.startInTimeline + mCurrentAudio.startTime;
                mMediaPlayer.seekTo(seekTime);
            }
        }
    };

    private void updateBtnPlay() {
        if (mActiveVideoView.isPlaying()) {
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateCurrentPosition() {
        if (mCurrentPositionMs >= mMaxTimeLine) {
            return;
        }
        if (mCurrentVideoId == -1) {
            mCurrentPositionMs = 0;
        } else {
            mCurrentPositionMs = 0;
            for (int i = 0; i < mCurrentVideoId; i++) {
                mCurrentPositionMs += mVideoList.get(i).width * Constants.SCALE_VALUE;
            }
            VideoTL currentTimeLine = mVideoList.get(mCurrentVideoId);
            int currentVideoView = mActiveVideoView.getCurrentPosition();
            if (currentVideoView < currentTimeLine.startTime) {
                currentVideoView = currentTimeLine.startTime;
            }
            mCurrentPositionMs += currentVideoView - currentTimeLine.startTime + 50;
        }
        if (!mScroll) {
            return;
        }

        int scrollPosition = mCurrentPositionMs / Constants.SCALE_VALUE;
        mScrollView.scrollTo(scrollPosition, 0);
    }

    private void updatePreviewStatus() {
        if (mActiveVideoView.isPlaying()) {
            mPreviewStatus = PLAY;

        } else {
            mPreviewStatus = PAUSE;
        }
        if (mCurrentPositionMs == 0) {
            mPreviewStatus = BEGIN;
        }
        if (mCurrentPositionMs >= mMaxTimeLine) {
            mPreviewStatus = END;
        }
    }

    private void updateVideoView() {
        if (mCurrentPositionMs >= mMaxTimeLine) {
            mActiveVideoView.pause();
            return;
        }
        if (mActiveVideoView != null && mCurrentVideoTL != null) {
            mActiveVideoView.setVolume(mCurrentVideoTL.volume);
        }
        VideoTL videoTL = null;
        int timelineId = mCurrentVideoId;
        for (int i = 0; i < mVideoList.size(); i++) {
            videoTL = mVideoList.get(i);
            if (mCurrentPositionMs >= videoTL.startInTimeLine && mCurrentPositionMs <= videoTL.endInTimeLine) {
                timelineId = i;
                break;
            }
        }

        if (mCurrentVideoId != -1 && mCurrentVideoId < mCountVideo - 1) {
            mCurrentVideoTL = mVideoList.get(mCurrentVideoId);
            VideoTL nextVideoTL = mVideoList.get(mCurrentVideoId + 1);

            if (mCurrentPositionMs >= mCurrentVideoTL.endInTimeLine - 200) {
                mInActiveVideoView.setVideoPath(nextVideoTL.videoPath);
                mInActiveVideoView.seekTo(10);
            }
        }

        if (mCurrentVideoId != timelineId && videoTL != null) {
            if (mCurrentVideoId != mCountVideo - 1) {
                toggleVideoView();
            }
            mCurrentVideoId = timelineId;
            if (mPreviewStatus == BEGIN) {
                mActiveVideoView.setVideoPath(videoTL.videoPath);
            }
            mActiveVideoView.seekTo(videoTL.startTime);
            mActiveVideoView.start();
        }

        if (mCurrentVideoId != -1 && mCountVideo > 0) {
            mCurrentVideoTL = mVideoList.get(mCurrentVideoId);
        }

    }

    private void toggleVideoView() {
        if (mActiveVideoView.equals(mVideoView1)) {
            mActiveVideoView = mVideoView2;
            mInActiveVideoView = mVideoView1;
        } else {
            mActiveVideoView = mVideoView1;
            mInActiveVideoView = mVideoView2;
        }
        mActiveVideoView.setVisibility(View.VISIBLE);
        mInActiveVideoView.setVisibility(View.GONE);
    }

    private void resetVideoView() {
        mCurrentVideoId = -1;
        mCurrentPositionMs = 0;
    }

    View.OnClickListener onBtnPlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mActiveVideoView.isPlaying()) {
                mActiveVideoView.pause();
                startMediaPlayer(false);
                mBtnPlay.setImageResource(R.drawable.ic_play);
            } else {
                playVideo();
                mBtnPlay.setImageResource(R.drawable.ic_pause);
                openFileManager(false);
                startMediaPlayer(true);
            }
        }
    };

    public void startMediaPlayer(boolean start) {
        if (mMediaPlayer == null) {
            return;
        }
        if (start) {
            mMediaPlayer.start();
        } else {

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                log("pause");
            }
        }
    }


    private void playVideo() {
        if (mPreviewStatus == BEGIN || mPreviewStatus == END) {
            resetVideoView();
        }
        if (mPreviewStatus == PAUSE) {
            mActiveVideoView.start();
        }
    }

    public void addVideo(String videoPath) {
        VideoTL videoTL = new VideoTL(this, videoPath, mTimeLineVideoHeight);
        videoTL.setOnClickListener(onMainTimeLineClick);
        videoTL.setOnLongClickListener(onVideoLongClick);
        mVideoList.add(videoTL);
        mLayoutVideo.addView(videoTL);
        mCountVideo++;
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLine = videoTL.endInTimeLine;
        mCurrentPositionMs = videoTL.startInTimeLine;
        mActiveVideoView.setVideoPath(videoTL.videoPath);
        mCurrentVideoId = mCountVideo - 1;
        if (mCountVideo == 1) {
            setToolbarVisible(mBtnPlay, true);
        }
        mTimelineCode = TIMELINE_VIDEO;
    }

    public void addImage(String imagePath) {
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        ExtraTL extraTL = new ExtraTL(this, imagePath, mTimeLineImageHeight, leftMargin, true);

        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        addExtraTLToTL(extraTL, leftMargin);
        mImageList.add(extraTL);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        FloatImage floatImage = new FloatImage(this, bitmap);
        extraTL.floatImage = floatImage;
        floatImage.timeline = extraTL;
        mVideoViewLayout.addView(floatImage);
        setFloatImageVisible(mSelectedExtraTL);
        setFloatTextVisible(null);
        restoreExtraControl(extraTL);
        setExtraControlVisible(true);
        mTimelineCode = TIMELINE_EXTRA;
    }

    private void addExtraTLToTL(ExtraTL extraTL, int position) {
        if (checkAvailablePositionInLayoutImage(position)) {
            addExtraTLToLayoutImage(extraTL);
        } else {
            if (checkAvailablePositionInLayoutText(position)) {
                addExtraTLToLayoutText(extraTL);
            } else {
                addExtraTLToLayoutImage(extraTL);
            }
        }
    }

    private void addExtraTLToLayoutImage(ExtraTL extraTL) {
        mLayoutImage.addView(extraTL);
        int order = getOrderInLayoutImage(extraTL);
        mListInLayoutImage.add(order, extraTL);
        extraTL.inLayoutImage = true;
        reorganizeLayoutImage();
    }

    private void reorganizeLayoutImage() {
        for (int i = 0; i < mListInLayoutImage.size() - 1; i++) {
            ExtraTL currentTimeLine = mListInLayoutImage.get(i);
            ExtraTL nextTimeLine = mListInLayoutImage.get(i + 1);
            if (currentTimeLine.right > nextTimeLine.left) {
                nextTimeLine.moveTimeLine(currentTimeLine.right);
            }
        }
    }


    private int getOrderInLayoutImage(ExtraTL extraTL) {
        int order = 0;
        for (int i = 0; i < mListInLayoutImage.size(); i++) {
            ExtraTL timeline = mListInLayoutImage.get(i);
            if (extraTL.left < timeline.left) {
                break;
            } else {
                order = i + 1;
            }
        }
        return order;
    }

    private void addExtraTLToLayoutText(ExtraTL extraTL) {
        mLayoutText.addView(extraTL);
        int order = getOrderInLayoutText(extraTL);
        mListInLayoutText.add(order, extraTL);
        extraTL.inLayoutImage = false;
        reorganizeLayoutText();
    }

    private void reorganizeLayoutText() {
        for (int i = 0; i < mListInLayoutText.size() - 1; i++) {
            ExtraTL currentTimeLine = mListInLayoutText.get(i);
            ExtraTL nextTimeLine = mListInLayoutText.get(i + 1);
            if (currentTimeLine.right > nextTimeLine.left) {
                nextTimeLine.moveTimeLine(currentTimeLine.right);
            }
        }
    }

    private int getOrderInLayoutText(ExtraTL extraTL) {
        int order = 0;
        for (int i = 0; i < mListInLayoutText.size(); i++) {
            ExtraTL timeline = mListInLayoutText.get(i);
            if (extraTL.left < timeline.left) {
                break;
            } else {
                order = i + 1;
            }
        }
        return order;
    }

    private boolean checkAvailablePositionInLayoutImage(int position) {
        boolean isAvailable = true;
        for (int i = 0; i < mListInLayoutImage.size(); i++) {
            ExtraTL extraTL = mListInLayoutImage.get(i);
            if (position >= extraTL.left && position < extraTL.right) {
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
    }

    private boolean checkAvailablePositionInLayoutText(int position) {
        boolean isAvailable = true;
        for (int i = 0; i < mListInLayoutText.size(); i++) {
            ExtraTL extraTL = mListInLayoutText.get(i);
            if (position >= extraTL.left && position < extraTL.right) {
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
    }

    public void addText() {
        String text = "Lai Trung Tien";
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        ExtraTL extraTL = new ExtraTL(this, text, mTimeLineImageHeight, leftMargin, false);
        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        addExtraTLToTL(extraTL, leftMargin);
        mTextList.add(extraTL);
        FloatText floatText = new FloatText(this, text);
        mVideoViewLayout.addView(floatText);
        extraTL.floatText = floatText;
        floatText.timeline = extraTL;
        restoreExtraControl(extraTL);
        setExtraControlVisible(true);
        setFloatImageVisible(null);
        mSelectedExtraTL = extraTL;
        setFloatTextVisible(extraTL);
        showBtnEditText(true);
        mTimelineCode = TIMELINE_EXTRA;
    }

    public void restoreExtraControl(ExtraTL extraTL) {
        mExtraTLControl.restoreTimeLineStatus(extraTL);
        readdExtraControl();
        mSelectedExtraTL = extraTL;
    }

    private void readdExtraControl() {
        ViewGroup parent = (ViewGroup) mExtraTLControl.getParent();
        if (parent != null) {
            parent.removeView(mExtraTLControl);
        }
        if (mExtraTLControl.inLayoutImage) {
            mTimeLineImage.addView(mExtraTLControl);
        } else {
            mTimeLineText.addView(mExtraTLControl);
        }
    }

    public void addAudio(String audioPath) {
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        AudioTL audioTL = new AudioTL(this, audioPath, mTimeLineImageHeight, leftMargin);
        addAudioTLToTL(audioTL);
        audioTL.setOnClickListener(onAudioTimeLineClick);
        audioTL.setOnLongClickListener(onAudioLongClick);
        mSelectedAudioTL = audioTL;
        mTimelineCode = TIMELINE_AUDIO;
    }

    private void addAudioTLToTL(AudioTL audioTL) {
        mLayoutAudio.addView(audioTL, audioTL.params);
        int order = getOrderInLayoutAudio(audioTL);
        mAudioList.add(order, audioTL);
        reorganizeLayoutAudio();
    }

    private void reorganizeLayoutAudio() {
        for (int i = 0; i < mAudioList.size() - 1; i++) {
            AudioTL currentTimeLine = mAudioList.get(i);
            AudioTL nextTimeLine = mAudioList.get(i + 1);
            if (currentTimeLine.right > nextTimeLine.left) {
                nextTimeLine.moveTimeLine(currentTimeLine.right);
            }
        }
    }

    private int getOrderInLayoutAudio(AudioTL audioTL) {
        int order = 0;
        for (int i = 0; i < mAudioList.size(); i++) {
            AudioTL timeline = mAudioList.get(i);
            if (audioTL.left < timeline.left) {
                break;
            } else {
                order = i + 1;
            }
        }
        return order;
    }

    private void stopPlayAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            log("stop");
        }
    }

    private void playAudio(String audioPath, int startTime) {
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(audioPath));
        mMediaPlayer.seekTo(startTime);
    }

    View.OnClickListener onTabLayoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.equals(mVideoTabLayout)) {
                mViewPager.setCurrentItem(VIDEO_TAB, true);
                setHightLighTab(VIDEO_TAB);
                setFolderName(mFragmentVideosGallery.mFolderName);
            }

            if (view.equals(mImageTabLayout)) {
                mViewPager.setCurrentItem(IMAGE_TAB, true);
                setHightLighTab(IMAGE_TAB);
                setFolderName(mFragmentImagesGallery.mFolderName);
            }

            if (view.equals(mAudioTabLayout)) {
                mViewPager.setCurrentItem(AUDIO_TAB, true);
                setHightLighTab(AUDIO_TAB);
                setFolderName(mFragmentAudioGallery.mFolderName);
            }
        }
    };

    public void setFolderName(String name) {
        mFolderName.setText(name);
    }

    private void openAddLayout(boolean open) {
        int visible = open ? View.VISIBLE : View.GONE;
        mLayoutAdd.setVisibility(visible);
        mOpenLayoutAdd = open;
    }

    View.OnClickListener onBtnAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOpenFileManager) {
                openFileManager(false);

                return;
            }
            if (mOpenLayoutAdd) {
                openAddLayout(false);
            } else {
                openAddLayout(true);
            }
        }
    };

    private void openFileManager(boolean open) {
        if (open) {
            mFileManager.setVisibility(View.VISIBLE);
            mOpenFileManager = true;
            mBtnAdd.setImageResource(R.drawable.ic_close);
        } else {
            mFileManager.setVisibility(View.GONE);
            mOpenFileManager = false;
            mBtnAdd.setImageResource(R.drawable.ic_add_media);
        }
    }

    View.OnClickListener onBtnBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOpenLayoutEditText) {
                openEditText(false);
            }

            switch (mFragmentCode) {
                case VIDEO_TAB:
                    if (mOpenVideoSubFolder) {
                        mFragmentVideosGallery.backToMain();
                        mOpenVideoSubFolder = false;
                        return;
                    }
                    break;
                case IMAGE_TAB:
                    if (mOpenImageSubFolder) {
                        mFragmentImagesGallery.backToMain();
                        mOpenImageSubFolder = false;
                        return;
                    }
                    break;
                case AUDIO_TAB:
                    if (mOpenAudioSubFolder) {
                        mFragmentAudioGallery.backToMain();
                        mOpenAudioSubFolder = false;
                        return;
                    }
                    break;
            }

            if (mOpenFileManager) {
                openFileManager(false);
            }
        }
    };

    ViewPager.OnPageChangeListener onViewPagerChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mFragmentCode = position;
            setHightLighTab(position);
            switch (position) {
                case VIDEO_TAB:

                    setFolderName(mFragmentVideosGallery.mFolderName);
                    break;
                case IMAGE_TAB:

                    setFolderName(mFragmentImagesGallery.mFolderName);
                    break;
                case AUDIO_TAB:

                    setFolderName(mFragmentAudioGallery.mFolderName);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setHightLighTab(int tab) {
        int videoTabId = tab == VIDEO_TAB ? R.drawable.ic_video_tab_blue : R.drawable.ic_video_tab;
        int imageTabId = tab == IMAGE_TAB ? R.drawable.ic_image_tab_blue : R.drawable.ic_image_tab;
        int audioTabId = tab == AUDIO_TAB ? R.drawable.ic_audio_tab_blue : R.drawable.ic_audio_tab;
        mVideoTab.setImageResource(videoTabId);
        mImageTab.setImageResource(imageTabId);
        mAudioTab.setImageResource(audioTabId);
    }

    private class GalleryPagerAdapter extends FragmentPagerAdapter {

        public GalleryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mFragmentVideosGallery;
                case 1:
                    return mFragmentImagesGallery;
                case 2:
                    return mFragmentAudioGallery;
                default:
                    return mFragmentVideosGallery;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    View.OnLongClickListener onVideoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedVideoTL = (VideoTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            mDragCode = DRAG_VIDEO;
            view.startDrag(clipData, shadowBuilder, view, 0);
            addShadowToLayoutVideo();
            mLayoutScrollView.setOnDragListener(onVideoDragListener);
            invisibleAllController();
            return false;
        }
    };

    private void addShadowToLayoutVideo() {
        mTLShadowParams.width = mSelectedVideoTL.width;
        mTLShadowParams.height = mSelectedVideoTL.height;
        ViewGroup parent = (ViewGroup) mTLShadow.getParent();
        if (parent != null) {
            parent.removeView(mTLShadow);
        }
        mLayoutVideo.addView(mTLShadow);
        mShadowIndicatorParams.height = mTimeLineVideoHeight;
        parent = (ViewGroup) mShadowIndicator.getParent();
        if (parent != null) {
            parent.removeView(mShadowIndicator);
        }
        mLayoutVideo.addView(mShadowIndicator);
    }

    View.OnDragListener onVideoDragListener = new View.OnDragListener() {
        int finalMargin;
        int changePosition;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_VIDEO) {
                return false;
            }
            int x = (int) dragEvent.getX();
            if (x > 0) {
                finalMargin = x - 200;
            }
            for (int i = 0; i < mVideoList.size(); i++) {
                VideoTL videoTL = mVideoList.get(i);
                if (x >= videoTL.left && x <= videoTL.right) {
                    mShadowIndicator.setVisibility(View.VISIBLE);
                    mShadowIndicatorParams.leftMargin = videoTL.left;
                    mShadowIndicator.setLayoutParams(mShadowIndicatorParams);
                    changePosition = i;
                    break;
                } else {
                    mShadowIndicator.setVisibility(View.GONE);
                }
            }

            if (finalMargin < mLeftMarginTimeLine) {
                finalMargin = mLeftMarginTimeLine;
            }
            mTLShadowParams.leftMargin = finalMargin;
            mTLShadow.setLayoutParams(mTLShadowParams);

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    VideoTL changeTimeLine = mVideoList.get(changePosition);
                    if (!changeTimeLine.equals(mSelectedVideoTL)) {
                        mSelectedVideoTL.setLeftMargin(changeTimeLine.left);
                        mVideoList.remove(mSelectedVideoTL);
                        mVideoList.add(changePosition, mSelectedVideoTL);
                        getLeftMargin(mCountVideo - 1);
                        mScrollView.scrollTo(mSelectedVideoTL.startInTimeLine / Constants.SCALE_VALUE, 0);
                        mActiveVideoView.setVideoPath(mSelectedVideoTL.videoPath);
                        mCurrentVideoTL = mSelectedVideoTL;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mLayoutVideo.removeView(mTLShadow);
                    mLayoutVideo.removeView(mShadowIndicator);
                    break;
            }
            return true;
        }
    };

    View.OnDragListener onAudioDragListener = new View.OnDragListener() {
        int finalMargin;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_AUDIO) {
                return false;
            }
            int x = (int) dragEvent.getX();
            if (x > 0) {
                finalMargin = x - 200;
            }
            if (finalMargin < mLeftMarginTimeLine) {
                finalMargin = mLeftMarginTimeLine;
            }
            mTLShadowParams.leftMargin = finalMargin;
            mTLShadow.setLayoutParams(mTLShadowParams);

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mSelectedAudioTL.moveTimeLine(finalMargin);
                    int order = 0;
                    for (int i=0; i<mAudioList.size(); i++){
                        AudioTL audioTL = mAudioList.get(i);
                        if (finalMargin<audioTL.right-audioTL.width/2){
                            break;
                        } else {
                            order = i+1;
                        }
                    }
                    mAudioList.remove(mSelectedAudioTL);
                    mAudioList.add(order, mSelectedAudioTL);
                    reorganizeLayoutAudio();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mLayoutAudio.removeView(mTLShadow);
                    break;
            }
            return true;
        }
    };

    View.OnLongClickListener onAudioLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedAudioTL = (AudioTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();

            view.startDrag(clipData, shadowBuilder, view, 0);

            mDragCode = DRAG_AUDIO;
            addShadowToLayoutAudio();
            mLayoutScrollView.setOnDragListener(onAudioDragListener);

            for (int i = 0; i < mAudioList.size(); i++) {
                if (mAudioList.get(i).equals(mSelectedAudioTL)) {
                    mSelectedPosition = i;
                    break;
                }
            }
            invisibleAllController();
            mChangePosition = mSelectedPosition;
            return false;
        }
    };

    private void invisibleAllController(){
        invisibleExtraControl();
        invisibleMainControl();
        invisibleAudioControl();
    }

    private void addShadowToLayoutAudio() {
        mTLShadowParams.width = mSelectedAudioTL.width;
        mTLShadowParams.height = mSelectedAudioTL.height;
        ViewGroup parent = (ViewGroup) mTLShadow.getParent();
        if (parent != null) {
            parent.removeView(mTLShadow);
        }
        mLayoutAudio.addView(mTLShadow);
    }

    View.OnClickListener onAudioTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedAudioTL = (AudioTL) view;
            setAudioControlVisible(true);
            mAudioTLControl.restoreTimeLineStatus(mSelectedAudioTL);
            mTimelineCode = TIMELINE_AUDIO;
            setBtnDeleteVisible(true);
        }
    };

    View.OnLongClickListener onExtraTimelineLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedExtraTL = (ExtraTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();

            view.startDrag(clipData, shadowBuilder, view, 0);
            mTLShadowParams.width = mSelectedExtraTL.width;
            mTLShadowParams.height = mSelectedExtraTL.height;
            mShadowIndicatorParams.height = mSelectedExtraTL.height;
            mDragCode = DRAG_EXTRA;
            invisibleAllController();
            return false;
        }
    };

    private boolean shadowInLayout(View layout) {
        if (mTLShadow.getParent() == null) {
            return false;
        }
        return mTLShadow.getParent().equals(layout);
    }

    View.OnDragListener onExtraDragListener = new View.OnDragListener() {
        boolean inLayoutImage;
        int finalMargin = 0;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_EXTRA) {
                return false;
            }

            int x = (int) dragEvent.getX();

            if (x != 0) {
                finalMargin = x - 200;
                if (finalMargin < mLeftMarginTimeLine) {
                    finalMargin = mLeftMarginTimeLine;
                }
                mTLShadowParams.leftMargin = finalMargin;
                mTLShadow.setLayoutParams(mTLShadowParams);
            }
            if (view.equals(mTimeLineImage) || view.equals(mTimeLineVideo)) {
                inLayoutImage = true;
            }

            if (view.equals(mTimeLineText) || view.equals(mTimeLineAudio)) {
                inLayoutImage = false;
            }

            if (inLayoutImage) {
                if (!shadowInLayout(mTimeLineImage)) {
                    if (shadowInLayout(mTimeLineText)) {
                        mTimeLineText.removeView(mTLShadow);
                    }
                    mTimeLineImage.addView(mTLShadow);
                }
            } else {
                if (!shadowInLayout(mTimeLineText)) {
                    if (shadowInLayout(mTimeLineImage)) {
                        mTimeLineImage.removeView(mTLShadow);
                    }
                    mTimeLineText.addView(mTLShadow);
                }
            }

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mSelectedExtraTL.moveTimeLine(finalMargin);
                    ViewGroup parent = (ViewGroup) mSelectedExtraTL.getParent();

                    if (parent != null) {
                        parent.removeView(mSelectedExtraTL);
                    }
                    if (inLayoutImage) {
                        int order = 0;
                        for (int i=0; i<mListInLayoutImage.size(); i++){
                            ExtraTL extraTL = mListInLayoutImage.get(i);
                            if (finalMargin<extraTL.right-extraTL.width/2){
                                break;
                            } else {
                                order = i+1;
                            }
                        }
                        if (mSelectedExtraTL.inLayoutImage){
                            mListInLayoutImage.remove(mSelectedExtraTL);
                        } else {
                            mListInLayoutText.remove(mSelectedExtraTL);
                        }
                        mListInLayoutImage.add(order, mSelectedExtraTL);
                        mLayoutImage.addView(mSelectedExtraTL);
                        reorganizeLayoutImage();
                        mSelectedExtraTL.inLayoutImage = true;
                    } else {
                        int order = 0;
                        for (int i=0; i<mListInLayoutText.size(); i++){
                            ExtraTL extraTL = mListInLayoutText.get(i);
                            if (finalMargin<extraTL.right-extraTL.width/2){
                                break;
                            } else {
                                order = i+1;
                            }
                        }
                        if (mSelectedExtraTL.inLayoutImage){
                            mListInLayoutImage.remove(mSelectedExtraTL);
                        } else {
                            mListInLayoutText.remove(mSelectedExtraTL);
                        }
                        mListInLayoutText.add(order, mSelectedExtraTL);
                        mLayoutText.addView(mSelectedExtraTL);
                        reorganizeLayoutText();
                        mSelectedExtraTL.inLayoutImage = false;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (shadowInLayout(mTimeLineImage)) {
                        mTimeLineImage.removeView(mTLShadow);
                    }
                    if (shadowInLayout(mTimeLineText)) {
                        mTimeLineText.removeView(mTLShadow);
                    }
                    break;
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {

        if (mOpenLayoutEditText) {
            openEditText(false);
            return;
        }
        super.onBackPressed();
    }

    View.OnClickListener onExtraTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedExtraTL = (ExtraTL) view;
            setExtraControlVisible(true);
            mExtraTLControl.restoreTimeLineStatus(mSelectedExtraTL);
            mScrollView.scrollTo(mSelectedExtraTL.startInTimeLine / Constants.SCALE_VALUE, 0);
            onCustomScrollChanged.onScrollChanged();
            readdExtraControl();
            mTimelineCode = TIMELINE_EXTRA;
            if (mSelectedExtraTL.isImage) {
                setFloatImageVisible(mSelectedExtraTL);
                setFloatTextVisible(null);
            } else {
                setFloatImageVisible(null);
                setFloatTextVisible(mSelectedExtraTL);
                showBtnEditText(true);
            }
            setBtnDeleteVisible(true);
        }
    };

    public void showBtnEditText(boolean show) {
        setToolbarVisible(mBtnEditText, show);
        if (!show && mOpenLayoutEditText) {
            openEditText(false);
        }
    }

    public void setFloatTextVisible(ExtraTL extraTL) {
        for (int i = 0; i < mTextList.size(); i++) {
            FloatText floatText = mTextList.get(i).floatText;
            if (mTextList.get(i).equals(extraTL)) {
                floatText.drawBorder(true);
            } else {
                floatText.drawBorder(false);
            }
        }
    }

    public void setFloatImageVisible(ExtraTL extraTL) {
        for (int i = 0; i < mImageList.size(); i++) {
            FloatImage floatImage = mImageList.get(i).floatImage;
            if (mImageList.get(i).equals(extraTL)) {
                floatImage.drawBorder(true);
            } else {
                floatImage.drawBorder(false);
            }
        }
    }

    public void setExtraControlVisible(boolean visible) {
        if (visible) {
            mExtraTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mVideoTLControl.setVisibility(View.GONE);
            mAudioTLControl.setVisibility(View.GONE);
        } else {
            mExtraTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
            if (mSelectedExtraTL != null) {
                if (mSelectedExtraTL.isImage) {
                    mSelectedExtraTL.floatImage.drawBorder(false);
                } else {
                    mSelectedExtraTL.floatText.drawBorder(false);
                }
            }
        }
    }

    View.OnClickListener onMainTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedVideoTL = (VideoTL) view;
            if (!mSelectedVideoTL.equals(mCurrentVideoTL)) {
                mScrollView.scrollTo(mSelectedVideoTL.startInTimeLine / Constants.SCALE_VALUE, 0);
                onCustomScrollChanged.onScrollChanged();
            }
            setMainControlVisible(true);
            mVideoTLControl.restoreTimeLineStatus(mSelectedVideoTL);
            setBtnExportVisible(false);
            setBtnDeleteVisible(true);
            mTimelineCode = TIMELINE_VIDEO;
        }
    };

    private void setBtnExportVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnExport.setVisibility(visibility);
    }

    private void setBtnDeleteVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnDelete.setVisibility(visibility);
    }

    @Override
    public void updateMainTimeLine(int leftPosition, int width) {
        mSelectedVideoTL.drawTimeLine(leftPosition, width);
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLine = mVideoList.get(mCountVideo - 1).endInTimeLine;
    }

    private void log(String msg) {
        Log.e("Edit video", msg);
    }

    @Override
    public void invisibleMainControl() {
        setMainControlVisible(false);
        mBtnDelete.setVisibility(View.GONE);
        mBtnExport.setVisibility(View.VISIBLE);
    }

    private int getLeftMargin(int position) {
        VideoTL timeLine = mVideoList.get(position);
        int leftMargin;
        if (position == 0) {
            leftMargin = mLeftMarginTimeLine;
        } else {
            VideoTL videoTL = mVideoList.get(position - 1);
            leftMargin = videoTL.width + getLeftMargin(position - 1);
        }
        timeLine.setLeftMargin(leftMargin);
        return leftMargin;
    }

    private void setVideoRatio() {
        ViewGroup.LayoutParams params = mVideoViewLayout.getLayoutParams();
        params.width = (int) (params.height * 1.77);
        mVideoViewLayout.setLayoutParams(params);
    }

    public float getLayoutVideoScale(float realWidth) {
        return realWidth / (float) mVideoViewLayout.getWidth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    public void hideStatusBar() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(setSystemUiVisibility());
    }

    public static int setSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    @Override
    public void updateExtraTimeLine(int left, int right) {
        mSelectedExtraTL.drawTimeLine(left, right);
    }

    @Override
    public void invisibleExtraControl() {
        setExtraControlVisible(false);
        if (mSelectedExtraTL == null){
            return;
        }
        if (!mSelectedExtraTL.isImage) {
            showBtnEditText(false);
        }
    }

    @Override
    public void updateAudioTimeLine(int start, int end) {
        mSelectedAudioTL.seekTimeLine(start, end);
    }

    @Override
    public void invisibleAudioControl() {
        setAudioControlVisible(false);
    }

    private void setMainControlVisible(boolean visible) {
        if (visible) {
            mVideoTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mExtraTLControl.setVisibility(View.GONE);
            mAudioTLControl.setVisibility(View.GONE);
        } else {
            mVideoTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }

    private void setAudioControlVisible(boolean visible) {
        if (visible) {
            mAudioTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mVideoTLControl.setVisibility(View.GONE);
            mExtraTLControl.setVisibility(View.GONE);
        } else {
            mAudioTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }

    private void addControler() {
        mVideoTLControl = new VideoTLControl(this, 500, mTimeLineVideoHeight, mLeftMarginTimeLine);
        mTimeLineVideo.addView(mVideoTLControl, mVideoTLControl.params);
        setMainControlVisible(false);

        mExtraTLControl = new ExtraTLControl(this, mLeftMarginTimeLine, 500, mTimeLineImageHeight);
        mTimeLineImage.addView(mExtraTLControl);
        mExtraTLControl.inLayoutImage = true;
        setExtraControlVisible(false);

        mAudioTLControl = new AudioTLControl(this, mLeftMarginTimeLine, mLeftMarginTimeLine + 500, mTimeLineImageHeight);
        mTimeLineAudio.addView(mAudioTLControl);
        setAudioControlVisible(false);
    }

    private void updateLayoutTimeLine() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLimitTimeLineVideo.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        params = (RelativeLayout.LayoutParams) mLimitTimeLineImage.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        params = (RelativeLayout.LayoutParams) mLimitTimeLineText.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        params = (RelativeLayout.LayoutParams) mLimitTimeLineAudio.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        LinearLayout.LayoutParams seperateParams = (LinearLayout.LayoutParams) mSeperateLineVideo.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
        seperateParams = (LinearLayout.LayoutParams) mSeperateLineImage.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
        seperateParams = (LinearLayout.LayoutParams) mSeperateLineText.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
    }
}
