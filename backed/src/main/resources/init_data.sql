-- ============================================================
-- 海洋养殖管理系统 - 完整初始数据
-- 按依赖顺序清理 + 填充，遵守所有约束
-- 日期使用相对时间，启动时自动生成最新数据
-- ============================================================

-- ---------- 清理（按外键依赖逆序）----------
DELETE FROM inventory_log;
DELETE FROM operation_log;
DELETE FROM traceability_code;
DELETE FROM sales_record;
DELETE FROM harvest_record;
DELETE FROM revenue_record;
DELETE FROM cost_record;
DELETE FROM production_plan;
DELETE FROM disease_record;
DELETE FROM feeding_record;
DELETE FROM seedling_record;
DELETE FROM water_quality;
DELETE FROM water_quality_simulation;
DELETE FROM water_quality_threshold;
DELETE FROM smart_reminder;
DELETE FROM notification;
DELETE FROM knowledge_base;
DELETE FROM medicine_inventory;
DELETE FROM seedling_inventory;
DELETE FROM feed_inventory;
DELETE FROM system_config;
DELETE FROM aquaculture_zone;
DELETE FROM sys_user;

-- ============================================================
-- 1. 用户 (sys_user) — 13人，覆盖全部4种角色
-- ============================================================
INSERT INTO sys_user (username, password, real_name, phone, email, role, enabled, avatar, create_time, update_time) VALUES
('superadmin',  '123456', '林志祥',   '13800000000', 'superadmin@marine.com', 'SUPER_ADMIN', 1, NULL, datetime('now'), datetime('now')),
('admin01',     '123456', '陈总工',   '13800000001', 'chenzg@marine.com',    'SUPER_ADMIN', 1, NULL, datetime('now'), datetime('now')),
('operator01',  '123456', '王运维',   '13800000002', 'wangyw@marine.com',    'OPERATOR',    1, NULL, datetime('now'), datetime('now')),
('operator02',  '123456', '刘技术',   '13800000003', 'liujs@marine.com',     'OPERATOR',    1, NULL, datetime('now'), datetime('now')),
('operator03',  '123456', '周系统',   '13800000004', 'zhouxt@marine.com',    'OPERATOR',    1, NULL, datetime('now'), datetime('now')),
('manager01',   '123456', '李管理',   '13800000005', 'ligl@marine.com',      'MANAGER',     1, NULL, datetime('now'), datetime('now')),
('manager02',   '123456', '赵经理',   '13800000006', 'zhaojl@marine.com',    'MANAGER',     1, NULL, datetime('now'), datetime('now')),
('manager03',   '123456', '陈主管',   '13800000007', 'chenzg2@marine.com',   'MANAGER',     1, NULL, datetime('now'), datetime('now')),
('breeder01',   '123456', '张养殖',   '13800000008', 'zhangyz@marine.com',   'BREEDER',     1, NULL, datetime('now'), datetime('now')),
('breeder02',   '123456', '李师傅',   '13800000009', 'lisf@marine.com',      'BREEDER',     1, NULL, datetime('now'), datetime('now')),
('breeder03',   '123456', '王养殖',   '13800000010', 'wangyz2@marine.com',   'BREEDER',     1, NULL, datetime('now'), datetime('now')),
('breeder04',   '123456', '黄员',     '13800000011', 'huangy@marine.com',    'BREEDER',     1, NULL, datetime('now'), datetime('now')),
('breeder05',   '123456', '林师傅',   '13800000012', 'linsf@marine.com',     'BREEDER',     1, NULL, datetime('now'), datetime('now'));

-- ============================================================
-- 2. 养殖区域 (aquaculture_zone) — 5个主区 + 子区域
-- ============================================================
INSERT INTO aquaculture_zone (name, location, area, species, description, parent_id, create_time, update_time) VALUES
('A区-南美白对虾池',   '东养殖场1号', 2500.00, '南美白对虾', '标准化高密度精养池，配备底部增氧系统',          NULL, datetime('now'), datetime('now')),
('B区-石斑鱼养殖区',   '东养殖场2号', 1800.00, '石斑鱼',     '深水网箱+水泥池混养模式',                        NULL, datetime('now'), datetime('now')),
('C区-海鲈鱼场',       '西养殖场1号', 2000.00, '海鲈鱼',     '跑道式循环水养殖系统',                            NULL, datetime('now'), datetime('now')),
('D区-鲍鱼养殖区',     '西养殖场2号', 1200.00, '鲍鱼',       '工厂化立体养殖，恒温控制',                        NULL, datetime('now'), datetime('now')),
('E区-大闸蟹区',       '南养殖场1号', 3000.00, '大闸蟹',     '生态养殖模式，水草覆盖率>60%',                    NULL, datetime('now'), datetime('now')),
('A1-虾苗标粗池',      '东养殖场1号',  400.00, '南美白对虾', 'A区附属标粗池，用于虾苗暂养过渡',                  1, datetime('now'), datetime('now')),
('B1-石斑鱼育苗池',    '东养殖场2号',  300.00, '石斑鱼',     'B区附属育苗池',                                   2, datetime('now'), datetime('now'));

-- ============================================================
-- 3. 水质阈值配置 (water_quality_threshold) — 每区一条，zone_id UNIQUE
-- ============================================================
-- A区南美白对虾: 温度22-32, DO≥5, pH7.2-8.8, 盐度15-32
-- B区石斑鱼:     温度22-30, DO≥5, pH7.5-8.5, 盐度25-35
-- C区海鲈鱼:     温度16-28, DO≥5, pH7.0-8.5, 盐度10-30
-- D区鲍鱼:       温度14-22, DO≥6, pH7.8-8.4, 盐度28-35
-- E区大闸蟹:     温度18-30, DO≥4, pH7.5-8.5, 盐度0-15
INSERT INTO water_quality_threshold (zone_id, temp_min, temp_max, do_min, do_max, ph_min, ph_max, salinity_min, salinity_max, create_time, update_time) VALUES
(1, 22.00, 32.00, 5.00, 12.00, 7.20, 8.80, 15.00, 32.00, datetime('now'), datetime('now')),
(2, 22.00, 30.00, 5.00, 12.00, 7.50, 8.50, 25.00, 35.00, datetime('now'), datetime('now')),
(3, 16.00, 28.00, 5.00, 12.00, 7.00, 8.50, 10.00, 30.00, datetime('now'), datetime('now')),
(4, 14.00, 22.00, 6.00, 12.00, 7.80, 8.40, 28.00, 35.00, datetime('now'), datetime('now')),
(5, 18.00, 30.00, 4.00, 10.00, 7.50, 8.50,  0.00, 15.00, datetime('now'), datetime('now'));

-- ============================================================
-- 4. 水质模拟配置 (water_quality_simulation) — 每区一条，zone_id UNIQUE
-- ============================================================
INSERT INTO water_quality_simulation (zone_id, temp_min, temp_max, do_min, do_max, ph_min, ph_max, salinity_min, salinity_max) VALUES
(1, 20.00, 35.00, 4.00, 14.00, 6.80, 9.20, 10.00, 35.00),
(2, 20.00, 32.00, 4.00, 14.00, 7.00, 9.00, 22.00, 38.00),
(3, 14.00, 30.00, 4.00, 14.00, 6.50, 9.00,  5.00, 33.00),
(4, 12.00, 24.00, 5.00, 14.00, 7.40, 8.80, 25.00, 38.00),
(5, 15.00, 33.00, 3.00, 12.00, 7.00, 9.00,  0.00, 18.00);

-- ============================================================
-- 5. 苗种投放记录 (seedling_record)
--    注意: release_date 使用 date() 而非 datetime()
-- ============================================================
INSERT INTO seedling_record (zone_id, species, batch_no, quantity, source, supplier, release_date, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260423A1', 80000, '湛江国家级虾苗场',   '湛江海洋种苗有限公司',     date('now', '-25 days'), '第一批虾苗，规格0.8-1.0cm',       datetime('now', '-25 days'), datetime('now', '-25 days')),
(1, '南美白对虾', 'B20260508A2', 40000, '湛江国家级虾苗场',   '湛江海洋种苗有限公司',     date('now', '-10 days'), '第二批补苗，规格1.0-1.2cm',       datetime('now', '-10 days'), datetime('now', '-10 days')),
(2, '石斑鱼',     'B20260418B1',  6000, '海南石斑鱼育苗基地', '海南深海养殖集团',         date('now', '-30 days'), '珍珠龙胆石斑鱼苗，5-8cm',         datetime('now', '-30 days'), datetime('now', '-30 days')),
(3, '海鲈鱼',     'B20260420C1', 10000, '珠海渔苗中心',       '珠海现代渔业有限公司',     date('now', '-28 days'), '优质花鲈鱼苗，3-5cm',             datetime('now', '-28 days'), datetime('now', '-28 days')),
(4, '鲍鱼',       'B20260428D1',  3000, '大连鲍鱼原种场',     '大连海洋珍品科技有限公司', date('now', '-20 days'), '皱纹盘鲍苗，壳长1.5-2cm',         datetime('now', '-20 days'), datetime('now', '-20 days')),
(5, '大闸蟹',     'B20260426E1',  5000, '阳澄湖蟹苗基地',     '苏州阳澄湖蟹业有限公司',   date('now', '-22 days'), '长江系大闸蟹苗，扣蟹规格',        datetime('now', '-22 days'), datetime('now', '-22 days')),
(2, '石斑鱼',     'B20260510B2',  3000, '海南石斑鱼育苗基地', '海南深海养殖集团',         date('now', '-8 days'),  '第二批珍珠龙胆苗，8-10cm',        datetime('now', '-8 days'),  datetime('now', '-8 days'));

-- ============================================================
-- 6. 饲料投喂记录 (feeding_record)
--    feeding_time 使用合法 SQLite datetime 修饰符
-- ============================================================
-- A区-南美白对虾 投喂 (每天2次: 07:00 + 17:00)
INSERT INTO feeding_record (zone_id, species, batch_no, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 55, datetime('now', '-24 days', 'start of day', '+7 hours'),  '投苗后第二天早晨',    datetime('now', '-24 days'), datetime('now', '-24 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 65, datetime('now', '-24 days', 'start of day', '+17 hours'), '傍晚正常投喂',        datetime('now', '-24 days'), datetime('now', '-24 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 58, datetime('now', '-23 days', 'start of day', '+7 hours'),  '虾苗活力好',          datetime('now', '-23 days'), datetime('now', '-23 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 68, datetime('now', '-23 days', 'start of day', '+17 hours'), '适量增加投喂',        datetime('now', '-23 days'), datetime('now', '-23 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 62, datetime('now', '-22 days', 'start of day', '+7 hours'),  '早晨水质良好',        datetime('now', '-22 days'), datetime('now', '-22 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 72, datetime('now', '-22 days', 'start of day', '+17 hours'), '',                     datetime('now', '-22 days'), datetime('now', '-22 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 65, datetime('now', '-20 days', 'start of day', '+7 hours'),  '虾体长势明显',        datetime('now', '-20 days'), datetime('now', '-20 days')),
(1, '南美白对虾', 'B20260423A1', '南美白对虾配合饲料', 75, datetime('now', '-20 days', 'start of day', '+17 hours'), '投喂量上调',          datetime('now', '-20 days'), datetime('now', '-20 days')),
(1, '南美白对虾', 'B20260508A2', '南美白对虾配合饲料', 30, datetime('now', '-9 days',  'start of day', '+7 hours'),  '第二批虾苗投喂首日',  datetime('now', '-9 days'),  datetime('now', '-9 days')),
(1, '南美白对虾', 'B20260508A2', '南美白对虾配合饲料', 35, datetime('now', '-8 days',  'start of day', '+7 hours'),  '新苗适应良好',        datetime('now', '-8 days'),  datetime('now', '-8 days')),
(1, '南美白对虾', 'B20260508A2', '南美白对虾配合饲料', 40, datetime('now', '-7 days',  'start of day', '+7 hours'),  '增加投喂量',          datetime('now', '-7 days'),  datetime('now', '-7 days'));

-- B区-石斑鱼 投喂 (每天1次: 09:00)
INSERT INTO feeding_record (zone_id, species, batch_no, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 35, datetime('now', '-29 days', 'start of day', '+9 hours'), '投苗后首餐',        datetime('now', '-29 days'), datetime('now', '-29 days')),
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 38, datetime('now', '-28 days', 'start of day', '+9 hours'), '',                   datetime('now', '-28 days'), datetime('now', '-28 days')),
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 42, datetime('now', '-27 days', 'start of day', '+9 hours'), '鱼群活跃抢食',      datetime('now', '-27 days'), datetime('now', '-27 days')),
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 45, datetime('now', '-25 days', 'start of day', '+9 hours'), '',                   datetime('now', '-25 days'), datetime('now', '-25 days')),
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 48, datetime('now', '-23 days', 'start of day', '+9 hours'), '鱼体生长迅速',      datetime('now', '-23 days'), datetime('now', '-23 days')),
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 50, datetime('now', '-21 days', 'start of day', '+9 hours'), '',                   datetime('now', '-21 days'), datetime('now', '-21 days')),
(2, '石斑鱼', 'B20260418B1', '石斑鱼膨化饲料', 52, datetime('now', '-18 days', 'start of day', '+9 hours'), '病愈后恢复投喂',    datetime('now', '-18 days'), datetime('now', '-18 days'));

-- C区-海鲈鱼 投喂 (每天1次: 08:00)
INSERT INTO feeding_record (zone_id, species, batch_no, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(3, '海鲈鱼', 'B20260420C1', '海鲈鱼专用饲料', 45, datetime('now', '-27 days', 'start of day', '+8 hours'), '投苗后第一餐',        datetime('now', '-27 days'), datetime('now', '-27 days')),
(3, '海鲈鱼', 'B20260420C1', '海鲈鱼专用饲料', 48, datetime('now', '-26 days', 'start of day', '+8 hours'), '精养饲料',            datetime('now', '-26 days'), datetime('now', '-26 days')),
(3, '海鲈鱼', 'B20260420C1', '海鲈鱼专用饲料', 52, datetime('now', '-24 days', 'start of day', '+8 hours'), '鱼苗进食良好',        datetime('now', '-24 days'), datetime('now', '-24 days')),
(3, '海鲈鱼', 'B20260420C1', '海鲈鱼专用饲料', 55, datetime('now', '-22 days', 'start of day', '+8 hours'), '',                     datetime('now', '-22 days'), datetime('now', '-22 days')),
(3, '海鲈鱼', 'B20260420C1', '海鲈鱼专用饲料', 58, datetime('now', '-20 days', 'start of day', '+8 hours'), '鱼群生长迅速',        datetime('now', '-20 days'), datetime('now', '-20 days'));

-- D区-鲍鱼 投喂 (每2天1次: 10:00)
INSERT INTO feeding_record (zone_id, species, batch_no, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(4, '鲍鱼', 'B20260428D1', '鲍鱼专用配合饲料', 15, datetime('now', '-19 days', 'start of day', '+10 hours'), '海藻混合饲料',      datetime('now', '-19 days'), datetime('now', '-19 days')),
(4, '鲍鱼', 'B20260428D1', '鲍鱼专用配合饲料', 17, datetime('now', '-17 days', 'start of day', '+10 hours'), '',                   datetime('now', '-17 days'), datetime('now', '-17 days')),
(4, '鲍鱼', 'B20260428D1', '鲍鱼专用配合饲料', 20, datetime('now', '-15 days', 'start of day', '+10 hours'), '鲍鱼进食正常',      datetime('now', '-15 days'), datetime('now', '-15 days')),
(4, '鲍鱼', 'B20260428D1', '鲍鱼专用配合饲料', 22, datetime('now', '-13 days', 'start of day', '+10 hours'), '',                   datetime('now', '-13 days'), datetime('now', '-13 days')),
(4, '鲍鱼', 'B20260428D1', '鲍鱼专用配合饲料', 24, datetime('now', '-11 days', 'start of day', '+10 hours'), '增加投喂量',        datetime('now', '-11 days'), datetime('now', '-11 days'));

-- E区-大闸蟹 投喂 (每天1次傍晚: 17:00)
INSERT INTO feeding_record (zone_id, species, batch_no, feed_type, quantity, feeding_time, remarks, create_time, update_time) VALUES
(5, '大闸蟹', 'B20260426E1', '大闸蟹专用饲料', 22, datetime('now', '-21 days', 'start of day', '+17 hours'), '投苗后第一餐',      datetime('now', '-21 days'), datetime('now', '-21 days')),
(5, '大闸蟹', 'B20260426E1', '大闸蟹专用饲料', 25, datetime('now', '-20 days', 'start of day', '+17 hours'), '配合饲料+螺蛳',     datetime('now', '-20 days'), datetime('now', '-20 days')),
(5, '大闸蟹', 'B20260426E1', '大闸蟹专用饲料', 28, datetime('now', '-19 days', 'start of day', '+17 hours'), '蟹苗状态良好',      datetime('now', '-19 days'), datetime('now', '-19 days')),
(5, '大闸蟹', 'B20260426E1', '大闸蟹专用饲料', 30, datetime('now', '-18 days', 'start of day', '+17 hours'), '',                   datetime('now', '-18 days'), datetime('now', '-18 days')),
(5, '大闸蟹', 'B20260426E1', '大闸蟹专用饲料', 32, datetime('now', '-16 days', 'start of day', '+17 hours'), '螃蟹活跃觅食',      datetime('now', '-16 days'), datetime('now', '-16 days')),
(5, '大闸蟹', 'B20260426E1', '大闸蟹专用饲料', 35, datetime('now', '-14 days', 'start of day', '+17 hours'), '',                   datetime('now', '-14 days'), datetime('now', '-14 days'));

-- ============================================================
-- 7. 水质监测数据 (water_quality) — 65条，覆盖全部5区，含异常记录
-- ============================================================
-- A区-南美白对虾 (最近20天，每天3次: 06:00, 12:00, 18:00)
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(1, 25.2, 7.8, 7.75, 24.5, 0, NULL, datetime('now', '-20 days', 'start of day', '+6 hours')),
(1, 27.5, 7.4, 7.90, 25.2, 0, NULL, datetime('now', '-20 days', 'start of day', '+12 hours')),
(1, 26.8, 7.6, 7.82, 24.9, 0, NULL, datetime('now', '-20 days', 'start of day', '+18 hours')),
(1, 25.8, 7.7, 7.80, 24.8, 0, NULL, datetime('now', '-18 days', 'start of day', '+6 hours')),
(1, 28.0, 7.2, 7.95, 25.5, 0, NULL, datetime('now', '-18 days', 'start of day', '+12 hours')),
(1, 27.2, 7.5, 7.85, 25.1, 0, NULL, datetime('now', '-18 days', 'start of day', '+18 hours')),
(1, 26.2, 7.6, 7.78, 25.0, 0, NULL, datetime('now', '-15 days', 'start of day', '+6 hours')),
(1, 28.5, 7.0, 8.02, 26.0, 0, NULL, datetime('now', '-15 days', 'start of day', '+12 hours')),
(1, 27.5, 7.3, 7.88, 25.6, 0, NULL, datetime('now', '-15 days', 'start of day', '+18 hours')),
(1, 26.5, 7.5, 7.82, 25.3, 0, NULL, datetime('now', '-12 days', 'start of day', '+6 hours')),
(1, 28.2, 7.1, 7.95, 25.9, 0, NULL, datetime('now', '-12 days', 'start of day', '+12 hours')),
(1, 27.3, 7.6, 7.86, 25.5, 0, NULL, datetime('now', '-12 days', 'start of day', '+18 hours')),
(1, 26.8, 7.5, 7.84, 25.7, 0, NULL, datetime('now', '-8 days', 'start of day', '+6 hours')),
(1, 28.8, 6.5, 8.08, 26.5, 1, '溶解氧偏低，需要增氧', datetime('now', '-8 days', 'start of day', '+12 hours')),
(1, 27.8, 7.0, 7.92, 26.0, 0, NULL, datetime('now', '-8 days', 'start of day', '+18 hours')),
(1, 27.0, 7.4, 7.88, 26.0, 0, NULL, datetime('now', '-3 days', 'start of day', '+6 hours')),
(1, 28.5, 7.2, 7.95, 26.3, 0, NULL, datetime('now', '-3 days', 'start of day', '+12 hours')),
(1, 27.5, 7.5, 7.90, 26.1, 0, NULL, datetime('now', '-3 days', 'start of day', '+18 hours'));

-- B区-石斑鱼 (最近18天，每天2次: 08:00, 14:00)
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(2, 24.5, 7.5, 7.75, 28.0, 0, NULL, datetime('now', '-18 days', 'start of day', '+8 hours')),
(2, 25.8, 7.2, 7.85, 28.8, 0, NULL, datetime('now', '-18 days', 'start of day', '+14 hours')),
(2, 24.8, 7.4, 7.78, 28.2, 0, NULL, datetime('now', '-14 days', 'start of day', '+8 hours')),
(2, 26.2, 7.0, 7.90, 29.0, 0, NULL, datetime('now', '-14 days', 'start of day', '+14 hours')),
(2, 25.0, 7.5, 7.80, 28.5, 0, NULL, datetime('now', '-10 days', 'start of day', '+8 hours')),
(2, 26.5, 6.9, 7.92, 29.5, 0, NULL, datetime('now', '-10 days', 'start of day', '+14 hours')),
(2, 25.5, 7.3, 7.82, 28.8, 0, NULL, datetime('now', '-6 days', 'start of day', '+8 hours')),
(2, 26.8, 6.8, 7.95, 29.8, 0, NULL, datetime('now', '-6 days', 'start of day', '+14 hours')),
(2, 25.5, 7.2, 7.82, 29.2, 0, NULL, datetime('now', '-2 days', 'start of day', '+8 hours')),
(2, 26.5, 7.0, 7.90, 30.0, 0, NULL, datetime('now', '-2 days', 'start of day', '+14 hours'));

-- C区-海鲈鱼 (最近15天，每天2次: 07:00, 15:00)
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(3, 21.5, 7.8, 7.70, 20.5, 0, NULL, datetime('now', '-15 days', 'start of day', '+7 hours')),
(3, 23.2, 7.5, 7.82, 21.2, 0, NULL, datetime('now', '-15 days', 'start of day', '+15 hours')),
(3, 22.0, 7.6, 7.75, 20.8, 0, NULL, datetime('now', '-11 days', 'start of day', '+7 hours')),
(3, 23.8, 7.2, 7.88, 21.6, 0, NULL, datetime('now', '-11 days', 'start of day', '+15 hours')),
(3, 22.5, 7.5, 7.80, 21.5, 0, NULL, datetime('now', '-7 days', 'start of day', '+7 hours')),
(3, 24.0, 7.1, 7.90, 22.0, 0, NULL, datetime('now', '-7 days', 'start of day', '+15 hours')),
(3, 22.8, 7.5, 7.82, 21.8, 0, NULL, datetime('now', '-3 days', 'start of day', '+7 hours')),
(3, 24.2, 7.0, 7.90, 22.3, 0, NULL, datetime('now', '-3 days', 'start of day', '+15 hours'));

-- D区-鲍鱼 (最近12天，每天2次: 09:00, 15:00)
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(4, 15.8, 8.2, 7.95, 29.5, 0, NULL, datetime('now', '-12 days', 'start of day', '+9 hours')),
(4, 16.8, 8.0, 8.02, 30.0, 0, NULL, datetime('now', '-12 days', 'start of day', '+15 hours')),
(4, 16.2, 8.1, 7.98, 29.8, 0, NULL, datetime('now', '-8 days', 'start of day', '+9 hours')),
(4, 17.2, 7.8, 8.05, 30.5, 0, NULL, datetime('now', '-8 days', 'start of day', '+15 hours')),
(4, 16.5, 7.9, 7.96, 30.0, 0, NULL, datetime('now', '-4 days', 'start of day', '+9 hours')),
(4, 17.5, 7.7, 8.08, 30.8, 0, NULL, datetime('now', '-4 days', 'start of day', '+15 hours'));

-- E区-大闸蟹 (最近14天，每天2次: 08:00, 16:00)
INSERT INTO water_quality (zone_id, temperature, dissolved_oxygen, ph, salinity, is_warning, warning_info, record_time) VALUES
(5, 22.5, 6.2, 7.85, 7.5, 0, NULL, datetime('now', '-14 days', 'start of day', '+8 hours')),
(5, 24.0, 5.8, 7.95, 8.0, 0, NULL, datetime('now', '-14 days', 'start of day', '+16 hours')),
(5, 23.0, 6.0, 7.90, 7.8, 0, NULL, datetime('now', '-10 days', 'start of day', '+8 hours')),
(5, 25.2, 5.2, 8.05, 9.0, 1, '溶解氧偏低，检查增氧设备', datetime('now', '-10 days', 'start of day', '+16 hours')),
(5, 23.5, 5.8, 7.88, 8.5, 0, NULL, datetime('now', '-6 days', 'start of day', '+8 hours')),
(5, 24.8, 5.5, 7.98, 8.8, 0, NULL, datetime('now', '-6 days', 'start of day', '+16 hours')),
(5, 24.0, 5.8, 8.02, 8.6, 0, NULL, datetime('now', '-2 days', 'start of day', '+8 hours')),
(5, 25.0, 5.5, 8.05, 8.9, 0, NULL, datetime('now', '-2 days', 'start of day', '+16 hours')),
(5, 24.2, 5.8, 8.00, 8.7, 0, NULL, datetime('now', '-1 days', 'start of day', '+8 hours')),
(5, 25.2, 5.5, 8.05, 9.0, 0, NULL, datetime('now', '-1 days', 'start of day', '+16 hours'));

-- ============================================================
-- 8. 疾病记录 (disease_record)
--    status: 'ACTIVE' / 'RECOVERED'
-- ============================================================
INSERT INTO disease_record (zone_id, species, batch_no, disease_name, occurrence_date, symptoms, treatment, status, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260423A1', '白斑病',   date('now', '-22 days'), '虾体出现白色斑点，活力下降，食欲减少',         '聚维酮碘全池泼洒0.3ppm，加强底部增氧',       'RECOVERED', datetime('now', '-22 days'), datetime('now', '-12 days')),
(1, '南美白对虾', 'B20260423A1', '肠炎病',   date('now', '-15 days'), '肠道发红，粪便不成形，摄食减少',               '大蒜素拌料投喂(0.1%)，连续5天，换水20%',      'RECOVERED', datetime('now', '-15 days'), datetime('now', '-5 days')),
(1, '南美白对虾', 'B20260508A2', '弧菌病',   date('now', '-3 days'),  '虾体发红，活力差，部分虾靠边',                  '二氧化氯0.5ppm消毒，氟苯尼考拌料，加强换水',   'ACTIVE',    datetime('now', '-3 days'),  datetime('now', '-3 days')),
(2, '石斑鱼',     'B20260418B1', '弧菌病',   date('now', '-20 days'), '体表溃疡，鳍条基部出血，游动迟缓',             '氟苯尼考拌料投喂5天，20mg/kg鱼体重',          'RECOVERED', datetime('now', '-20 days'), datetime('now', '-10 days')),
(2, '石斑鱼',     'B20260418B1', '小瓜虫病', date('now', '-13 days'), '体表出现白色小点，鱼摩擦池壁，食欲下降',       '提高水温至28℃，硫酸铜0.5ppm全池泼洒',        'RECOVERED', datetime('now', '-13 days'), datetime('now', '-3 days')),
(2, '石斑鱼',     'B20260510B2', '烂尾病',   date('now', '-2 days'),  '尾鳍腐烂，边缘发白，鱼游动困难',               '碘制剂消毒，维生素C拌料增强体质',              'ACTIVE',    datetime('now', '-2 days'),  datetime('now', '-2 days')),
(3, '海鲈鱼',     'B20260420C1', '烂鳃病',   date('now', '-5 days'),  '鳃丝腐烂发白，呼吸困难，体色发黑',             '二氧化氯消毒，恩诺沙星拌料投喂',               'ACTIVE',    datetime('now', '-5 days'),  datetime('now', '-5 days')),
(4, '鲍鱼',       'B20260428D1', '气泡病',   date('now', '-10 days'), '外套膜出现气泡，附着力下降，部分脱落',         '加强换水，稳定水质，降低光照强度',             'RECOVERED', datetime('now', '-10 days'), datetime('now', '-3 days')),
(5, '大闸蟹',     'B20260426E1', '黑鳃病',   date('now', '-4 days'),  '鳃部发黑，行动迟缓，摄食减少',                  '改善底质，使用过硫酸氢钾改底，加强增氧',      'ACTIVE',    datetime('now', '-4 days'),  datetime('now', '-4 days'));

-- ============================================================
-- 9. 捕捞记录 (harvest_record)
-- ============================================================
INSERT INTO harvest_record (zone_id, species, batch_no, quantity, avg_weight, harvest_date, remarks, create_time, update_time) VALUES
(1, '南美白对虾', 'B20260423A1',  6000, 14.50, date('now', '-12 days'), '第一批次捕捞，规格14-16g，成活率75%',      datetime('now', '-12 days'), datetime('now', '-12 days')),
(1, '南美白对虾', 'B20260423A1',  8000, 18.50, date('now', '-6 days'),  '第二批次捕捞，规格18-20g',                 datetime('now', '-6 days'),  datetime('now', '-6 days')),
(1, '南美白对虾', 'B20260423A1', 12000, 22.50, date('now', '-1 days'),  '第三批次捕捞，规格22-25g，产量高峰期',     datetime('now', '-1 days'),  datetime('now', '-1 days')),
(2, '石斑鱼',     'B20260418B1',   600, 450.0, date('now', '-8 days'),  '第一批珍珠龙胆，均重450g/尾',             datetime('now', '-8 days'),  datetime('now', '-8 days')),
(2, '石斑鱼',     'B20260418B1',  1000, 600.0, date('now', '-1 days'),  '第二批珍珠龙胆，均重600g/尾，品质优良',   datetime('now', '-1 days'),  datetime('now', '-1 days')),
(3, '海鲈鱼',     'B20260420C1',  2500, 350.0, date('now', '-5 days'),  '第一批海鲈鱼，规格350-400g',              datetime('now', '-5 days'),  datetime('now', '-5 days')),
(4, '鲍鱼',       'B20260428D1',   500,  45.0, date('now', '-2 days'),  '第一批鲍鱼，8-10头/斤',                    datetime('now', '-2 days'),  datetime('now', '-2 days'));

-- ============================================================
-- 10. 生产计划 (production_plan) — 每区多条，覆盖全部状态
-- ============================================================
INSERT INTO production_plan (zone_id, zone_name, plan_name, species, batch_no, plan_type, start_date, end_date, target_quantity, target_size, status, description, create_time, update_time) VALUES
(1, 'A区-南美白对虾池', '2026年A区第一造虾',    '南美白对虾', 'PC202604A1', '养殖计划', date('now', '-25 days'), date('now', '+60 days'),  50000, '20g/尾',   '进行中', '标准化精养南美白对虾90天周期',             datetime('now'), datetime('now')),
(1, 'A区-南美白对虾池', '2026年A区第二造虾',    '南美白对虾', 'PC202606A2', '养殖计划', date('now', '+65 days'),  date('now', '+155 days'), 60000, '22g/尾',   '待开始', '第二造夏虾，利用高温季节快速生长',         datetime('now'), datetime('now')),
(2, 'B区-石斑鱼养殖区', '2026年B区石斑鱼',      '石斑鱼',     'PC202604B1', '养殖计划', date('now', '-30 days'), date('now', '+150 days'), 9000, '600g/尾',  '进行中', '珍珠龙胆石斑鱼养殖，目标单产提升15%',       datetime('now'), datetime('now')),
(2, 'B区-石斑鱼养殖区', '2026年B区东星斑试养',  '石斑鱼',     'PC202606B2', '试验计划', date('now', '+30 days'),  date('now', '+210 days'), 2000, '500g/尾',  '待开始', '东星斑新品种引进试养项目',                  datetime('now'), datetime('now')),
(3, 'C区-海鲈鱼场',     '2026年C区海鲈鱼',      '海鲈鱼',     'PC202604C1', '养殖计划', date('now', '-28 days'), date('now', '+120 days'),15000, '400g/尾',  '进行中', '海鲈鱼精养项目',                             datetime('now'), datetime('now')),
(3, 'C区-海鲈鱼场',     '2026冬季C区海鲈鱼',    '海鲈鱼',     'PC202609C2', '养殖计划', date('now', '+130 days'), date('now', '+250 days'),15000, '450g/尾',  '待开始', '冬造海鲈鱼，利用冬季市场空档',              datetime('now'), datetime('now')),
(4, 'D区-鲍鱼养殖区',   '2026年D区鲍鱼',        '鲍鱼',       'PC202604D1', '养殖计划', date('now', '-20 days'), date('now', '+240 days'), 3000, '8头/斤',   '进行中', '工厂化鲍鱼养殖，8个月出产',                 datetime('now'), datetime('now')),
(5, 'E区-大闸蟹区',     '2026年E区大闸蟹',      '大闸蟹',     'PC202604E1', '养殖计划', date('now', '-22 days'), date('now', '+180 days'), 5000, '4两/只',   '进行中', '生态大闸蟹精养，中秋节前上市',               datetime('now'), datetime('now')),
(1, 'A区-南美白对虾池', '2025年A区冬造虾总结',  '南美白对虾', 'PC202510A0', '养殖计划', date('now', '-200 days'),date('now', '-110 days'), 45000, '18g/尾',   '已完成', '2025年冬季养殖批次，已顺利收官',             datetime('now', '-200 days'), datetime('now', '-200 days')),
(3, 'C区-海鲈鱼场',     '2025年C区秋鲈鱼',      '海鲈鱼',     'PC202508C0', '养殖计划', date('now', '-280 days'),date('now', '-160 days'),12000, '380g/尾',  '已完成', '2025年秋季海鲈鱼养殖批次',                  datetime('now', '-280 days'), datetime('now', '-280 days')),
(5, 'E区-大闸蟹区',     '2025年E区试验塘',      '大闸蟹',     'PC202506E0', '试验计划', date('now', '-350 days'),date('now', '-170 days'), 2000, '3.5两/只', '已取消', '高密度试验取消，转为常规养殖',              datetime('now', '-350 days'), datetime('now', '-350 days'));

-- ============================================================
-- 11. 成本记录 (cost_record) — zone_id 可空，覆盖全部成本类型
-- ============================================================
INSERT INTO cost_record (zone_id, batch_no, cost_type, cost_name, amount, record_date, remarks, create_time, update_time) VALUES
(1, 'B20260423A1', '苗种成本', '南美白对虾苗',          9600, date('now', '-25 days'), '首批8万尾,0.12元/尾',                datetime('now'), datetime('now')),
(1, 'B20260423A1', '饲料成本', '南美白对虾配合饲料',    4500, date('now', '-15 days'), '25袋x180元',                          datetime('now'), datetime('now')),
(1, 'B20260423A1', '饲料成本', '南美白对虾开口料',      1200, date('now', '-25 days'), '虾苗前期专用开口料',                  datetime('now'), datetime('now')),
(1, 'B20260423A1', '药品成本', '消毒剂+抗生素',          2200, date('now', '-10 days'), '聚维酮碘+氟苯尼考',                    datetime('now'), datetime('now')),
(1, 'B20260423A1', '水电成本', 'A区增氧机电费',          1800, date('now', '-5 days'),  '增氧机24h运行电费',                    datetime('now'), datetime('now')),
(1, 'B20260508A2', '苗种成本', '南美白对虾苗(第二批)',  4800, date('now', '-10 days'), '补苗4万尾',                           datetime('now'), datetime('now')),
(1, 'B20260508A2', '药品成本', '弧菌病治疗药物',         1500, date('now', '-3 days'),  '氟苯尼考+VC',                          datetime('now'), datetime('now')),
(2, 'B20260418B1', '苗种成本', '珍珠龙胆石斑鱼苗',     15000, date('now', '-30 days'), '6000尾x2.5元',                         datetime('now'), datetime('now')),
(2, 'B20260418B1', '饲料成本', '石斑鱼膨化饲料',         4800, date('now', '-20 days'), '15袋x320元',                          datetime('now'), datetime('now')),
(2, 'B20260418B1', '饲料成本', '石斑鱼幼鱼饲料',         2200, date('now', '-15 days'), '高蛋白幼鱼料',                        datetime('now'), datetime('now')),
(2, 'B20260418B1', '药品成本', '小瓜虫病治疗',           3200, date('now', '-13 days'), '硫酸铜+升温电费',                      datetime('now'), datetime('now')),
(2, 'B20260510B2', '苗种成本', '石斑鱼苗(第二批)',       7500, date('now', '-8 days'),  '3000尾x2.5元',                         datetime('now'), datetime('now')),
(3, 'B20260420C1', '苗种成本', '海鲈鱼苗',              18000, date('now', '-28 days'), '10000尾x1.8元',                        datetime('now'), datetime('now')),
(3, 'B20260420C1', '饲料成本', '海鲈鱼专用饲料',         4400, date('now', '-18 days'), '20袋x220元',                          datetime('now'), datetime('now')),
(3, 'B20260420C1', '药品成本', '烂鳃病治疗',             2600, date('now', '-5 days'),  '恩诺沙星+消毒剂',                      datetime('now'), datetime('now')),
(3, 'B20260420C1', '设备成本', '循环水系统维保',         3500, date('now', '-10 days'), '过滤系统更换滤芯',                     datetime('now'), datetime('now')),
(4, 'B20260428D1', '苗种成本', '鲍鱼苗',                 8400, date('now', '-20 days'), '3000粒x2.8元',                         datetime('now'), datetime('now')),
(4, 'B20260428D1', '饲料成本', '鲍鱼专用配合饲料',       2200, date('now', '-20 days'), '含海藻粉',                             datetime('now'), datetime('now')),
(4, 'B20260428D1', '水电成本', '恒温系统电费',           2800, date('now', '-5 days'),  '工厂化恒温养殖电费',                    datetime('now'), datetime('now')),
(5, 'B20260426E1', '苗种成本', '大闸蟹扣蟹',             7500, date('now', '-22 days'), '5000只x1.5元',                         datetime('now'), datetime('now')),
(5, 'B20260426E1', '饲料成本', '大闸蟹专用饲料',         2400, date('now', '-18 days'), '配合饲料+螺蛳',                        datetime('now'), datetime('now')),
(5, 'B20260426E1', '药品成本', '黑鳃病改底',             1800, date('now', '-4 days'),  '过硫酸氢钾+EM菌',                      datetime('now'), datetime('now')),
(NULL, NULL,       '水电成本', '全场水电费',             2500, date('now', '-1 months'),'上月全场水电费',                       datetime('now'), datetime('now')),
(NULL, NULL,       '人工成本', '养殖工人工资',          28000, date('now', '-1 months'),'10人月工资',                           datetime('now'), datetime('now')),
(NULL, NULL,       '人工成本', '技术管理人员工资',      15000, date('now', '-1 months'),'技术+管理人员3人',                     datetime('now'), datetime('now')),
(NULL, NULL,       '设备成本', '增氧机采购',            12000, date('now', '-40 days'), '新增2台叶轮式增氧机',                   datetime('now'), datetime('now')),
(NULL, NULL,       '其他成本', '水质检测试剂',           3500, date('now', '-20 days'), '溶解氧+pH+氨氮检测试剂套装',           datetime('now'), datetime('now'));

-- ============================================================
-- 12. 收益记录 (revenue_record)
-- ============================================================
INSERT INTO revenue_record (zone_id, batch_no, revenue_type, revenue_name, amount, record_date, remarks, create_time, update_time) VALUES
(1, 'B20260423A1', '销售收入', '南美白对虾-第一批',   54000, date('now', '-12 days'), '6000斤x9元/斤',     datetime('now'), datetime('now')),
(1, 'B20260423A1', '销售收入', '南美白对虾-第二批',   96000, date('now', '-6 days'),  '8000斤x12元/斤',    datetime('now'), datetime('now')),
(1, 'B20260423A1', '销售收入', '南美白对虾-第三批',  180000, date('now', '-1 days'),  '12000斤x15元/斤',   datetime('now'), datetime('now')),
(2, 'B20260418B1', '销售收入', '石斑鱼-第一批',      108000, date('now', '-8 days'),  '600斤x180元/斤',    datetime('now'), datetime('now')),
(2, 'B20260418B1', '销售收入', '石斑鱼-第二批',      300000, date('now', '-1 days'),  '1000斤x300元/斤',   datetime('now'), datetime('now')),
(3, 'B20260420C1', '销售收入', '海鲈鱼-第一批',       52500, date('now', '-5 days'),  '2500斤x21元/斤',    datetime('now'), datetime('now')),
(4, 'B20260428D1', '销售收入', '鲍鱼-第一批',          90000, date('now', '-2 days'),  '500斤x180元/斤',    datetime('now'), datetime('now'));

-- ============================================================
-- 13. 销售记录 (sales_record) — 依赖 harvest_record.id
--     order_no UNIQUE, harvest_id 可空，覆盖全部状态
-- ============================================================
INSERT INTO sales_record (order_no, zone_id, harvest_id, product_name, species, quantity, unit, unit_price, total_amount, customer_name, customer_phone, sale_date, payment_method, status, remarks, create_time, update_time) VALUES
('SO20260506001', 1, 1, '南美白对虾', '南美白对虾', 6000, '斤',  9.00,  54000, '广州水产批发市场',   '13900001111', date('now', '-12 days'), 'BANK',   '已完成', '第一批销售',          datetime('now'), datetime('now')),
('SO20260512002', 1, 2, '南美白对虾', '南美白对虾', 8000, '斤', 12.00,  96000, '深圳海产贸易公司',   '13900002222', date('now', '-6 days'),  'WECHAT', '已完成', '第二批销售',          datetime('now'), datetime('now')),
('SO20260517003', 1, 3, '南美白对虾', '南美白对虾',12000, '斤', 15.00, 180000, '东莞食品加工厂',     '13900003333', date('now', '-1 days'),  'ALIPAY', '已完成', '第三批大单',          datetime('now'), datetime('now')),
('SO20260510004', 2, 4, '珍珠龙胆石斑鱼','石斑鱼',  600, '斤', 180.0, 108000, '广州海鲜酒楼',       '13900004444', date('now', '-8 days'),  'BANK',   '已完成', '石斑鱼高档订单',      datetime('now'), datetime('now')),
('SO20260517005', 2, 5, '珍珠龙胆石斑鱼','石斑鱼', 1000, '斤', 300.0, 300000, '深圳高端日料连锁',   '13900005555', date('now', '-1 days'),  'WECHAT', '已完成', '高端日料专供',        datetime('now'), datetime('now')),
('SO20260513006', 3, 6, '花鲈鱼',       '海鲈鱼', 2500, '斤',  21.0,  52500, '厦门水产批发商',     '13900006666', date('now', '-5 days'),  'BANK',   '已完成', '海鲈鱼批发',          datetime('now'), datetime('now')),
('SO20260516007', 4, 7, '皱纹盘鲍',     '鲍鱼',    500, '斤', 180.0,  90000, '大连海珍品经销商',   '13900007777', date('now', '-2 days'),  'ALIPAY', '已完成', '鲍鱼精品订单',        datetime('now'), datetime('now')),
('SO20260518008', 1, NULL, '南美白对虾', '南美白对虾', 3000, '斤', 16.00, 48000, '上海海鲜批发商',     '13900008888', date('now', '+1 days'),  'BANK',   '待发货', '第四批预售订单',      datetime('now'), datetime('now')),
('SO20260520009', 2, NULL, '珍珠龙胆石斑鱼','石斑鱼',  400, '斤', 320.0, 128000, '北京高端餐饮集团',   '13900009999', date('now', '+3 days'),  'WECHAT', '待发货', '北京专供预售',        datetime('now'), datetime('now')),
('SO20260515010', 3, NULL, '花鲈鱼',       '海鲈鱼', 1500, '斤',  22.0,  33000, '福州鱼丸加工厂',     '13900010000', date('now', '-3 days'),  'ALIPAY', '已发货', '物流中，预计2日到',  datetime('now'), datetime('now')),
('SO20260508011', 5, NULL, '大闸蟹礼盒',   '大闸蟹',  200, '盒', 288.0,  57600, '上海蟹都汇专卖店',   '13900011111', date('now', '+90 days'), 'BANK',   '待发货', '中秋节礼盒预售',      datetime('now'), datetime('now'));

-- ============================================================
-- 14. 溯源码 (traceability_code) — code UNIQUE
-- ============================================================
INSERT INTO traceability_code (code, harvest_id, batch_no, zone_id, qr_code_url, description, scan_count, create_time, update_time) VALUES
('TRC202605060001', 1, 'B20260423A1', 1, NULL, '南美白对虾-第一批-14-16g',    45, datetime('now', '-12 days'), datetime('now', '-12 days')),
('TRC202605120002', 2, 'B20260423A1', 1, NULL, '南美白对虾-第二批-18-20g',    32, datetime('now', '-6 days'),  datetime('now', '-6 days')),
('TRC202605170003', 3, 'B20260423A1', 1, NULL, '南美白对虾-第三批-22-25g',    18, datetime('now', '-1 days'),  datetime('now', '-1 days')),
('TRC202605100004', 4, 'B20260418B1', 2, NULL, '石斑鱼-珍珠龙胆-450g',        28, datetime('now', '-8 days'),  datetime('now', '-8 days')),
('TRC202605170005', 5, 'B20260418B1', 2, NULL, '石斑鱼-珍珠龙胆-600g',        15, datetime('now', '-1 days'),  datetime('now', '-1 days')),
('TRC202605130006', 6, 'B20260420C1', 3, NULL, '海鲈鱼-花鲈-350-400g',        22, datetime('now', '-5 days'),  datetime('now', '-5 days')),
('TRC202605160007', 7, 'B20260428D1', 4, NULL, '鲍鱼-皱纹盘鲍-8-10头/斤',     10, datetime('now', '-2 days'),  datetime('now', '-2 days'));

-- ============================================================
-- 15. 饲料库存 (feed_inventory)
-- ============================================================
INSERT INTO feed_inventory (feed_name, brand, feed_type, specification, quantity, unit, supplier, zone_name, purchase_date, expiry_date, price, min_threshold, status, remarks, create_time, update_time) VALUES
('南美白对虾配合饲料', '海大', '对虾饲料', '40kg/袋', 25, 'kg', '湛江海大饲料有限公司',   'A区-南美白对虾养殖区', date('now', '-10 days'), date('now', '+180 days'), 4.50, 200, 'ACTIVE', '主要饲料，A区专用，占库存70%',       datetime('now'), datetime('now')),
('石斑鱼膨化饲料',     '粤海', '鱼类饲料', '20kg/袋', 15, 'kg', '广州粤海饲料有限公司',   'B区-石斑鱼养殖区',     date('now', '-8 days'),  date('now', '+150 days'), 6.40, 100, 'ACTIVE', '石斑鱼专用浮性饲料，B区专用',       datetime('now'), datetime('now')),
('海鲈鱼专用饲料',     '顺德', '鱼类饲料', '25kg/袋', 20, 'kg', '佛山顺德饲料厂',         'C区-海鲈鱼养殖区',     date('now', '-15 days'), date('now', '+120 days'), 4.40, 150, 'ACTIVE', '海鲈鱼精养料，C区专用',              datetime('now'), datetime('now')),
('鲍鱼配合饲料',       '大连', '贝类饲料', '10kg/袋',  8, 'kg', '大连水产饲料厂',         'D区-鲍鱼养殖区',       date('now', '-20 days'), date('now', '+180 days'), 5.60,  50, 'ACTIVE', '含海藻粉的专用饲料，D区专用',       datetime('now'), datetime('now')),
('大闸蟹专用饲料',     '天邦', '蟹类饲料', '15kg/袋', 12, 'kg', '苏州天邦饲料有限公司',   'E区-大闸蟹养殖区',     date('now', '-12 days'), date('now', '+90 days'),  3.50,  80, 'ACTIVE', '含螺蛳粉、玉米粉，E区专用',         datetime('now'), datetime('now')),
('鱼粉',               '秘鲁', '原料',     '50kg/袋',  6, 'kg', '秘鲁进口鱼粉代理商',     NULL,                    date('now', '-30 days'), date('now', '+365 days'), 9.60, 100, 'ACTIVE', '高档蛋白原料，通用备料',              datetime('now'), datetime('now')),
('南美白对虾开口料',   '通威', '对虾饲料', '20kg/袋', 10, 'kg', '通威饲料有限公司',       'A区-南美白对虾养殖区', date('now', '-5 days'),  date('now', '+120 days'), 6.00, 100, 'ACTIVE', '虾苗专用开口料，A区标粗池备用',     datetime('now'), datetime('now')),
('石斑鱼幼鱼饲料',     '恒兴', '鱼类饲料', '15kg/袋',  8, 'kg', '广东恒兴饲料有限公司',   'B区-石斑鱼养殖区',     date('now', '-3 days'),  date('now', '+150 days'), 5.50,  60, 'ACTIVE', '石斑鱼幼鱼期专用高蛋白饲料',        datetime('now'), datetime('now'));

-- ============================================================
-- 16. 苗种库存 (seedling_inventory)
-- ============================================================
INSERT INTO seedling_inventory (species, seedling_type, quantity, unit, supplier, zone_name, purchase_date, hatch_date, price, min_threshold, batch_no, status, remarks, create_time, update_time) VALUES
('南美白对虾', '虾苗', 180000, 'tail', '湛江国家级虾苗场',   'A区-南美白对虾养殖区', date('now', '-5 days'), date('now', '-8 days'), 0.12, 50000, 'XM20260513A1', 'ACTIVE', '备用虾苗，规格0.8-1.0cm',      datetime('now'), datetime('now')),
('石斑鱼',     '鱼苗',   8000, 'tail', '海南石斑鱼育苗基地', 'B区-石斑鱼养殖区',     date('now', '-3 days'), date('now', '-6 days'), 2.50,  3000, 'XM20260515B1', 'ACTIVE', '珍珠龙胆苗，5-8cm',           datetime('now'), datetime('now')),
('海鲈鱼',     '鱼苗',  12000, 'tail', '珠海渔苗中心',       'C区-海鲈鱼养殖区',     date('now', '-7 days'), date('now', '-10 days'),1.80,  5000, 'XM20260511C1', 'ACTIVE', '花鲈鱼苗，3-5cm',              datetime('now'), datetime('now')),
('鲍鱼',       '鲍苗',   5000, 'tail', '大连鲍鱼原种场',     'D区-鲍鱼养殖区',       date('now', '-2 days'), date('now', '-5 days'), 2.80,  2000, 'XM20260516D1', 'ACTIVE', '皱纹盘鲍苗，壳长1.5cm',       datetime('now'), datetime('now')),
('大闸蟹',     '扣蟹',   3000, 'tail', '阳澄湖蟹苗基地',     'E区-大闸蟹养殖区',     date('now', '-10 days'),date('now', '-13 days'),1.50,  1500, 'XM20260508E1', 'ACTIVE', '长江系扣蟹，规格整齐',         datetime('now'), datetime('now')),
('南美白对虾', '虾苗', 100000, 'tail', '正大虾苗场',         'A区-南美白对虾养殖区', date('now', '-1 days'), date('now', '-3 days'), 0.15, 30000, 'XM20260518A2', 'ACTIVE', '正大SPF虾苗，规格1.0cm',     datetime('now'), datetime('now')),
('海鲈鱼',     '鱼苗',   6000, 'tail', '福建宁德育苗场',     'C区-海鲈鱼养殖区',     date('now', '-4 days'), date('now', '-6 days'), 1.60,  2000, 'XM20260514C2', 'ACTIVE', '加州鲈鱼苗，4-6cm',          datetime('now'), datetime('now'));

-- ============================================================
-- 17. 药品库存 (medicine_inventory)
-- ============================================================
INSERT INTO medicine_inventory (medicine_name, medicine_type, specification, quantity, unit, supplier, zone_name, purchase_date, expiry_date, price, min_threshold, usage_description, status, remarks, create_time, update_time) VALUES
('聚维酮碘',     '消毒剂', '10L/桶',   8, 'bottle', '山西润盈生物科技', NULL, date('now', '-15 days'), date('now', '+365 days'), 180, 3, '水体消毒1ppm，稀释后全池泼洒',                    'ACTIVE', '常规消毒剂',           datetime('now'), datetime('now')),
('二氧化氯',     '消毒剂', '1kg/袋',  15, 'bag',   '山东华实药业',     NULL, date('now', '-20 days'), date('now', '+540 days'), 120, 5, '快速消毒剂0.5ppm，现配现用',                      'ACTIVE', '快速消毒',             datetime('now'), datetime('now')),
('氟苯尼考',     '抗生素', '500g/瓶',  6, 'bottle','浙江海正药业',     NULL, date('now', '-10 days'), date('now', '+720 days'), 350, 2, '拌料投喂10-20mg/kg鱼体重，连用3-5天',           'ACTIVE', '广谱抗菌',             datetime('now'), datetime('now')),
('大蒜素',       '中草药', '1kg/袋',  10, 'bag',   '山东圣琪生物',     NULL, date('now', '-25 days'), date('now', '+540 days'),  80, 3, '拌料0.1-0.2%，天然诱食+抗菌',                    'ACTIVE', '天然添加剂',           datetime('now'), datetime('now')),
('恩诺沙星',     '抗生素', '500g/瓶',  4, 'bottle','上海诺华动物保健', NULL, date('now', '-12 days'), date('now', '+720 days'), 420, 2, '拌料10mg/kg，连用5-7天',                          'ACTIVE', '鱼类专用抗生素',       datetime('now'), datetime('now')),
('过硫酸氢钾',   '改底剂', '10kg/桶',  5, 'bottle','北京水世纪生物',   NULL, date('now', '-8 days'),  date('now', '+365 days'), 260, 2, '底质改良250g/亩，氧化底泥',                       'ACTIVE', '改底专用',             datetime('now'), datetime('now')),
('维生素C',      '营养剂', '500g/袋', 20, 'bag',   '广州白云山兽药',   NULL, date('now', '-5 days'),  date('now', '+720 days'),  45, 5, '拌料投喂0.5-1g/kg，增强免疫力，抗应激',          'ACTIVE', '虾蟹鱼通用营养剂',     datetime('now'), datetime('now')),
('EM菌液',       '益生菌', '5L/桶',    4, 'bottle','山东蔚蓝生物',     NULL, date('now', '-3 days'),  date('now', '+180 days'), 150, 2, '调水使用1L/亩，全池泼洒，改善水质',              'ACTIVE', '水质调节专用',         datetime('now'), datetime('now'));

-- ============================================================
-- 18. 通知告警 (notification)
-- ============================================================
INSERT INTO notification (title, content, type, priority, zone_id, related_id, status, sender_id, sender_name, target_type, target_role, target_user_id, create_time, read_time) VALUES
('水质异常警告',    'A区-南美白对虾池溶解氧降至6.5mg/L，接近阈值，请及时增氧处理！',           'WARNING',  'HIGH',   1, NULL, 'READ',   1, '林志祥', 'ALL',   NULL, NULL, datetime('now', '-8 days'), datetime('now', '-7 days')),
('水质异常警告',    'E区-大闸蟹区溶解氧偏低5.2mg/L，请检查增氧设备运行状态！',                 'WARNING',  'HIGH',   5, NULL, 'READ',   1, '林志祥', 'ALL',   NULL, NULL, datetime('now', '-10 days'),datetime('now', '-9 days')),
('饲料库存提醒',    '鲍鱼配合饲料库存仅剩8袋，低于最低阈值（10袋），请及时采购补货！',          'INFO',     'MEDIUM', NULL, 4,   'UNREAD', 1, '林志祥', 'ROLE',  'MANAGER', NULL, datetime('now', '-2 days'), NULL),
('投喂提醒',        '请按时投喂A区南美白对虾，早晨7:00和傍晚17:00各一次',                       'REMINDER', 'LOW',    1, NULL, 'UNREAD', 1, '林志祥', 'ROLE',  'BREEDER', NULL, datetime('now', '-1 days'), NULL),
('疾病预警',        'A区南美白对虾(第二批)检测出弧菌病，请立即采取治疗措施！',                   'WARNING',  'HIGH',   1, NULL, 'UNREAD', 1, '林志祥', 'ALL',   NULL, NULL, datetime('now', '-3 days'), NULL),
('疾病预警',        'C区海鲈鱼出现烂鳃病症状，请尽快安排治疗',                                   'WARNING',  'HIGH',   3, NULL, 'UNREAD', 1, '林志祥', 'ALL',   NULL, NULL, datetime('now', '-5 days'), NULL),
('系统通知',        '数据库自动备份已成功完成，备份文件大小：45MB',                               'INFO',     'LOW',    NULL,NULL,'READ',   1, '林志祥', 'ROLE',  'OPERATOR', NULL, datetime('now', '-4 days'), datetime('now', '-4 days')),
('药品库存提醒',    '二氧化氯库存15袋，暂时充足，但请注意保质期管理',                            'INFO',     'LOW',    NULL,NULL,'UNREAD', 1, '林志祥', 'ROLE',  'MANAGER', NULL, datetime('now', '-1 days'), NULL),
('养殖任务通知',    '本周五下午2点召开养殖技术研讨会，请各区域负责人准时参加',                     'INFO',     'MEDIUM', NULL,NULL,'UNREAD', 2, '陈总工', 'ROLE',  'BREEDER', NULL, datetime('now', '-2 days'), NULL),
('新人入职通知',    '欢迎新同事黄员加入A区养殖团队，请大家多多关照',                              'INFO',     'LOW',    NULL,NULL,'UNREAD', 6, '李管理', 'ROLE',  'BREEDER', NULL, datetime('now', '-1 days'), NULL);

-- ============================================================
-- 19. 智能提醒 (smart_reminder)
-- ============================================================
INSERT INTO smart_reminder (reminder_type, title, content, zone_id, reminder_date, reminder_time, repeat_type, is_enabled, user_id, last_trigger_time, create_time, update_time) VALUES
('FEEDING',       'A区早晨投喂',   '请于7:00投喂A区南美白对虾，投喂量约65kg',          1, date('now'), '07:00:00', 'DAILY',   1, 9,  datetime('now', '-1 days', 'start of day', '+7 hours'), datetime('now'), datetime('now')),
('FEEDING',       'A区傍晚投喂',   '请于17:00投喂A区南美白对虾，投喂量约75kg',         1, date('now'), '17:00:00', 'DAILY',   1, 9,  datetime('now', '-1 days', 'start of day', '+17 hours'),datetime('now'), datetime('now')),
('WATER_QUALITY', 'A区水质监测',   '请于8:00、12:00、18:00各监测一次A区水质',          1, date('now'), '08:00:00', 'DAILY',   1, 9,  NULL,                                                    datetime('now'), datetime('now')),
('FEEDING',       'B区石斑鱼投喂', '请于9:00投喂B区石斑鱼，投喂量约52kg',              2, date('now'), '09:00:00', 'DAILY',   1, 10, datetime('now', '-1 days', 'start of day', '+9 hours'), datetime('now'), datetime('now')),
('FEEDING',       'C区海鲈鱼投喂', '请于8:00投喂C区海鲈鱼，投喂量约58kg',              3, date('now'), '08:00:00', 'DAILY',   1, 11, datetime('now', '-1 days', 'start of day', '+8 hours'), datetime('now'), datetime('now')),
('INVENTORY',     '饲料库存周检',   '请于每周一检查所有饲料库存，低于阈值及时采购',     NULL, date('now', 'weekday 1'), '10:00:00', 'WEEKLY', 1, 6,  NULL,                                              datetime('now'), datetime('now')),
('MAINTENANCE',   '增氧设备检查',   '每周检查增氧机运行状态，清理滤网',                  NULL, date('now', 'weekday 3'), '09:00:00', 'WEEKLY', 1, 3,  NULL,                                              datetime('now'), datetime('now')),
('CUSTOM',        '月度总结报告',   '整理本月养殖数据和成本效益分析报告',                NULL, date('now', '+3 days'), '15:00:00', 'ONCE',   1, 6,  NULL,                                              datetime('now'), datetime('now'));

-- ============================================================
-- 20. 知识库 (knowledge_base)
-- ============================================================
INSERT INTO knowledge_base (category, title, content, tags, view_count, is_published, create_time, update_time) VALUES
('水质管理', '溶解氧管理要点',
 '溶解氧是水产养殖最重要的参数之一。虾类要求5mg/L以上，鱼类4mg/L以上。早晚温差大时注意凌晨溶氧最低值。增氧机应在凌晨2-6点开启，阴雨天全天开启。',
 '水质,溶解氧,管理', 156, 1, datetime('now'), datetime('now')),
('水质管理', 'pH值调控方法',
 'pH值7.5-8.5为最适范围。pH过低可用生石灰(5-10kg/亩)调节；pH过高可用醋酸或EM菌调节。避免pH日波动超过0.5。',
 '水质,pH,调节', 98, 1, datetime('now'), datetime('now')),
('病害防治', '南美白对虾常见病害与防治',
 '白斑病：虾体白色斑点→聚维酮碘0.3ppm消毒；肠炎病：肠道发红→大蒜素0.1%拌料；弧菌病：体色发红→二氧化氯消毒+氟苯尼考拌料；早期死亡综合征(EMS)：肝胰腺萎缩→加强水质管理。',
 '南美白对虾,病害,防治', 210, 1, datetime('now'), datetime('now')),
('病害防治', '石斑鱼病害防治手册',
 '弧菌病：体表溃疡，氟苯尼考20mg/kg拌料5天；小瓜虫病：体表白点，升温28℃+硫酸铜0.5ppm；烂尾病：碘制剂消毒+VC增强体质；病毒性神经坏死(VNN)：无特效药，重在预防。',
 '石斑鱼,病害,防治', 132, 1, datetime('now'), datetime('now')),
('养殖技术', '南美白对虾高位池精养技术',
 '放苗密度8-10万尾/亩，水温保持25-30℃，盐度15-25‰。每天投喂2-3次，投喂量占体重3-5%。定期使用EM菌调水，每7-10天换水15-20%。养殖周期90-120天。',
 '南美白对虾,养殖,技术', 185, 1, datetime('now'), datetime('now')),
('养殖技术', '石斑鱼工厂化养殖要点',
 '适宜水温22-30℃，盐度25-35‰，pH7.5-8.5。每天投喂1次，投喂量为体重的2-3%。流水养殖流速0.5-1L/s。定期分池避免大小残杀。',
 '石斑鱼,养殖,技术', 89, 1, datetime('now'), datetime('now')),
('养殖技术', '大闸蟹生态养殖技术',
 '水质要求：pH7.5-8.5，溶解氧≥4mg/L。水草覆盖率60%以上。投喂螺蛳、玉米、小麦、小鱼等，傍晚投喂。定期使用EM菌和底改剂。每年9-11月为上市期。',
 '大闸蟹,生态,养殖', 76, 1, datetime('now'), datetime('now')),
('养殖技术', '鲍鱼养殖管理',
 '适宜水温14-22℃（超过25℃危险），盐度28-35‰。水质要求清澈高氧(≥6mg/L)。每2天投喂一次海藻或专用配合饲料。定期清理池底残饵。',
 '鲍鱼,养殖,技术', 65, 1, datetime('now'), datetime('now'));

-- ============================================================
-- 21. 操作日志 (operation_log) — user_id 引用 sys_user.id
-- ============================================================
INSERT INTO operation_log (user_id, username, module, operation, method, params, ip_address, status, error_msg, execute_time, create_time) VALUES
(1,  'superadmin',  '用户管理', '新增用户',         'POST', '{"username":"breeder01"}',                    '192.168.1.100', 'SUCCESS', NULL, 120, datetime('now', '-3 days')),
(1,  'superadmin',  '用户管理', '新增用户',         'POST', '{"username":"operator01"}',                  '192.168.1.100', 'SUCCESS', NULL, 135, datetime('now', '-2 days')),
(1,  'superadmin',  '区域管理', '新增区域',         'POST', '{"name":"A区-南美白对虾池"}',                '192.168.1.100', 'SUCCESS', NULL,  95, datetime('now', '-2 days')),
(6,  'manager01',   '水质管理', '添加水质记录',     'POST', '{"zoneId":1,"temperature":27.5}',            '192.168.1.101', 'SUCCESS', NULL,  88, datetime('now', '-1 days')),
(9,  'breeder01',   '投喂管理', '添加投喂记录',     'POST', '{"zoneId":1,"quantity":65,"feedType":"配合饲料"}','192.168.1.102','SUCCESS', NULL,  76, datetime('now', '-1 days')),
(6,  'manager01',   '库存管理', '饲料入库',         'POST', '{"feedId":1,"quantity":10}',                  '192.168.1.101', 'SUCCESS', NULL, 150, datetime('now', '-10 hours')),
(3,  'operator01',  '系统管理', '修改水质阈值',     'PUT',  '{"zoneId":1,"tempMin":22,"tempMax":32}',      '192.168.1.103', 'SUCCESS', NULL, 105, datetime('now', '-6 hours')),
(6,  'manager01',   '销售管理', '新增销售记录',     'POST', '{"orderNo":"SO20260517003"}',                 '192.168.1.101', 'SUCCESS', NULL, 160, datetime('now', '-3 hours')),
(9,  'breeder01',   '病害管理', '添加疾病记录',     'POST', '{"zoneId":1,"diseaseName":"弧菌病"}',         '192.168.1.102', 'SUCCESS', NULL,  92, datetime('now', '-3 hours')),
(3,  'operator01',  '数据备份', '手动备份数据库',   'POST', '{"type":"manual"}',                            '192.168.1.103', 'SUCCESS', NULL, 280, datetime('now', '-5 hours'));

-- ============================================================
-- 22. 系统配置 (system_config) — config_key UNIQUE
-- ============================================================
INSERT INTO system_config (config_key, config_value, config_type, description, is_system, create_time, update_time) VALUES
('water_quality_check_interval',   '30',       'NUMBER',  '水质自动监测间隔(分钟)',           1, datetime('now'), datetime('now')),
('low_dissolved_oxygen_threshold', '4.0',      'NUMBER',  '低溶解氧告警阈值(mg/L)',          1, datetime('now'), datetime('now')),
('high_temp_threshold',            '32.0',     'NUMBER',  '高温告警阈值(°C)',                 1, datetime('now'), datetime('now')),
('enable_auto_notification',       'true',     'BOOLEAN', '是否启用自动通知推送',            1, datetime('now'), datetime('now')),
('default_language',               'zh-CN',    'STRING',  '系统默认语言',                     1, datetime('now'), datetime('now')),
('backup_enabled',                 'true',     'BOOLEAN', '是否启用自动备份',                 0, datetime('now'), datetime('now')),
('backup_time',                    '02:00',    'STRING',  '每日自动备份时间',                 0, datetime('now'), datetime('now')),
('backup_retention_days',          '30',       'NUMBER',  '备份文件保留天数',                 0, datetime('now'), datetime('now')),
('system_name',                    '海洋养殖管理系统',   'STRING', '系统名称',                1, datetime('now'), datetime('now')),
('system_version',                 '2.0.0',    'STRING',  '系统版本号',                       1, datetime('now'), datetime('now')),
('contact_phone',                  '',          'STRING',  '联系电话',                         0, datetime('now'), datetime('now')),
('announcement',                   '',          'STRING',  '系统公告',                         0, datetime('now'), datetime('now')),
('max_login_attempts',             '5',         'NUMBER',  '登录失败锁定次数',                 1, datetime('now'), datetime('now')),
('session_timeout',                '120',       'NUMBER',  '会话超时时间(分钟)',              1, datetime('now'), datetime('now')),
('min_password_length',            '6',         'NUMBER',  '密码最小长度',                     1, datetime('now'), datetime('now')),
('enable_water_alarm',             'true',      'BOOLEAN', '水质告警阈值启用',                 1, datetime('now'), datetime('now')),
('enable_inventory_alarm',         'true',      'BOOLEAN', '库存预警启用',                     1, datetime('now'), datetime('now')),
('enable_disease_alarm',           'true',      'BOOLEAN', '病害防控提醒启用',                 1, datetime('now'), datetime('now')),
('remind_advance_hours',           '24',        'NUMBER',  '提醒提前时间(小时)',              1, datetime('now'), datetime('now'));

-- ============================================================
-- 23. 库存出入库记录 (inventory_log)
-- ============================================================
INSERT INTO inventory_log (inventory_type, inventory_id, operation_type, quantity, balance, operator_id, operator_name, operation_date, remarks, create_time) VALUES
('FEED',    1, 'IN',  20,  45, 6, 'manager01',  date('now', '-10 days'), '首批入库',              datetime('now', '-10 days')),
('FEED',    1, 'OUT',  5,  40, 9, 'breeder01',  date('now', '-8 days'),  'A区领用',               datetime('now', '-8 days')),
('FEED',    1, 'OUT', 10,  30, 9, 'breeder01',  date('now', '-3 days'),  'A区批量领用',           datetime('now', '-3 days')),
('FEED',    2, 'IN',  15,  30, 6, 'manager01',  date('now', '-8 days'),  '石斑鱼饲料入库',        datetime('now', '-8 days')),
('FEED',    2, 'OUT',  5,  25, 10,'breeder02',  date('now', '-5 days'),  'B区领用',               datetime('now', '-5 days')),
('FEED',    2, 'OUT',  5,  20, 10,'breeder02',  date('now', '-2 days'),  'B区石斑鱼幼鱼料领用',   datetime('now', '-2 days')),
('FEED',    3, 'IN',  25,  45, 6, 'manager01',  date('now', '-15 days'), '海鲈鱼饲料入库',        datetime('now', '-15 days')),
('FEED',    3, 'OUT',  5,  40, 11,'breeder03',  date('now', '-10 days'), 'C区领用',               datetime('now', '-10 days')),
('FEED',    4, 'IN',   8,  16, 6, 'manager01',  date('now', '-20 days'), '鲍鱼饲料入库',          datetime('now', '-20 days')),
('FEED',    4, 'OUT',  2,  14, 12,'breeder04',  date('now', '-15 days'), 'D区领用',               datetime('now', '-15 days')),
('FEED',    5, 'IN',  12,  24, 6, 'manager01',  date('now', '-12 days'), '大闸蟹饲料入库',        datetime('now', '-12 days')),
('FEED',    5, 'OUT',  3,  21, 13,'breeder05',  date('now', '-8 days'),  'E区领用',               datetime('now', '-8 days')),
('MEDICINE',1, 'IN',  10,  18, 6, 'manager01',  date('now', '-15 days'), '聚维酮碘入库',          datetime('now', '-15 days')),
('MEDICINE',1, 'OUT',  2,  16, 9, 'breeder01',  date('now', '-10 days'), 'A区消毒领用',           datetime('now', '-10 days')),
('MEDICINE',2, 'OUT',  3,  15, 10,'breeder02',  date('now', '-13 days'), 'B区小瓜虫病消毒领用',   datetime('now', '-13 days')),
('MEDICINE',3, 'OUT',  1,   5, 9, 'breeder01',  date('now', '-3 days'),  'A区治疗弧菌病领用',     datetime('now', '-3 days')),
('MEDICINE',4, 'IN',  10,  20, 6, 'manager01',  date('now', '-25 days'), '大蒜素入库',            datetime('now', '-25 days')),
('MEDICINE',7, 'IN',  20,  40, 6, 'manager01',  date('now', '-5 days'),  '维生素C入库',           datetime('now', '-5 days')),
('MEDICINE',7, 'OUT',  3,  37, 11,'breeder03',  date('now', '-3 days'),  'C区烂鳃病辅助治疗',     datetime('now', '-3 days')),
('SEEDLING', 1, 'IN', 180000, 180000, 6, 'manager01', date('now', '-5 days'), '虾苗入库',          datetime('now', '-5 days')),
('SEEDLING', 1, 'OUT', 20000, 160000, 9, 'breeder01',  date('now', '-5 days'), 'A区补苗出库',      datetime('now', '-5 days')),
('SEEDLING', 2, 'IN',  8000,   8000, 6, 'manager01',  date('now', '-3 days'), '石斑鱼苗入库',     datetime('now', '-3 days')),
('SEEDLING', 3, 'IN', 12000,  12000, 6, 'manager01',  date('now', '-7 days'), '海鲈鱼苗入库',     datetime('now', '-7 days')),
('SEEDLING', 4, 'IN',  5000,   5000, 6, 'manager01',  date('now', '-2 days'), '鲍鱼苗入库',       datetime('now', '-2 days'));

-- ============================================================
-- 数据初始化完成
-- 总计: 13用户 + 7区域 + 5阈值 + 5模拟 + 7苗种投放 + 36投喂
--       + 65水质 + 9疾病 + 7捕捞 + 11生产计划 + 27成本 + 7收益
--       + 11销售 + 7溯源 + 8饲料库存 + 7苗种库存 + 8药品库存
--       + 8通知 + 7智能提醒 + 8知识库 + 10操作日志 + 8系统配置 + 24库存流水
--       ≈ 280+ 条记录
-- ============================================================