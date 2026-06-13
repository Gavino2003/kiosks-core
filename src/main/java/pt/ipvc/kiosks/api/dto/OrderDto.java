package pt.ipvc.kiosks.api.dto;

import pt.ipvc.kiosks.dal.entities.Order;
import pt.ipvc.kiosks.dal.entities.OrderLine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    public Long          id;
    public String        reference;
    public String        status;
    public BigDecimal    orderTotal;
    public LocalDateTime createdAt;
    public Long          kioskId;
    public String        kioskName;
    public Long          storeId;
    public String        storeName;
    public List<LineDto> lines;

    public static class LineDto {
        public Long       productId;
        public String     productName;
        public Integer    quantity;
        public BigDecimal unitPrice;
        public BigDecimal lineTotal;

        public static LineDto from(OrderLine l) {
            LineDto d = new LineDto();
            d.productId   = l.getProduct() != null ? l.getProduct().getIdProduct() : null;
            d.productName = l.getProductNameSnap();
            d.quantity    = l.getQuantity();
            d.unitPrice   = l.getUnitPrice();
            d.lineTotal   = l.getLineTotal();
            return d;
        }
    }

    public static OrderDto from(Order o) {
        OrderDto d = new OrderDto();
        d.id         = o.getIdOrder();
        d.reference  = o.getReference();
        d.status     = o.getStatus() != null ? o.getStatus().name() : null;
        d.orderTotal = o.getOrderTotal();
        d.createdAt  = o.getCreatedAt();
        if (o.getKiosk() != null) {
            d.kioskId   = o.getKiosk().getIdKiosk();
            d.kioskName = o.getKiosk().getKioskName();
            if (o.getKiosk().getStore() != null) {
                d.storeId   = o.getKiosk().getStore().getIdStore();
                d.storeName = o.getKiosk().getStore().getStoreName();
            }
        }
        if (o.getOrderLines() != null) {
            d.lines = o.getOrderLines().stream().map(LineDto::from).toList();
        }
        return d;
    }
}
