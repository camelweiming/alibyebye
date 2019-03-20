package com.abb.bye.mapper;

import com.abb.bye.client.domain.TaskQueueDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/19
 */
public interface TaskQueueMapper {
    /**
     * 插入
     *
     * @param taskQueueDO
     */
    void insert(TaskQueueDO taskQueueDO);

    /**
     * 锁
     *
     * @param id
     * @param ip
     * @return
     */
    int lock(@Param("id") long id, @Param("ip") String ip);

    /**
     * 释放
     *
     * @param id
     * @return
     */
    int release(@Param("id") long id);

    /**
     * 标记重试
     *
     * @param id
     * @param startTime
     * @param msg
     * @return
     */
    int makeRetry(@Param("id") long id, @Param("startTime") Date startTime, @Param("msg") String msg);

    /**
     * 标记失败
     *
     * @param id
     * @param msg
     * @return
     */
    int makeFailed(@Param("id") long id, @Param("msg") String msg);

    /**
     * 标记成功
     *
     * @param id
     * @return
     */
    int makeSuccess(@Param("id") long id);

    /**
     * 子节点成功
     *
     * @param parentId
     * @return
     */
    int markChildFinish(@Param("parentId") long parentId);

    /**
     * 取待执行任务
     *
     * @param limit
     * @return
     */
    List<TaskQueueDO> listWaiting(@Param("limit") int limit);

    /**
     * 重置超时任务,机器死掉之后死任务重置
     *
     * @param currentTime
     */
    void forceStop(@Param("currentTime") Date currentTime);

    /**
     * 清理成功任务
     *
     * @param lastModified 上次更新之前的
     */
    void cleanSuccess(@Param("lastModified") Date lastModified);

    /**
     * 查询单个任务
     *
     * @param type
     * @param uniqueKey
     * @return
     */
    TaskQueueDO get(@Param("type") int type, @Param("uniqueKey") String uniqueKey);
}
