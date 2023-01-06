package com.swcs.esop.api.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 阮程
 * @date 2022/12/28
 */
public class StringUtil {

    public static String replaceVar(String source, String var, String value) {
        if (StringUtils.isNotBlank(value)) {
            return source.replace("{{" + var + "}}", value);
        }
        return source;
    }

}
