package com.dji.uavapp.UI.video;

import android.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dji.uavapp.R;
import com.dji.uavapp.util.netutil.ServerService;
import com.dji.uavapp.util.CaptureFirst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment {
    /**
     * 显示图片的GridView
     */
    private GridView gvPhoto;
    /**
     * 文件夹下所有图片的bitmap
     */
    private List<Bitmap> listpath;
    private List<String> imagePaths;
    /**
     * 文件夹下图片的真实路径
     */
    private String scanpath;
    /**
     * 显示图片的适配器
     */
    private Photodaapter adapter;
    /**
     * 所有图片的名字
     */
    public String[] allFiles;
    /**
     * 想要查找的文件夹
     */
    private File folder;

    Button video;
    private Button cloud_btn;
    SharedPreferences preferences;
    private Button toCloud;
    private VideoView videoView;
    private MediaController mediaController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        video = root.findViewById(R.id.button2);
        gvPhoto = (GridView) root.findViewById(R.id.videos);
        videoView = root.findViewById(R.id.videoView);
        toCloud = root.findViewById(R.id.cloud);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_navigation_video_to_navigation_gallery);
            }
        });
        toCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_navigation_video_to_cloudFragment);
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

    private void initData() throws IOException {
        listpath = new ArrayList<>();
        imagePaths = new ArrayList<>();
        folder = new File(getActivity().getExternalFilesDir(null).getPath());
        /**将文件夹下所有文件名存入数组*/
        allFiles = folder.list();
        /**遍历数组*/
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].endsWith("mov") && (!allFiles[i].startsWith("ThumbN"))) {
                scanpath = folder + "/" + allFiles[i];

                String newPath = folder + "/ThumbN_" + allFiles[i];
                //showToast(scanpath);
                Bitmap bitmap;
                if (!new File(newPath).exists()) {//如果缩略图不存在

                    bitmap = CaptureFirst.getVideoThumb(scanpath);

                    //bitmap = GenThumbnail.getBitmap(newPath,100);
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
        adapter = new Photodaapter(listpath, imagePaths, getActivity());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gvPhoto.setAdapter(adapter);
            }
        });

    }

    public class Photodaapter extends BaseAdapter {
        private List<Bitmap> mlist;
        private Context mcontext;
        private LayoutInflater minflater;
        private List<String> mpaths;

        public Photodaapter(List<Bitmap> list, List<String> paths, Context context) {
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
            vh.iv.setOnClickListener(new itemViewOnClickListener(vh.filePath));
            vh.iv.setOnLongClickListener(new itemViewOnLongClickListener(vh.filePath));
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
//            if (videoView.isPlaying()){
//                videoView.stopPlayback();
//                videoView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            initVideoPlay(path);

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.stopPlayback();
                    videoView.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    ;

    class VIewHolder {
        ImageView iv;
        String filePath;
    }

    private void initVideoPlay(String filePath) {
        mediaController = new MediaController(getActivity());
        Uri uri = getURI(filePath);
        videoView.setVideoURI(uri);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
        videoView.requestFocus();
        videoView.start();
    }

    private Uri getURI(String filePath) {
        File file = new File(filePath);
        return Uri.fromFile(file);

    }
}
