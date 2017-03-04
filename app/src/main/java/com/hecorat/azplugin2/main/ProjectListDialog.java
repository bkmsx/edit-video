package com.hecorat.azplugin2.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.ProjectObject;
import com.hecorat.azplugin2.database.ProjectTable;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.RecyclerViewItemDivider;

import java.util.ArrayList;

import static com.hecorat.azplugin2.main.Constants.DEFAULT_PROJECT_NAME;

/**
 * Created by macos on 22/02/2017.
 */

public class ProjectListDialog extends DialogFragment {
    private static final String KEY = "current_project_id";

    private Activity mActivity;
    private ArrayList<ProjectObject> mProjectList;
    private ProjectTable mProjectTable;
    private ProjectAdapter mAdapter;
    private Callback mCallback;
    private int mCurrentProjectId;

    public static ProjectListDialog newInstance(int currentProjectId) {
        ProjectListDialog instance = new ProjectListDialog();
        Bundle args = new Bundle();
        args.putInt(KEY, currentProjectId);
        instance.setArguments(args);
        return instance;
    }

    public interface Callback {
        void onOpenProjectClicked(ProjectObject projectObject);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjectList = new ArrayList<>();
        mActivity = getActivity();
        mCallback = (Callback) getActivity();
        mProjectTable = new ProjectTable(mActivity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCurrentProjectId = getArguments().getInt(KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View attachView = mActivity.getLayoutInflater().inflate(R.layout.project_items_list, null);
        RecyclerView recyclerView = (RecyclerView) attachView.findViewById(R.id.items_list);
        recyclerView.addItemDecoration(new RecyclerViewItemDivider(mActivity,
                LinearLayoutManager.VERTICAL));

        mAdapter = new ProjectAdapter(mActivity);
        recyclerView.setAdapter(mAdapter);
        loadRecentProjectsFromDb();

        builder.setTitle(R.string.recent_project_dialog_title)
                .setView(attachView)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    private void loadRecentProjectsFromDb() {
        CursorLoader cursorLoader = new CursorLoader(mActivity) {
            @Override
            public Cursor loadInBackground() {
                return mProjectTable.queryAllRecentProject();
            }

            @Override
            public void deliverResult(Cursor cursor) {
                super.deliverResult(cursor);
                mProjectList = mProjectTable.getRecentProjectsFromCursor(cursor);
                if (mProjectList.size() > 0) mAdapter.notifyDataSetChanged();
            }
        };
        cursorLoader.startLoading();
    }

    private final class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

        private Activity mActivity;

        ProjectAdapter(Activity activity) {
            mActivity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.project_item2, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProjectAdapter.ViewHolder holder, final int position) {
            final ProjectObject project = mProjectList.get(position);
            final String projectName = project.name;
            final String projectFirstVideo = project.firstVideo;

            if (!TextUtils.isEmpty(projectFirstVideo)) {
                holder.mLoadThumbProgress.setVisibility(View.VISIBLE);
                Glide.with(mActivity).load(projectFirstVideo)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFirstResource) {
                                holder.mLoadThumbProgress.setVisibility(View.GONE);
                                holder.mImgThumb.setImageResource(R.drawable.ic_default_video_icon);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                holder.mLoadThumbProgress.setVisibility(View.GONE);
                                holder.mImgPlayFake.setVisibility(View.VISIBLE);
                                return false;
                            }
                        }).into(holder.mImgThumb);
            }

            holder.mTvTitle.setText(projectName != null ? projectName : DEFAULT_PROJECT_NAME);

            holder.mImgPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mActivity, holder.mImgPopup);
                    popup.getMenuInflater().inflate(R.menu.recent_project_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_open:
                                    mCallback.onOpenProjectClicked(project);
                                    break;
                                case R.id.action_rename:
                                    onRenameProjectClicked(project);
                                    break;
                                case R.id.action_delete:
                                    onDeleteProjectClicked(project);
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });

            holder.mRootView.setBackgroundResource(project.id == mCurrentProjectId ?
                    R.drawable.bg_current_item : R.drawable.btn_touch_feedback_rec_nobackground);

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onOpenProjectClicked(project);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mProjectList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mImgThumb;
            TextView mTvTitle;
            ImageView mImgPopup;
            LinearLayout mRootView;
            ImageView mImgPlayFake;
            ProgressBar mLoadThumbProgress;

            ViewHolder(View view) {
                super(view);
                mImgThumb = (ImageView) view.findViewById(R.id.img_thumbnail);
                mTvTitle = (TextView) view.findViewById(R.id.tv_title);
                mImgPopup = (ImageView) view.findViewById(R.id.img_popup_menu);
                mImgPlayFake = (ImageView) view.findViewById(R.id.img_play_fake);
                mLoadThumbProgress = (ProgressBar) view.findViewById(R.id.load_thumbnail_progress);
                mRootView = (LinearLayout) view.findViewById(R.id.root_view);
            }
        }

        private void onRenameProjectClicked(final ProjectObject projectObject) {
            final String oldName = projectObject.name;
            final int projectId = projectObject.id;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            final EditText editText = new EditText(mActivity);
            editText.setText(oldName);
            if (!TextUtils.isEmpty(oldName)) editText.setSelection(oldName.length());
            builder.setView(editText)
                    .setTitle(R.string.dialog_title_rename)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String newName = editText.getText().toString();
                                    if (newName.equals(oldName)) return;
                                    mProjectTable.updateValue(projectId, ProjectTable.PROJECT_NAME, newName);
                                    projectObject.name = newName;
                                    notifyItemChanged(mProjectList.indexOf(projectObject));
                                    AnalyticsHelper.getInstance().send(mActivity,
                                            Constants.CATEGORY_PROJECT,
                                            Constants.ACTION_RENAME_PROJECT);
                                }
                            });
            final AlertDialog renameDialog = builder.create();
            renameDialog.show();
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Button button = renameDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                    button.setEnabled(!TextUtils.isEmpty(s.toString()));
                }
            });
        }

        private void onDeleteProjectClicked(final ProjectObject projectObject) {
            final int projectId = projectObject.id;

            if (projectId == mCurrentProjectId) {
                Toast.makeText(mActivity, getString(R.string.toast_delete_current_project)
                        , Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.dialog_title_rename)
                    .setMessage(R.string.dialog_msg_delete_project)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mProjectTable.deleteProject(projectObject.id);
                                    notifyItemRemoved(mProjectList.indexOf(projectObject));
                                    mProjectList.remove(projectObject);
//                                    notifyDataSetChanged();
                                    AnalyticsHelper.getInstance().send(mActivity,
                                            Constants.CATEGORY_PROJECT,
                                            Constants.ACTION_DELETE_PROJECT);
                                }
                            })
                    .show();
        }
    }

    private void log(String msg) {
        Log.e("ProjectListDialog", msg);
    }
}