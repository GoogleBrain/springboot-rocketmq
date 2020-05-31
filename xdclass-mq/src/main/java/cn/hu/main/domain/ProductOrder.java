package cn.hu.main.domain;

import java.util.ArrayList;
import java.util.List;

public class ProductOrder {

    private Long id;

    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProductOrder(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public ProductOrder() {
    }

    public static List getProductOrderList() {
        List list = new ArrayList();
        ProductOrder po1 = new ProductOrder(111L, "创建订单");
        ProductOrder po2 = new ProductOrder(222L, "创建订单");
        ProductOrder po3 = new ProductOrder(333L, "创建订单");
        ProductOrder po4 = new ProductOrder(111L, "支付订单");
        ProductOrder po5 = new ProductOrder(222L, "支付订单");
        ProductOrder po6 = new ProductOrder(333L, "支付订单");
        ProductOrder po7 = new ProductOrder(111L, "完成订单");
        ProductOrder po8 = new ProductOrder(222L, "完成订单");
        ProductOrder po9 = new ProductOrder(333L, "完成订单");
        list.add(po1);
        list.add(po2);
        list.add(po3);
        list.add(po4);
        list.add(po5);
        list.add(po6);
        list.add(po7);
        list.add(po8);
        list.add(po9);

        return list;
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
