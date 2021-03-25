package com.java.cloud.training.payroll;

import com.java.cloud.training.controller.ProductController;
import com.java.cloud.training.dto.product.ProductDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductDtoAssembler implements RepresentationModelAssembler<ProductDto, EntityModel<ProductDto>> {

    @Override
    public EntityModel<ProductDto> toModel(ProductDto dto) {
        return EntityModel.of(dto,
                              linkTo(methodOn(ProductController.class).getProduct(dto.getCode())).withSelfRel());
    }
}
