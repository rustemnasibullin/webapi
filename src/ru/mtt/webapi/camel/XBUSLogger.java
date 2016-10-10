package ru.mtt.webapi.camel;

import ru.mtt.webapi.utils.XUtils;

import java.util.Date;

/**
 * Proprietary Debug logger for standalone log file
 * 
 * @author rnasibullin@mtt.ru
 */
public class XBUSLogger {


       public void info (Object log) {


        XUtils.ilog("log/xbus.log", new Date().toString()+": "+log+"\n");



       }



}
