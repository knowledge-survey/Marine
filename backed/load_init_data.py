import sqlite3
import os

db_path = 'marine_aquaculture.db'
sql_path = os.path.join('src', 'main', 'resources', 'init_data.sql')

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

with open(sql_path, 'r', encoding='utf-8') as f:
    sql_content = f.read()

# Split by semicolon and execute each statement
statements = sql_content.split(';')
for stmt in statements:
    stmt = stmt.strip()
    if stmt and not stmt.startswith('--'):
        # Remove inline comments
        lines = []
        for line in stmt.split('\n'):
            line = line.strip()
            if line and not line.startswith('--'):
                lines.append(line)
        clean_stmt = ' '.join(lines)
        if clean_stmt:
            try:
                cursor.execute(clean_stmt)
            except Exception as e:
                print(f'Error: {e}')
                print(f'Statement: {clean_stmt[:100]}...')

conn.commit()

# Verify data loaded
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record', 
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record']

print('\n=== 数据加载完成，各表记录数 ===')
for table in tables:
    cursor.execute(f'SELECT COUNT(*) FROM {table}')
    count = cursor.fetchone()[0]
    print(f'{table}: {count} 条')

conn.close()
print('\n初始数据加载成功！')
