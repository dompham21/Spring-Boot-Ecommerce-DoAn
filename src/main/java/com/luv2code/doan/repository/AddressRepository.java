package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address a WHERE a.user.id = :id")
    public List<Address> getAddressByUserId(Integer id);

    @Query("SELECT count(a) FROM Address a WHERE a.user.id = :id")
    long countAddressByUserId(Integer id);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.user.id = ?2")
    public Address findByIdAndUserId(Integer addressId, Integer userId);

    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.user.id = ?2")
    @Modifying
    public void deleteByIdAndUserId(Integer addressId, Integer userId);

    @Query("UPDATE Address a SET a.isDefault = true WHERE a.id = ?1")
    @Modifying
    public void setDefaultAddress(Integer id);

    @Query("UPDATE Address a SET a.isDefault = false "
            + "WHERE a.id <> ?1 AND a.user.id = ?2")
    @Modifying
    public void setNonDefaultForOthers(Integer defaultAddressId, Integer userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = ?1 AND a.isDefault = true")
    public Address findDefaultByUserId(Integer customerId);
}
