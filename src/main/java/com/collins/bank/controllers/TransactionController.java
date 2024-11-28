package com.collins.bank.controllers;

import com.collins.bank.entities.Transaction;
import com.collins.bank.services.BankStatement;
import com.collins.bank.utils.UrlMappings;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

import static com.collins.bank.utils.UrlMappings.*;

@RestController
@RequestMapping(TRANSACTION_API_URL)
@RequiredArgsConstructor
public class TransactionController {
    private final BankStatement bankStatement;

    @GetMapping(BANK_STATEMENT)
    public List<Transaction> generateStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}
