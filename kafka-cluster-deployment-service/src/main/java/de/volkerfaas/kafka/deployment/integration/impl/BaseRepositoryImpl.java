package de.volkerfaas.kafka.deployment.integration.impl;

import de.volkerfaas.kafka.deployment.controller.model.SkipablePageRequest;
import de.volkerfaas.kafka.deployment.integration.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collections;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    @Override
    public Page<T> findAllBySkipablePageRequest(SkipablePageRequest pageable) {
        final Class<T> javaType = entityInformation.getJavaType();
        final Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        final TypedQuery<T> query = getQuery(javaType, sort);

        return pageable.isUnpaged()
                ? new PageImpl<>(query.getResultList())
                : readSkipablePage(query, javaType, pageable);
    }

    private <S extends T> Page<S> readSkipablePage(TypedQuery<S> query, final Class<S> javaType, SkipablePageRequest pageable) {
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(
                query.getResultList(),
                pageable,
                () -> executeSkipableCountQuery(getCountQuery(javaType), pageable.getSkip())
        );
    }

    private long executeSkipableCountQuery(TypedQuery<Long> query, int skip) {
        Assert.notNull(query, "TypedQuery must not be null!");

        return query.getResultList().stream()
                .reduce(0L, Long::sum) - skip;
    }

    private <S extends T> TypedQuery<Long> getCountQuery(Class<S> javaType) {
        Assert.notNull(javaType, "Java type must not be null!");
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<S> root = query.from(javaType);
        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }
        query.orderBy(Collections.emptyList());

        return entityManager.createQuery(query);
    }

    private <S extends T> TypedQuery<S> getQuery(Class<S> javaType, Sort sort) {
        Assert.notNull(javaType, "Java type must not be null!");
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<S> query = builder.createQuery(javaType);
        final Root<S> root = query.from(javaType);
        query.select(root);
        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return entityManager.createQuery(query);
    }

}
