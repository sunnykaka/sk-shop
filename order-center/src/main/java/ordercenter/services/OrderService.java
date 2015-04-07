package ordercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import ordercenter.constants.OrderStatus;
import ordercenter.dtos.OrderSearcher;
import ordercenter.models.*;
import ordercenter.models.Order;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

import static common.utils.SQLUtils.*;

/**
 * Created by liubin on 15-4-2.
 */
@Service
public class OrderService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDao;

    @Transactional(readOnly = true)
    public List<Order> findByKey(Optional<Page<Order>> page, Optional<String> orderNo,
        Optional<OrderStatus> status, Optional<DateTime> createTimeStart, Optional<DateTime> createTimeEnd) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> order = cq.from(Order.class);

        List<Predicate> predicateList = new ArrayList<>();
        if(orderNo.isPresent()) {
            predicateList.add(cb.equal(order.get("orderNo"), orderNo.get()));
        }
        if(createTimeStart.isPresent()) {
            predicateList.add(cb.greaterThanOrEqualTo(order.get("createTime"), createTimeStart.get()));
        }
        if(createTimeEnd.isPresent()) {
            predicateList.add(cb.lessThanOrEqualTo(order.get("createTime"), createTimeEnd.get()));
        }
        if(status.isPresent()) {
            predicateList.add(cb.equal(order.get("status"), status.get()));
        }

        cq.select(order).where(predicateList.toArray(new Predicate[predicateList.size()]));

        TypedQuery<Order> query = em.createQuery(cq);

        if(page.isPresent()) {
            CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
            countCq.select(cb.count(countCq.from(Order.class))).where(predicateList.toArray(new Predicate[predicateList.size()]));
            Long count = em.createQuery(countCq).getSingleResult();
            page.get().setTotalCount(count.intValue());

            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());
        }

        List<Order> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<Order> findByComplicateKey(Optional<Page<Order>> page, OrderSearcher orderSearcher) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> order = cq.from(Order.class);
        ListJoin<Order, OrderItem> orderItemList = order.joinList("orderItemList");

        List<Predicate> predicateList = new ArrayList<>();
        if(orderSearcher.orderNo != null) {
            predicateList.add(cb.equal(order.get("orderNo"), orderSearcher.orderNo));
        }
        if(orderSearcher.createTimeStart != null) {
            predicateList.add(cb.greaterThanOrEqualTo(order.get("createTime"), orderSearcher.createTimeStart));
        }
        if(orderSearcher.createTimeEnd != null) {
            predicateList.add(cb.lessThanOrEqualTo(order.get("createTime"), orderSearcher.createTimeEnd));
        }
        if(orderSearcher.status != null) {
            predicateList.add(cb.equal(order.get("status"), orderSearcher.status));
        }
        if(orderSearcher.type != null) {
            predicateList.add(cb.equal(order.get("type"), orderSearcher.type));
        }
        if(orderSearcher.orderItemStatus != null) {
            predicateList.add(cb.equal(orderItemList.get("status"), orderSearcher.orderItemStatus));
        }
        if(!StringUtils.isBlank(orderSearcher.productSku)) {
            predicateList.add(cb.equal(orderItemList.get("productSku"), orderSearcher.productSku));
        }

        cq.select(order).where(predicateList.toArray(new Predicate[predicateList.size()])).groupBy(order.get("id"));

        TypedQuery<Order> query = em.createQuery(cq);

        if(page.isPresent()) {

            CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
            countCq.from(Order.class).joinList("orderItemList");
            countCq.select(cb.count(order)).where(predicateList.toArray(new Predicate[predicateList.size()])).groupBy(order.get("id"));
            List resultList = em.createQuery(countCq).getResultList();
            page.get().setTotalCount(resultList.size());

            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());
        }

        List<Order> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<Order> findByComplicateKeyWithJpql(Optional<Page<Order>> page, OrderSearcher orderSearcher) {

        String jpql = "select o from Order o join o.orderItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(orderSearcher.orderNo != null) {
            jpql += " and o.orderNo = :orderNo ";
            queryParams.put("orderNo", orderSearcher.orderNo);
        }
        if(orderSearcher.createTimeStart != null) {
            jpql += " and o.createTime >= :createTimeStart ";
            queryParams.put("createTimeStart", orderSearcher.createTimeStart);
        }
        if(orderSearcher.createTimeEnd != null) {
            jpql += " and o.createTime <= :createTimeEnd ";
            queryParams.put("createTimeEnd", orderSearcher.createTimeEnd);
        }
        if(orderSearcher.status != null) {
            jpql += " and o.status = :status ";
            queryParams.put("status", orderSearcher.status);
        }
        if(orderSearcher.type != null) {
            jpql += " and o.type = :type ";
            queryParams.put("type", orderSearcher.type);
        }
        if(orderSearcher.orderItemStatus != null) {
            jpql += " and oi.status = :orderItemStatus ";
            queryParams.put("orderItemStatus", orderSearcher.orderItemStatus);
        }
        if(!StringUtils.isBlank(orderSearcher.productSku)) {
            jpql += " and oi.productSku = :productSku ";
            queryParams.put("productSku", orderSearcher.productSku);
        }

        jpql += " group by o.id ";

        Query query = em.createQuery(jpql);
        queryParams.forEach(query::setParameter);

        if(page.isPresent()) {

            String countJpql = " select count(1) " + removeFetchInCountQl(removeSelect(removeOrderBy(jpql)));
            Query countQuery = em.createQuery(countJpql);
            queryParams.forEach(countQuery::setParameter);


            if(hasGroupBy(jpql)) {
                List resultList = countQuery.getResultList();
                page.get().setTotalCount(resultList.size());

            } else {

                Long count = (Long)countQuery.getSingleResult();
                page.get().setTotalCount(count.intValue());

            }
            query.setFirstResult(page.get().getStart());
            query.setMaxResults(page.get().getLimit());

        }

        List<ordercenter.models.Order> results = query.getResultList();

        if(page.isPresent()) {
            page.get().setResult(results);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<Order> findByComplicateKeyWithGeneralDaoQuery(Optional<Page<Order>> page, OrderSearcher orderSearcher) {

        String jpql = "select o from Order o join o.orderItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(orderSearcher.orderNo != null) {
            jpql += " and o.order = :orderNo ";
            queryParams.put("orderNo", orderSearcher.orderNo);
        }
        if(orderSearcher.createTimeStart != null) {
            jpql += " and o.createTime >= :createTimeStart ";
            queryParams.put("createTimeStart", orderSearcher.createTimeStart);
        }
        if(orderSearcher.createTimeEnd != null) {
            jpql += " and o.createTime <= :createTimeEnd ";
            queryParams.put("createTimeEnd", orderSearcher.createTimeEnd);
        }
        if(orderSearcher.status != null) {
            jpql += " and o.status = :status ";
            queryParams.put("status", orderSearcher.status);
        }
        if(orderSearcher.type != null) {
            jpql += " and o.type = :type ";
            queryParams.put("type", orderSearcher.type);
        }
        if(orderSearcher.orderItemStatus != null) {
            jpql += " and oi.status = :orderItemStatus ";
            queryParams.put("orderItemStatus", orderSearcher.orderItemStatus);
        }
        if(!StringUtils.isBlank(orderSearcher.productSku)) {
            jpql += " and oi.productSku = :productSku ";
            queryParams.put("productSku", orderSearcher.productSku);
        }

        jpql += " group by o.id ";

        return generalDao.query(jpql, page, queryParams);

    }

}
