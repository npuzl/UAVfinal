package com.dji.uavapp.UI.cloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.dji.uavapp.util.netutil.ServerService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CloudFragment extends Fragment {

    private View root;
    private Button video;
    private Button gallery;
    private SharedPreferences preferences;
    private List<Bitmap> listpath;
    private Photoadpter adapter;
    private GridView gvPhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cloud, container, false);
        //editor = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        video = root.findViewById(R.id.button3);
        gallery = root.findViewById(R.id.button2);
        gvPhoto = (GridView) root.findViewById(R.id.cloud_pic);
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                controller.navigate(R.id.action_cloudFragment_to_navigation_video);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_cloudFragment_to_navigation_gallery);
            }
        });
    }

    private void initData() throws Exception {
        String smallpath = getActivity().getExternalFilesDir(null).getPath() + "/thumb/";
        String username = preferences.getString("username", "default");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerService.fetchAll(username, smallpath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        List<String> imagePaths=new ArrayList<>();
        listpath = new ArrayList<>();
        Thread.sleep(500);
        File folder = new File(getActivity().getExternalFilesDir(null).getPath() + "/thumb/");
        /**将文件夹下所有文件名存入数组*/
        String[] allFiles = folder.list();
        String scanpath;
        /**遍历数组*/
        for (int i = 0; i < allFiles.length; i++) {
            scanpath = folder + "/" + allFiles[i];
            //showToast(scanpath);
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(scanpath);
            if (bitmap != null){
                listpath.add(bitmap);
                imagePaths.add(scanpath);}
        }
        /** 图片写入适配器*/
        adapter = new CloudFragment.Photoadpter(listpath, imagePaths, getActivity());
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

        public Photoadpter(List<Bitmap> list,List<String> paths, Context context) {
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
            CloudFragment.VIewHolder vh;
            if (convertView == null) {
                vh = new CloudFragment.VIewHolder();
                convertView = minflater.inflate(R.layout.picture_item, null);
                vh.iv = (ImageView) convertView.findViewById(R.id.filethumbnail);
                convertView.setTag(vh);
            } else {
                vh = (CloudFragment.VIewHolder) convertView.getTag();
            }
            vh.iv.setImageBitmap(mlist.get(position));
            vh.filePath = mpaths.get(position);
            //fix1

            vh.iv.setOnClickListener(new CloudFragment.itemViewOnClickListener(vh.filePath));
            //fix2
            return convertView;
        }

    }

    public class itemViewOnClickListener implements View.OnClickListener {

        String path;

        public itemViewOnClickListener(String mpath) {
            this.path = mpath;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("是否下载服务器上的图像")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //showToast(preferences.getString("username", "default"));
                                        String username=preferences.getString("username","default");
                                        ServerService.download(username,path);

                                    } catch (Exception e) {
                                        showToast("failed " + e);
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                    }).setNegativeButton("取消", null).create().show();
        }

    }

    class VIewHolder {
        ImageView iv;
        String filePath;
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


}
