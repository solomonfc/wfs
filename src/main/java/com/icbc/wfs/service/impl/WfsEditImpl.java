package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.WfsRouter;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsEdit;

@Service("wfsEditImpl")
public class WfsEditImpl implements WfsEdit {
    private static Logger logger = LoggerFactory.getLogger(WfsEditImpl.class);

    @Resource
    private WfsEdit wfsEdit;

    @Override
    public boolean del(String path) {
        File phyFile = WfsUtil.getPhyFile(path);
        if (phyFile.exists()) {
            return phyFile.delete();
        }
        return true;
    }

    public boolean put0(String path) {
        String directory = WfsUtil.getParent(path);
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, directory);
        String fileName = WfsUtil.getFileName(path);
        return wfsEdit.put(directory, fileName);
    }

    /*
     * 创建虚拟目录
     */
    @Override
    public boolean put(String dir, String fileName) {

        try {

            // 创建虚拟文件夹
            File vFolder = WfsUtil.getPhyFile(dir);
            if (!vFolder.exists()) {

                // 如果虚拟路径不等于根路径，则递归创建上级目录
                if (!WfsUtil.ROOT.equals(dir)) {
                    if (!put0(dir)) {
                        return false;
                    }
                }
                vFolder.mkdirs();
            }

            // 创建假文件，HASH路径+真文件名
            File vFile = new File(WfsUtil.getPhyFilePath(dir) + WfsUtil.PATH_SEPARATOR + fileName);

            // 如果文件不存在，则创建目录
            if (!vFile.exists()) {
                vFile.createNewFile();
            }

		} catch (IOException e) {
			logger.error(WfsEditImpl.class.getName() + "->put", e);
			return false;
		}

        return true;
    }

	@Override
	public boolean del(String directory, String fileName) {
		File dirPhyFile = WfsUtil.getPhyFile(directory);
		if (dirPhyFile.exists() && dirPhyFile.isDirectory()) {
			File targetFile = new File(dirPhyFile.getAbsolutePath() + File.separator + fileName);
			WfsUtil.delete(targetFile);
			return true;
		}
		return false;
	}

}
