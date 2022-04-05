package com.cobnet.spring.boot.entity.support;

import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OwnedStoreStaffCollection extends AbstractSetWrapper<StoreStaff> {

    private final Set<StoreStaff> staffs;

    public OwnedStoreStaffCollection(Set<StoreStaff> staffs) {

        this.staffs = staffs;
    }

    @Override
    protected Set<StoreStaff> getSet() {
        return this.staffs;
    }

    @Override
    public boolean add(StoreStaff staff) {

        if(ProjectBeanHolder.getStoreStaffRepository() != null) {

            Optional<StoreStaff> existed = ProjectBeanHolder.getStoreStaffRepository().findById(staff.getId());

            if(existed.isEmpty()) {

                ProjectBeanHolder.getStoreStaffRepository().save(staff);

            } else if(existed.get().getLastModfiedTime().before(staff.getLastModfiedTime())) {

                ProjectBeanHolder.getStoreStaffRepository().saveAndFlush(staff);
            }

        }

        return super.add(staff);
    }

    public Optional<StoreStaff> getByUsernameAndStore(String username, Store store) {

        return this.stream().filter(staff -> staff.getUser().getUsername().equalsIgnoreCase(username) && staff.getStore().getId() == store.getId()).findFirst();
    }

    public Optional<StoreStaff> getByUserAndStore(User user, Store store) {

        return this.getByUsernameAndStore(user.getUsername(), store);
    }

    public List<StoreStaff> getByUsername(String username) {

        return this.stream().filter(staff -> staff.getUser().getUsername().equalsIgnoreCase(username)).collect(Collectors.toUnmodifiableList());
    }

    public List<StoreStaff> getByUser(User user) {

        return this.getByUsername(user.getUsername());
    }

    public List<StoreStaff> getByStore(Store store) {

        return this.stream().filter(staff -> staff.getStore().getId() == store.getId()).collect(Collectors.toUnmodifiableList());
    }
}
