from lxml import etree
file='src/main/webapp/views/archive/my-cabinet/document/view.xhtml'
parser = etree.XMLParser(recover=False)
try:
    etree.parse(file, parser)
    print('Parsed OK')
except etree.XMLSyntaxError as e:
    print('XMLSyntaxError:', e)

