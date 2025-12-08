tags = ['<p:panelGrid', '</p:panelGrid>', '<p:scrollPanel', '</p:scrollPanel>']
file_path = 'src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
with open(file_path, 'r', encoding='utf-8') as f:
    for i, line in enumerate(f, start=1):
        for t in tags:
            if t in line:
                print(f"{i}: {line.strip()}")
                break

