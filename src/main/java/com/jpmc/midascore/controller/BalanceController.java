package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes balance information for users.
 * 
 * Key Spring concepts used here:
 * - @RestController: Marks this as a REST API controller (automatically serializes responses to JSON)
 * - @GetMapping: Maps HTTP GET requests to a specific endpoint
 * - @RequestParam: Extracts query parameters from the URL (e.g., ?userId=123)
 * - @Autowired: Dependency injection - Spring provides the UserRepository instance
 */
@RestController
public class BalanceController {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * GET /balance endpoint that returns a user's balance.
     * 
     * Example request: GET http://localhost:33400/balance?userId=9
     * Example response: {"amount": 3089.45}
     * 
     * If the user doesn't exist, returns a default balance of 0.
     * 
     * @param userId The ID of the user to look up
     * @return A Balance object containing the user's balance amount
     */
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        // Try to find the user by ID
        UserRecord user = userRepository.findById(userId.longValue());
        
        // If user exists, return their balance; otherwise return 0
        if (user != null) {
            return new Balance(user.getBalance());
        } else {
            return new Balance(0.0f);
        }
    }
}
