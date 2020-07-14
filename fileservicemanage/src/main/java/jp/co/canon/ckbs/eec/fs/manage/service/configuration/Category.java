package jp.co.canon.ckbs.eec.fs.manage.service.configuration;

import lombok.Getter;
import lombok.Setter;

public class Category {
    @Getter @Setter
    String name;

    @Getter @Setter
    String description;

    public Category(String name, String description){
        this.name = name;
        this.description = description;
    }
}
