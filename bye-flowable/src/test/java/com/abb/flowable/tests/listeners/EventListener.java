package com.abb.flowable.tests.listeners;

import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;

/**
 * @author cenpeng.lwm
 * @since 2019/5/29
 */
public class EventListener implements FlowableEventListener {
    @Override
    public void onEvent(FlowableEvent event) {
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
