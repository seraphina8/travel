package com.travel.common;

import lombok.Data;
import java.util.List;

//通用的分页结果
@Data
public class PageResult<T> {
    private Long total;
    private List<T> records;

    public PageResult() {}

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(total, records);
    }
}
