package com.cobnet.interfaces.event;

public interface Cancellable {

    public boolean isCancelled();

    public void setCancelled(boolean cancel);
}
