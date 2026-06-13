package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.OrderDto;
import pt.ipvc.kiosks.bll.services.OrderService;
import pt.ipvc.kiosks.dal.entities.Order;
import pt.ipvc.kiosks.dal.entities.OrderStatus;
import pt.ipvc.kiosks.dal.repository.OrderRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired private OrderService    orderService;
    @Autowired private OrderRepository orderRepository;

    @GetMapping
    public List<OrderDto> getAll(@RequestParam(required = false) String status,
                                  @RequestParam(required = false) Long storeId,
                                  @RequestParam(required = false) String ref) {
        List<Order> orders;
        if (ref != null && !ref.isBlank()) {
            orders = storeId != null
                    ? orderRepository.findByReferenceContainingIgnoreCaseAndKioskStoreIdStoreOrderByCreatedAtDesc(ref, storeId)
                    : orderRepository.findByReferenceContainingIgnoreCaseOrderByCreatedAtDesc(ref);
        } else if (status != null && !status.equals("TODOS")) {
            OrderStatus os = OrderStatus.valueOf(status);
            orders = storeId != null
                    ? orderRepository.findByStatusAndKioskStoreIdStoreOrderByCreatedAtDesc(os, storeId)
                    : orderRepository.findByStatusOrderByCreatedAtDesc(os);
        } else {
            orders = storeId != null
                    ? orderRepository.findByKioskStoreIdStoreOrderByCreatedAtDesc(storeId)
                    : orderRepository.findAll();
        }
        return orders.stream().map(OrderDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(o -> ResponseEntity.ok(OrderDto.from(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<OrderDto> getByReference(@PathVariable String ref) {
        Order o = orderService.findByReference(ref);
        return o != null ? ResponseEntity.ok(OrderDto.from(o)) : ResponseEntity.notFound().build();
    }

    /** POST /api/orders  body: { kioskId: 1, items: { "2": 3, "5": 1 } } */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Long kioskId = Long.valueOf(body.get("kioskId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Integer> rawItems = (Map<String, Integer>) body.get("items");
            Map<Long, Integer> items = new java.util.HashMap<>();
            rawItems.forEach((k, v) -> items.put(Long.valueOf(k), v));
            Order o = orderService.createOrder(kioskId, items);
            return ResponseEntity.ok(OrderDto.from(o));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String newStatus = status != null ? status
                    : (body != null ? body.get("status") : null);
            if (newStatus == null) return ResponseEntity.badRequest().body("status obrigatório");
            Order o = orderService.updateStatus(id, newStatus);
            return ResponseEntity.ok(OrderDto.from(o));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
