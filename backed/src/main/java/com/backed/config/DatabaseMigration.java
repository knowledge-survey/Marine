package com.backed.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// @Component  // 注释掉这个注解，禁用自动启动时的数据迁移和初始化
public class DatabaseMigration implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // 用户头像字段
            try {
                stmt.execute("ALTER TABLE sys_user ADD COLUMN avatar TEXT");
            } catch (Exception ignored) {
            }

            // 核心表 - 用户
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS sys_user (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username VARCHAR(100) NOT NULL UNIQUE," +
                        "password VARCHAR(255) NOT NULL," +
                        "real_name VARCHAR(100)," +
                        "phone VARCHAR(20)," +
                        "email VARCHAR(200)," +
                        "role VARCHAR(20) DEFAULT 'BREEDER'," +
                        "enabled BOOLEAN DEFAULT TRUE," +
                        "avatar TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 养殖区域
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS aquaculture_zone (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(100) NOT NULL," +
                        "location VARCHAR(200)," +
                        "area DECIMAL(12,2)," +
                        "species VARCHAR(100)," +
                        "description TEXT," +
                        "parent_id INTEGER DEFAULT 0," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 水质检测
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS water_quality (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "temperature DECIMAL(5,2)," +
                        "dissolved_oxygen DECIMAL(5,2)," +
                        "ph DECIMAL(5,2)," +
                        "salinity DECIMAL(5,2)," +
                        "is_warning BOOLEAN DEFAULT FALSE," +
                        "warning_info TEXT," +
                        "record_time DATETIME NOT NULL)");
            } catch (Exception ignored) {}

            // 核心表 - 水质阈值
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS water_quality_threshold (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "temp_min DECIMAL(5,2)," +
                        "temp_max DECIMAL(5,2)," +
                        "do_min DECIMAL(5,2)," +
                        "do_max DECIMAL(5,2)," +
                        "ph_min DECIMAL(5,2)," +
                        "ph_max DECIMAL(5,2)," +
                        "salinity_min DECIMAL(5,2)," +
                        "salinity_max DECIMAL(5,2)," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 苗种投放
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS seedling_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "species VARCHAR(100) NOT NULL," +
                        "batch_no VARCHAR(100)," +
                        "quantity DECIMAL(12,2)," +
                        "source VARCHAR(200)," +
                        "supplier VARCHAR(200)," +
                        "release_date DATE," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 投喂记录
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS feeding_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "species VARCHAR(100) NOT NULL," +
                        "batch_no VARCHAR(100)," +
                        "feed_type VARCHAR(50) NOT NULL," +
                        "quantity DECIMAL(12,2) NOT NULL," +
                        "feeding_time DATETIME NOT NULL," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 疾病记录
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS disease_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "species VARCHAR(100) NOT NULL," +
                        "batch_no VARCHAR(100)," +
                        "disease_name VARCHAR(200) NOT NULL," +
                        "occurrence_date DATE NOT NULL," +
                        "symptoms TEXT," +
                        "treatment TEXT," +
                        "status VARCHAR(20) DEFAULT 'ACTIVE'," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 捕捞记录
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS harvest_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "species VARCHAR(100) NOT NULL," +
                        "batch_no VARCHAR(100)," +
                        "quantity DECIMAL(12,2)," +
                        "avg_weight DECIMAL(10,2)," +
                        "harvest_date DATE," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 核心表 - 溯源码
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS traceability_code (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "code VARCHAR(200) NOT NULL UNIQUE," +
                        "harvest_id INTEGER," +
                        "batch_no VARCHAR(100)," +
                        "zone_id INTEGER," +
                        "qr_code_url TEXT," +
                        "description TEXT," +
                        "scan_count INTEGER DEFAULT 0," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            // 水质模拟配置表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS water_quality_simulation (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL UNIQUE," +
                        "temp_min DECIMAL(5,2)," +
                        "temp_max DECIMAL(5,2)," +
                        "do_min DECIMAL(5,2)," +
                        "do_max DECIMAL(5,2)," +
                        "ph_min DECIMAL(5,2)," +
                        "ph_max DECIMAL(5,2)," +
                        "salinity_min DECIMAL(5,2)," +
                        "salinity_max DECIMAL(5,2))");
            } catch (Exception ignored) {
            }

            // 1. 库存管理表 - 饲料库存
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS feed_inventory (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "feed_name VARCHAR(200) NOT NULL," +
                        "brand VARCHAR(100)," +
                        "feed_type VARCHAR(50) NOT NULL," +
                        "specification VARCHAR(100)," +
                        "quantity DECIMAL(12,2) DEFAULT 0," +
                        "unit VARCHAR(20) NOT NULL," +
                        "supplier VARCHAR(200)," +
                        "zone_name VARCHAR(100)," +
                        "purchase_date DATE," +
                        "expiry_date DATE," +
                        "price DECIMAL(12,2)," +
                        "min_threshold DECIMAL(12,2)," +
                        "status VARCHAR(20) DEFAULT 'ACTIVE'," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 2. 库存管理表 - 苗种库存
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS seedling_inventory (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "species VARCHAR(100) NOT NULL," +
                        "seedling_type VARCHAR(50) NOT NULL," +
                        "quantity DECIMAL(12,2) DEFAULT 0," +
                        "unit VARCHAR(20) NOT NULL," +
                        "supplier VARCHAR(200)," +
                        "zone_name VARCHAR(100)," +
                        "purchase_date DATE," +
                        "hatch_date DATE," +
                        "price DECIMAL(12,2)," +
                        "min_threshold DECIMAL(12,2)," +
                        "batch_no VARCHAR(100)," +
                        "status VARCHAR(20) DEFAULT 'ACTIVE'," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 3. 库存管理表 - 药品库存
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS medicine_inventory (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "medicine_name VARCHAR(200) NOT NULL," +
                        "medicine_type VARCHAR(50) NOT NULL," +
                        "specification VARCHAR(100)," +
                        "quantity DECIMAL(12,2) DEFAULT 0," +
                        "unit VARCHAR(20) NOT NULL," +
                        "supplier VARCHAR(200)," +
                        "zone_name VARCHAR(100)," +
                        "purchase_date DATE," +
                        "expiry_date DATE," +
                        "price DECIMAL(12,2)," +
                        "min_threshold DECIMAL(12,2)," +
                        "usage_description TEXT," +
                        "status VARCHAR(20) DEFAULT 'ACTIVE'," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 4. 成本核算表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS cost_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER," +
                        "batch_no VARCHAR(100)," +
                        "cost_type VARCHAR(50) NOT NULL," +
                        "cost_name VARCHAR(200) NOT NULL," +
                        "amount DECIMAL(12,2) NOT NULL," +
                        "record_date DATE NOT NULL," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 5. 收益记录表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS revenue_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER," +
                        "batch_no VARCHAR(100)," +
                        "revenue_type VARCHAR(50) NOT NULL," +
                        "revenue_name VARCHAR(200) NOT NULL," +
                        "amount DECIMAL(12,2) NOT NULL," +
                        "record_date DATE NOT NULL," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 6. 通知告警表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS notification (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title VARCHAR(200) NOT NULL," +
                        "content TEXT NOT NULL," +
                        "type VARCHAR(50) NOT NULL," +
                        "priority VARCHAR(20) DEFAULT 'MEDIUM'," +
                        "zone_id INTEGER," +
                        "related_id INTEGER," +
                        "status VARCHAR(20) DEFAULT 'UNREAD'," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "read_time DATETIME)");
            } catch (Exception ignored) {
            }

            // 7. 生产计划表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS production_plan (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "zone_id INTEGER NOT NULL," +
                        "zone_name TEXT," +
                        "plan_name VARCHAR(200) NOT NULL," +
                        "species VARCHAR(100) NOT NULL," +
                        "batch_no VARCHAR(100) NOT NULL," +
                        "plan_type VARCHAR(50) NOT NULL," +
                        "start_date DATE NOT NULL," +
                        "end_date DATE," +
                        "target_quantity DECIMAL(12,2)," +
                        "target_size VARCHAR(50)," +
                        "status VARCHAR(20) DEFAULT 'PLANNING'," +
                        "description TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 8. 销售记录表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS sales_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "order_no VARCHAR(100) NOT NULL UNIQUE," +
                        "zone_id INTEGER," +
                        "harvest_id INTEGER," +
                        "product_name VARCHAR(200) NOT NULL," +
                        "species VARCHAR(100) NOT NULL," +
                        "quantity DECIMAL(12,2) NOT NULL," +
                        "unit VARCHAR(20) NOT NULL," +
                        "unit_price DECIMAL(12,2) NOT NULL," +
                        "total_amount DECIMAL(12,2) NOT NULL," +
                        "customer_name VARCHAR(200)," +
                        "customer_phone VARCHAR(50)," +
                        "sale_date DATE NOT NULL," +
                        "payment_method VARCHAR(50)," +
                        "status VARCHAR(20) DEFAULT 'COMPLETED'," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 9. 智能提醒表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS smart_reminder (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "reminder_type VARCHAR(50) NOT NULL," +
                        "title VARCHAR(200) NOT NULL," +
                        "content TEXT NOT NULL," +
                        "zone_id INTEGER," +
                        "reminder_date DATE NOT NULL," +
                        "reminder_time TIME," +
                        "repeat_type VARCHAR(20) DEFAULT 'ONCE'," +
                        "is_enabled BOOLEAN DEFAULT TRUE," +
                        "last_trigger_time DATETIME," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 10. 知识库表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS knowledge_base (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "category VARCHAR(50) NOT NULL," +
                        "title VARCHAR(200) NOT NULL," +
                        "summary TEXT," +
                        "content TEXT NOT NULL," +
                        "tags TEXT," +
                        "view_count INTEGER DEFAULT 0," +
                        "is_published BOOLEAN DEFAULT TRUE," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 11. 操作日志表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS operation_log (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "username VARCHAR(100)," +
                        "module VARCHAR(50) NOT NULL," +
                        "operation VARCHAR(100) NOT NULL," +
                        "method VARCHAR(20)," +
                        "params TEXT," +
                        "ip_address VARCHAR(50)," +
                        "status VARCHAR(20)," +
                        "error_msg TEXT," +
                        "execute_time INTEGER," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 12. 系统设置表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS system_config (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "config_key VARCHAR(100) NOT NULL UNIQUE," +
                        "config_value TEXT," +
                        "config_type VARCHAR(50) DEFAULT 'STRING'," +
                        "description VARCHAR(200)," +
                        "is_system BOOLEAN DEFAULT FALSE," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "update_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 13. 库存出入库记录表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS inventory_log (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "inventory_type VARCHAR(50) NOT NULL," +
                        "inventory_id INTEGER NOT NULL," +
                        "operation_type VARCHAR(20) NOT NULL," +
                        "quantity DECIMAL(12,2) NOT NULL," +
                        "balance DECIMAL(12,2) NOT NULL," +
                        "operator_id INTEGER," +
                        "operator_name VARCHAR(100)," +
                        "operation_date DATE NOT NULL," +
                        "remarks TEXT," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {
            }

            // 14. 通知表增加发送者/目标字段
            try { stmt.execute("ALTER TABLE notification ADD COLUMN sender_id INTEGER"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE notification ADD COLUMN sender_name VARCHAR(100)"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE notification ADD COLUMN target_type VARCHAR(20) DEFAULT 'ALL'"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE notification ADD COLUMN target_role VARCHAR(20)"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE notification ADD COLUMN target_user_id INTEGER"); } catch (Exception ignored) {}

            // 15. 智能提醒表增加用户字段
            try { stmt.execute("ALTER TABLE smart_reminder ADD COLUMN user_id INTEGER"); } catch (Exception ignored) {}
            
            // 16. 知识库表增加摘要字段
            try { stmt.execute("ALTER TABLE knowledge_base ADD COLUMN summary TEXT"); } catch (Exception ignored) {}
            
            // 17. 生产计划表增加区域名称字段
            try { stmt.execute("ALTER TABLE production_plan ADD COLUMN zone_name TEXT"); } catch (Exception ignored) {}
            
            // 18. 用户通知阅读状态表
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS user_notification_read (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "notification_id INTEGER NOT NULL," +
                        "read_time DATETIME," +
                        "create_time DATETIME DEFAULT CURRENT_TIMESTAMP)");
            } catch (Exception ignored) {}

            loadInitData(stmt);
        }
    }

    private void loadInitData(Statement stmt) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sys_user");
            if (rs.next() && rs.getInt(1) > 0) {
                rs.close();
                return;
            }
            rs.close();

            ClassPathResource resource = new ClassPathResource("init_data.sql");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    sb.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        stmt.execute(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
            System.out.println(">>> init_data.sql loaded successfully");
        } catch (Exception e) {
            System.out.println(">>> init_data.sql loading skipped: " + e.getMessage());
        }
    }
}