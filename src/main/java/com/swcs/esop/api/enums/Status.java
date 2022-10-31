/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swcs.esop.api.enums;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Optional;

public enum Status {

    SUCCESS(0, "success", "成功", "成功"),

    INTERNAL_SERVER_ERROR_ARGS(500, "Internal Server Error: {0}", "服务端异常: {0}", "服務端異常:{0}"),

    NOTIFY_ERROR(1000, "Notification Service Error", "通知服务异常", "通知服務異常"),
    ;


    private final int code;
    private final String enMsg;
    private final String zhCNMsg;
    private final String zhHKMsg;

    Status(int code, String enMsg, String zhCNMsg, String zhHKMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhCNMsg = zhCNMsg;
        this.zhHKMsg = zhHKMsg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        String local = LocaleContextHolder.getLocale().getLanguage();
        if (Locale.US.getLanguage().equals(local)) {
            return this.enMsg;
        } else if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(local)) {
            return this.zhCNMsg;
        } else {
            return this.zhHKMsg;
        }
    }

    /**
     * Retrieve Status enum entity by status code.
     *
     * @param code
     * @return
     */
    public static Optional<Status> findStatusBy(int code) {
        for (Status status : Status.values()) {
            if (code == status.getCode()) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
