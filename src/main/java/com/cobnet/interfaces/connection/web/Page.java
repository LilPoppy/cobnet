package com.cobnet.interfaces.connection.web;

import java.util.Properties;

public interface Page extends ApplicationJson {

    PageField[] getFields();

    Properties getProperties();

}
