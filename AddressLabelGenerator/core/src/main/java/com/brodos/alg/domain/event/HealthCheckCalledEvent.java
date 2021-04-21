/*
 * Copyright (C) Brodos AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.brodos.alg.domain.event;

import com.brodos.commons.domain.model.AbstractDomainEvent;

/**
 * This class is to demonstrate a pure internal event that is NOT to be
 * published externally.
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
public class HealthCheckCalledEvent extends AbstractDomainEvent {

    private final boolean success;

    public HealthCheckCalledEvent(boolean success) {
        this.success = success;
    }

    @Override
    public int eventVersion() {
        return 1;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return String.format("HealthCheckCalledEvent{version=%s, occurredOn=%tc, success=%s}",
                eventVersion(), occurredOn(), Boolean.toString(success));
    }

}
