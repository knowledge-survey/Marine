import sqlite3

db_path = 'marine_aquaculture.db'

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

print("=== 数据库表结构检查 ===\n")

# Get all tables
cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
tables = [t[0] for t in cursor.fetchall()]

for table in tables:
    print(f"\n表: {table}")
    print("-" * 40)
    
    cursor.execute(f"PRAGMA table_info({table})")
    columns = cursor.fetchall()
    
    for col in columns:
        print(f"  {col[1]} - {col[2]}")

print("\n=== 表结构检查完成 ===")

# Check feeding_record specifically
if 'feeding_record' in tables:
    print("\n=== feeding_record 详细检查 ===")
    cursor.execute("PRAGMA table_info(feeding_record)")
    print("字段列表:")
    for col in cursor.fetchall():
        print(f"  {col[1]}")
    
    # Check if we have the new columns
    cursor.execute("PRAGMA table_info(feeding_record)")
    columns = [c[1] for c in cursor.fetchall()]
    
    required_fields = ['species', 'batch_no']
    for field in required_fields:
        if field in columns:
            print(f"✓ {field} 存在")
        else:
            print(f"✗ {field} 缺失！")

conn.close()
