from pathlib import Path
p = Path(r"c:\Users\Hussein\workspace\attach_improve\smat-ins\src\main\java\com\smat\ins\view\resources\interface_ar.properties")
if not p.exists():
    print('File not found:', p)
    raise SystemExit(1)
lines = p.read_text(encoding='utf-8').splitlines()
keys = {}
for i,l in enumerate(lines):
    s = l.strip()
    if not s or s.startswith('#') or s.startswith('//'):
        continue
    if '=' in l:
        key = l.split('=',1)[0].strip()
        keys.setdefault(key, []).append(i+1)

dups = {k:v for k,v in keys.items() if len(v)>1}
if not dups:
    print('No duplicates found in', p.name)
else:
    print('Duplicate keys in', p.name)
    for k,v in sorted(dups.items()):
        print(f"{k}: lines {', '.join(map(str,v))}")

