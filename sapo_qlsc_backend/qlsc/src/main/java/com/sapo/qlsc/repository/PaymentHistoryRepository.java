package com.sapo.qlsc.repository;

import com.sapo.qlsc.entity.Customer;
import com.sapo.qlsc.entity.PaymentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {

    @Query(value = " SELECT p FROM PaymentHistory p LEFT JOIN MaintenanceCard m ON p.maintenanceCard.id = m.id " +
            "LEFT JOIN Customer c ON m.customer.id = c.id " +
            "WHERE c.id = ?1 AND ( CONCAT( p.createdDate,'') LIKE %?2% OR m.code LIKE %?2% ) " +
            "AND p.paymentMethod.id IN ?3 GROUP BY p.createdDate ")
    Page<PaymentHistory> getPaymentHistoryByIdCustomer(Pageable pageable, Long id, String keyWork, Long[] payMethods);
}
