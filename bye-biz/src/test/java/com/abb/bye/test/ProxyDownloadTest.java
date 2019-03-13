package com.abb.bye.test;

import com.abb.bye.client.domain.ProxyDO;
import com.abb.bye.mapper.ProxyMapper;
import com.abb.bye.utils.http.HttpHelper;
import com.abb.bye.utils.http.ReqConfig;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public class ProxyDownloadTest extends BaseDAOTest {
    private static Map<String, String> headers = new HashMap<>();

    static {
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 7 Build/MOB30X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
    }

    private static Closeable httpCLient = new SimpleHttpBuilder().build();
    @Resource
    private ProxyMapper proxyMapper;
    //private static String[] domains = new String[] {"https://www.xicidaili.com/nt/"};
    private static String[] domains = new String[] {"https://www.xicidaili.com/nt/", "https://www.xicidaili.com/nn/", "https://www.xicidaili.com/wn/", "https://www.xicidaili.com/wt/"};

    @Test
    public void test() {
        ProxyDO proxyDO = new ProxyDO();
        proxyDO.setHost("221.217.55.235:9000");
        proxyDO.setAvgCost(1);
        proxyDO.setSuccessRate(0d);
        proxyDO.setFailedCount(-1);
        proxyMapper.insert(proxyDO);
    }

    @Test
    public void doImport() {
        for (String domain : domains) {
            for (int k = 1; k < 50; k++) {
                String url = domain + k;
                try {
                    String content = HttpHelper.get(httpCLient, url, new ReqConfig().setHeaders(headers));
                    Document doc = Jsoup.parse(content);
                    Elements trs = doc.select("table#ip_list").select("tr");
                    ProxyDO proxyDO = new ProxyDO();
                    Map<String, String> attrs = new HashMap<>();
                    if (trs.size() >= 2) {
                        for (int i = 1; i < trs.size(); i++) {
                            Elements tds = trs.get(i).select("td");
                            String host = tds.get(1).text() + ":" + tds.get(2).text();
                            String address = tds.get(3).text();
                            String type = tds.get(4).text();
                            System.out.println(host);
                            proxyDO.setHost(host);
                            proxyDO.setAvgCost(0);
                            proxyDO.setSuccessRate(0d);
                            proxyDO.setFailedCount(-1);
                            attrs.clear();
                            attrs.put("address", address);
                            attrs.put("type", type);
                            proxyDO.setAttributes(JSON.toJSONString(attrs));
                            proxyMapper.insert(proxyDO);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
