package com.abb.bye.test;

import com.abb.bye.client.domain.ProxyDO;
import com.abb.bye.mapper.ProxyMapper;
import com.abb.bye.test.dao.BaseDAOTest;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
        proxyDO.setHost("http-proxy-t1.dobel.cn:9180");
        proxyDO.setUserName("MRCAMELFCF3LO8P02");
        proxyDO.setPassword("wPfm8o9d");
        proxyDO.setAvgCost(0);
        proxyDO.setSuccessRate(1d);
        proxyDO.setFailedCount(0);
        proxyMapper.insert(proxyDO);
    }

    @Test
    public void doImport31f() throws Exception {
        String content = IOUtils.toString(ProxyDownloadTest.class.getClassLoader().getResourceAsStream("proxy.txt"), "UTF-8");
        String[] lines = StringUtils.split(content, "\r\n");
        for (String s : lines) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            s = s.replace("\t", " ").replaceAll("\\s{1,}", " ").trim();
            String[] array = StringUtils.split(s, " ");
            String host = array[0] + ":" + array[1];
            insert(host);
        }
    }

    private void insert(String host) {
        ProxyDO proxyDO = new ProxyDO();
        proxyDO.setHost(host);
        proxyMapper.insert(proxyDO);
    }

    //public void doImport() {
    //    for (String domain : domains) {
    //        for (int k = 1; k < 50; k++) {
    //            String url = domain + k;
    //            try {
    //                String content = HttpHelper.get(httpCLient, url, new ReqConfig().setHeaders(headers));
    //                Document doc = Jsoup.parse(content);
    //                Elements trs = doc.select("table#ip_list").select("tr");
    //                ProxyDO proxyDO = new ProxyDO();
    //                Map<String, String> attrs = new HashMap<>();
    //                if (trs.size() >= 2) {
    //                    for (int i = 1; i < trs.size(); i++) {
    //                        Elements tds = trs.get(i).select("td");
    //                        String host = tds.get(1).text() + ":" + tds.get(2).text();
    //                        String address = tds.get(3).text();
    //                        String type = tds.get(4).text();
    //                        System.out.println(host);
    //                        proxyDO.setHost(host);
    //                        proxyDO.setAvgCost(0);
    //                        proxyDO.setSuccessRate(0d);
    //                        proxyDO.setFailedCount(-1);
    //                        attrs.clear();
    //                        attrs.put("address", address);
    //                        attrs.put("type", type);
    //                        proxyDO.setAttributes(JSON.toJSONString(attrs));
    //                        proxyMapper.insert(proxyDO);
    //                    }
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //}
}
