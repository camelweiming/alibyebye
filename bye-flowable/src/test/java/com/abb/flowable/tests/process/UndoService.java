package com.abb.flowable.tests.process;

import org.flowable.engine.common.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class UndoService implements JavaDelegate {
    private Expression counterName;

    @Override
    public void execute(DelegateExecution execution) {
        String variableName = (String)counterName.getValue(execution);
        System.out.println("undo........");
        Object variable = execution.getVariable(variableName);
        if (variable == null) {
            execution.setVariable(variableName, 1);
        } else {
            execution.setVariable(variableName, ((Integer)variable) + 1);
        }
    }

}
