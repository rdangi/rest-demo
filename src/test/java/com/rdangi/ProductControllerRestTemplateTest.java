package com.rdangi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class ProductControllerRestTemplateTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ProductRepository mockRepository;

    @Before
    public void init() {
        Product product = new Product(1L, "Product Name", "Tool", new BigDecimal("9.99"));
        when(mockRepository.findById(1L)).thenReturn(Optional.of(product));
    }

    @Test
    public void find_productId_OK() throws JSONException {

        String expected = "{id:1,name:\"Product Name\",description:\"Tool\",price:9.99}";

        ResponseEntity<String> response = restTemplate.withBasicAuth("user", "password").getForEntity("/products/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);

    }

    @Test
    public void find_allProduct_OK() throws Exception {

        List<Product> products = Arrays.asList(
                new Product(1L, "Product A", "Ah Pig", new BigDecimal("1.99")),
                new Product(2L, "Product  B", "Ah Dog", new BigDecimal("2.99")));

        when(mockRepository.findAll()).thenReturn(products);

        String expected = om.writeValueAsString(products);

        ResponseEntity<String> response = restTemplate.withBasicAuth("user", "password").getForEntity("/products", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void find_productIdNotFound_404() throws Exception {

        String expected = "{status:404,error:\"Not Found\",message:\"Product id not found : 5\",path:\"/products/5\"}";

        ResponseEntity<String> response = restTemplate.withBasicAuth("user", "password").getForEntity("/products/5", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void save_product_OK() throws Exception {

        Product newProduct = new Product(1L, "Spring Boot Guide", "Product tool", new BigDecimal("2.99"));
        when(mockRepository.save(any(Product.class))).thenReturn(newProduct);

        String expected = om.writeValueAsString(newProduct);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "password").postForEntity("/products", newProduct, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).save(any(Product.class));

    }

    @Test
    public void update_product_OK() throws Exception {

        Product updateProduct = new Product(1L, "ABC", "Tool opener", new BigDecimal("19.99"));
        when(mockRepository.save(any(Product.class))).thenReturn(updateProduct);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updateProduct), headers);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "password").exchange("/products/1", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(updateProduct), response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(1)).save(any(Product.class));

    }

    @Test
    public void patch_productDescription_OK() {

        when(mockRepository.save(any(Product.class))).thenReturn(new Product());
        String patchInJson = "{\"description\":\"ultraman\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "password").exchange("/products/1", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(1)).save(any(Product.class));

    }

    @Test
    public void patch_productPrice_405() throws JSONException {

        String expected = "{status:405,error:\"Method Not Allowed\",message:\"Field [price] update is not allow.\"}";

        String patchInJson = "{\"price\":\"99.99\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "password").exchange("/products/1", HttpMethod.PATCH, entity, String.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void delete_product_OK() {

        doNothing().when(mockRepository).deleteById(1L);

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "password").exchange("/products/1", HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(mockRepository, times(1)).deleteById(1L);
    }

    private static void printJSON(Object object) {
        String result;
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
