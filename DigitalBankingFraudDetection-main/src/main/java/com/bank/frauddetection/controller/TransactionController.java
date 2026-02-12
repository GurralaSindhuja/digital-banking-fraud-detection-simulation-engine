package com.bank.frauddetection.controller;

import com.bank.frauddetection.model.Transaction;
import com.bank.frauddetection.service.TransactionService;
import com.bank.frauddetection.service.TransactionSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionSimulationService simulationService;

    @Autowired
    private TransactionService service;

    // Create single transaction
    @PostMapping
    public Transaction create(@RequestBody Transaction tx) {
        return service.createTransaction(tx);
    }

    // Get all transactions
    @GetMapping
    public List<Transaction> all() {
        return service.getAll();
    }

    // 🔥 Simulate bulk transactions
    @PostMapping("/simulate/{count}")
    public String simulateTransactions(@PathVariable int count) {

        simulationService.generateTransactions(count)
                .forEach(service::createTransaction);

        return count + " transactions simulated successfully";
    }
}
