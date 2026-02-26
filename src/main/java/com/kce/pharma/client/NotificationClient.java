package com.kce.pharma.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification", url = "${notification.service.url}")
public interface NotificationClient {

    @PostMapping("/email/send")
    void sendEmail(@RequestParam String to,
                   @RequestParam String subject,
                   @RequestParam String body);
}