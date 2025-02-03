    package com.luanvan.userservice.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Entity
    @Table(name = "wards")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class Ward {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "code_name", nullable = false, length = 50)
        private String codeName;

        @Column(name = "name", nullable = false, length = 50)
        private String name;

        @Column(name = "division_type", nullable = false, length = 50)
        private String divisionType;

        @ManyToOne
        @JoinColumn(name = "district", nullable = false)
        private District district;
    }
