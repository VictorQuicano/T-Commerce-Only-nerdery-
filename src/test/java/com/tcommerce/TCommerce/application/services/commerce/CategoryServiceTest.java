package com.tcommerce.TCommerce.application.services.commerce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CreateCategoryRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.UpdateCategoryRequest;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id("cat-id")
                .name("Appliances")
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Appliances");
    }

    @Test
    void createCategory_ShouldSaveCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("New Category");
        when(categoryRepository.existsByName("New Category")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(request);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateName() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Updated Name");
        when(categoryRepository.findById("cat-id")).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        categoryService.updateCategory("cat-id", request);

        assertThat(testCategory.getName()).isEqualTo("Updated Name");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void deleteCategory_ShouldCallDelete() {
        when(categoryRepository.findById("cat-id")).thenReturn(Optional.of(testCategory));

        categoryService.deleteCategory("cat-id");

        verify(categoryRepository).deleteById("cat-id");
    }
}
