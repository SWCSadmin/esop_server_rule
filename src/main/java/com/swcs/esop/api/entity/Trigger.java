package com.swcs.esop.api.entity;

import lombok.Data;

import java.util.Properties;

@Data
public class Trigger {

    private String name;

    private String cron;

    private String javaBean;

    private boolean enable = true;

    private Properties props = new Properties();

}