package ordercenter.services;

import common.services.GeneralDao;
import common.utils.IdUtils;
import common.utils.page.Page;
import ordercenter.constants.OrderItemType;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.OrderType;
import ordercenter.constants.PlatformType;
import ordercenter.dtos.TestObjectSearcher;
import ordercenter.models.*;
import ordercenter.models.Order;
import org.apache.commons.lang3.RandomStringUtils;
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

        cq.select(order).where(predicateList.toArray(new Predicate[predicateList.size()])).orderBy(cb.desc(order.get("updateTime")));

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

    @Transactional
    public void saveOrder(Order order) {
        if(IdUtils.isEmpty(order.getId())) {
            //ID为空,新增
            order.setType(OrderType.NORMAL);
            order.setOrderNo(RandomStringUtils.randomAlphanumeric(8));
            order.setPlatformType(PlatformType.WEB);
            generalDao.persist(order);

            for(OrderItem orderItem : order.getOrderItemList()) {
                orderItem.setPlatformType(PlatformType.WEB);
                orderItem.setType(OrderItemType.PRODUCT);
                orderItem.setOrderId(order.getId());
                generalDao.persist(orderItem);
            }

        } else {
            //修改

            Order orderInDb = generalDao.get(Order.class, order.getId());

            generalDao.merge(order);
            for(OrderItem orderItem : order.getOrderItemList()) {
                if(IdUtils.isEmpty(orderItem.getId())) {
                    //ID为空,新增
                    orderItem.setPlatformType(PlatformType.WEB);
                    orderItem.setType(OrderItemType.PRODUCT);
                    orderItem.setOrderId(order.getId());
                    generalDao.persist(orderItem);
                } else {
                    //修改
                    generalDao.merge(orderItem);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Order get(Integer id) {
        return generalDao.get(Order.class, id);
    }

}
