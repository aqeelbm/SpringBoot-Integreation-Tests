/**
 * 
 */
package com.practice.repositorty;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.entity.Product;

/**
 *  author: Aqeel
 */
public interface ProductRepository extends JpaRepository<Product,Integer> {
    Product findByName(String name);
}