package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.KnowledgeBase;
import com.backed.mapper.KnowledgeBaseMapper;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseService extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> {
}
