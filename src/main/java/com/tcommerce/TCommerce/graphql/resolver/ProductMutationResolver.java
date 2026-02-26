package com.tcommerce.TCommerce.graphql.resolver;

import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.graphql.mapper.GraphQLMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductMutationResolver {

    private final ProductService productService;

    @MutationMapping
    @PreAuthorize("hasRole('MANAGER')")
    public boolean deleteProduct(@Argument String productId) {
        productService.deleteProduct(productId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, Object> disableProduct(@Argument String productId) {
        Product product = productService.disableProduct(productId);
        return GraphQLMapper.toGraphQLProduct(product);
    }
}
