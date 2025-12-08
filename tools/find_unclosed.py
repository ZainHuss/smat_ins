import re
file='src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
open_pat = re.compile(r'<p:(panelGrid|scrollPanel)\b', re.IGNORECASE)
close_pat = re.compile(r'</p:(panelGrid|scrollPanel)>', re.IGNORECASE)
stack=[]
with open(file,'r',encoding='utf-8') as f:
    for i,line in enumerate(f, start=1):
        for m in open_pat.finditer(line):
            stack.append((m.group(1), i))
        for m in close_pat.finditer(line):
            tag = m.group(1)
            if stack and stack[-1][0].lower() == tag.lower():
                stack.pop()
            else:
                # try to find matching opener earlier
                found=False
                for j in range(len(stack)-1,-1,-1):
                    if stack[j][0].lower()==tag.lower():
                        # pop everything above
                        stack=stack[:j]
                        found=True
                        break
                if not found:
                    print(f"Unmatched closing </p:{tag}> at line {i}")

if stack:
    print('Unmatched opening tags (bottom->top):')
    for t,ln in stack:
        print(f"<{t}> opened at line {ln}")
else:
    print('All matched')

