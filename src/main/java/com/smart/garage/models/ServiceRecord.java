package com.smart.garage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "history_of_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;
    @Column(name = "visit_id")
    private int visitID;
    @Column(name = "service_id")
    private int serviceID;
    @Column(name = "service_name")
    String serviceName;
    @Column(name = "service_price_bgn")
    int servicePriceBGN;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRecord that = (ServiceRecord) o;
        return visitID == that.visitID && servicePriceBGN == that.servicePriceBGN && serviceName.equals(that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitID, serviceName, servicePriceBGN);
    }
}
