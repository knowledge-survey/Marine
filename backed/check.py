import sqlite3
conn = sqlite3.connect('marine_aquaculture.db')
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record',
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record']
print('=== 各表记录数 ===')
for t in tables:
    c = conn.execute(f'SELECT COUNT(*) FROM {t}')
    print(f'{t}: {c.fetchone()[0]} 条')
conn.close()
