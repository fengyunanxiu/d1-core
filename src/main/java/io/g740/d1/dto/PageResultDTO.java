package io.g740.d1.dto;

import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author:   dengam
 * @date:    2019/7/8 15:19
 * @param:
 * @return:
 */
public class PageResultDTO<T> {

    private Long total;
    private List<T> content;

    public PageResultDTO(List<T> content, Long total) {
        this.total = total;
        this.content = content;
    }

    public PageResultDTO() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return this.total;
    }

    @Override
    public String toString() {
        return "PageQueryResult{" +
                "total=" + total +
                ", content=" + content +
                '}';
    }
}
