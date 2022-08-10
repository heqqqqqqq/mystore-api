package com.mystore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

//@Data
@Getter
@Setter
@TableName("mystore_category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value="parent_id")
    private Integer parentId;
    private String name;
    private Boolean status;
    @TableField(value="sort_order")
    private  Integer sortOrder;

    @TableField(value="create_time")
    private LocalDateTime createTime;
    @TableField(value="update_time")
    private LocalDateTime updateTime;

    // Set是通过hashCode和equals方法来判断集合中的元素是否重复，需要重写
    //Lombok中帮忙重写了hashCode和equals方法，需要将@Data改成@Getter和@Setter，否则无法重写
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null||getClass()!=o.getClass()) return false;
        Category category=(Category) o;
        return id.equals(category.id);
    }
}
