package com.cobnet.spring.boot.core;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageFetcher;
import com.twilio.type.PhoneNumber;

import java.util.Random;

public class Messager {

     Messager() {

        Twilio.init(ProjectBeanHolder.getTwilioConfiguration().getAccountSid(), ProjectBeanHolder.getTwilioConfiguration().getAuthenticationToken());
    }

    public static MessageCreator message(String number, String text) {

         String[] numbers = ProjectBeanHolder.getTwilioConfiguration().getNumbers().toArray(String[]::new);

         return message(numbers[new Random().nextInt(numbers.length)], number, text);
    }

    public static MessageCreator message(String from, String to, String text) {

         return Message.creator(new PhoneNumber(to),  new PhoneNumber(from), text);
    }

    public static MessageFetcher fetch(String uid) {

         return Message.fetcher(uid);
    }

}
