package com.rdangi.error;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product id not found : " + id);
    }

}
