package com.leaf.plugin.utils;

/**
 * Desc:  <br/>
 * Author: YJG <br/>
 * Email: ye.jg@outlook.com <br/>
 * Date: 2017/3/28 0028 <br/>
 */
public class DownLoadManager {

    private DownLoadManager(){}

    private static class DownLoadManagerInstance{
        static DownLoadManager INSTANCE = new DownLoadManager();
    }

    public static DownLoadManager getInstance(){
        return DownLoadManagerInstance.INSTANCE;
    }

}
