package com.cobnet.interfaces.connection.web;

import com.cobnet.interfaces.connection.Transmission;

import java.io.Serializable;

//TODO define bad status so we can count the rejects from the same session and kick out if it is spam
public interface Content<T> extends Transmission<T>, Serializable {

}
