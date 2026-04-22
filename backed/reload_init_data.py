import sqlite3
import os

db_path = 'marine_aquaculture.db'
sql_path = os.path.join('src', 'main', 'resources', 'init_data.sql')

conn = sqlite3.connect(db_path)

# Clear existing data (keep water_quality from simulator)
conn.execute('DELETE FROM sys_user')
conn.execute('DELETE FROM aquaculture_zone')
conn.execute('DELETE FROM water_quality_threshold')
conn.execute('DELETE FROM seedling_record')
conn.execute('DELETE FROM feeding_record')
conn.execute('DELETE FROM disease_record')
conn.execute('DELETE FROM disease_knowledge')
conn.execute('DELETE FROM harvest_record')
conn.execute('DELETE FROM traceability_code')
conn.execute('DELETE FROM inspection_record')
conn.commit()

# Read and clean SQL file
with open(sql_path, 'r', encoding='utf-8') as f:
    sql_content = f.read()

# Remove comment lines
cleaned = []
for line in sql_content.split('\n'):
    stripped = line.strip()
    if not stripped.startswith('--'):
        cleaned.append(line)
clean_sql = '\n'.join(cleaned)

# Execute all statements
conn.executescript(clean_sql)
conn.commit()

# Verify
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record', 
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record']
print('=== 各表记录数 ===')
for table in tables:
    cursor = conn.execute(f'SELECT COUNT(*) FROM {table}')
    count = cursor.fetchone()[0]
    print(f'  {table}: {count} 条')

conn.close()
print('\n初始数据加载成功！')
