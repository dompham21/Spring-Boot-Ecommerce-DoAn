package com.luv2code.doan.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @NotBlank(message = "Email không được bỏ trống!")
    @Email(message = "Email không hợp lệ!")
    @Column(name="email", unique = true, length = 100, nullable = false)
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống!")
    @Size(min = 8, max = 100, message = "Password phải từ 8 kí tự trở lên!")
    @Column(name="password", nullable = false, length = 100)
    private String password;

    @NotBlank(message = "Họ không được bỏ trống!")
    @Size(min = 1, max = 50, message = "Họ không được dài quá 50 ký tự!")
    @Column(name="first_name", nullable = false, length = 50)
    private String firstName;

    @Size(min = 1, max = 100, message = "Tên không được dài quá 50 ký tự!")
    @NotBlank(message = "Tên không được bỏ trống!")
    @Column(name="last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name="avatar", length = 300)
    private String avatar;


    @Column(name="phone", length = 20)
    private String phone;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd/MM/yyyy")
    @Column(name="created_at", nullable = false)
    private Date registrationDate;

    @Column(name="is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="role_id", referencedColumnName="id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="address_id")
    private Address address;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Collection<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Collection<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Collection<Cart> carts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName(){ return this.firstName + " " + this.lastName;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    public Collection<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Collection<Review> reviews) {
        this.reviews = reviews;
    }

    public Collection<Cart> getCarts() {
        return carts;
    }

    public void setCarts(Collection<Cart> carts) {
        this.carts = carts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                ", roles=" + roles +
                '}';
    }
}
