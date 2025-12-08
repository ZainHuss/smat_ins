file='src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
openers = ['<p:panelGrid', '<p:scrollPanel']
closers = ['</p:panelGrid>', '</p:scrollPanel>']
stack=[]
with open(file, 'r', encoding='utf-8') as f:
    for i,line in enumerate(f, start=1):
        # find any opener occurrences
        for op in openers:
            idx = line.find(op)
            if idx!=-1:
                stack.append((op, i))
        for cl in closers:
            idx = line.find(cl)
            if idx!=-1:
                # determine which opener corresponds
                expected = '<' + cl[2:]
                if stack and (stack[-1][0].lower().startswith(expected.lower().replace('>', '')) or stack[-1][0].lower().startswith(expected.lower())):
                    stack.pop()
                else:
                    # try to pop matching opener regardless of last
                    found=False
                    for j in range(len(stack)-1,-1,-1):
                        if stack[j][0].lower().startswith(expected.lower()):
                            # remove that
                            stack.pop(j)
                            found=True
                            break
                    if not found:
                        print(f"Unmatched closing {cl} at line {i}")

if stack:
    print('Unmatched opening tags:')
    for t,ln in stack:
        print(t, ln)
else:
    print('All matched')

