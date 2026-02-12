package com.bank.frauddetection.service;

import com.bank.frauddetection.model.Transaction;
import com.bank.frauddetection.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private TransactionRepository transactionRepository;

    public String exportTrainingData() {

        List<Transaction> txs = transactionRepository.findAll();
        String filePath = "fraud-training-data.csv";

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.append("amount,location,riskScore,status\n");

            for (Transaction tx : txs) {
                writer.append(String.valueOf(tx.getAmount())).append(",").append(tx.getLocation()).append(",").append(String.valueOf(tx.getRiskScore())).append(",").append(tx.getStatus()).append("\n");
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV Export Failed");
        }

        return filePath;
    }

    // Fraud summary
    public Map<String, Object> getFraudSummary() {

        List<Transaction> all = transactionRepository.findAll();
        List<Transaction> fraud = transactionRepository.findByStatus("FRAUD");
        List<Transaction> suspicious = transactionRepository.findByStatus("SUSPICIOUS");

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTransactions", all.size());
        summary.put("fraudTransactions", fraud.size());
        summary.put("suspiciousTransactions", suspicious.size());

        double fraudRate = all.isEmpty() ? 0 :
                (fraud.size() * 100.0) / all.size();

        summary.put("fraudRatePercent", fraudRate);

        return summary;
    }

    // Fraud by location
    public Map<String, Integer> getFraudByLocation() {

        List<Transaction> fraudTx =
                transactionRepository.findByStatus("FRAUD");

        Map<String, Integer> map = new HashMap<>();

        for (Transaction tx : fraudTx) {
            map.put(
                    tx.getLocation(),
                    map.getOrDefault(tx.getLocation(), 0) + 1
            );
        }

        return map;
    }
}
