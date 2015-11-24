package com.supersoft.comm;

import java.io.Closeable;
import java.io.IOException;

/**
 * User:hacker
 * Date:2015/10/27
 * Time:22:19
 * Description:This class is created to ...
 */
public class Utils {
    /**
     * 关闭多个流
     * @param closeable
     */
    public static void closeStream(Closeable...closeable)	{
        for(Closeable c : closeable)	{
            try {
                if(c != null)	c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
