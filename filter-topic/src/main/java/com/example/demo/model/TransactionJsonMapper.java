package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionJsonMapper {

	private String ref;
	private Float montant;
}
