package Util;

import POJO.NewsData;
import POJO.Zhuanti;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jitana on 2016/08/11.
 */
public interface HttpUtils {
    /**
     * @Query在你对url设置参数时,自动帮你拼凑;
     * @param flag
     * @return
     */
    @GET(HttpUrl.http_news)
    Call<NewsData> getNewData(@Query("flag") String flag);


    @GET(HttpUrl.http_zhuanti)
    Call<Zhuanti> getZhuanti();
}
