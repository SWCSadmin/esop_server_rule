package com.swcs.esop.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author 阮程
 * @date 2022/12/7
 */
@Data
public class KycOutput {

    private Integer reminder;
    private Date nextKycReviewDate;

}
