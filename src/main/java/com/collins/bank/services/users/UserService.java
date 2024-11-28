package com.collins.bank.services.users;

import com.collins.bank.configs.security.jwts.JwtTokenProvider;
import com.collins.bank.entities.User;
import com.collins.bank.entities.enums.Role;
import com.collins.bank.payloads.AccountInfo;
import com.collins.bank.payloads.EmailDetails;
import com.collins.bank.payloads.TransactionDto;
import com.collins.bank.payloads.requests.*;
import com.collins.bank.payloads.responses.BankResponse;
import com.collins.bank.repositories.UserRepository;
import com.collins.bank.services.emails.EmailService;
import com.collins.bank.services.transactions.TransactionService;
import com.collins.bank.utils.AccountGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static com.collins.bank.utils.BankUtils.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceImpl{
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TransactionService transactionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public BankResponse createAccount(UserRequest request) {

//        create a new Account
        //check if a user has an account already
        if (userRepository.existsByEmail(request.getEmail())) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_EXISTS_CODE)
                    .responseMessage(ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .surname(request.getSurname())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .gender(request.getGender())
                .address(request.getAddress())
                .stateOfOrigin(request.getStateOfOrigin())
                .accountNumber(AccountGenerator.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .alternativePhoneNumber(request.getAlternativePhoneNumber())
                .status("ACTIVE")
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();
        User savedUser = userRepository.save(newUser);

        //sending email alerts
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("BANK ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has Been Successfully Created.\n Your Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getMiddleName() + " " + savedUser.getSurname() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getMiddleName() + " " + savedUser.getSurname())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //checking if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                   .responseCode(ACCOUNT_NOT_FOUND_CODE)
                   .responseMessage(ACCOUNT_NOT_FOUND_MESSAGE)
                   .accountInfo(null)
                   .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(ACCOUNT_FOUND_CODE)
                .responseMessage(ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getMiddleName() + " " + foundUser.getSurname())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        //checking if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return ACCOUNT_NOT_FOUND_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getMiddleName() + " " + foundUser.getSurname();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //saving transaction
        TransactionDto transactionDto = TransactionDto.builder()
               .accountNumber(userToCredit.getAccountNumber())
               .transactionType("CREDIT")
               .amount(request.getAmount())
               .build();
        transactionService.saveTransaction(transactionDto);

        //sending email alerts
        EmailDetails emailDetails = EmailDetails.builder()
               .recipient(userToCredit.getEmail())
               .subject("ACCOUNT CREDITED")
               .messageBody("Congratulations! Your Account Has Been Credited.\n Your New Balance: " + userToCredit.getAccountBalance())
               .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(ACCOUNT_CREDIT_SUCCESS_CODE)
                .responseMessage(ACCOUNT_CREDIT_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getMiddleName() + " " + userToCredit.getSurname())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //checking if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

        //checking if the amount in the account can be credited by a user
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if(availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                   .responseCode(INSUFFICIENT_BALANCE_CODE)
                   .responseMessage(INSUFFICIENT_BALANCE_MESSAGE)
                   .accountInfo(null)
                   .build();
        }  else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            //saving transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                   .responseCode(ACCOUNT_DEBIT_SUCCESS_CODE)
                   .responseMessage(ACCOUNT_DEBIT_SUCCESS_MESSAGE)
                   .accountInfo(AccountInfo.builder()
                           .accountName(userToDebit.getFirstName() + " " + userToDebit.getMiddleName() + " " + userToDebit.getSurname())
                           .accountBalance(userToDebit.getAccountBalance())
                           .accountNumber(request.getAccountNumber())
                           .build())
                   .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit (check if exists)
        //check if the amount i'm debiting is not more than the current balance
        //debit the account
        //get the account to credit (check if exists)
        //credit the account
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                   .responseCode(INSUFFICIENT_BALANCE_CODE)
                   .responseMessage(INSUFFICIENT_BALANCE_MESSAGE)
                   .accountInfo(null)
                   .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getMiddleName() + " " + sourceAccountUser.getSurname();
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been deducted from your account! Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        //saving transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been added/sent to your account from " + sourceUsername.toUpperCase() + ". Your current balance is : " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        //saving transaction
        TransactionDto transactionDto1 = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto1);

        return BankResponse.builder()
                .responseCode(TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }

    @Override
    public BankResponse authenticate(LoginRequest request) {
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're Logged In!")
                .recipient(request.getEmail())
                .messageBody("Your logged into your account. If you did not initiate this request, please contact your bank")
                .build();
        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode(LOGIN_SUCCESS_CODE)
                .responseMessage(LOGIN_SUCCESS_MESSAGE.toUpperCase() + " " + " Bearer: " + jwtTokenProvider.generateToken(authentication))
                .accountInfo(null)
                .build();
    }
    
}
