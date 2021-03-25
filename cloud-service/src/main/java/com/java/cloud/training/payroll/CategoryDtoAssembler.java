package com.java.cloud.training.payroll;

import com.java.cloud.training.controller.CategoryController;
import com.java.cloud.training.dto.category.CategoryDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryDtoAssembler implements RepresentationModelAssembler<CategoryDto, EntityModel<CategoryDto>> {

    @Override
    public EntityModel<CategoryDto> toModel(CategoryDto dto) {
        return EntityModel.of(dto,
                              linkTo(methodOn(CategoryController.class).getCategory(dto.getCode())).withSelfRel());
    }
}
