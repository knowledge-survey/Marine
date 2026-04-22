import sqlite3
import os

db_path = 'marine_aquaculture.db'
sql_path = 'src/main/resources/init_data.sql'

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
    lines = f.readlines()

cleaned_lines = []
for line in lines:
    stripped = line.strip()
    if not stripped.startswith('--'):
        cleaned_lines.append(line)
clean_sql = ''.join(cleaned_lines)

# Execute all statements
try:
    conn.executescript(clean_sql)
    conn.commit()
    msg = 'OK: SQL executed successfully'
except Exception as e:
    msg = 'ERROR: ' + str(e)

# Verify
tables = ['sys_user', 'aquaculture_zone', 'water_quality_threshold', 'seedling_record',
          'feeding_record', 'water_quality', 'disease_record', 'disease_knowledge',
          'harvest_record', 'traceability_code', 'inspection_record']
counts = []
counts.append('=== 各表记录数 ===')
counts.append(msg)
counts.append('')
for t in tables:
    c = conn.execute('SELECT COUNT(*) FROM ' + t)
    counts.append(t + ': ' + str(c.fetchone()[0]) + ' 条')
conn.close()
counts.append('')
counts.append('DONE')

with open('db_results.txt', 'w', encoding='utf-8') as f:
    f.write('\n'.join(counts))
