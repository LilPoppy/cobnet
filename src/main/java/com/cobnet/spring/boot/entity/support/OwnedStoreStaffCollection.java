package com.cobnet.spring.boot.entity.support;

import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.interfaces.cache.CacheKeyProvider;
import com.cobnet.interfaces.cache.annotation.SimpleCacheEvict;
import com.cobnet.interfaces.spring.entity.StoreMemberRelated;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.*;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OwnedStoreStaffCollection extends AbstractSetWrapper<Staff> implements CacheKeyProvider<String> {

    private final StoreMemberRelated owner;

    private final Set<Staff> staffs;

    public OwnedStoreStaffCollection(StoreMemberRelated owner, Set<Staff> staffs) {

        this.owner = owner;
        this.staffs = staffs;
    }

    @Override
    protected Set<Staff> getSet() {
        return this.staffs;
    }

    @CacheEvict(cacheNames = "StoreStaffs", key = "#staff.getIdentity()")
    @SimpleCacheEvict(cacheNames = {"Stores", "Users"})
    @Override
    public boolean add(Staff staff) {

        if(ProjectBeanHolder.getStaffRepository() != null) {

            Optional<Staff> existed = ProjectBeanHolder.getStaffRepository().findById(staff.getId());

            if(existed.isEmpty()) {

                ProjectBeanHolder.getStaffRepository().save(staff);

            } else if(existed.get().getLastModfiedTime().before(staff.getLastModfiedTime())) {

                ProjectBeanHolder.getStaffRepository().saveAndFlush(staff);
            }

        }

        return super.add(staff);
    }

    @CacheEvict(cacheNames = "StoreStaffs", key = "#staff.getIdentity()")
    @SimpleCacheEvict(cacheNames = {"Stores", "Users"})
    @Override
    public boolean remove(Object obj) {

        if(obj instanceof Staff staff) {

            if(ProjectBeanHolder.getStaffRepository().existsById(staff.getId())) {

                ProjectBeanHolder.getStaffRepository().delete(staff);
            }
        }

        return super.remove(obj);
    }

    public Optional<Staff> getByUsernameAndStore(String username, Store store) {

        return this.stream().filter(staff -> staff.getUser().getUsername().equalsIgnoreCase(username) && staff.getStore().getId() == store.getId()).findFirst();
    }

    public Optional<Staff> getByUserAndStore(User user, Store store) {

        return this.getByUsernameAndStore(user.getUsername(), store);
    }

    public List<Staff> getByUsername(String username) {

        return this.stream().filter(staff -> staff.getUser().getUsername().equalsIgnoreCase(username)).collect(Collectors.toUnmodifiableList());
    }

    public List<Staff> getByUser(User user) {

        return this.getByUsername(user.getUsername());
    }

    public List<Staff> getByStore(Store store) {

        return this.stream().filter(staff -> staff.getStore().getId() == store.getId()).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String[] getKeys() {
        return new String[] { this.owner.getIdentity() };
    }
}
