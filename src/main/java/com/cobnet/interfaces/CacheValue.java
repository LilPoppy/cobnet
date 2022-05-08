package com.cobnet.interfaces;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface CacheValue extends Serializable {

    Date creationTime();

    int count();
}
