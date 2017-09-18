package com.thinkgem.jeesite.modules.api.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "产品相关接口文档")
@RequestMapping(value = {"/api/product/"} )
public class ProductController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "添加用户", notes = "增加用户")
    public ResponseEntity<Product> get(@ApiParam(name = "id", value = "id", required = true) @PathVariable Long id) {
        Product product = new Product();
        product.setName("七级滤芯净水器");
        product.setId(1L);
        product.setProductClass("seven_filters");
        product.setProductId("T12345");
        return ResponseEntity.ok(product);
    }
}
