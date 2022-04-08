package com.luv2code.doan;


import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Province;
import com.luv2code.doan.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryControllerTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testGetTop5() {
        List<Category> listCategories = categoryRepository.top5CategoryBestSell();
        for(Category c : listCategories) {
            System.out.println(c.getImage());
        }

        System.out.println(listCategories.size());
        assertThat(listCategories.size()).isGreaterThan(0);
    }

}
