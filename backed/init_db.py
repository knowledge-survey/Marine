import sqlite3
import os

db_path = 'marine_aquaculture.db'
sql_path = os.path.join('src', 'main', 'resources', 'init_data.sql')

# 删除旧数据库
if os.path.exists(db_path):
    os.remove(db_path)
    print(f'已删除旧数据库: {db_path}')

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# 执行SQL文件
with open(sql_path, 'r', encoding='utf-8') as f:
    sql_content = f.read()

# 直接执行整个SQL内容
try:
    cursor.executescript(sql_content)
    print('SQL执行成功')
except Exception as e:
    print(f'错误: {e}')

conn.commit()

# 检查数据
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record', 
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record', 'medication_record']

print('\n=== 各表记录数 ===')
for table in tables:
    try:
        cursor.execute(f'SELECT COUNT(*) FROM {table}')
        count = cursor.fetchone()[0]
        print(f'{table}: {count} 条')
    except Exception as e:
        print(f'{table}: 查询失败: {e}')

conn.close()
print('\n数据库初始化完成！')
