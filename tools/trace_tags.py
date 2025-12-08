import re
file='src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
open_pat = re.compile(r'<p:(panelGrid|scrollPanel)\b', re.IGNORECASE)
close_pat = re.compile(r'</p:(panelGrid|scrollPanel)>', re.IGNORECASE)
stack=[]
with open(file,'r',encoding='utf-8') as f:
    for i,line in enumerate(f, start=1):
        for m in open_pat.finditer(line):
            tag=m.group(1)
            stack.append((tag,i))
            print(f"{i}: PUSH <{tag}> stack_size={len(stack)}")
        for m in close_pat.finditer(line):
            tag=m.group(1)
            if stack and stack[-1][0].lower()==tag.lower():
                popped=stack.pop()
                print(f"{i}: POP </{tag}> matched {popped} stack_size={len(stack)}")
            else:
                print(f"{i}: POP </{tag}> but top is {stack[-1] if stack else 'EMPTY'}")

print('\nFINAL STACK:')
for t,ln in stack:
    print(f"<{t}> opened at {ln}")

