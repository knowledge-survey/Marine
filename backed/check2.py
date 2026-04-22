import sqlite3
conn = sqlite3.connect('marine_aquaculture.db')
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record',
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record']
result = []
result.append('=== 各表记录数 ===')
for t in tables:
    c = conn.execute('SELECT COUNT(*) FROM ' + t)
    result.append(t + ': ' + str(c.fetchone()[0]) + ' 条')
conn.close()
result.append('DONE')
with open('db_counts.txt', 'w', encoding='utf-8') as f:
    for line in result:
        f.write(line + '\n')
print('Results written to db_counts.txt')
