package com.bank.frauddetection.service;

import com.bank.frauddetection.ml.FraudMLPlugin;
import com.bank.frauddetection.model.FraudLog;
import com.bank.frauddetection.model.FraudStatus;
import com.bank.frauddetection.model.Transaction;
import com.bank.frauddetection.repository.FraudLogRepository;
import com.bank.frauddetection.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudDetectionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FraudLogRepository fraudLogRepository;

    // RULE 1: High Amount
    private static final double HIGH_AMOUNT = 50000;

    // RULE 2: Rapid Transactions
    private static final int RAPID_TX_LIMIT = 3;

    // RULE 3: Time Window (minutes)
    private static final int TIME_WINDOW = 2;

    // RULE 4: Night Transaction
    private static final int NIGHT_START = 0;
    private static final int NIGHT_END = 5;


    @Value("${fraud.ml.enabled:false}")
    private boolean mlEnabled;

    @Autowired(required = false)
    private FraudMLPlugin mlPlugin;

    /**
     * 🔥 MAIN ENTRY
     */
    public int calculateRisk(Transaction tx) {

        int ruleRisk = calculateRuleBasedRisk(tx);
        int mlRisk = 0;

        if (mlEnabled && mlPlugin != null) {
            mlRisk = mlPlugin.predictRisk(tx);
        }

        return Math.min(ruleRisk + mlRisk, 100);
    }

    /**
     * 🔹 Rule Based Anomaly Detection Core
     */
    private int calculateRuleBasedRisk(Transaction tx) {

        int riskScore = 0;

        // 🔴 RULE 1: High Amount Transaction
        if (tx.getAmount() > HIGH_AMOUNT) {
            riskScore += 50;
            log(tx, "HIGH_AMOUNT_TRANSACTION", riskScore);
        }

        // 🔴 RULE 2: Rapid Multiple Transactions
        List<Transaction> recentTransactions =
                transactionRepository.findByAccountNumberAndTransactionTimeAfter(
                        tx.getAccountNumber(),
                        LocalDateTime.now().minusMinutes(TIME_WINDOW)
                );

        if (recentTransactions.size() >= RAPID_TX_LIMIT) {
            riskScore += 30;
            log(tx, "RAPID_MULTIPLE_TRANSACTIONS", riskScore);
        }

        // 🔴 RULE 3: Location Mismatch
        List<Transaction> pastTransactions =
                transactionRepository.findByAccountNumber(tx.getAccountNumber());

        if (!pastTransactions.isEmpty()) {
            String lastLocation =
                    pastTransactions.get(pastTransactions.size() - 1).getLocation();

            if (!lastLocation.equalsIgnoreCase(tx.getLocation())) {
                riskScore += 20;
                log(tx, "LOCATION_MISMATCH", riskScore);
            }
        }

        // 🔴 RULE 4: Night-time Transaction
        int hour = tx.getTransactionTime().getHour();
        if (hour >= NIGHT_START && hour <= NIGHT_END) {
            riskScore += 15;
            log(tx, "NIGHT_TIME_TRANSACTION", riskScore);
        }

        return riskScore;
    }

    /**
     * 🔹 Final Fraud Status
     */
    public FraudStatus detectStatus(int riskScore) {

        if (riskScore >= 75) {
            return FraudStatus.FRAUD;
        } else if (riskScore >= 35) {
            return FraudStatus.SUSPICIOUS;
        } else {
            return FraudStatus.NORMAL;
        }
    }

    /**
     * 🔹 Fraud Logging
     */
    private void log(Transaction tx, String rule, int score) {

        FraudLog log = new FraudLog(
                tx.getId(),
                rule,
                score
        );
        fraudLogRepository.save(log);
    }
}
