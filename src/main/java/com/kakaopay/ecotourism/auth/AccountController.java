package com.kakaopay.ecotourism.auth;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    @NonNull private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestParam("username") String username,
                                                      @RequestParam("password") String password) {
        val account = accountService.create(username, password);

        val responseBody = new HashMap<String, Object>();

        responseBody.put("account", account.getFirst());
        responseBody.put("token", account.getSecond());

        return ResponseEntity.ok(responseBody);
    }
}
