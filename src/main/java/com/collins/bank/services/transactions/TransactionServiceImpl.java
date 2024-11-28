package com.collins.bank.services.transactions;

import com.collins.bank.payloads.TransactionDto;

public interface TransactionServiceImpl {
    void saveTransaction(TransactionDto transactionDto);
}
