package com.tcommerce.TCommerce.infrastructure.services.mail.templates;
import java.util.Map;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;

public interface MailTemplate {
    String getSubject(Product product, ERole role);
    String getCode(Product product, ERole role);
    Map<String, String> buildParams(Product product, User user);
}
