package com.audiencecheckout.bff.domain.port.out;

import com.audiencecheckout.bff.domain.model.PaymentMethod;

import java.util.List;

public interface PaymentMethodPort {

    List<PaymentMethod> findAvailableMethods();
}
