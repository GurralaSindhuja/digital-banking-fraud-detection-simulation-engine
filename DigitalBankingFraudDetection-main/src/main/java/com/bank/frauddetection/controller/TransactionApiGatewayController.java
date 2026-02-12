package com.bank.frauddetection.controller;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.model.Transaction;
import com.bank.frauddetection.service.TransactionService;
import com.bank.frauddetection.service.TransactionSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateway")
public class TransactionApiGatewayController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionSimulationService simulationService;

    /**
     * 🔹 External Transaction Ingestion API
     * POST /api/gateway/transaction
     */
    @PostMapping("/transaction")
    public Transaction ingestTransaction(
            @RequestBody TransactionRequestDTO dto) {

        Transaction tx = new Transaction();
        tx.setAccountNumber(dto.getAccountNumber());
        tx.setAmount(dto.getAmount());
        tx.setLocation(dto.getLocation());

        return transactionService.createTransaction(tx);
    }

    /**
     * 🔹 Get All Transactions
     * GET /api/gateway/transactions
     */
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAll();
    }

    /**
     * 🔹 Get Fraud Transactions Only
     * GET /api/gateway/transactions/fraud
     */
    @GetMapping("/transactions/fraud")
    public List<Transaction> getFraudTransactions() {
        return transactionService.getFraudTransactions();
    }

    /**
     * 🔹 Simulate Transactions
     * POST /api/gateway/simulate/{count}
     */
    @PostMapping("/simulate/{count}")
    public String simulate(@PathVariable int count) {

        simulationService.generateTransactions(count)
                .forEach(transactionService::createTransaction);

        return "Gateway simulated " + count + " transactions";
    }
}
