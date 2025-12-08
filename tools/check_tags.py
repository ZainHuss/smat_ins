import re
import sys

file_path = 'src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
open_tags = ['p:panelGrid', 'p:scrollPanel']

stack = []
line_no = 0
pattern = re.compile(r'<(/?)(p:(?:panelGrid|scrollPanel))\b', re.IGNORECASE)
with open(file_path, 'r', encoding='utf-8') as f:
    for line_no, line in enumerate(f, start=1):
        for m in pattern.finditer(line):
            closing = m.group(1) == '/'
            tag = m.group(2)
            if not closing:
                stack.append((tag, line_no))
            else:
                if stack and stack[-1][0].lower() == tag.lower():
                    stack.pop()
                else:
                    # mismatch
                    print(f"MISMATCH: closing </{tag}> at line {line_no} but top of stack is {stack[-1] if stack else 'EMPTY'}")

if stack:
    print('\nUNMATCHED OPEN TAGS (from bottom to top):')
    for t, ln in stack:
        print(f"OPEN <{t}> at line {ln}")
else:
    print('All tags matched')

# exit code
sys.exit(0)

