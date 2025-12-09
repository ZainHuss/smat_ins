import sys, shutil
from pathlib import Path

if len(sys.argv) < 2:
    print('Usage: python fix_properties_duplicates_arg.py <path-to-properties-file>')
    sys.exit(2)

p = Path(sys.argv[1])
if not p.exists():
    print('File not found:', p)
    sys.exit(1)

text = p.read_text(encoding='utf-8')
lines = text.splitlines()
# find last occurrence of each non-commented key
last = {}
for i,l in enumerate(lines):
    s = l.strip()
    if not s or s.startswith('#') or s.startswith('//'):
        continue
    if '=' in l:
        key = l.split('=',1)[0].strip()
        last[key] = i
# make backup
bak = p.with_suffix(p.suffix + '.bak')
shutil.copyfile(p, bak)
print('Backup created at', bak)
# rewrite with earlier duplicates commented
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

p.write_text('\n'.join(out) + '\n', encoding='utf-8')
# report remaining duplicates
lines2 = p.read_text(encoding='utf-8').splitlines()
keys = {}
for i,l in enumerate(lines2):
    s = l.strip()
    if not s or s.startswith('#') or s.startswith('//'):
        continue
    if '=' in l:
        key = l.split('=',1)[0].strip()
        keys.setdefault(key, []).append(i+1)

dups = {k:v for k,v in keys.items() if len(v) > 1}
if not dups:
    print('No duplicates remain.')
else:
    print('Remaining duplicate keys:')
    for k,v in dups.items():
        print(f"{k}: lines {v}")
print('Done')

