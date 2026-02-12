package com.bank.frauddetection.controller;

import com.bank.frauddetection.model.Transaction;
import com.bank.frauddetection.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @Autowired
    private TransactionService transactionService;

    // Dashboard
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("transactions",
                transactionService.getAll());
        return "dashboard";
    }

    // Create transaction (form submit)
    @PostMapping("/transaction/create")
    public String createTransaction(
            @ModelAttribute Transaction transaction) {

        transactionService.createTransaction(transaction);
        return "redirect:/";
    }

    // ✅ FRAUD REPORT UI
    @GetMapping("/fraud-report")
    public String fraudReport(Model model) {
        model.addAttribute("transactions",
                transactionService.getFraudTransactions());
        return "fraud-report";
    }
}
