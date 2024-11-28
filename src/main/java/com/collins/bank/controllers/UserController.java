package com.collins.bank.controllers;

import com.collins.bank.payloads.requests.*;
import com.collins.bank.payloads.responses.BankResponse;
import com.collins.bank.services.users.UserService;
import com.collins.bank.utils.UrlMappings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.collins.bank.utils.UrlMappings.*;

@RestController
@RequestMapping(USER_API_URL)
@RequiredArgsConstructor
@Tag(name = "User Account Management APIs")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "This API creates a new user with the provided details and assigning of an account."
    )
    @ApiResponse(
            responseCode = "201",
            description = "User account created successfully"
    )
    @PostMapping(CREATE_USER)
    public BankResponse createAccount(@RequestBody UserRequest request) {
        return userService.createAccount(request);
    }

    @Operation(
            summary = "Account Balance Enquiry",
            description = "This API retrieves the current balance of the user's account."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account balance enquiry successful"
    )
    @GetMapping(BALANCE)
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @Operation(
            summary = "User Name Enquiry",
            description = "This API retrieves the name of the user associated with the provided account."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User name enquiry successful"
    )
    @GetMapping(NAME)
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @Operation(
            summary = "Credit User Account",
            description = "This API adds funds to the user's account."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Funds credited successfully"
    )
    @PostMapping(CREDIT)
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @Operation(
            summary = "Debit User Account",
            description = "This API removes funds from the user's account."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Funds deducted successfully"
    )
    @PostMapping(DEBIT)
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        return userService.debitAccount(request);
    }


    @Operation(
            summary = "Transfer Money To Different Account",
            description = "This API transfers funds from one user's account to another."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Funds transferred successfully"
    )
    @PostMapping(TRANSFER)
    public BankResponse transfer(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }

    @PostMapping(LOGIN_USER)
    public BankResponse authenticate(@RequestBody LoginRequest request) {
        return userService.authenticate(request);
    }

}
