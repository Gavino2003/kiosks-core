package pt.ipvc.kiosks.bll.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipvc.kiosks.bll.interfaces.IOrderService;
import pt.ipvc.kiosks.dal.entities.*;
import pt.ipvc.kiosks.dal.entities.OrderStatus;
import pt.ipvc.kiosks.dal.repository.KioskRepository;
import pt.ipvc.kiosks.dal.repository.OrderLineRepository;
import pt.ipvc.kiosks.dal.repository.OrderRepository;
import pt.ipvc.kiosks.dal.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private KioskRepository kioskRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Order createOrder(Long idKiosk, Map<Long, Integer> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Encomenda sem produtos");

        Kiosk kiosk = kioskRepository.findById(idKiosk)
                .orElseThrow(() -> new IllegalArgumentException("Quiosque não encontrado"));

        List<OrderLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null || !product.getActive()) continue;

            int qty = entry.getValue();
            if (qty <= 0) continue;

            OrderLine line = new OrderLine(qty, product.getPrice(), product, null);
            lines.add(line);
            total = total.add(line.getLineTotal());
        }

        if (lines.isEmpty()) throw new IllegalArgumentException("Nenhum produto válido na encomenda");

        String reference = generateReference();
        Order order = new Order(reference, total, kiosk);
        Order savedOrder = orderRepository.save(order);

        for (OrderLine line : lines) {
            line.setOrder(savedOrder);
            orderLineRepository.save(line);
        }

        return savedOrder;
    }

    @Override
    @Transactional
    public Order updateStatus(Long idOrder, String newStatus) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + newStatus);
        }

        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findByReference(String reference) {
        return orderRepository.findByReference(reference).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByKiosk(Long idKiosk) {
        return orderRepository.findByKioskIdKioskOrderByCreatedAtDesc(idKiosk);
    }

    private String generateReference() {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return String.format("ORD-%d-%s", java.time.Year.now().getValue(), unique);
    }
}
