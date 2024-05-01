package org.example.ralin_bank_app_demo.services;

import lombok.extern.slf4j.Slf4j;
import org.example.ralin_bank_app_demo.models.entities.Account;
import org.example.ralin_bank_app_demo.models.entities.User;
import org.example.ralin_bank_app_demo.models.requestModels.CreateAccountRequestDTO;
import org.example.ralin_bank_app_demo.models.responseModels.CustomErrorMessage;
import org.example.ralin_bank_app_demo.repository.AccountRepository;
import org.example.ralin_bank_app_demo.repository.UserRepository;
import org.example.ralin_bank_app_demo.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountService {
//    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public Account createNewAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        log.info("Creating new account {}", createAccountRequestDTO);

        Account account = new Account();
        account.setAccountNumber(UUIDGenerator.generateUUID());
        account.setAccountType(createAccountRequestDTO.getAccountType());
        account.setAccountBalance(createAccountRequestDTO.getAccountBalance());

        Optional<User> user = userRepository.findUserByUserId(createAccountRequestDTO.getUserId());
        if(user.isEmpty()) {throw new CustomErrorMessage("User not found");}
        account.setUser(user.get());
        return accountRepository.save(account);
    }

    public Account getAccountById(Long accountId) {
        log.info("Service - Retrieving account {}", accountId);
        return accountRepository.findAccountByAccountId(accountId);
    }

    public List<Account> getAccountsForUserByUserId(Long userId){
        log.info("Service - Retrieving accounts for user {}", userId);
        log.debug("Service - Returned Result for user {} is response {}", userId, accountRepository.findAccountsByUser_UserId(userId));
        return accountRepository.findAccountsByUser_UserId(userId);
    }

    public void updateAccountBalance(Long accountId, Long updatedAccountBalance) {
        log.debug("Service - Updating account balance {} for account {}", updatedAccountBalance, accountId);
        accountRepository.updateAccountBalance(accountId, updatedAccountBalance);
    }
}
