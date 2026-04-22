import sqlite3
conn = sqlite3.connect('marine_aquaculture.db')
cursor = conn.cursor()
cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
tables = cursor.fetchall()
for t in tables:
    print(f'=== {t[0]} ===')
    cursor.execute(f'PRAGMA table_info({t[0]})')
    cols = cursor.fetchall()
    for c in cols:
        print(f'  {c[1]} ({c[2]})')
    print()
conn.close()
