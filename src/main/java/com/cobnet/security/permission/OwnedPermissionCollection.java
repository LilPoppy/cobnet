package com.cobnet.security.permission;

import com.cobnet.common.MultiwayTreeNode;
import com.cobnet.common.MultiwayTreeNodeChild;
import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.Permission;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;


public class OwnedPermissionCollection extends AbstractSetWrapper<Permission> {

    private final Permissible permissible;

    private final Set<Permission> permissions;

    public OwnedPermissionCollection(Permissible permissible, Set<Permission> permissions) {

        this.permissible = permissible;
        this.permissions = permissions;
    }

    @Override
    protected Set<Permission> getSet() {
        return permissions;
    }


    public MultiwayTreeNode<String> getTreeNode() {

        MultiwayTreeNode<String> root = new MultiwayTreeNode<String>(this.permissible.getIdentity());

        for(Permission permission : this.permissions) {

            addToParent(root, MultiwayTreeNodeChild.from(permission.getAuthority()));
        }

        return root;

    }

    private void addToParent(MultiwayTreeNode<String> parent, MultiwayTreeNode<String> node) {

        Optional<MultiwayTreeNode<String>> exist = parent.getFirstChildByValue(node.getValue());

        if(exist.isEmpty()) {
            ((MultiwayTreeNodeChild<String>)node).unparent();
            parent.add(node);

        } else {

            for(int i = 0; i < node.size(); i++) {

                MultiwayTreeNodeChild<String> child = (MultiwayTreeNodeChild<String>) node.get(i);
                child.unparent();
                addToParent(exist.get(), child);
            }

        }
    }

    private boolean hasPermission(MultiwayTreeNode<String> root, String permission) {

        String[] authorities = permission.split("\\.");

        if(authorities != null && authorities.length > 0) {

            int size = root.size();

            for(int i = 0; i < size; i++) {

                MultiwayTreeNode<String> node = root.get(i);

                if(node.getValue().equals("*")) {

                    return true;
                }

                if(node.getValue().equals(authorities[0])) {

                    if(node.size() > 0 && authorities.length > 0) {

                        return node.stream().anyMatch(child -> this.hasPermission(node, String.join(".", Arrays.copyOfRange(authorities, 1, authorities.length))));
                    }

                    if(authorities.length <= 1) {

                        return true;
                    }

                }
            }

            return false;
        }

        return false;
    }

    public boolean hasPermission(Permission permission) {

        return this.hasPermission(permission.getAuthority());
    }

    public boolean hasPermission(String permission) {

        return this.hasPermission(getTreeNode(), permission);
    }
}
