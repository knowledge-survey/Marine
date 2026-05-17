-- ============================================
-- 水产养殖管理系统 - 完整数据库初始化
-- ============================================

-- 清理所有数据（按依赖关系顺序）
DELETE FROM operation_log;
DELETE FROM medication_record;
DELETE FROM inspection_record;
DELETE FROM traceability_code;
DELETE FROM harvest_record;
DELETE FROM disease_knowledge;
DELETE FROM disease_record;
DELETE FROM water_quality;
DELETE FROM feeding_record;
DELETE FROM seedling_record;
DELETE FROM water_quality_threshold;
DELETE FROM aquaculture_zone;
DELETE FROM sys_user;

-- ============================================
-- 1. 用户数据
-- ============================================
INSERT INTO sys_user (username, password, real_name, phone, email, role, enabled, create_time, update_time) VALUES
('admin', '123456', '系统管理员', '13800000001', 'admin@marine.com', 'SUPER_ADMIN', 1, datetime('now'), datetime('now')),
('farmer01', '123456', '张养殖', '13800000002', 'zhang@marine.com', 'FARMER', 1, datetime('now'), datetime('now')),
('manager01', '123456', '李管理', '13800000003', 'li@marine.com', 'ADMIN', 1, datetime('now'), datetime('now'));

-- ============================================
-- 2. 养殖区域数据
-- ============================================
INSERT INTO aquaculture_zone (name, location, area, species, description, parent_id, create_time, update_time) VALUES
('A区-南美白对虾池', '东养殖场1号', 2500.00, '南美白对虾', '标准化南美白对虾养殖池', NULL, datetime('now'), datetime('now')),
('B区-石斑鱼养殖区', '东养殖场2号', 1800.00, '石斑鱼', '深水石斑鱼养殖基地', NULL, datetime('now'), datetime('now')),
('C区-海鲈鱼场', '西养殖场1号', 2000.00, '海鲈鱼', '海鲈鱼精养池', NULL, datetime('now'), datetime('now')),
('D区-鲍鱼养殖区', '西养殖场2号', 1200.00, '鲍鱼', '鲍鱼工厂化养殖', NULL, datetime('now'), datetime('now')),
('E区-大闸蟹区', '南养殖场1号', 3000.00, '大闸蟹', '生态大闸蟹养殖', NULL, datetime('now'), datetime('now'));

-- ============================================
-- 3. 水质阈值配置
-- ============================================
INSERT INTO water_quality_threshold (zone_id, temp_min, temp_max, do_min, do_max, ph_min, ph_max, salinity_min, salinity_max, create_time, update_time) VALUES
(1, 22.00, 32.00, 5.00, 12.00, 7.20, 8.80, 15.00, 32.00, datetime('now'), datetime('now')),
(2, 20.00, 30.00, 5.00, 10.00, 7.00, 8.50, 20.00, 35.00, datetime('now'), datetime('now')),
(3, 16.00, 28.00, 5.00, 11.00, 7.00, 8.50, 18.00, 30.00, datetime('now'), datetime('now')),
(4, 12.00, 24.00, 5.50, 12.00, 7.50, 8.50, 28.00, 35.00, datetime('now'), datetime('now')),
(5, 18.00, 30.00, 4.00, 9.00, 7.50, 8.50, 5.00, 15.00, datetime('now'), datetime('now'));

-- ============================================
-- 4. 苗种投放记录
-- ============================================
INSERT INTO seedling_record (zone_id, species, batch_no, quantity, source, supplier, release_date, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260501A1', 80000.00, '湛江国家级虾苗场', '湛江海洋种苗有限公司', date('now', '-25 days'), '2026年第一批虾苗', datetime('now', '-25 days'), datetime('now', '-25 days')),
(1, '南美白对虾', 'B20260510A2', 40000.00, '湛江国家级虾苗场', '湛江海洋种苗有限公司', date('now', '-15 days'), '第二批补苗', datetime('now', '-15 days'), datetime('now', '-15 days')),
(2, '石斑鱼', 'B20260502B1', 6000.00, '海南石斑鱼育苗基地', '海南深海养殖集团', date('now', '-30 days'), '珍珠龙胆石斑鱼苗', datetime('now', '-30 days'), datetime('now', '-30 days')),
(3, '海鲈鱼', 'B20260505C1', 10000.00, '珠海渔苗中心', '珠海现代渔业有限公司', date('now', '-28 days'), '优质花鲈鱼苗', datetime('now', '-28 days'), datetime('now', '-28 days')),
(4, '鲍鱼', 'B20260508D1', 3000.00, '大连鲍鱼原种场', '大连海洋珍品科技', date('now', '-20 days'), '皱纹盘鲍苗', datetime('now', '-20 days'), datetime('now', '-20 days')),
(5, '大闸蟹', 'B20260512E1', 5000.00, '阳澄湖蟹苗基地', '苏州阳澄湖蟹业公司', date('now', '-22 days'), '长江系大闸蟹苗', datetime('now', '-22 days'), datetime('now', '-22 days'));

-- ============================================
-- 5. 饲料投喂记录
-- ============================================
INSERT INTO feeding_record (zone_id, species, batch_no, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260501A1', '南美白对虾配合饲料', 60.00, datetime('now', '-24 days', '07:00:00'), '早晨投喂', datetime('now', '-24 days'), datetime('now', '-24 days')),
(1, '南美白对虾', 'B20260501A1', '南美白对虾配合饲料', 70.00, datetime('now', '-22 days', '17:00:00'), '傍晚投喂', datetime('now', '-22 days'), datetime('now', '-22 days')),
(1, '南美白对虾', 'B20260510A2', '南美白对虾配合饲料', 35.00, datetime('now', '-14 days', '08:00:00'), '新苗第一次喂食', datetime('now', '-14 days'), datetime('now', '-14 days')),
(2, '石斑鱼', 'B20260502B1', '石斑鱼膨化饲料', 40.00, datetime('now', '-28 days', '09:00:00'), '石斑鱼饲料', datetime('now', '-28 days'), datetime('now', '-28 days')),
(2, '石斑鱼', 'B20260502B1', '石斑鱼膨化饲料', 45.00, datetime('now', '-20 days', '09:00:00'), '增加投喂量', datetime('now', '-20 days'), datetime('now', '-20 days')),
(3, '海鲈鱼', 'B20260505C1', '海鲈鱼专用饲料', 50.00, datetime('now', '-26 days', '08:00:00'), '海鲈鱼精养饲料', datetime('now', '-26 days'), datetime('now', '-26 days')),
(3, '海鲈鱼', 'B20260505C1', '海鲈鱼专用饲料', 55.00, datetime('now', '-18 days', '08:00:00'), '日常投喂', datetime('now', '-18 days'), datetime('now', '-18 days')),
(4, '鲍鱼', 'B20260508D1', '鲍鱼专用配合饲料', 18.00, datetime('now', '-18 days', '10:00:00'), '鲍鱼海藻饲料', datetime('now', '-18 days'), datetime('now', '-18 days')),
(5, '大闸蟹', 'B20260512E1', '大闸蟹专用饲料', 25.00, datetime('now', '-20 days', '17:00:00'), '大闸蟹配合饲料', datetime('now', '-20 days'), datetime('now', '-20 days'));

-- ============================================
-- 6. 水质监测数据
-- ============================================
-- A区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(1, 26.80, 7.50, 7.90, 26.00, 0, NULL, datetime('now', '-10 days', '06:00:00')),
(1, 28.20, 7.20, 7.95, 26.50, 0, NULL, datetime('now', '-10 days', '12:00:00')),
(1, 27.50, 7.80, 7.88, 26.20, 0, NULL, datetime('now', '-10 days', '18:00:00')),
(1, 27.00, 7.60, 7.85, 26.10, 0, NULL, datetime('now', '-9 days', '06:00:00')),
(1, 28.50, 7.00, 8.00, 26.80, 0, NULL, datetime('now', '-9 days', '12:00:00')),
(1, 27.80, 7.40, 7.92, 26.30, 0, NULL, datetime('now', '-9 days', '18:00:00')),
(1, 26.50, 7.70, 7.82, 25.80, 0, NULL, datetime('now', '-8 days', '06:00:00')),
(1, 28.00, 7.30, 7.98, 26.60, 0, NULL, datetime('now', '-8 days', '12:00:00')),
(1, 27.20, 7.90, 7.86, 26.00, 0, NULL, datetime('now', '-8 days', '18:00:00')),
(1, 27.60, 7.40, 7.90, 26.40, 0, NULL, datetime('now', '-7 days', '06:00:00')),
(1, 28.80, 6.80, 8.05, 27.00, 1, '溶解氧偏低，请注意', datetime('now', '-7 days', '12:00:00')),
(1, 28.00, 7.20, 7.95, 26.70, 0, NULL, datetime('now', '-7 days', '18:00:00'));

-- B区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(2, 25.20, 6.80, 7.85, 29.50, 0, NULL, datetime('now', '-12 days', '08:00:00')),
(2, 26.50, 7.20, 7.92, 30.00, 0, NULL, datetime('now', '-12 days', '14:00:00')),
(2, 25.80, 7.00, 7.88, 29.70, 0, NULL, datetime('now', '-11 days', '08:00:00')),
(2, 26.80, 6.90, 7.95, 30.30, 0, NULL, datetime('now', '-11 days', '14:00:00')),
(2, 25.50, 7.10, 7.82, 29.60, 0, NULL, datetime('now', '-10 days', '08:00:00')),
(2, 26.20, 7.30, 7.90, 29.80, 0, NULL, datetime('now', '-10 days', '14:00:00'));

-- C区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(3, 22.80, 7.50, 7.80, 22.00, 0, NULL, datetime('now', '-15 days', '07:00:00')),
(3, 24.00, 7.20, 7.88, 22.50, 0, NULL, datetime('now', '-15 days', '15:00:00')),
(3, 23.20, 7.60, 7.85, 22.20, 0, NULL, datetime('now', '-14 days', '07:00:00')),
(3, 24.50, 7.10, 7.92, 22.80, 0, NULL, datetime('now', '-14 days', '15:00:00')),
(3, 23.50, 7.40, 7.86, 22.40, 0, NULL, datetime('now', '-13 days', '07:00:00')),
(3, 24.20, 7.30, 7.90, 22.60, 0, NULL, datetime('now', '-13 days', '15:00:00'));

-- D区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(4, 16.50, 7.80, 8.00, 30.50, 0, NULL, datetime('now', '-10 days', '09:00:00')),
(4, 17.20, 7.60, 8.05, 30.80, 0, NULL, datetime('now', '-10 days', '15:00:00')),
(4, 16.80, 7.90, 8.02, 30.60, 0, NULL, datetime('now', '-9 days', '09:00:00')),
(4, 17.50, 7.70, 8.08, 31.00, 0, NULL, datetime('now', '-9 days', '15:00:00'));

-- E区水质数据
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(5, 23.50, 5.50, 8.00, 8.50, 0, NULL, datetime('now', '-12 days', '08:00:00')),
(5, 24.80, 5.20, 8.05, 8.80, 0, NULL, datetime('now', '-12 days', '16:00:00')),
(5, 24.00, 5.80, 8.02, 8.60, 0, NULL, datetime('now', '-11 days', '08:00:00')),
(5, 25.20, 5.00, 8.08, 9.00, 1, '溶解氧偏低', datetime('now', '-11 days', '16:00:00'));

-- ============================================
-- 7. 疾病记录
-- ============================================
INSERT INTO disease_record (zone_id, species, batch_no, disease_name, occurrence_date, symptoms, treatment, status, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260501A1', '白斑病', date('now', '-18 days'), '虾体出现白色斑点，活力下降，食欲减少', '使用聚维酮碘消毒，加强增氧', 'RECOVERED', datetime('now', '-18 days'), datetime('now', '-8 days')),
(2, '石斑鱼', 'B20260502B1', '弧菌病', date('now', '-14 days'), '体表溃疡，鳍条出血，游动迟缓', '氟苯尼考拌料投喂5天', 'RECOVERED', datetime('now', '-14 days'), datetime('now', '-4 days')),
(3, '海鲈鱼', 'B20260505C1', '烂鳃病', date('now', '-7 days'), '鳃丝腐烂，呼吸困难，体色发黑', '二氧化氯消毒，抗生素治疗', 'ACTIVE', datetime('now', '-7 days'), datetime('now', '-7 days')),
(5, '大闸蟹', 'B20260512E1', '黑鳃病', date('now', '-5 days'), '鳃部发黑，行动迟缓', '改善水质，使用消毒剂', 'ACTIVE', datetime('now', '-5 days'), datetime('now', '-5 days'));

-- ============================================
-- 8. 疾病知识库
-- ============================================
INSERT INTO disease_knowledge (disease_name, symptoms, causes, prevention, treatment, create_time, update_time) VALUES
('白斑病', '体表白斑，活力下降，食欲减退，重则死亡', '病毒感染，水质恶化，密度过高', '定期消毒，控制密度，稳定水质', '聚维酮碘消毒，增加溶氧', datetime('now'), datetime('now')),
('弧菌病', '体表溃烂，鳍条充血，游动缓慢，食欲差', '弧菌感染，水温过高，水质恶化', '控制水温，保持水质，定期预防', '氟苯尼考拌料，水体消毒', datetime('now'), datetime('now')),
('烂鳃病', '鳃丝腐烂，呼吸困难，体色暗淡', '细菌感染，氨氮过高，水质差', '定期换水，控制氨氮，保持水质', '二氧化氯消毒，抗生素治疗', datetime('now'), datetime('now')),
('黑鳃病', '鳃部发黑，行动迟缓，食欲下降', '水质污染，细菌感染', '改善水质，定期消毒', '水体消毒，改良底质', datetime('now'), datetime('now')),
('肠炎病', '腹部肿胀，肛门红肿，排黄色黏液', '饲料变质，细菌感染，过量投喂', '新鲜饲料，控制投喂量', '大蒜素拌料，抗生素治疗', datetime('now'), datetime('now')),
('红体病', '身体发红，游动异常，食欲减退', '水质突变，应激反应，细菌感染', '稳定水质，减少应激', '维生素C增强体质', datetime('now'), datetime('now'));

-- ============================================
-- 9. 捕捞记录
-- ============================================
INSERT INTO harvest_record (zone_id, species, batch_no, quantity, avg_weight, harvest_date, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260501A1', 8000.00, 18.50, date('now', '-3 days'), '第一批次捕捞，规格18g', datetime('now', '-3 days'), datetime('now', '-3 days')),
(2, '石斑鱼', 'B20260502B1', 1000.00, 600.00, date('now', '-1 day'), '珍珠龙胆，规格600g', datetime('now', '-1 day'), datetime('now', '-1 day'));

-- ============================================
-- 10. 溯源码
-- ============================================
INSERT INTO traceability_code (code, harvest_id, batch_no, zone_id, qr_code_url, description, scan_count, create_time, update_time) VALUES
('TRC202605120001', 1, 'B20260501A1', 1, NULL, '南美白对虾-第一批上市', 25, datetime('now', '-3 days'), datetime('now', '-3 days')),
('TRC202605140002', 2, 'B20260502B1', 2, NULL, '石斑鱼-珍珠龙胆', 18, datetime('now', '-1 day'), datetime('now', '-1 day'));

-- ============================================
-- 11. 巡查记录
-- ============================================
INSERT INTO inspection_record (zone_id, inspector_id, inspection_time, issues, solutions, create_time, update_time) VALUES
(1, 2, datetime('now', '-15 days', '09:00:00'), '发现对虾有白斑症状', '立即消毒，加强增氧', datetime('now', '-15 days'), datetime('now', '-15 days')),
(2, 3, datetime('now', '-12 days', '10:00:00'), '石斑鱼体表有溃疡', '隔离病鱼，药物治疗', datetime('now', '-12 days'), datetime('now', '-12 days')),
(3, 2, datetime('now', '-6 days', '09:00:00'), '海鲈鱼鳃部异常', '使用消毒剂，加强监测', datetime('now', '-6 days'), datetime('now', '-6 days')),
(4, 3, datetime('now', '-5 days', '10:00:00'), '鲍鱼摄食正常，水质良好', '继续保持当前管理', datetime('now', '-5 days'), datetime('now', '-5 days'));

-- ============================================
-- 12. 用药记录（示例数据）
-- ============================================
INSERT INTO medication_record (disease_id, medicine_name, dosage, dosage_unit, medication_date, effect, create_time, update_time) VALUES
(1, '聚维酮碘', 500.00, 'ml', date('now', '-18 days'), '病情得到控制', datetime('now', '-18 days'), datetime('now', '-18 days')),
(2, '氟苯尼考', 100.00, 'g', date('now', '-14 days'), '恢复良好', datetime('now', '-14 days'), datetime('now', '-14 days')),
(3, '二氧化氯', 200.00, 'g', date('now', '-7 days'), '正在治疗中', datetime('now', '-7 days'), datetime('now', '-7 days'));

-- ============================================
-- 初始化完成
-- ============================================
