package com.collins.bank.services.users;

import com.collins.bank.payloads.requests.*;
import com.collins.bank.payloads.responses.BankResponse;

public interface UserServiceImpl {
    BankResponse createAccount(UserRequest request);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);

    BankResponse authenticate(LoginRequest request);
}
