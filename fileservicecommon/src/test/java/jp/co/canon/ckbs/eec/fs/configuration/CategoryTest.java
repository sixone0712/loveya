package jp.co.canon.ckbs.eec.fs.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryTest {
    @Test
    void test_001(){
        Category category = new Category("001", "001 Status");
        Assertions.assertEquals("001", category.getCategoryCode());
        Assertions.assertEquals("001 Status", category.getCategoryName());
    }

    @Test
    void test_002(){
        Category category = new Category();
        category.setCategoryCode("002");
        Assertions.assertEquals("002", category.getCategoryCode());
        category.setCategoryName("002 Status");
        Assertions.assertEquals("002 Status", category.getCategoryName());
    }
}
