package com.javatechie.orderservice.service;

import com.javatechie.orderservice.common.Payment;
import com.javatechie.orderservice.common.TransactionRequest;
import com.javatechie.orderservice.common.TransactionResponse;
import com.javatechie.orderservice.entity.Order;
import com.javatechie.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private RestTemplate template;

    public TransactionResponse saveOrder(TransactionRequest transactionRequest){
        String response = "";
        Order order = transactionRequest.getOrder();
        Payment payment = transactionRequest.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        //rest call
         Payment paymentResponse = template.postForObject("http://PAYMENT-SERVICE/payment/doPayment",payment, Payment.class);
        response =  paymentResponse.getPaymentStatus().equals("success")?"payment processing successful and order placed." : "There is a failure in payment api , order added to cart";
        repository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), response);
    }

}
