package pt.ipvc.kiosks.bll.interfaces;

import pt.ipvc.kiosks.dal.entities.Order;
import java.util.List;
import java.util.Map;

public interface IOrderService {
    Order createOrder(Long idKiosk, Map<Long, Integer> items);
    Order updateStatus(Long idOrder, String newStatus);
    Order findByReference(String reference);
    List<Order> getPendingOrders();
    List<Order> getOrdersByKiosk(Long idKiosk);
}
