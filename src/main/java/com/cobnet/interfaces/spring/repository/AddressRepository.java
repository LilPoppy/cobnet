package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Address;
import com.cobnet.spring.boot.entity.support.AddressKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JPABaseRepository<Address, AddressKey> {


}
