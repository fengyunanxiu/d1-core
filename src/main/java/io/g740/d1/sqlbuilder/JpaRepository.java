package io.g740.d1.sqlbuilder;

import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 19:02
 * @description :
 */
public interface JpaRepository<T, ID extends Serializable> {

    void delete(ID id);

    void delete(List<ID> idIterable);

    void deleteAll();

    T findOne(ID id) throws Exception;

    T getOne(ID id) throws Exception;

    boolean exists(ID id) throws Exception;

    List<T> findAll() throws Exception;

    List<T> findAll(List<ID> idIterable) throws Exception;

    List<T> findAllSort(Sort sort) throws Exception;

    long count() throws Exception;

    void save(T t) throws Exception;

    void save(List<T> iterable) throws Exception;
}
