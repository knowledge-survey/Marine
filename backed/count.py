import sqlite3
db_path = 'marine_aquaculture.db'
conn = sqlite3.connect(db_path)
cursor = conn.cursor()
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record', 
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record']
print('=== 各表记录数 ===')
for table in tables:
    cursor.execute(f'SELECT COUNT(*) FROM {table}')
    count = cursor.fetchone()[0]
    print(f'  {table}: {count} 条')
conn.close()
