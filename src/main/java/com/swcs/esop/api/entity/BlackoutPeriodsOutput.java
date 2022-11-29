package com.swcs.esop.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author 阮程
 * @date 2022/11/22
 */
@Data
public class BlackoutPeriodsOutput {

    private Date noticeDate;
    private Date grantProhibitionDate;
    private Date exerciseProhibitionDate;
    private Date blackoutStartDate;
    private Date blackoutEndDate;
    private Date deadlineDate;

}
