package com.abb.bye.spider;

import com.abb.bye.utils.Md5;
import com.abb.bye.utils.SpiderHelper;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author cenpeng.lwm
 * @since 2019/3/14
 */
public class CustomScheduler implements Scheduler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Set<String> dupSet = Collections.synchronizedSet(new HashSet<>());
    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();
    private BlockingQueue<String> splitListQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<String> splitListItemQueue = new LinkedBlockingQueue<>();

    @Override
    public void push(Request request, Task task) {
        String sign = Md5.getInstance().getMD5String(request.getUrl());
        if (dupSet.add(sign) && HttpPost.METHOD_NAME.equalsIgnoreCase(request.getMethod())) {
            logger.debug("dup url:" + request.getUrl());
            return;
        }
        if (SpiderHelper.isSplitPages(request.getUrl())) {
            splitListQueue.add(request.getUrl());
        } else {
            queue.add(request);
        }
    }

    @Override
    public Request poll(Task task) {
        /**
         * 优先处理单页面
         */
        Request request = queue.poll();
        if (request != null) {
            return request;
        }
        /**
         * 处理分页页面
         */
        String url = splitListItemQueue.poll();
        if (url != null) {
            return new Request(url);
        }
        /**
         * 处理分页规则页面
         */
        url = splitListQueue.poll();
        if (url != null) {
            List<String> urls = SpiderHelper.splitPages(url);
            splitListItemQueue.addAll(urls);
            return poll(task);
        }
        return request;
    }

    public static void main(String[] args) {
        CustomScheduler scheduler = new CustomScheduler();
        scheduler.push(new Request("https://movie.douban.com/j/new_search_subjects?sort=R&range=1,10&tags=%E7%94%B5%E5%BD%B1&start=@page-split[199:20->100]"), null);
        scheduler.push(new Request("https://movie.douban.com/aaa.html"), null);
        while (true) {
            Request r = scheduler.poll(null);
            if (r == null) {
                break;
            }
            System.out.println(r);
        }
    }
}
