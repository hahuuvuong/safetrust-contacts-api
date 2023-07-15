package com.safetrust.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {

    private static final Logger logger = LoggerFactory.getLogger(LogHelper.class);

    public static void logException(Exception ex) {
        logger.error("Exception occurred:", ex);
    }
}
