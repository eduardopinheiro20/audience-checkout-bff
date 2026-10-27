package com.audiencecheckout.bff.infrastructure.client.mock;

import com.audiencecheckout.bff.domain.model.PaymentMethod;
import com.audiencecheckout.bff.domain.port.out.PaymentMethodPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockPaymentMethodAdapter implements PaymentMethodPort {

    @Override
    public List<PaymentMethod> findAvailableMethods() {
        return List.of(
                new PaymentMethod("Credit Card"),
                new PaymentMethod("Debit Card"),
                new PaymentMethod("Pix")
        );
    }
}
