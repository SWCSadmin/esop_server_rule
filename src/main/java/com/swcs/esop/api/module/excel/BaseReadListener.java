package com.swcs.esop.api.module.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.module.excel.annotion.ExcelCheckField;
import com.swcs.esop.api.module.excel.annotion.ExcelDateFormat;
import com.swcs.esop.api.module.excel.annotion.ExcelNumberFormat;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.NodeServiceUtil;
import com.swcs.esop.api.util.RefUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 批量新增
 *
 * @author 阮程
 * @date 2022/11/1
 */
public abstract class BaseReadListener<T extends ExcelUploadEntity> implements ReadListener<T> {

    protected MessageSource messageSource;

    protected List<T> cacheList = new ArrayList<>();
    protected List<T> insertList = new ArrayList<>();
    protected List<T> updateList = new ArrayList<>();

    protected Map<String, Boolean> errorCachedDataMap = new HashMap<>();
    protected List<String> primaryKeyList = new ArrayList<>();

    /**
     * 是否覆盖
     */
    protected boolean upsert;
    protected int errorNum = 0;

    protected List<Field> checkFields = new ArrayList<>();
    protected List<Field> formatFields = new ArrayList<>();
    protected List<Field> numberFormatFields = new ArrayList<>();

    {
        messageSource = AppUtils.getBean(MessageSource.class);
        for (Field field : getGenericClassT().getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelCheckField.class)) {
                checkFields.add(field);
            }
            if (field.isAnnotationPresent(ExcelDateFormat.class)) {
                formatFields.add(field);
            }
            if (field.isAnnotationPresent(ExcelNumberFormat.class)) {
                numberFormatFields.add(field);
            }
        }

    }

    public BaseReadListener(boolean upsert) {
        this.upsert = upsert;
    }

    public List<T> getCacheList() {
        return cacheList;
    }

    public boolean isSuccess() {
        return errorNum == 0;
    }

    @Override
    public void invoke(T o, AnalysisContext analysisContext) {
        List<String> errorList = new ArrayList<>();
        fieldCheck(o, errorList);

        beforeInvoke(o);

        if (!o.primaryKeyIsBlank()) {
            // Excel 内记录重复校验
            String primaryKey = o.getPrimaryKey();
            if (primaryKeyList.contains(primaryKey)) {
                errorList.add(getMessage("DUPLICATE_RECORD_EXIST"));
            } else {
                primaryKeyList.add(primaryKey);
            }
            // 数据库中记录重复校验
            if (!upsert) {
                List<T> list = NodeServiceUtil.getEntity(o);
                for (T item : list) {
                    if (item.getPrimaryKey().equals(primaryKey)) {
                        errorList.add(getMessage("RECORD_EXIST"));
                        break;
                    }
                }
            }
        }

        dataValid(o, errorList);

        if (!errorList.isEmpty()) {
            // 设置错误信息
            o.setError(StringUtils.join(errorList, " | "));
            errorNum++;
        } else {
            // 没有错误, 将数据添加到对应集合
            if (!upsert) {
                insertList.add(o);
            } else {
                if (NodeServiceUtil.getEntity(o).isEmpty()) {
                    insertList.add(o);
                } else {
                    updateList.add(o);
                }
            }
        }
        cacheList.add(o);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!insertList.isEmpty()) {
            List<T> list = NodeServiceUtil.batchAddEntity(insertList);
            if (!list.isEmpty()) {
                for (T t : list) {
                    errorNum++;
                    errorCachedDataMap.put(t.getPrimaryKey(), true);
                }
            }
            // 添加错误信息
            for (T t : insertList) {
                if (errorCachedDataMap.containsKey(t.getPrimaryKey())) {
                    t.setError(getMessage("RECORD_ADD_ERROR") + ": " + t.getStatus());
                } else {
                    t.setError(getMessage("SUCCESS"));
                }
            }
        }
        if (!updateList.isEmpty()) {
            try {
                NodeServiceUtil.batchUpdateEntity(updateList);
                for (T t : updateList) {
                    t.setError(getMessage("SUCCESS"));
                }
            } catch (Exception e) {
                for (T t : updateList) {
                    t.setError(e.getMessage());
                }
            }
        }
    }

    /**
     * 数据校验, 子类可以实现自己的特殊校验
     */
    protected void dataValid(T o, List<String> errorList) {
    }

    /**
     * 字段校验
     *
     * @param o
     * @param errorList
     */
    protected void fieldCheck(T o, List<String> errorList) {
        for (Field filed : checkFields) {
            Object value = RefUtil.getFieldValue(o, filed);
            ExcelCheckEnum[] enums = filed.getAnnotation(ExcelCheckField.class).value();
            String re = filed.getAnnotation(ExcelCheckField.class).re();
            for (ExcelCheckEnum excelCheckEnum : enums) {
                switch (excelCheckEnum) {
                    case NotEmpty:
                        if (value == null || StringUtils.isBlank(value.toString())) {
                            errorList.add(filed.getName() + " " + getMessage("IS_EMPTY"));
                        }
                        break;
                    case Number:
                        // 由于 字段类型为 String, excel单元格类型为货币类型的话 easyexcel 转换的字段值默认带千分位分隔符, 这里默认先替换掉千分位分隔符
                        if (value != null) {
                            String strValue = value.toString().replace(",", "");
                            if (!StringUtils.isNumeric(strValue)) {
                                errorList.add(filed.getName() + " " + getMessage("IS_NOT_NUMERIC"));
                            } else {
                                RefUtil.setFieldValue(o, filed, strValue);
                            }
                        }
                        break;
                    case Double:
                        try {
                            if (value != null) {
                                String strValue = value.toString().replace(",", "");
                                Double.valueOf(strValue);
                                RefUtil.setFieldValue(o, filed, strValue);
                            }
                        } catch (Exception e) {
                            errorList.add(filed.getName() + " " + getMessage("IS_INVALID_VALUE"));
                        }
                        break;
                    default:
                        break;
                }
            }
            if (StringUtils.isNotBlank(re)) {
                if (value != null && !Pattern.compile(re).matcher(value.toString()).find()) {
                    errorList.add(filed.getName() + " " + getMessage("IS_INVALID_VALUE"));
                }
            }
        }

        for (Field filed : formatFields) {
            Object value = RefUtil.getFieldValue(o, filed);
            String pattern = filed.getAnnotation(ExcelDateFormat.class).value();
            String original = filed.getAnnotation(ExcelDateFormat.class).original();
            if (value != null) {
                String strValue = null;
                try {
                    strValue = DateFormatUtils.format(DateUtils.parseDate(value.toString(), original), pattern);
                    RefUtil.setFieldValue(o, filed, strValue);
                } catch (ParseException e) {
                    errorList.add(filed.getName() + " " + getMessage("IS_INVALID_VALUE"));
                }
            }
        }

        for (Field filed : numberFormatFields) {
            Object value = RefUtil.getFieldValue(o, filed);
            String pattern = filed.getAnnotation(ExcelNumberFormat.class).value();
            if (value != null) {
                try {
                    String strValue = String.format(pattern, Integer.valueOf(value.toString()));
                    RefUtil.setFieldValue(o, filed, strValue);
                } catch (Exception e) {
                    errorList.add(filed.getName() + " " + getMessage("IS_INVALID_VALUE"));
                }
            }
        }
    }

    protected void beforeInvoke(T o) {
    }

    private Class<T> getGenericClassT() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, new Object[]{}, locale);
    }
}
