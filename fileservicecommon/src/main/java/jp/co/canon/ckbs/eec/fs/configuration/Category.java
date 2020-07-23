package jp.co.canon.ckbs.eec.fs.configuration;

import lombok.Getter;
import lombok.Setter;

public class Category {
    @Getter @Setter
    String categoryCode;

    @Getter @Setter
    String categoryName;

    public Category(){

    }

    public Category(String categoryCode, String categoryName){
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }
}
