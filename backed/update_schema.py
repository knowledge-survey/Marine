import sqlite3

db_path = 'marine_aquaculture.db'

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

print('=== Updating table structure ===')

# 1. 为disease_record添加species和batch_no列
try:
    cursor.execute('ALTER TABLE disease_record ADD COLUMN species TEXT')
    print('disease_record: added species column')
except Exception as e:
    print(f'disease_record species: {e}')

try:
    cursor.execute('ALTER TABLE disease_record ADD COLUMN batch_no TEXT')
    print('disease_record: added batch_no column')
except Exception as e:
    print(f'disease_record batch_no: {e}')

# 2. 为feeding_record添加species和batch_no列
try:
    cursor.execute('ALTER TABLE feeding_record ADD COLUMN species TEXT')
    print('feeding_record: added species column')
except Exception as e:
    print(f'feeding_record species: {e}')

try:
    cursor.execute('ALTER TABLE feeding_record ADD COLUMN batch_no TEXT')
    print('feeding_record: added batch_no column')
except Exception as e:
    print(f'feeding_record batch_no: {e}')

# 3. 为harvest_record添加species和batch_no列
try:
    cursor.execute('ALTER TABLE harvest_record ADD COLUMN species TEXT')
    print('harvest_record: added species column')
except Exception as e:
    print(f'harvest_record species: {e}')

try:
    cursor.execute('ALTER TABLE harvest_record ADD COLUMN batch_no TEXT')
    print('harvest_record: added batch_no column')
except Exception as e:
    print(f'harvest_record batch_no: {e}')

conn.commit()

print('\n=== Table structure updated ===')

# 显示各表的结构
tables = ['disease_record', 'feeding_record', 'harvest_record']
for table in tables:
    print(f'\n{table} structure:')
    cursor.execute(f'PRAGMA table_info({table})')
    for col in cursor.fetchall():
        print(f'  {col[1]} - {col[2]}')

conn.close()
print('\nDone!')
