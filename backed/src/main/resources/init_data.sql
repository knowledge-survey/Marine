-- ============================================
-- 水产养殖管理系统 - 初始数据
-- ============================================

-- 1. 用户数据
INSERT INTO sys_user (username, password, real_name, phone, email, role, enabled, create_time, update_time) VALUES
('admin', '123456', '系统管理员', '13800000001', 'admin@marine.com', 'SUPER_ADMIN', 1, datetime('now'), datetime('now')),
('farmer01', '123456', '张养殖', '13800000002', 'zhang@marine.com', 'FARMER', 1, datetime('now'), datetime('now')),
('farmer02', '123456', '李管理', '13800000003', 'li@marine.com', 'ADMIN', 1, datetime('now'), datetime('now'));

-- 2. 养殖区域数据
INSERT INTO aquaculture_zone (name, location, area, species, description, parent_id, create_time, update_time) VALUES
('A区-南美白对虾养殖池', '东区1号', 2000.00, '南美白对虾', '主要养殖南美白对虾的标准化养殖池', NULL, datetime('now'), datetime('now')),
('B区-石斑鱼养殖池', '东区2号', 1500.00, '石斑鱼', '石斑鱼深水养殖区', NULL, datetime('now'), datetime('now')),
('C区-海鲈鱼养殖池', '西区1号', 1800.00, '海鲈鱼', '海鲈鱼养殖基地', NULL, datetime('now'), datetime('now')),
('D区-鲍鱼养殖池', '西区2号', 1000.00, '鲍鱼', '鲍鱼精品养殖区', NULL, datetime('now'), datetime('now')),
('E区-多宝鱼养殖池', '南区1号', 1200.00, '多宝鱼', '多宝鱼工厂化养殖', NULL, datetime('now'), datetime('now'));

-- 3. 水质阈值配置
INSERT INTO water_quality_threshold (zone_id, temp_min, temp_max, do_min, do_max, ph_min, ph_max, salinity_min, salinity_max, create_time, update_time) VALUES
(1, 20.00, 32.00, 5.00, 12.00, 7.00, 8.50, 15.00, 35.00, datetime('now'), datetime('now')),
(2, 18.00, 28.00, 5.00, 10.00, 7.00, 8.50, 20.00, 35.00, datetime('now'), datetime('now')),
(3, 15.00, 28.00, 5.00, 10.00, 7.00, 8.50, 20.00, 35.00, datetime('now'), datetime('now')),
(4, 10.00, 25.00, 5.00, 12.00, 7.50, 8.50, 25.00, 35.00, datetime('now'), datetime('now')),
(5, 10.00, 22.00, 6.00, 10.00, 7.00, 8.50, 20.00, 35.00, datetime('now'), datetime('now'));

-- 4. 苗种投放记录
INSERT INTO seedling_record (zone_id, species, batch_no, quantity, source, supplier, release_date, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260401001', 50000.00, '湛江育苗场', '湛江海洋生物有限公司', date('now', '-30 days'), '第一批投放', datetime('now', '-30 days'), datetime('now', '-30 days')),
(1, '南美白对虾', 'B20260415002', 30000.00, '湛江育苗场', '湛江海洋生物有限公司', date('now', '-15 days'), '第二批补苗', datetime('now', '-15 days'), datetime('now', '-15 days')),
(2, '石斑鱼', 'B20260402001', 5000.00, '海南育苗基地', '海南水产养殖集团', date('now', '-28 days'), '珍珠龙胆石斑鱼苗', datetime('now', '-28 days'), datetime('now', '-28 days')),
(3, '海鲈鱼', 'B20260405001', 8000.00, '珠海育苗中心', '珠海渔业发展有限公司', date('now', '-25 days'), '花鲈鱼苗', datetime('now', '-25 days'), datetime('now', '-25 days')),
(4, '鲍鱼', 'B20260408001', 2000.00, '大连鲍鱼育苗场', '大连海洋珍品有限公司', date('now', '-20 days'), '皱纹盘鲍', datetime('now', '-20 days'), datetime('now', '-20 days')),
(5, '多宝鱼', 'B20260410001', 3000.00, '烟台多宝鱼育苗基地', '烟台水产科技有限公司', date('now', '-18 days'), '大菱鲆苗', datetime('now', '-18 days'), datetime('now', '-18 days'));

-- 5. 饲料投喂记录
INSERT INTO feeding_record (zone_id, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(1, '南美白对虾配合饲料', 50.00, datetime('now', '-29 days', '08:00:00'), '早间投喂', datetime('now', '-29 days'), datetime('now', '-29 days')),
(1, '南美白对虾配合饲料', 45.00, datetime('now', '-28 days', '08:00:00'), '正常投喂', datetime('now', '-28 days'), datetime('now', '-28 days')),
(1, '南美白对虾配合饲料', 55.00, datetime('now', '-27 days', '08:00:00'), '增加投喂量', datetime('now', '-27 days'), datetime('now', '-27 days')),
(2, '石斑鱼膨化饲料', 30.00, datetime('now', '-27 days', '09:00:00'), '石斑鱼日常投喂', datetime('now', '-27 days'), datetime('now', '-27 days')),
(2, '石斑鱼膨化饲料', 32.00, datetime('now', '-25 days', '09:00:00'), '增加投喂', datetime('now', '-25 days'), datetime('now', '-25 days')),
(3, '海鲈鱼配合饲料', 40.00, datetime('now', '-24 days', '08:30:00'), '海鲈鱼投喂', datetime('now', '-24 days'), datetime('now', '-24 days')),
(3, '海鲈鱼配合饲料', 42.00, datetime('now', '-22 days', '08:30:00'), '正常投喂', datetime('now', '-22 days'), datetime('now', '-22 days')),
(4, '鲍鱼专用饲料', 15.00, datetime('now', '-19 days', '10:00:00'), '鲍鱼裙带菜饲料', datetime('now', '-19 days'), datetime('now', '-19 days')),
(5, '多宝鱼膨化饲料', 20.00, datetime('now', '-17 days', '09:00:00'), '多宝鱼投喂', datetime('now', '-17 days'), datetime('now', '-17 days'));

-- 6. 水质监测数据 (最近30天，每天3-4条)
-- A区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(1, 26.50, 7.20, 7.80, 28.00, 0, NULL, datetime('now', '-30 days', '06:00:00')),
(1, 27.80, 7.50, 7.85, 28.20, 0, NULL, datetime('now', '-30 days', '12:00:00')),
(1, 28.20, 6.80, 7.90, 28.50, 0, NULL, datetime('now', '-30 days', '18:00:00')),
(1, 27.00, 7.00, 7.82, 28.10, 0, NULL, datetime('now', '-29 days', '06:00:00')),
(1, 28.50, 7.30, 7.88, 28.30, 0, NULL, datetime('now', '-29 days', '12:00:00')),
(1, 29.00, 6.50, 7.92, 28.60, 0, NULL, datetime('now', '-29 days', '18:00:00')),
(1, 26.80, 7.10, 7.78, 27.90, 0, NULL, datetime('now', '-28 days', '06:00:00')),
(1, 28.00, 7.40, 7.85, 28.10, 0, NULL, datetime('now', '-28 days', '12:00:00')),
(1, 28.80, 6.90, 7.90, 28.40, 0, NULL, datetime('now', '-28 days', '18:00:00')),
(1, 27.20, 7.20, 7.80, 28.00, 0, NULL, datetime('now', '-27 days', '06:00:00')),
(1, 28.60, 7.50, 7.86, 28.20, 0, NULL, datetime('now', '-27 days', '12:00:00')),
(1, 29.50, 6.20, 7.95, 28.70, 1, '水温偏高，溶解氧偏低', datetime('now', '-27 days', '18:00:00')),
(1, 26.50, 7.30, 7.78, 27.80, 0, NULL, datetime('now', '-26 days', '06:00:00')),
(1, 27.80, 7.60, 7.82, 28.00, 0, NULL, datetime('now', '-26 days', '12:00:00')),
(1, 28.20, 7.10, 7.88, 28.30, 0, NULL, datetime('now', '-26 days', '18:00:00')),
(1, 27.00, 7.40, 7.80, 27.90, 0, NULL, datetime('now', '-25 days', '06:00:00')),
(1, 28.30, 7.20, 7.85, 28.10, 0, NULL, datetime('now', '-25 days', '12:00:00')),
(1, 29.00, 6.80, 7.90, 28.50, 0, NULL, datetime('now', '-25 days', '18:00:00')),
(1, 26.80, 7.50, 7.78, 27.80, 0, NULL, datetime('now', '-24 days', '06:00:00')),
(1, 28.00, 7.30, 7.82, 28.00, 0, NULL, datetime('now', '-24 days', '12:00:00')),
(1, 28.50, 7.00, 7.88, 28.30, 0, NULL, datetime('now', '-24 days', '18:00:00')),
(1, 27.20, 7.60, 7.80, 27.90, 0, NULL, datetime('now', '-23 days', '06:00:00')),
(1, 28.50, 7.40, 7.85, 28.10, 0, NULL, datetime('now', '-23 days', '12:00:00')),
(1, 29.20, 6.90, 7.92, 28.40, 0, NULL, datetime('now', '-23 days', '18:00:00')),
(1, 26.50, 7.30, 7.78, 27.80, 0, NULL, datetime('now', '-22 days', '06:00:00')),
(1, 27.80, 7.50, 7.82, 28.00, 0, NULL, datetime('now', '-22 days', '12:00:00')),
(1, 28.30, 7.10, 7.86, 28.20, 0, NULL, datetime('now', '-22 days', '18:00:00')),
(1, 27.00, 7.40, 7.80, 27.90, 0, NULL, datetime('now', '-21 days', '06:00:00')),
(1, 28.20, 7.20, 7.84, 28.10, 0, NULL, datetime('now', '-21 days', '12:00:00')),
(1, 28.80, 6.80, 7.90, 28.40, 0, NULL, datetime('now', '-21 days', '18:00:00'));

-- B区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(2, 24.50, 6.80, 7.90, 30.00, 0, NULL, datetime('now', '-28 days', '08:00:00')),
(2, 25.20, 7.10, 7.95, 30.20, 0, NULL, datetime('now', '-28 days', '14:00:00')),
(2, 24.80, 6.90, 7.92, 30.10, 0, NULL, datetime('now', '-27 days', '08:00:00')),
(2, 25.50, 7.00, 7.98, 30.30, 0, NULL, datetime('now', '-27 days', '14:00:00')),
(2, 24.20, 7.20, 7.88, 29.90, 0, NULL, datetime('now', '-26 days', '08:00:00')),
(2, 25.00, 7.00, 7.94, 30.10, 0, NULL, datetime('now', '-26 days', '14:00:00')),
(2, 24.80, 6.80, 7.90, 30.00, 0, NULL, datetime('now', '-25 days', '08:00:00')),
(2, 25.30, 7.10, 7.96, 30.20, 0, NULL, datetime('now', '-25 days', '14:00:00')),
(2, 24.50, 7.30, 7.88, 29.90, 0, NULL, datetime('now', '-24 days', '08:00:00')),
(2, 25.00, 7.00, 7.92, 30.10, 0, NULL, datetime('now', '-24 days', '14:00:00')),
(2, 24.80, 6.90, 7.90, 30.00, 0, NULL, datetime('now', '-23 days', '08:00:00')),
(2, 25.20, 7.20, 7.95, 30.20, 0, NULL, datetime('now', '-23 days', '14:00:00')),
(2, 24.50, 7.00, 7.88, 29.90, 0, NULL, datetime('now', '-22 days', '08:00:00')),
(2, 25.00, 6.80, 7.92, 30.10, 0, NULL, datetime('now', '-22 days', '14:00:00')),
(2, 24.80, 7.10, 7.90, 30.00, 0, NULL, datetime('now', '-21 days', '08:00:00'));

-- C区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(3, 22.00, 7.50, 7.85, 25.00, 0, NULL, datetime('now', '-25 days', '07:00:00')),
(3, 23.50, 7.20, 7.90, 25.20, 0, NULL, datetime('now', '-25 days', '15:00:00')),
(3, 22.50, 7.40, 7.88, 25.10, 0, NULL, datetime('now', '-24 days', '07:00:00')),
(3, 23.80, 7.10, 7.92, 25.30, 0, NULL, datetime('now', '-24 days', '15:00:00')),
(3, 22.20, 7.60, 7.85, 25.00, 0, NULL, datetime('now', '-23 days', '07:00:00')),
(3, 23.50, 7.30, 7.90, 25.20, 0, NULL, datetime('now', '-23 days', '15:00:00')),
(3, 22.80, 7.40, 7.88, 25.10, 0, NULL, datetime('now', '-22 days', '07:00:00')),
(3, 23.20, 7.20, 7.92, 25.30, 0, NULL, datetime('now', '-22 days', '15:00:00')),
(3, 22.50, 7.50, 7.85, 25.00, 0, NULL, datetime('now', '-21 days', '07:00:00')),
(3, 23.80, 7.10, 7.90, 25.20, 0, NULL, datetime('now', '-21 days', '15:00:00'));

-- 7. 疾病记录
INSERT INTO disease_record (zone_id, disease_name, occurrence_date, symptoms, treatment, status, create_time, update_time) VALUES
(1, '白斑病', date('now', '-20 days'), '体表出现白色斑点，活动力下降，食欲减退', '使用聚维酮碘溶液消毒，增加溶氧', 'RECOVERED', datetime('now', '-20 days'), datetime('now', '-10 days')),
(2, '弧菌病', date('now', '-15 days'), '体表溃烂，鳍条充血，游动缓慢', '使用氟苯尼考拌料投喂，连续5天', 'RECOVERED', datetime('now', '-15 days'), datetime('now', '-5 days')),
(3, '烂鳃病', date('now', '-8 days'), '鳃丝腐烂，呼吸急促，体色变暗', '使用二氧化氯消毒水体，配合抗生素治疗', 'ACTIVE', datetime('now', '-8 days'), datetime('now', '-8 days'));

-- 8. 疾病知识库
INSERT INTO disease_knowledge (disease_name, symptoms, causes, prevention, treatment, create_time, update_time) VALUES
('白斑病', '体表出现白色斑点，活动力下降，食欲减退，严重时死亡', '病毒感染，水质恶化，密度过大', '保持水质清洁，合理控制养殖密度，定期消毒', '聚维酮碘消毒，增加溶氧，使用抗病毒药物', datetime('now'), datetime('now')),
('弧菌病', '体表溃烂，鳍条充血，游动缓慢，食欲减退', '弧菌感染，水温过高，水质不良', '控制水温，保持水质，定期使用益生菌', '氟苯尼考拌料投喂，二氧化氯消毒水体', datetime('now'), datetime('now')),
('烂鳃病', '鳃丝腐烂，呼吸急促，体色变暗，浮头', '细菌感染，水质恶化，氨氮超标', '定期换水，控制氨氮含量，保持良好水质', '二氧化氯消毒，抗生素治疗，改善水质', datetime('now'), datetime('now')),
('肠炎病', '腹部膨大，肛门红肿，排出黄色黏液', '饲料变质，细菌感染，投喂过量', '使用新鲜饲料，控制投喂量，定期消毒', '大蒜素拌料，氟哌酸治疗，停食1-2天', datetime('now'), datetime('now')),
('红体病', '体色发红，游动异常，食欲减退', '水质突变，细菌感染，应激反应', '保持水质稳定，避免应激，定期检测水质', '维生素C增强免疫力，抗生素治疗', datetime('now'), datetime('now'));

-- 9. 捕捞记录
INSERT INTO harvest_record (zone_id, species, quantity, avg_weight, harvest_date, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 5000.00, 15.50, date('now', '-5 days'), '第一批捕捞，规格15g/尾', datetime('now', '-5 days'), datetime('now', '-5 days')),
(2, '石斑鱼', 800.00, 500.00, date('now', '-3 days'), '珍珠龙胆石斑鱼，规格500g/尾', datetime('now', '-3 days'), datetime('now', '-3 days'));

-- 10. 溯源码
INSERT INTO traceability_code (code, harvest_id, batch_no, zone_id, description, scan_count, create_time, update_time) VALUES
('TRC202604170001', 1, 'B20260401001', 1, '南美白对虾 - 第一批捕捞', 12, datetime('now', '-5 days'), datetime('now', '-5 days')),
('TRC202604190002', 2, 'B20260402001', 2, '石斑鱼 - 珍珠龙胆', 8, datetime('now', '-3 days'), datetime('now', '-3 days'));

-- 11. 巡查记录
INSERT INTO inspection_record (zone_id, inspector_id, inspection_time, issues, solutions, create_time, update_time) VALUES
(1, 2, datetime('now', '-10 days', '09:00:00'), '水质溶解氧偏低，部分虾活动力下降', '增加增氧机运行时间，补充增氧剂', datetime('now', '-10 days'), datetime('now', '-10 days')),
(2, 3, datetime('now', '-7 days', '10:00:00'), '发现少量石斑鱼体表有溃疡', '隔离病鱼，使用药物治疗，加强水质监测', datetime('now', '-7 days'), datetime('now', '-7 days')),
(3, 2, datetime('now', '-3 days', '09:00:00'), '海鲈鱼摄食正常，水质指标良好', '继续保持当前管理措施', datetime('now', '-3 days'), datetime('now', '-3 days'));
