package com.kh.product.dao;

import com.kh.product.dao.entity.MyRowMapper;
import com.kh.product.dao.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductDAOImpl implements ProductDAO{

    private final NamedParameterJdbcTemplate template;

    // ---------------------------Create 상품등록--------------------------//
    @Override
    public Long createProduct(Product product) {
        // 1) sql문
        StringBuffer sql = new StringBuffer();
        sql.append(" insert into product (pname,quantity,price) ");
        sql.append(" values (:pname,:quantity,:price)           ");

        // 2) sql 실행

        // sql파라미터 자동매핑 BeanPropertySql...
        SqlParameterSource param = new BeanPropertySqlParameterSource(product);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql.toString(), param, keyHolder,new String[]{"pid"});

        long createdPid = keyHolder.getKey().longValue();
        return createdPid;
    }

    // --------------------- Read 상품상세조회 ------------------------//
    @Override
    public Optional<Product> readById(Long pid) {
        StringBuffer sql = new StringBuffer();
        sql.append("select pid,pname,quantity,price ");
        sql.append("  from product ");
        sql.append(" where pid = :pid ");

        MyRowMapper myRowMapper = new MyRowMapper();


        try {
            // SQL 파라미터 수동매핑
            Map<String, Long> param = Map.of("pid", pid);
            // RowMapper 수동 매핑
            Product product = template.queryForObject(sql.toString(), param, myRowMapper);
            return Optional.of(product);
        }catch(EmptyResultDataAccessException e){
            //조회결과가 없는경우
            return Optional.empty();
        }

    }

    // --------------------- Delete 상품삭제 ------------------------//
    @Override
    public int deleteProduct(int pid) {
        //1) sql문 작성
        StringBuffer sql = new StringBuffer();
        sql.append("    DELETE FROM product  ");
        sql.append("    WHERE pid = :pid  ");

        //2) 수동매핑
        int deleteById = template.update(sql.toString(), Map.of("pid", pid));

        return deleteById;
    }

    // --------------------- Update 상품수정 ------------------------//
    @Override
    public int updateProduct(Long pid, Product product) {
        //1) sql문 작성
        StringBuffer sql = new StringBuffer();
        sql.append(" update product ");
        sql.append("  set pname = :pname , quantity = :quantity , price = :price  ");
        sql.append("  where pid = :pid   ");

        //2) sql 매핑
        SqlParameterSource param = new BeanPropertySqlParameterSource(product);

        int updatedProduct = template.update(sql.toString(), param);

        return updatedProduct;
    }


    // --------------------- FindAll 상품목록조회 ------------------------//

    @Override
    public List<Product> findAll() {
        StringBuffer sql = new StringBuffer();
        sql.append("    select pid, pname, quantity, price ");
        sql.append("    from product               ");
        sql.append("    order by pid desc   ");

        // 결과셋 자동 매핑
        List<Product> list = template.query(sql.toString(), BeanPropertyRowMapper.newInstance(Product.class));
        return list;
    }
}
