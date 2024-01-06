package com.practice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;

import com.practice.entity.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootIntegrationTestsApplicationTests {

	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost:" + port;

	private static RestTemplate restTemplate;

	@Autowired
	private TestH2Repository h2Repository;

	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setUp() {
		baseUrl = baseUrl.concat("/products");
	}


	@Test 
	public void addProductTest() { 
		String url = baseUrl + "/addproduct";
		Product product = new Product("headset", 5, 555); 
		Product response = restTemplate.postForObject(url, product, Product.class);

		assertEquals("headset", response.getName());
		assertEquals(5, h2Repository.findById(response.getId()).get().getQuantity());
		assertEquals(555, h2Repository.findById(response.getId()).get().getPrice());
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) VALUES (1,'iPhone', 1, 3400.00)", 
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='iPhone'",
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) 
	public void getProductTest() {
		List<Product> products = restTemplate.getForObject(baseUrl, List.class);

		//assertEquals(1, h2Repository.findById(existingProduct.getId()));
		assertEquals(1, h2Repository.findAll().size()); 
	}


	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) VALUES (1,'toothpaste', 1, 34.00)", 
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", 
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) 
	public void findProductByIdTest() {
		Product product = restTemplate.getForObject(baseUrl + "/{id}", Product.class,1);

		assertAll( 
				() -> assertNotNull(product), 
				() -> assertEquals(1, product.getId()), 
				() -> assertEquals(1, h2Repository.findById(product.getId()).get().getId()), 
				() -> assertEquals("toothpaste", h2Repository.findById(product.getId()).get().getName()) 
			);

	}



	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (2,'Jacket', 1, 200.00)", 
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", 
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) 
	public void updateProductTest(){
		Product product = new Product("Bag", 1, 200.00);

		restTemplate.put(baseUrl+"/update/{id}", product, 2);

		Product productFromDB = h2Repository.findById(2).get();

		assertAll( 
				() -> assertNotNull(productFromDB), 
				() -> assertEquals(200.00, productFromDB.getPrice()), 
				() -> assertEquals("Bag", productFromDB.getName())
			); 
	}



	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (1,'Cap', 2, 130.00)", 
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	public void deleteProductTest(){

		int recordCount = h2Repository.findAll().size();

		assertEquals(1, recordCount);

		restTemplate.delete(baseUrl+"/delete/{id}", 1);

		assertEquals(0, h2Repository.findAll().size());

	}

}
