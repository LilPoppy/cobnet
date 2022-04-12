package com.cobnet.spring.boot.core;

import com.cobnet.spring.boot.configuration.TwilioConfiguration;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageFetcher;
import com.twilio.type.PhoneNumber;

import java.util.Random;

public class Messager {

     Messager(TwilioConfiguration config) {

        Twilio.init(config.getAccountSid(), config.getAuthenticationToken());

    }

    public MessageCreator message(String number, String text) {

         String[] numbers = ProjectBeanHolder.getTwilioConfiguration().getNumbers().toArray(String[]::new);

         return message(numbers[new Random().nextInt(numbers.length)], number, text);
    }

    public MessageCreator message(String from, String to, String text) {

         return Message.creator(new PhoneNumber(to),  "MG5d1ed7c5ae5635c5a9f23daa488000e1", text);
    }

    public MessageFetcher fetch(String uid) {

         return Message.fetcher(uid);
    }

}
