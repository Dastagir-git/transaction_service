package com.example.transaction_service.service;

import com.example.transaction_service.CustomizeException.AccountNotFoundException;
import com.example.transaction_service.CustomizeException.ServerDownException;
import com.example.transaction_service.kafkaConfig.KafkaProducer;
import com.example.transaction_service.model.AccountDto;
import com.example.transaction_service.model.TransactionCompletedEvent;
import com.example.transaction_service.model.TransactionDto;
import com.example.transaction_service.model.TransferRequest;
import com.example.transaction_service.repository.TransactionRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TransactionService {

    public final static String TRANSACTION_SERVICE = "transactionService";

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    RestTemplate restTemplate;


    @Value("${getObj}")
    private String getObj;

    @Value("${saveObj}")
    private String saveObj;

//    String getObj="http://localhost:9191/Account/getAccount/";
//    String saveObj="http://localhost:9191/Account/saveAccount";

    @Autowired
    TransactionCompletedEvent completedEvent;

    @Autowired
    KafkaProducer kafkaProducer;

    @Transactional
    public ResponseEntity<String> depositOrWithdrawService(TransactionDto transactionDto){

       AccountDto accountDto= restTemplate.getForObject(getObj+transactionDto.getAccountNo(),AccountDto.class);


       if(accountDto != null && transactionDto.getType().equals("DEPOSIT")){             // DEPOSIT, WITHDRAWAL
           accountDto.setBalance(accountDto.getBalance().add(transactionDto.getAmount()));
           restTemplate.postForObject(saveObj,accountDto,String.class);
           transactionRepo.save(transactionDto);

           completedEvent.setTransactionId(transactionDto.getId());
           completedEvent.setAmount(transactionDto.getAmount());
           completedEvent.setAccountNo(transactionDto.getAccountNo());
           completedEvent.setType(transactionDto.getType());

           kafkaProducer.publishEvent(completedEvent);

           kafkaProducer.notification("Amount Deposited with id "+ transactionDto.getId());

           return new ResponseEntity<>("Amount Credited", HttpStatus.OK);
       }
       else if(accountDto != null && transactionDto.getType().equals("WITHDRAW")){
           accountDto.setBalance(accountDto.getBalance().subtract(transactionDto.getAmount()));
           restTemplate.postForObject(saveObj,accountDto,String.class);
           transactionRepo.save(transactionDto);
           completedEvent.setTransactionId(transactionDto.getId());
           completedEvent.setAmount(transactionDto.getAmount());
           completedEvent.setAccountNo(transactionDto.getAccountNo());
           completedEvent.setType(transactionDto.getType());

           kafkaProducer.publishEvent(completedEvent);

           kafkaProducer.notification("Amount Debited with id "+ transactionDto.getId());

           return new ResponseEntity<>("Amount Debited", HttpStatus.OK);
       }

          else {
              kafkaProducer.notification("Please Check amount type that should be (WITHDRAW or DEPOSIT)");
              throw new AccountNotFoundException();
       }

    }

    public List<TransactionDto> transactionHistoryByAccNoService(Long accNo){
       return transactionRepo.transactionHistoryByAc_No(accNo);
    }

    @Transactional
    @CircuitBreaker(name = TRANSACTION_SERVICE, fallbackMethod = "serverDownMethod")
    public ResponseEntity<String> transferRequestService(TransferRequest transferRequest) {

//        try {
            AccountDto fromAccountDto = restTemplate.getForObject(getObj + transferRequest.getFromAccountNo(), AccountDto.class);

            AccountDto toAccountDto = restTemplate.getForObject(getObj + transferRequest.getToAccountNo(), AccountDto.class);

            if (fromAccountDto.getAccountNumber().equals(transferRequest.getFromAccountNo())
                    && toAccountDto.getAccountNumber().equals(transferRequest.getToAccountNo())) {

                fromAccountDto.setBalance(fromAccountDto.getBalance().subtract(transferRequest.getAmount()));
                toAccountDto.setBalance(toAccountDto.getBalance().add(transferRequest.getAmount()));
                restTemplate.postForObject(saveObj, fromAccountDto, String.class);

                restTemplate.postForObject(saveObj, toAccountDto, String.class);

                return ResponseEntity.ok("Amount Transferred");

            }
            else {
                throw new AccountNotFoundException();
            }
//        }
//        catch (Exception e) {
//            throw new AccountNotFoundException();
//        }

    }


    public ResponseEntity<String> serverDownMethod(Exception ex){
        throw new ServerDownException();
    }

}
