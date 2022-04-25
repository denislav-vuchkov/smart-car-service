package com.smart.garage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="payments")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "visit_id", referencedColumnName = "id")
    private Visit visit;

    @Column(name = "payment_method_id")
    private int paymentMethod;

    @Column(name= "unique_payment_id")
    private String paymentId;

}
