package com.dji.uavapp.UI.gallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dji.uavapp.R;
import com.dji.uavapp.base.DJIApplication;
import com.dji.uavapp.util.netutil.ServerService;
import com.dji.uavapp.util.GenThumbnail;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJICameraError;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;


public class GalleryFragment extends Fragment {
    //private GalleryViewModel galleryViewModel;
    /**
     * 显示图片的GridView
     */
    private GridView gvPhoto;
    /**
     * 文件夹下所有图片的bitmap
     */
    private List<Bitmap> listpath;
    /**
     * 文件夹下图片的真实路径
     */
    private String scanpath;
    /**
     * 显示图片的适配器
     */
    private Photoadpter adapter;
    /**
     * 所有图片的名字
     */
    public String[] allFiles;
    /**
     * 想要查找的文件夹
     */
    private File folder;
    private List<String> imagePaths;
    private Button video;
    private Button cloud_btn;
    private ImageView photo;
    private Button picture;
    private MediaManager mMediaManager;
    private File destDir;
    private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    private List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    private Thread downLoadThread = null;
    private ProgressDialog mDownloadDialog;
    private int totalFileSize;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    private SharedPreferences preferences;
    private Button toCloud;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        destDir = new File(this.getActivity().getExternalFilesDir(null).getPath());
        //Init Download Dialog
        preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        mDownloadDialog = new ProgressDialog(this.getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
        mDownloadDialog.setTitle("从无人机下载数据");
        mDownloadDialog.setIcon(android.R.drawable.ic_dialog_info);
        mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.setCancelable(true);
        mDownloadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mMediaManager != null) {
                    mMediaManager.exitMediaDownloading();
                }
            }
        });
        downloadFile();
        //galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        video = root.findViewById(R.id.button3);
        toCloud = root.findViewById(R.id.cloud);
        picture = root.findViewById(R.id.button2);
        gvPhoto = (GridView) root.findViewById(R.id.pictures);
        cloud_btn = root.findViewById(R.id.cloud);
        photo = root.findViewById(R.id.photo);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initData();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_navigation_gallery_to_navigation_video);
            }
        });

        toCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_navigation_gallery_to_cloudFragment);
            }
        });
    }

    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() throws IOException, InterruptedException {
        listpath = new ArrayList<>();
        imagePaths = new ArrayList<>();
        Thread.sleep(500);
        folder = new File(getActivity().getExternalFilesDir(null).getPath());
        /**将文件夹下所有文件名存入数组*/
        allFiles = folder.list();
        /**遍历数组*/
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].endsWith("jpg") && (!allFiles[i].startsWith("ThumbN"))) {
                scanpath = folder + "/" + allFiles[i];
                String newPath = folder + "/ThumbN_" + allFiles[i];
                //showToast(scanpath);
                Bitmap bitmap;
                if (!new File(newPath).exists()) {//如果缩略图不存在
                    bitmap = GenThumbnail.getBitmap(scanpath, 1);
                    //bitmap=GenThumbnail.compressImage(BitmapFactory.decodeFile(scanpath));
                } else {
                    bitmap = BitmapFactory.decodeFile(newPath);
                }
                if (bitmap != null) {
                    listpath.add(bitmap);
                    imagePaths.add(scanpath);
                }
            }
        }
        /** 图片写入适配器*/
        adapter = new Photoadpter(listpath, imagePaths, getActivity());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gvPhoto.setAdapter(adapter);
            }
        });

    }

    public class Photoadpter extends BaseAdapter {
        private List<Bitmap> mlist;
        private Context mcontext;
        private LayoutInflater minflater;
        private List<String> mpaths;

        public Photoadpter(List<Bitmap> list, List<String> paths, Context context) {
            super();
            this.mlist = list;
            this.mcontext = context;
            this.minflater = LayoutInflater.from(context);
            this.mpaths = paths;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            VIewHolder vh;
            if (convertView == null) {
                vh = new VIewHolder();
                convertView = minflater.inflate(R.layout.picture_item, null);
                vh.iv = (ImageView) convertView.findViewById(R.id.filethumbnail);
                convertView.setTag(vh);
            } else {
                vh = (VIewHolder) convertView.getTag();
            }
            vh.iv.setImageBitmap(mlist.get(position));
            vh.filePath = mpaths.get(position);
            //fix1

            vh.iv.setOnClickListener(new itemViewOnClickListener(vh.filePath));
            vh.iv.setOnLongClickListener(new itemViewOnLongClickListener(vh.filePath));
            //fix2
            return convertView;
        }

    }

    public class itemViewOnLongClickListener implements View.OnLongClickListener {

        String path;

        public itemViewOnLongClickListener(String mpath) {
            this.path = mpath;
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("是否将图片上传到云端")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //showToast(preferences.getString("username", "default"));
                                        ServerService.updateFile(preferences.getString("username", "default"), path);
                                    } catch (Exception e) {
                                        showToast("failed " + e);
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                    }).setNegativeButton("取消", null).create().show();
            return true;
        }
    }


    public class itemViewOnClickListener implements View.OnClickListener {
        String path;

        public itemViewOnClickListener(String mpath) {
            this.path = mpath;
        }

        @Override
        public void onClick(View v) {


            ImageView imageView = (ImageView) v;
            Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            photo.setImageBitmap(image);
            photo.setVisibility(View.VISIBLE);
            picture.setVisibility(View.INVISIBLE);
            video.setVisibility(View.INVISIBLE);
            gvPhoto.setVisibility(View.INVISIBLE);
            cloud_btn.setVisibility(View.INVISIBLE);

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photo.setVisibility(View.INVISIBLE);
                    picture.setVisibility(View.VISIBLE);
                    video.setVisibility(View.VISIBLE);
                    gvPhoto.setVisibility(View.VISIBLE);
                    cloud_btn.setVisibility(View.VISIBLE);
                }
            });

        }

    };


    class VIewHolder {
        ImageView iv;
        String filePath;
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            downloadFile();
        }
    };

    protected void downloadFile() {
        BaseProduct mProduct = DJIApplication.getProductInstance();
        if (null != mProduct && mProduct.isConnected()) {
            if (downLoadThread == null) {
                downLoadThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initMediaManager();
                        getFileList();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        totalFileSize = mediaFileList.size();
                        if (totalFileSize == 0)
                            return;
                        ShowDownloadProgressDialog();
                        downloadFileByIndex(totalFileSize - 1);

                    }

                    private void initMediaManager() {
                        if (DJIApplication.getProductInstance() == null) {
                            return;
                        } else {
                            if (null != DJIApplication.getCameraInstance() && DJIApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                                mMediaManager = DJIApplication.getCameraInstance().getMediaManager();
                                if (null != mMediaManager) {
                                    DJIApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError error) {
                                            if (error == null) {
                                            } else {
                                                //showToast("Set cameraMode failed");
                                            }
                                        }
                                    });
                                }

                            } else if (null != DJIApplication.getCameraInstance()
                                    && !DJIApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                                //showToast("Media Download Mode not Supported");
                            }
                        }
                        return;
                    }

                    private void getFileList() {
                        mMediaManager = DJIApplication.getCameraInstance().getMediaManager();
                        if (mMediaManager != null) {

                            mMediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    mediaFileList = mMediaManager.getSDCardFileListSnapshot();
                                }
                            });
                            Collections.sort(mediaFileList, new Comparator<MediaFile>() {
                                @Override
                                public int compare(MediaFile lhs, MediaFile rhs) {
                                    if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                        return -1;
                                    } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                        }
                    }

                    private void downloadFileByIndex(int index) {
                        //showToast(index + "/" + total);
                        mDownloadDialog.setProgress((int) (1.0 * (totalFileSize - 1 - index) / totalFileSize * 100));
                        if (index < 0) {
                            deleteFile();
                            return;
                        }
                        if ((mediaFileList.get(index).getMediaType() == MediaFile.MediaType.PANORAMA)
                                || (mediaFileList.get(index).getMediaType() == MediaFile.MediaType.SHALLOW_FOCUS)) {
                            HideDownloadProgressDialog();
                            return;
                        }
                        Date date = new Date();
                        mediaFileList.get(index).fetchFileData(destDir, "DJI_" + sdf.format(date), new DownloadListener<String>() {
                            @Override
                            public void onFailure(DJIError error) {
                                //showToast("Download File Failed" + error.getDescription());
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (Thread.interrupted())
                                    return;
                                HideDownloadProgressDialog();
                                return;
                            }

                            @Override
                            public void onProgress(long total, long current) {
                            }

                            @Override
                            public void onRateUpdate(long total, long current, long persize) {
                            }

                            @Override
                            public void onRealtimeDataUpdate(byte[] bytes, long l, boolean b) {

                            }

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(String filePath) {
                                //showToast("Download File Success" + ":" + filePath);
                                if (Thread.interrupted())
                                    return;
                                downloadFileByIndex(index - 1);
                            }
                        });
                    }

                    private void deleteFile() {
                        HideDownloadProgressDialog();
                        if (mediaFileList.size() == 0) {
                            return;
                        }
                        mediaFileList.add(mediaFileList.get(mediaFileList.size() - 1));
                        mMediaManager.deleteFiles(mediaFileList, new CommonCallbacks.CompletionCallbackWithTwoParam<List<MediaFile>, DJICameraError>() {
                            @Override
                            public void onSuccess(List<MediaFile> x, DJICameraError y) {
                                if (Thread.interrupted())
                                    return;
                            }

                            @Override
                            public void onFailure(DJIError error) {
                                if (Thread.interrupted())
                                    return;
                            }
                        });
                    }
                });
                downLoadThread.start();
            }
        }
    }

    private void ShowDownloadProgressDialog() {
        if (mDownloadDialog != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mDownloadDialog.incrementProgressBy(-mDownloadDialog.getProgress());
                    mDownloadDialog.show();
                }
            });
        }
    }

    private void HideDownloadProgressDialog() {

        if (null != mDownloadDialog && mDownloadDialog.isShowing()) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mDownloadDialog.dismiss();
                }
            });
        }
    }


}
