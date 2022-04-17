package com.smart.garage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="visit_pictures")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "visit_id")
    private Visit visit;

    @Column(name="photo")
    private String photo;

    @Column(name="token")
    private String token;

    public Photo(Visit visit, String photo, String token) {
        this.visit = visit;
        this.photo = photo;
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo1 = (Photo) o;
        return id == photo1.id && visit.equals(photo1.visit) && photo.equals(photo1.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, visit, photo);
    }
}
