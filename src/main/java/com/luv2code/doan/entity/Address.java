package com.luv2code.doan.entity;


import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="description")
    private String description;

    @ManyToOne
    @JoinColumn(name="ward_id")
    private Ward ward;

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private Collection<User> users;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }


}
