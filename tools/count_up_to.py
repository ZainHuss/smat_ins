file='src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
line_limit=636
open_count=0
close_count=0
with open(file,'r',encoding='utf-8') as f:
    for i,line in enumerate(f, start=1):
        if i>line_limit: break
        open_count += line.count('<p:panelGrid') + line.count('<p:scrollPanel')
        close_count += line.count('</p:panelGrid>') + line.count('</p:scrollPanel>')
print(f'Up to line {line_limit}: open={open_count} close={close_count}')
# also print last 20 tags with lines
f2=open(file,'r',encoding='utf-8')
entries=[]
for i,line in enumerate(f2, start=1):
    if '<p:panelGrid' in line or '</p:panelGrid>' in line or '<p:scrollPanel' in line or '</p:scrollPanel>' in line:
        entries.append((i,line.strip()))

for e in entries[:40]:
    print(e)

