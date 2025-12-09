import shutil
from pathlib import Path
p = Path(r"c:\Users\Hussein\workspace\attach_improve\smat-ins\src\main\java\com\smat\ins\view\resources\interface_en.properties")
if not p.exists():
    print('File not found:', p)
    raise SystemExit(1)
lines = p.read_text(encoding='utf-8').splitlines()
# Collect last index of each non-commented key
last = {}
for i,l in enumerate(lines):
    s = l.strip()
    if not s or s.startswith('#') or s.startswith('//'):
        continue
    if '=' in l:
        key = l.split('=',1)[0].strip()
        last[key] = i
# Create backup
bak = p.with_suffix('.properties.bak')
shutil.copyfile(p, bak)
print('Backup written to', bak)
# Rewrite file, commenting earlier duplicates
out = []
for i,l in enumerate(lines):
    s = l.strip()
    if not s or s.startswith('#') or s.startswith('//') or '=' not in l:
        out.append(l)
        continue
    key = l.split('=',1)[0].strip()
    if last.get(key) != i:
        out.append('#' + l)
    else:
        out.append(l)
# Write back
p.write_text('\n'.join(out) + '\n', encoding='utf-8')
# Report duplicates remaining
lines2 = p.read_text(encoding='utf-8').splitlines()
keys = {}
for i,l in enumerate(lines2):
    s = l.strip()
    if not s or s.startswith('#') or s.startswith('//'):
        continue
    if '=' in l:
        key = l.split('=',1)[0].strip()
        keys.setdefault(key, []).append(i+1)
dups = {k:v for k,v in keys.items() if len(v)>1}
if not dups:
    print('No duplicates remain.')
else:
    print('Remaining duplicates:')
    for k,v in dups.items():
        print(f"{k}: lines {v}")
print('Done')

