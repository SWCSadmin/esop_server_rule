package com.swcs.esop.api.util;

import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.tax.SASTaxRateInput;
import com.swcs.esop.api.entity.tax.SOSTaxRateInput;
import com.swcs.esop.api.entity.tax.TaxRate;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.enums.TaxRateUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/10/31
 */
@Component
@ConfigurationProperties(prefix = "tax")
@Data
public class TaxRateUtil {

    private TaxRateUnit unit;
    private TaxRate[] taxRate;
    private TaxRate[] nonTaxRate;

}
