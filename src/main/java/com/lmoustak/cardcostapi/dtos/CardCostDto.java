package com.lmoustak.cardcostapi.dtos;

import java.math.BigDecimal;

public record CardCostDto(String country, BigDecimal cost) {
}
