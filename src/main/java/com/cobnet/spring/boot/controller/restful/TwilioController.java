package com.cobnet.spring.boot.controller.restful;

import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Message;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//Self
@Hidden
@RestController
public class TwilioController {

    private static final Logger LOG = LoggerFactory.getLogger(TwilioController.class);

    @PostMapping(value="/sms/reply", produces = "application/xml")
    public String smsReply(@RequestParam("From") String from, @RequestParam("Body") String body) {

        LOG.info(from + ": " + body);

        return new MessagingResponse.Builder()
                .message(new Message.Builder(body).build())
                .build().toXml();
    }
}
