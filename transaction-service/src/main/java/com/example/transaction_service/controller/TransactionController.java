package com.example.transaction_service.controller;

import com.example.transaction_service.model.TransactionDto;
import com.example.transaction_service.model.TransferRequest;
import com.example.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/Transaction")
public class TransactionController {


    @Autowired
    TransactionService transactionService;

    @PostMapping("/depositOrWithdraw")
    public ResponseEntity<String> deposit(@RequestBody TransactionDto transactionDto){
     return transactionService.depositOrWithdrawService(transactionDto);
    }

    @GetMapping("/transactionHistory/{accNo}")
    public List<TransactionDto> transactionHistoryByAccNo(@PathVariable long accNo){
       return transactionService.transactionHistoryByAccNoService(accNo);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferRequest(@RequestBody TransferRequest transferRequest){
        return transactionService.transferRequestService(transferRequest);
    }
}
