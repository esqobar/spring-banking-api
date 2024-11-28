package com.collins.bank.services.emails;

import com.collins.bank.payloads.EmailDetails;

public interface EmailServiceImpl {
    void sendEmailAlert(EmailDetails emailDetails);
}
