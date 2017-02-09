package com.hecorat.azplugin2.filemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 08/11/2016.
 */
public class FragmentAudioGallery extends Fragment {
    public ArrayList<String> mListFolder;
    public ArrayList<String> mListFirstAudio, mListAudio;
    public GridView mGridView;
    public String mStoragePath;
    public AudioGalleryAdapter mFolderAdapter, mAudioAdapter;
    public MainActivity mActivity;
    private View mView;

    public int mCountSubFolder;
    public boolean mIsSubFolder;
    public String mFolderName;
    String[] patterns = {".mp3", ".aac"};

    public static FragmentAudioGallery newInstance(MainActivity activity) {
        FragmentAudioGallery fragmentAudioGallery = new FragmentAudioGallery();
        fragmentAudioGallery.mActivity = activity;
        fragmentAudioGallery.inflateViews();
        return fragmentAudioGallery;
    }

    private void inflateViews() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) mView.findViewById(R.id.video_gallery);
        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder = new ArrayList<>();
        mListFolder.add(mStoragePath);
        listFolderFrom(fileDirectory);
        mListFirstAudio = new ArrayList<>();
        mListAudio = new ArrayList<>();

        new AsyncTaskScanFolder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mIsSubFolder = false;
        mFolderAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstAudio);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = mActivity.getString(R.string.audio_tab_title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    private boolean matchFile(File file) {
        for (String pattern : patterns) {
            if (file.getName().endsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    public void backToMain() {
        mIsSubFolder = false;
        if (mFolderAdapter == null) {
            mFolderAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstAudio);
        }
        mGridView.setAdapter(mFolderAdapter);
        mFolderName = getString(R.string.audio_tab_title);
        mActivity.setFolderName(mFolderName);
        mGridView.setOnItemClickListener(onFolderClickListener);
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mIsSubFolder = true;
            mListAudio.clear();
            mAudioAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListAudio);
            mGridView.setAdapter(mAudioAdapter);
            mGridView.setOnItemClickListener(onAudioClickListener);
            mActivity.mOpenAudioSubFolder = true;
            mActivity.setBtnUpLevelVisible(true);
            mFolderName += " / " + new File(mListFolder.get(i)).getName();
            mActivity.setFolderName(mFolderName);
            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    };

    AdapterView.OnItemClickListener onAudioClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int[] audioCoord = new int[2];
            view.getLocationOnScreen(audioCoord);
            mActivity.addAudio(mListAudio.get(i), audioCoord);
        }
    };

    private class AsyncTaskScanFile extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... value) {
            boolean subFolder = true;
            String folderPath = mListFolder.get(value[0]);
            if (folderPath.equals(mStoragePath)) {
                subFolder = false;
            }
            loadAllAudio(new File(folderPath), mListAudio, subFolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAudioAdapter.notifyDataSetChanged();
        }
    }

    private void loadAllAudio(File fileDirectory, ArrayList<String> listAudio, boolean subFolder) {
        File[] fileList = fileDirectory.listFiles();
        if (fileList == null) {
            return;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                if (subFolder) {
                    loadAllAudio(file, listAudio, true);
                }
            } else {
                if (matchFile(file)) {
                    listAudio.add(file.getAbsolutePath());
                }
            }
        }
    }

    private class AsyncTaskScanFolder extends AsyncTask<Void, Void, Void> {
        long start;

        @Override
        protected void onPreExecute() {
            start = System.currentTimeMillis();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < mListFolder.size(); i++) {
                boolean scanSubFolder = !mListFolder.get(i).equals(mStoragePath);
                mCountSubFolder = 0;
                if (!isAudioFolder(new File(mListFolder.get(i)), scanSubFolder)) {
                    mListFolder.remove(i);
                    i--;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFolderAdapter.notifyDataSetChanged();
        }
    }

    private void listFolderFrom(File fileDirectory) {
        File[] listFile = fileDirectory.listFiles();
        if (listFile == null) {
            return;
        }
        for (File file : listFile) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (name.charAt(0) != '.') {
                    mListFolder.add(file.getAbsolutePath());
                }
            }
        }
    }

    private boolean isAudioFolder(File fileDirectory, boolean includeSubDir) {
        if (mCountSubFolder > 7) {
            return false;
        }
        boolean result = false;
        File[] fileList = fileDirectory.listFiles();
        if (fileList == null) {
            return false;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                if (includeSubDir) {
                    result = isAudioFolder(file, true);
                }
            } else {
                if (matchFile(file)) {
                    mListFirstAudio.add(file.getAbsolutePath());
                    result = true;
                }
            }
            if (result) {
                break;
            }
        }
        mCountSubFolder++;
        return result;
    }

    private class AudioGalleryAdapter extends ArrayAdapter<String> {

        private AudioGalleryAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.folder_gallery_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.iconFolder.setVisibility(View.GONE);
            String name;
            if (mIsSubFolder) {
                name = new File(mListAudio.get(position)).getName();
            } else {
                name = new File(mListFolder.get(position)).getName();
            }
            viewHolder.textView.setText(name);
            int iconId;
            if (name.endsWith(".mp3")) {
                iconId = R.drawable.ic_mp3;
            } else if (name.endsWith(".aac")) {
                iconId = R.drawable.ic_aac;
            } else {
                iconId = R.drawable.ic_audio_folder;
            }
            viewHolder.imageView.setImageResource(iconId);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView, iconFolder;
            TextView textView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private void log(String msg) {
        Log.e("Fragment Video", msg);
    }
}
