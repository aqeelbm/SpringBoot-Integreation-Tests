/**
 * 
 */
package com.practice;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.entity.Product;

/**
 *  author: Aqeel
 */
public interface TestH2Repository extends JpaRepository<Product,Integer> {
	
}