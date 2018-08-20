package ru.arsenal.service;

import org.springframework.data.util.Pair;

/**
 * Created by Anton Nesudimov on 04.10.2016.
 */
public interface PayService {

    int getBalance(String contractId);

    Pair<String, String> makePaymentFromMobilePhoneAccount(String MSISDN, int sumInt, String providerId);

    Pair<String, String> makePaymentFromCard(String contractId, int sumInt, String providerId);

    boolean contractNumberExists(String contractNumber);

    boolean hasUserCard(String contractId);

    String getStatusOfPayment(String numberOfPayment);
}
