package pt.ipvc.kiosks.api.dto;

import pt.ipvc.kiosks.dal.entities.Category;

public class CategoryDto {
    public Long   id;
    public String categoryName;
    public String description;
    public Integer displayOrder;
    public Boolean active;
    public Long   storeId;

    public static CategoryDto from(Category c) {
        CategoryDto d = new CategoryDto();
        d.id           = c.getIdCategory();
        d.categoryName = c.getCategoryName();
        d.description  = c.getDescription();
        d.displayOrder = c.getDisplayOrder();
        d.active       = c.getActive();
        d.storeId      = c.getStore() != null ? c.getStore().getIdStore() : null;
        return d;
    }
}
