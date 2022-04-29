package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Content;

public record CommentWrapper<T extends Content<?>>(String comment, T content) implements ApplicationJson {


}
