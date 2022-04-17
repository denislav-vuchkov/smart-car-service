package com.smart.garage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicez {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    String name;

    @Column(name = "price_bgn")
    int priceBGN;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servicez servicez = (Servicez) o;
        return id == servicez.id && priceBGN == servicez.priceBGN && Objects.equals(name, servicez.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, priceBGN);
    }

}
