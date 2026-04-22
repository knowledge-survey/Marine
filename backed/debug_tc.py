import sqlite3
conn = sqlite3.connect('marine_aquaculture.db')
cursor = conn.cursor()
cursor.execute('PRAGMA table_info(traceability_code)')
print('=== traceability_code 表结构 ===')
for c in cursor.fetchall():
    print(c)
cursor.execute('SELECT * FROM traceability_code')
print('\n=== 数据 ===')
for r in cursor.fetchall():
    print(r)
conn.close()
