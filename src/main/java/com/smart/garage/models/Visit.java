package com.smart.garage.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private VisitStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "visit_id")
    private Set<ServiceRecord> services;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "visit_id")
    private Set<Photo> photos;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public String getFormattedStartDate() {
        return startDate == null ? "-" : startDate.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public String getFormattedEndDate() {
        return endDate == null ? "-" : endDate.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public String displayEndDate() {
        return endDate == null ? "-" : endDate.toString();
    }

    public int getTotalCost() {
        return services.stream().map(ServiceRecord::getServicePriceBGN).reduce(0, Integer::sum);
    }

    public Set<ServiceRecord> displayServices() {
        return services.stream()
                .sorted(Comparator.comparing(ServiceRecord::getServiceName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return id == visit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
