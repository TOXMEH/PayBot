package ru.arsenal.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.arsenal.service.BotService;
import ru.arsenal.service.PayService;

/**
 * Created by Anton Nesudimov on 04.10.2016.
 */
@Service
public class ArsenalPayService implements PayService {

    @Autowired
    BotService botService;

    @Override
    public int getBalance(String contractId) {
        int balance = 0;

        return balance;
    }

    /**
     * @return number Of Payment, status of payment
     */
    @Override
    public Pair<String, String> makePaymentFromMobilePhoneAccount(String MSISDN, int sumInt, String providerId) {
        return Pair.of("123", "in progress");
    }

    @Override
    public Pair<String, String> makePaymentFromCard(String contractId, int sumInt, String providerId) {
        return Pair.of("980", "processing");
    }

    @Override
    public boolean contractNumberExists(String contractNumber) {
        return true;
    }

    @Override
    public boolean hasUserCard(String contractId) {
        return true;
    }

    @Override
    public String getStatusOfPayment(String numberOfPayment) {
        return "passed";
    }
}
