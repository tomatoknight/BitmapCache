package com.qf.bitmapcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.mtp.MtpConstants;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Jitana on 2016/08/11.
 */
public class MyAdapter extends BaseAdapter {
    final static String TAG = "print";
    List<String> data;
    int count;
    Context mContext;
    Map<String, Bitmap> map;


    //过滤容器
    LruCache<String, Bitmap>  lruCache;

    Map<String, SoftReference<Bitmap>> softReferenceMap = new HashMap<>();
    public MyAdapter(List<String> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        map = new HashMap<>();

        //参数为最大max值
        lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 4)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回该张图片的大小
                return value.getWidth() * value.getRowBytes();
            }
        };
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_imagecache, null);

        }
        TextView textView = (TextView) convertView.findViewById(R.id.tv1);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.iv1);
        textView.setText(data.get(position));
        final String url = getUrl(position);
        imageView.setTag(url);




        Observable<Bitmap> lru = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bm = lruCache.get(url);
                if (bm != null) {
                    subscriber.onNext(bm);

                } else {
                    subscriber.onCompleted();
                }
            }
        });

        final Observable<Bitmap> soft =  Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if (softReferenceMap.get(url) != null) {
                    Bitmap bm = softReferenceMap.get(url).get();
                    subscriber.onNext(bm);

                } else {
                    subscriber.onCompleted();
                }
            }
        });


        Observable<Bitmap> diskCache = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                File  file =  new File(mContext.getFilesDir(),StringUtils.getMD5Str32byte(url));
                Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                if (bm != null) {
                    lruCache.put(url,bm);
                    subscriber.onNext(bm);
                } else {
                    subscriber.onCompleted();
                }
            }
        });



        if (lruCache.get(url) != null) {
            imageView.setImageBitmap(lruCache.get(url));
        } else {
            if (softReferenceMap.get(url) != null) {
                imageView.setImageBitmap(softReferenceMap.get(url).get());

            } else {
                File  file =  new File(mContext.getFilesDir(),StringUtils.getMD5Str32byte(url));
                Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                if (bm != null) {
                    imageView.setImageBitmap(bm);
                    lruCache.put(url, bm);

                } else {
                    new Mytask(imageView, url).execute();
                }
            }
            //获得本地缓存图片

        }
        return convertView;
    }

    private class Mytask extends AsyncTask<String, String, Bitmap> {
        ImageView imageView;
        String urlStr;

        public Mytask(ImageView imageView, String urlStr) {
            this.imageView = imageView;
            this.urlStr = urlStr;
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            FileOutputStream os = null;
            File file = new File(mContext.getFilesDir(), StringUtils.getMD5Str32byte(urlStr));
            try {
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                map.put(urlStr, bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (urlStr.equals(imageView.getTag())) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }

    public String getUrl(int index) {
        return "http://10.36.137.42:8080/androidoo/q" + (index % 10) + ".jpg";
    }
}
