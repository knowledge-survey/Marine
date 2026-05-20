package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.ProductionPlan;
import com.backed.mapper.ProductionPlanMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductionPlanService extends ServiceImpl<ProductionPlanMapper, ProductionPlan> {
}
