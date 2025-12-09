import sys
from pathlib import Path

def find_dups(path):
    p = Path(path)
    if not p.exists():
        print(f'FILE-NOT-FOUND: {p}')
        return 2
    lines = p.read_text(encoding='utf-8').splitlines()
    keys = {}
    for i,l in enumerate(lines):
        s = l.strip()
        if not s or s.startswith('#') or s.startswith('//'):
            continue
        if '=' in l:
            k = l.split('=',1)[0].strip()
            keys.setdefault(k, []).append(i+1)
    dups = {k:v for k,v in keys.items() if len(v)>1}
    if not dups:
        print(f'No duplicates in: {p}')
        return 0
    print(f'Duplicates in: {p}')
    for k,v in sorted(dups.items()):
        print(f'{k}: lines {", ".join(map(str,v))}')
    return 1

if __name__ == "__main__":
    if len(sys.argv)<2:
        print('Usage: find_duplicates.py <file> [<file> ...]')
        sys.exit(2)
    rc = 0
    for f in sys.argv[1:]:
        rc |= find_dups(f)
    sys.exit(rc)

