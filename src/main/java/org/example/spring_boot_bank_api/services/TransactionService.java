package org.example.spring_boot_bank_api.services;

import jakarta.transaction.Transactional;
import org.example.spring_boot_bank_api.models.entities.Account;
import org.example.spring_boot_bank_api.models.entities.Transaction;
import org.example.spring_boot_bank_api.models.dtos.request.transaction.TransactionRequestDTO;
import org.example.spring_boot_bank_api.models.dtos.response.errors.CustomErrorMessage;
import org.example.spring_boot_bank_api.repository.AccountRepository;
import org.example.spring_boot_bank_api.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Transaction deposit(TransactionRequestDTO createTransactionRequestDTO) {

        Account account = accountService.getAccountById(createTransactionRequestDTO.getAccountId());
        Long currentAccountBalance = account.getAccountBalance();
        Long updatedAccountBalance = Long.sum(currentAccountBalance, createTransactionRequestDTO.getAmount());
        log.info("Transaction deposited to account prev_balance {} updated balance {}", currentAccountBalance, updatedAccountBalance);

        accountService.updateAccountBalance(account.getAccountId(), updatedAccountBalance);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(createTransactionRequestDTO.getTransactionType());
        transaction.setAmount(createTransactionRequestDTO.getAmount());
        transaction.setAccount(account);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(TransactionRequestDTO createTransactionRequestDTO) {
        Account account = accountService.getAccountById(createTransactionRequestDTO.getAccountId());

        Long currentAccountBalance = account.getAccountBalance();
        Long updatedAccountBalance = currentAccountBalance - createTransactionRequestDTO.getAmount();

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(createTransactionRequestDTO.getTransactionType());
        transaction.setAmount(createTransactionRequestDTO.getAmount());

        transactionRepository.save(transaction);



        if(createTransactionRequestDTO.getAmount() > currentAccountBalance) {
            throw new CustomErrorMessage("Transaction amount exceeds current account balance");
        }

        accountRepository.updateAccountBalance(account.getAccountId(), updatedAccountBalance);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findTransactionByAccount_AccountId(accountId);
    }
}
