package com.example.transaction_service.repository;

import com.example.transaction_service.model.TransactionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionDto,Long> {

    @Query(value = "Select * from transaction_dto where account_no =?1", nativeQuery = true)
    public List<TransactionDto> transactionHistoryByAc_No(Long accNo);
}
