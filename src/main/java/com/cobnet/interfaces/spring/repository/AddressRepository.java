package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Address;
import com.cobnet.spring.boot.entity.support.AddressKey;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JPABaseRepository<Address, AddressKey> {

}
