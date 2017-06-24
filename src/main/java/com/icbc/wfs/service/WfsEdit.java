package com.icbc.wfs.service;

import com.icbc.dubbo.router.HashRouterFlag;

@HashRouterFlag
public interface WfsEdit {

	boolean[] del(String path);

	boolean[] put(String directory, String fileName);

	boolean[] del(String directory, String fileName);
}
